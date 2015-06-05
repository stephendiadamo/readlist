package com.s_diadamo.readlist;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.s_diadamo.readlist.book.BookFragment;
import com.s_diadamo.readlist.goal.GoalsFragment;
import com.s_diadamo.readlist.lazylist.ImageLoader;
import com.s_diadamo.readlist.shelf.ShelfFragment;
import com.s_diadamo.readlist.statistics.StatisticsFragment;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;
    public static ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        imageLoader = new ImageLoader(this);

        String bookISBN = checkForBookISBN(savedInstanceState);
        if (bookISBN != null && !bookISBN.isEmpty()) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            BookFragment bookFragment = new BookFragment();
            Bundle bundle = new Bundle();
            bundle.putString("BOOK_ISBN", bookISBN);
            bookFragment.setArguments(bundle);
            fragmentManager.beginTransaction()
                    .replace(R.id.container, bookFragment)
                    .commit();
        }
    }

    private String checkForBookISBN(Bundle bundle) {
        String bookISBN = "";
        if (bundle == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                bookISBN = extras.getString("BOOK_ISBN");
            }
        } else {
            bookISBN = (String) bundle.getSerializable("BOOK_ISBN");
        }
        return bookISBN;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mNavigationDrawerFragment.mDrawerToggle.syncState();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        Fragment objFragment = null;

        switch (position) {
            case 0:
                objFragment = new BookFragment();
                break;
            case 1:
                objFragment = new ShelfFragment();
                break;
            case 2:
                objFragment = new GoalsFragment();
                break;
            case 3:
                objFragment = new StatisticsFragment();
                break;
        }

        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, objFragment)
                .commit();
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
