package com.s_diadamo.readlist.shelf;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.s_diadamo.readlist.R;

import java.util.ArrayList;


public class ShelfSpinnerAdapter extends ArrayAdapter<Shelf> {
    private final Context context;
    private final int layoutResourceID;
    private final ArrayList<Shelf> shelves;

    public ShelfSpinnerAdapter(Context context, ArrayList<Shelf> shelves) {
        super(context, R.layout.row_sub_shelf, R.id.sub_shelf_label, shelves);
        this.context = context;
        this.layoutResourceID = R.layout.row_sub_shelf;
        this.shelves = shelves;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    private View getCustomView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        TextView label;
        ImageView shelfColour;

        if (row == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            row = inflater.inflate(layoutResourceID, parent, false);
        }
        label = (TextView) row.findViewById(R.id.sub_shelf_label);
        shelfColour = (ImageView) row.findViewById(R.id.sub_shelf_image);

        Shelf shelf = shelves.get(position);

        label.setText(shelf.getName());
        shelfColour.setBackground(new ColorDrawable(shelf.getColour()));

        return row;
    }
}
