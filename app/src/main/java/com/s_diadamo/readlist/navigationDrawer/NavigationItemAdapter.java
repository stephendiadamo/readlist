package com.s_diadamo.readlist.navigationDrawer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.s_diadamo.readlist.R;

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

        if (position != 1) {
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
        } else {
            NavigationShelfItemHolder navShelfItemHolder;
            if (row == null || row.getTag() == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.row_navigation_shelves, parent, false);
                navShelfItemHolder = new NavigationShelfItemHolder();
                navShelfItemHolder.icon = (ImageView) row.findViewById(R.id.nav_element_image);
                navShelfItemHolder.label = (TextView) row.findViewById(R.id.nav_element_label);
                navShelfItemHolder.addShelf = (ImageView) row.findViewById(R.id.nav_element_add_shelf);
                navShelfItemHolder.shelves = (ListView) row.findViewById(R.id.nav_element_shelf_list);
            } else {
                navShelfItemHolder = (NavigationShelfItemHolder) row.getTag();
            }
            navShelfItemHolder.label.setText(navigationElementLabels[position]);
            navShelfItemHolder.icon.setImageResource(icons[position]);
            navShelfItemHolder.addShelf.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "Clicked add :)", Toast.LENGTH_SHORT).show();
                }
            });
        }

        return row;
    }

    static class NavigationItemHolder {
        ImageView icon;
        TextView label;
    }

    static class NavigationShelfItemHolder extends NavigationItemHolder {
        ImageView addShelf;
        ListView shelves;
    }
}


