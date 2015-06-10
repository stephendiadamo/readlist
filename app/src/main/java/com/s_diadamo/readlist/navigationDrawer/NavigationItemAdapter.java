package com.s_diadamo.readlist.navigationDrawer;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.s_diadamo.readlist.R;
import com.s_diadamo.readlist.book.BookFragment;
import com.s_diadamo.readlist.shelf.Shelf;
import com.s_diadamo.readlist.shelf.ShelfOperations;

public class NavigationItemAdapter extends BaseAdapter {
    String[] navigationElementLabels;
    private Context context;

    int[] icons = {
            R.drawable.ic_book
    };

    public NavigationItemAdapter(Context context) {
        this.context = context;
        navigationElementLabels = context.getResources().getStringArray(R.array.navigation_element_names);
    }

    @Override
    public int getCount() {
        return navigationElementLabels.length;
    }

    @Override
    public Object getItem(int position) {
        return navigationElementLabels[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        if (position == 1) {
            NavigationShelfItemHolder navItemShelfHolder = new NavigationShelfItemHolder();
            if (row == null || row.getTag() == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                row = inflater.inflate(R.layout.row_navigation_shelves, parent, false);
                navItemShelfHolder.shelves = (NavigationExpandableListView) row.findViewById(R.id.navigation_drawer_expandable_list);
            } else {
                navItemShelfHolder = (NavigationShelfItemHolder) row.getTag();
            }
            ShelfOperations shelfOperations = new ShelfOperations(row.getContext());
            NavigationExpandableListAdapter adapter = new NavigationExpandableListAdapter(row.getContext(),
                    shelfOperations.getAllShelves());
            navItemShelfHolder.shelves.setAdapter(adapter);
            navItemShelfHolder.shelves.setOnChildClickListener(makeChildClickListener(adapter));

        } else {
            NavigationItemHolder navItemHolder;
            if (row == null || row.getTag() == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                row = inflater.inflate(R.layout.row_navigation_element, parent, false);
                navItemHolder = new NavigationItemHolder();
                navItemHolder.icon = (ImageView) row.findViewById(R.id.nav_element_image);
                navItemHolder.label = (TextView) row.findViewById(R.id.nav_element_label);
            } else {
                navItemHolder = (NavigationItemHolder) row.getTag();
            }
            navItemHolder.label.setText(navigationElementLabels[position]);
            navItemHolder.icon.setImageResource(icons[0]);
        }
        return row;
    }

    private ExpandableListView.OnChildClickListener makeChildClickListener(final NavigationExpandableListAdapter adapter) {
        ExpandableListView.OnChildClickListener listener = new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                long shelfId = adapter.getChildId(0, childPosition);
                //TODO: Clean this up...
                Fragment bookFragment = new BookFragment();
                Bundle bundle = new Bundle();
                bundle.putString(Shelf.SHELF_ID, String.valueOf(shelfId));
                bookFragment.setArguments(bundle);
                FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, bookFragment)
                        .commit();
                DrawerLayout mDrawerLayout;
                mDrawerLayout = (DrawerLayout) ((AppCompatActivity) context).findViewById(R.id.drawer_layout);
                mDrawerLayout.closeDrawers();
                return true;
            }
        };
        return listener;
    }


    static class NavigationItemHolder {
        ImageView icon;
        TextView label;
    }

    static class NavigationShelfItemHolder {
        NavigationExpandableListView shelves;
    }
}


