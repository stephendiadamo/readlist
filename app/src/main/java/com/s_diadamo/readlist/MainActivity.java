package com.s_diadamo.readlist;

import android.app.Dialog;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private DatabaseHelper databaseHelper = null;
    private Menu menu;

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

        databaseHelper = new DatabaseHelper(this);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        Fragment objFragment = null;

        switch (position) {
            case 0:
                objFragment = new ReadingFragment();
                break;
            case 1:
                objFragment = new ShelvesFragment();
                initializeShelfMenu();
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

    private void initializeShelfMenu() {
        this.menu.clear();
        MenuItem addShelf = this.menu.add(0, 0, 0, "Add Shelf");
        addShelf.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
//        if (!mNavigationDrawerFragment.isDrawerOpen()) {
//            getMenuInflater().inflate(R.menu.main, menu);
//            restoreActionBar();
//            return true;
//        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        switch (id) {
            case 0:
                addNewShelf();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addNewShelf() {
        final Dialog addShelfDialog = new Dialog(this);
        addShelfDialog.setContentView(R.layout.add_shelf);
        addShelfDialog.setTitle("New Shelf");

        final Button addShelfButton = (Button) addShelfDialog.findViewById(R.id.add_shelf_button);
        addShelfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText shelfNameEditText = (EditText) addShelfDialog.findViewById(R.id.shelf_name_edit_text);
                String shelfName = shelfNameEditText.getText().toString();

                if (!shelfName.isEmpty()) {
                    databaseHelper.addShelf(new Shelf(shelfName, Shelf.DEFAULT_COLOR));
                    addShelfDialog.dismiss();
                } else {
                    Toast.makeText(v.getContext(), "Please enter a shelf name", Toast.LENGTH_LONG).show();
                }
            }
        });
        addShelfDialog.show();
    }
}
