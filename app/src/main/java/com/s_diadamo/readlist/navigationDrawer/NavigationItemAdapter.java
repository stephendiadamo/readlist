package com.s_diadamo.readlist.navigationDrawer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.s_diadamo.readlist.R;
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
            navItemShelfHolder.shelves.setOnChildClickListener(makeChildClickListener());
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


    private ExpandableListView.OnChildClickListener makeChildClickListener() {
        ExpandableListView.OnChildClickListener listener = new ExpandableListView.OnChildClickListener(){
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                //TODO

                // Get the selected shelf
                // Create a BookFragment and pass the ID in the bundle
                // Display book fragment



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


