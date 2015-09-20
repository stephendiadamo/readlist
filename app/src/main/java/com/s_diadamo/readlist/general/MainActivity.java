package com.s_diadamo.readlist.general;

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
import com.s_diadamo.readlist.settings.SettingsFragment;
import com.s_diadamo.readlist.shelf.Shelf;
import com.s_diadamo.readlist.shelf.ShelfOperations;
import com.s_diadamo.readlist.sync.SyncData;
import com.s_diadamo.readlist.updates.PageUpdate;
import com.s_diadamo.readlist.updates.PageUpdateOperations;
import com.s_diadamo.readlist.updates.StatisticsFragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private static final String CREATED_SHELF = "CREATED_SHELF";
    private static final String FIXED_REMEMBER_ME_STRING = "FIXED_REMEMBER_ME_STRING";
    private static final String FIXED_DEFAULT_SHELF_COLOR = "FIXED_DEFAULT_SHELF_COLOR";

    private NavigationDrawerFragment mNavigationDrawerFragment;
    public static ImageLoader imageLoader;
    public static String PACKAGE_NAME;

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

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0);
            PACKAGE_NAME = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (prefs.getBoolean(Utils.SYNC_ON_START, true)) {
            if (Utils.checkUserIsLoggedIn(this) && Utils.isNetworkAvailable(this)) {
                SyncData syncData = new SyncData(this, true, true);
                syncData.syncAllData(this);
            }
        }

        editor.apply();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.ActionBarColor)));
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
            Utils.logout(this);
        }
        super.onDestroy();
    }

    public void closeDrawer() {
        mNavigationDrawerFragment.closeDrawer();
    }
}
