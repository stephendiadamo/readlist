package com.s_diadamo.readlist.navigationDrawer;

import android.support.v7.app.AppCompatActivity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.s_diadamo.readlist.R;
import com.s_diadamo.readlist.shelf.Shelf;
import com.s_diadamo.readlist.shelf.ShelfAddEditFragment;

import java.util.ArrayList;

class NavigationExpandableListAdapter extends BaseExpandableListAdapter implements ExpandableListAdapter {

    private final Context context;
    private final ArrayList<Shelf> shelves;

    public NavigationExpandableListAdapter(Context context, ArrayList<Shelf> shelves) {
        super();
        this.context = context;
        this.shelves = shelves;
    }

    public void deleteShelf(Shelf shelf) {
        int i = 0;
        for (Shelf s : shelves) {
            if (s.getId() == shelf.getId()) {
                shelves.remove(i);
                notifyChanges();
                return;
            }
            i++;
        }
    }

    public void updateShelf(Shelf shelf) {
        int i = 0;
        for (Shelf s : shelves) {
            if (s.getId() == shelf.getId()) {
                s.setColour(shelf.getColour());
                s.setName(shelf.getName());
            }
            i++;
        }
        notifyChanges();
    }

    public void addShelf(Shelf shelf) {
        shelves.add(shelf);
        notifyChanges();
    }

    private void notifyChanges() {
        this.notifyDataSetInvalidated();
        this.notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return 1;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return shelves.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return "Shelves";
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return shelves.get(childPosition).getName();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return shelves.get(childPosition).getId();
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View row = convertView;
        final NavigationShelfParentHolder navShelfParentHolder;
        if (row == null || row.getTag() == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            row = inflater.inflate(R.layout.row_navigation_shelves_header, parent, false);
            navShelfParentHolder = new NavigationShelfParentHolder();
            navShelfParentHolder.icon = (ImageView) row.findViewById(R.id.nav_element_image);
            navShelfParentHolder.label = (TextView) row.findViewById(R.id.nav_element_label);
            navShelfParentHolder.addShelf = (ImageView) row.findViewById(R.id.nav_element_add_shelf);
        } else {
            navShelfParentHolder = (NavigationShelfParentHolder) row.getTag();
        }
        navShelfParentHolder.label.setText("Shelves");
        navShelfParentHolder.addShelf.setOnTouchListener(new ExpandableListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        navShelfParentHolder.addShelf.setBackgroundColor(Color.LTGRAY);
                        break;
                    case MotionEvent.ACTION_UP:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        launchAddShelfFragment();
                        navShelfParentHolder.addShelf.setBackgroundColor(Color.TRANSPARENT);
                        break;
                }
                v.onTouchEvent(event);
                return true;
            }
        });
        navShelfParentHolder.icon.setImageResource(R.drawable.ic_shelf);
        return row;
    }

    private void launchAddShelfFragment() {
        try {
            AppCompatActivity activity = (AppCompatActivity) context;
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            Fragment fragment = new ShelfAddEditFragment();
            fragmentManager.beginTransaction()
                    .addToBackStack("MAIN_ACTIVITY")
                    .replace(R.id.container, fragment)
                    .commit();
        } catch (ClassCastException e) {
        }
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View row = convertView;
        NavigationShelfChildHolder navShelfChildHolder;
        if (row == null || row.getTag() == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            row = inflater.inflate(R.layout.row_sub_shelf, parent, false);
            navShelfChildHolder = new NavigationShelfChildHolder();
            navShelfChildHolder.icon = (ImageView) row.findViewById(R.id.sub_shelf_image);
            navShelfChildHolder.label = (TextView) row.findViewById(R.id.sub_shelf_label);
        } else {
            navShelfChildHolder = (NavigationShelfChildHolder) row.getTag();
        }

        Shelf shelf = shelves.get(childPosition);
        navShelfChildHolder.label.setText(shelf.getName());
        navShelfChildHolder.icon.setBackground(new ColorDrawable(shelf.getColour()));
        return row;
    }

    static class NavigationShelfChildHolder {
        ImageView icon;
        TextView label;
    }

    static class NavigationShelfParentHolder extends NavigationShelfChildHolder {
        ImageView addShelf;
    }
}
