package com.s_diadamo.readlist.navigationDrawer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.s_diadamo.readlist.R;

import java.util.ArrayList;

public class NavigationExpandableListAdapter extends BaseExpandableListAdapter implements ExpandableListAdapter {

    private Context context;
    private ArrayList<String> shelfNames;


    public NavigationExpandableListAdapter(Context context, ArrayList<String> shelfNames) {
        super();
        this.context = context;
        this.shelfNames = shelfNames;
    }

    @Override
    public int getGroupCount() {
        return 1;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return shelfNames.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return "Shelves";
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return shelfNames.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
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
        NavigationShelfParentHolder navShelfParentHolder;
        if (row == null || row.getTag() == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            row = inflater.inflate(R.layout.row_navigation_shelves_header, null);
            navShelfParentHolder = new NavigationShelfParentHolder();
            navShelfParentHolder.icon = (ImageView) row.findViewById(R.id.nav_element_image);
            navShelfParentHolder.label = (TextView) row.findViewById(R.id.nav_element_label);
        } else {
            navShelfParentHolder = (NavigationShelfParentHolder) row.getTag();
        }
        navShelfParentHolder.label.setText("Shelves");

        parent.invalidate();

        //TODO: Change this icon!
        // navShelfParentHolder.icon.setImageResource(R.drawable.ic_book);
        return row;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View row = convertView;
        NavigationShelfChildHolder navShelfChildHolder;
        if (row == null || row.getTag() == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            row = inflater.inflate(R.layout.row_navigation_element, null);
            navShelfChildHolder = new NavigationShelfChildHolder();
            navShelfChildHolder.icon = (ImageView) row.findViewById(R.id.nav_element_image);
            navShelfChildHolder.label = (TextView) row.findViewById(R.id.nav_element_label);
        } else {
            navShelfChildHolder = (NavigationShelfChildHolder) row.getTag();
        }
        navShelfChildHolder.label.setText(shelfNames.get(childPosition));
        navShelfChildHolder.icon.setImageResource(R.drawable.ic_book);
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
