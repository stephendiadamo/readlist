package com.s_diadamo.readlist;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;

import com.s_diadamo.readlist.book.BookFragment;
import com.s_diadamo.readlist.goal.GoalsFragment;
import com.s_diadamo.readlist.lazylist.ImageLoader;
import com.s_diadamo.readlist.navigationDrawer.NavigationDrawerFragment;
import com.s_diadamo.readlist.shelf.Shelf;
import com.s_diadamo.readlist.shelf.ShelfOperations;
import com.s_diadamo.readlist.statistics.StatisticsFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {


    private static final String CREATED_SHELF = "CREATED_SHELF";
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

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.getBoolean(CREATED_SHELF, false)) {
            Shelf defaultShelf = new Shelf(Shelf.DEFAULT_SHELF_ID, "All Books", Shelf.DEFAULT_COLOR);
            (new ShelfOperations(this)).addShelf(defaultShelf);

            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(CREATED_SHELF, true);
            editor.apply();
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

        switch (position) {
            case 0:
                objFragment = new BookFragment();
                break;
            case 1:
                break;
            case 2:
                objFragment = new GoalsFragment();
                break;
            case 3:
                objFragment = new StatisticsFragment();
                break;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, objFragment)
                .commit();
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
