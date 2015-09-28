package com.s_diadamo.readlist.general;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.support.v4.widget.DrawerLayout;

import com.parse.ParseAnalytics;
import com.s_diadamo.readlist.R;
import com.s_diadamo.readlist.book.Book;
import com.s_diadamo.readlist.book.BookFragment;
import com.s_diadamo.readlist.book.BookOperations;
import com.s_diadamo.readlist.goal.GoalFragment;
import com.s_diadamo.readlist.lazylist.ImageLoader;
import com.s_diadamo.readlist.lent.LentFragment;
import com.s_diadamo.readlist.navigationDrawer.NavigationDrawerFragment;
import com.s_diadamo.readlist.settings.LoginFragment;
import com.s_diadamo.readlist.settings.SettingsFragment;
import com.s_diadamo.readlist.shelf.Shelf;
import com.s_diadamo.readlist.shelf.ShelfOperations;
import com.s_diadamo.readlist.sync.SyncData;
import com.s_diadamo.readlist.updates.BookUpdate;
import com.s_diadamo.readlist.updates.BookUpdateOperations;
import com.s_diadamo.readlist.updates.PageUpdate;
import com.s_diadamo.readlist.updates.PageUpdateOperations;
import com.s_diadamo.readlist.updates.StatisticsFragment;

import java.util.ArrayList;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private static final String CREATED_SHELF = "CREATED_SHELF";
    private static final String FIXED_REMEMBER_ME_STRING = "FIXED_REMEMBER_ME_STRING";
    private static final String FIXED_DEFAULT_SHELF_COLOR = "FIXED_DEFAULT_SHELF_COLOR";
    private static final String INFORMED_USER_ABOUT_LOGIN = "INFORMED_USER_ABOUT_LOGIN";
    private static final String FIXED_DUPLICATE_DATA = "FIXED_DUPLICATE_DATA";

    private NavigationDrawerFragment mNavigationDrawerFragment;
    public static ImageLoader imageLoader;
    public static String PACKAGE_NAME;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        imageLoader = new ImageLoader(this);
        context = this;
        init();

        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }

    private void init() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        if (!prefs.getBoolean(CREATED_SHELF, false)) {
            Shelf defaultShelf = new Shelf(Shelf.DEFAULT_SHELF_ID, "All Books", Shelf.DEFAULT_COLOR);
            new ShelfOperations(this).addShelf(defaultShelf);
            editor.putBoolean(CREATED_SHELF, true);
        }

        if (!prefs.getBoolean(FIXED_DEFAULT_SHELF_COLOR, false)) {
            ShelfOperations shelfOperations = new ShelfOperations(this);
            Shelf defaultShelf = shelfOperations.getShelf(Shelf.DEFAULT_SHELF_ID);
            defaultShelf.setColour(Shelf.DEFAULT_COLOR);
            shelfOperations.updateShelf(defaultShelf);
            editor.putBoolean(FIXED_DEFAULT_SHELF_COLOR, true);
        }

        if (!prefs.getBoolean(FIXED_REMEMBER_ME_STRING, false)) {
            String rememberMeSet = prefs.getString(Utils.REMEMBER_ME, "");
            if (!rememberMeSet.isEmpty()) {
                editor.remove(Utils.REMEMBER_ME);
                if (rememberMeSet.equals("yes")) {
                    editor.putBoolean(Utils.REMEMBER_ME, true);
                } else {
                    editor.putBoolean(Utils.REMEMBER_ME, false);
                }
            }
            editor.putBoolean(FIXED_REMEMBER_ME_STRING, true);
        }

        if (!prefs.getBoolean(INFORMED_USER_ABOUT_LOGIN, false) && !prefs.contains(Utils.USER_NAME) && new BookOperations(this).getBooksCount() > 0) {
            String message = "Looks like you haven't created an account yet. " +
                    "To better protect your data, you can store it on the cloud and recover " +
                    "it when ever you'd like. " +
                    "Would you like to create an account now? We won't ask again.";

            new AlertDialog.Builder(this)
                    .setTitle("You haven't created an account!")
                    .setMessage(message)
                    .setCancelable(true)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Fragment loginFragment = new LoginFragment();

                            Bundle bundle = new Bundle();
                            bundle.putInt(Utils.CREATE_ACCOUNT_FROM_MAIN, 100);
                            loginFragment.setArguments(bundle);

                            FragmentManager fragmentManager = getSupportFragmentManager();
                            fragmentManager.beginTransaction()
                                    .addToBackStack("MAIN")
                                    .replace(R.id.container, loginFragment)
                                    .commit();
                        }
                    }).setNegativeButton("No thanks", null)
                    .show();
            editor.putBoolean(INFORMED_USER_ABOUT_LOGIN, true);
        }


        if (!prefs.getBoolean(FIXED_DUPLICATE_DATA, false)) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Hold tight, some adjustments to the database are being made.");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    deleteDuplicateBooks();
                    deleteDuplicatePageUpdates();
                    deleteDuplicateBookUpdates();
                    progressDialog.dismiss();
                }
            }).run();
            editor.putBoolean(FIXED_DUPLICATE_DATA, true);
        }

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0);
            PACKAGE_NAME = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (prefs.getBoolean(Utils.SYNC_ON_START, true)) {
            if (Utils.checkUserIsLoggedIn(this) && Utils.isNetworkAvailable(this)) {
                SyncData syncData = new SyncData(this, true);
                syncData.syncAllData(this);
            }
        }

        editor.apply();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.ActionBarColor)));
    }

    private void deleteDuplicateBooks() {
        HashSet<Tuple> seenBooks = new HashSet<>();
        BookOperations bookOperations = new BookOperations(context);
        ArrayList<Book> books = bookOperations.getAllBooks();
        Tuple<String, String> currentBook;
        for (Book book : books) {
            currentBook = new Tuple<>(book.getTitle(), book.getDateAdded());
            if (seenBooks.contains(currentBook)) {
                book.delete();
                bookOperations.updateBook(book);
            } else {
                seenBooks.add(currentBook);
            }
        }
    }

    private void deleteDuplicatePageUpdates() {
        HashSet<Triple> pageUpdateList = new HashSet<>();
        PageUpdateOperations pageUpdateOperations = new PageUpdateOperations(context);
        ArrayList<PageUpdate> pageUpdates = pageUpdateOperations.getAllPageUpdates();
        Triple curPageUpdate;
        for (PageUpdate pageUpdate : pageUpdates) {
            curPageUpdate = new Triple(pageUpdate.getDate(), pageUpdate.getBookId(), pageUpdate.getPages());
            if (pageUpdateList.contains(curPageUpdate)) {
                pageUpdate.delete();
                pageUpdateOperations.updatePageUpdate(pageUpdate);
            } else {
                pageUpdateList.add(curPageUpdate);
            }
        }
    }

    private void deleteDuplicateBookUpdates() {
        HashSet<Tuple> bookUpdateList = new HashSet<>();
        BookUpdateOperations bookUpdateOperations = new BookUpdateOperations(this);
        ArrayList<BookUpdate> bookUpdates = bookUpdateOperations.getAllBookUpdates();
        Tuple<String, Integer> currentBookUpdate;
        for (BookUpdate bookUpdate : bookUpdates) {
            currentBookUpdate = new Tuple<>(bookUpdate.getDate(), bookUpdate.getBookId());
            if (bookUpdateList.contains(currentBookUpdate)) {
                bookUpdate.delete();
                bookUpdateOperations.updateBookUpdate(bookUpdate);
            } else {
                bookUpdateList.add(currentBookUpdate);
            }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mNavigationDrawerFragment.mDrawerToggle.syncState();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        Fragment objFragment = null;
        Utils.hideKeyBoard(this);

        switch (position) {
            case 0:
                objFragment = new BookFragment();
                break;
            case 1:
                objFragment = new BookFragment();
                break;
            case 2:
                objFragment = new LentFragment();
                break;
            case 3:
                objFragment = new GoalFragment();
                break;
            case 4:
                objFragment = new StatisticsFragment();
                break;
            case 5:
                objFragment = new SettingsFragment();
                break;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        for (int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
            fragmentManager.popBackStack();
        }
        fragmentManager.beginTransaction()
                .replace(R.id.container, objFragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public void onBackPressed() {
        if (mNavigationDrawerFragment.isDrawerOpen()) {
            mNavigationDrawerFragment.closeDrawer();
        } else {
            int count = getFragmentManager().getBackStackEntryCount();
            if (count == 0) {
                super.onBackPressed();
            } else {
                getFragmentManager().popBackStack();
            }
        }
        ActionBar ab = getSupportActionBar();
        if (ab != null && !ab.isShowing()) {
            ab.show();
        }
    }

    @Override
    public void onDestroy() {
        if (Utils.checkRememberMe(this)) {
            Utils.logout(context);
        }
        super.onDestroy();
    }

    public void closeDrawer() {
        mNavigationDrawerFragment.closeDrawer();
    }

    private class Triple {
        int bookId, pages;
        String dateAdded;

        Triple(String dateAdded, int bookID, int pages) {
            this.dateAdded = dateAdded;
            this.bookId = bookID;
            this.pages = pages;
        }

        public boolean equals(Object arg) {
            if (this == arg) return true;
            if (arg == null) return false;
            if (arg instanceof Triple) {
                Triple other = (Triple) arg;
                return this.bookId == other.bookId && this.pages == other.pages && this.dateAdded.equals(other.dateAdded);
            }
            return false;
        }

        public int hashCode() {
            int res = 5;
            res = res * 17 + bookId;
            res = res * 17 + pages;
            res = res * 17 + dateAdded.hashCode();
            return res;
        }
    }

    public class Tuple<X, Y> {
        public X x;
        public Y y;

        public Tuple(X x, Y y) {
            this.x = x;
            this.y = y;
        }

        public boolean equals(Object arg) {
            if (this == arg) return true;
            if (arg == null) return false;
            if (arg instanceof Tuple) {
                Tuple other = (Tuple) arg;
                return this.x.equals(other.x) && this.y.equals(other.y);
            }
            return false;
        }

        public int hashCode() {
            int res = 5;
            res = res * 17 + x.hashCode();
            res = res * 17 + y.hashCode();
            return res;
        }
    }

}
