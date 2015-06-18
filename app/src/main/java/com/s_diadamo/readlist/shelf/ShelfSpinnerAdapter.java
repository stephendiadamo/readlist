package com.s_diadamo.readlist.shelf;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class ShelfSpinnerAdapter extends ArrayAdapter<Shelf> {
    private final Context context;
    private final int layoutResourceID;
    private final ArrayList<Shelf> shelves;


    public ShelfSpinnerAdapter(Context context, int layoutResourceID, ArrayList<Shelf> shelves) {
        super(context, layoutResourceID, shelves);
        this.context = context;
        this.layoutResourceID = layoutResourceID;
        this.shelves = shelves;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        TextView label;
        if (row == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            row = inflater.inflate(layoutResourceID, parent, false);
        }
        label = (TextView) row.findViewById(android.R.id.text1);
        label.setText(shelves.get(position).getName());

        return row;
    }
}
