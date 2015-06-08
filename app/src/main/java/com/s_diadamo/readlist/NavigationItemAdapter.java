package com.s_diadamo.readlist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NavigationItemAdapter extends BaseAdapter {

    String[] navigationElementLabels;
    private Context context;

    int[] icons = {
            R.drawable.ic_book,
            R.drawable.ic_book,
            R.drawable.ic_book,
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
        NavigationItemHolder navItemHolder;

        if (row == null || row.getTag() == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.row_navigation_element, parent, false);
            navItemHolder = new NavigationItemHolder();
            navItemHolder.icon = (ImageView) row.findViewById(R.id.nav_element_image);
            navItemHolder.label = (TextView) row.findViewById(R.id.nav_element_label);
        } else {
            navItemHolder = (NavigationItemHolder) row.getTag();
        }
        navItemHolder.label.setText(navigationElementLabels[position]);
        navItemHolder.icon.setImageResource(icons[position]);

        return row;
    }

    static class NavigationItemHolder {
        ImageView icon;
        TextView label;
    }
}


