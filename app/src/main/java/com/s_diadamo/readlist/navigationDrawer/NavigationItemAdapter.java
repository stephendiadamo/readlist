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

class NavigationItemAdapter extends BaseAdapter {
    private final String[] navigationElementLabels;
    private final Context context;
    private NavigationExpandableListAdapter expandableListAdapter;

    private final int[] icons = {
            R.drawable.ic_book,
            R.drawable.ic_shelf,
            R.drawable.ic_lent_book,
            R.drawable.ic_check,
            R.drawable.ic_stats,
            R.drawable.ic_settings
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
            expandableListAdapter = new NavigationExpandableListAdapter(row.getContext(),
                    shelfOperations.getNonDefaultShelves());
            navItemShelfHolder.shelves.setAdapter(expandableListAdapter);
            navItemShelfHolder.shelves.setOnChildClickListener(makeChildClickListener(expandableListAdapter));
            navItemShelfHolder.shelves.expandGroup(0);
            int shiftLeft = navItemShelfHolder.shelves.getRight() + getDpFromPixel(100);
            int shiftRight = navItemShelfHolder.shelves.getWidth();
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                navItemShelfHolder.shelves.setIndicatorBounds(shiftLeft, shiftRight);
            } else {
                navItemShelfHolder.shelves.setIndicatorBoundsRelative(shiftLeft, shiftRight);
            }
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
            navItemHolder.icon.setImageResource(icons[position]);
        }
        return row;
    }

    private int getDpFromPixel(float pixels) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pixels * scale + 0.5f);
    }

    private ExpandableListView.OnChildClickListener makeChildClickListener(final NavigationExpandableListAdapter adapter) {
        return new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                long shelfId = adapter.getChildId(0, childPosition);
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
    }

    public void deleteItemFromExpandableList(Shelf shelf) {
        this.expandableListAdapter.deleteShelf(shelf);
    }

    public void updateItemFromExpandableList(Shelf shelf) {
        this.expandableListAdapter.updateShelf(shelf);
    }

    public void addShelf(Shelf shelf) {
        this.expandableListAdapter.addShelf(shelf);
    }

    static class NavigationItemHolder {
        ImageView icon;
        TextView label;
    }

    static class NavigationShelfItemHolder {
        NavigationExpandableListView shelves;
    }
}


