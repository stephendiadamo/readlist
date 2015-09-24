package com.s_diadamo.readlist.updates;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.s_diadamo.readlist.R;

import java.util.ArrayList;

public class PageUpdateAdapter extends ArrayAdapter<PageUpdate> {

    private final Context mContext;
    private final ArrayList<PageUpdate> mPageUpdates;

    public PageUpdateAdapter(Context context, ArrayList<PageUpdate> pageUpdates) {
        super(context, R.layout.row_page_update, pageUpdates);
        mContext = context;
        mPageUpdates = pageUpdates;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        UpdateHolder updateHolder;
        if (row == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            row = inflater.inflate(R.layout.row_page_update, parent, false);

            updateHolder = new UpdateHolder();
            updateHolder.dateAdded = (TextView) row.findViewById(R.id.page_update_row_date);
            updateHolder.pages = (TextView) row.findViewById(R.id.page_update_row_num_pages);

            row.setTag(updateHolder);
        } else {
            updateHolder = (UpdateHolder) row.getTag();
        }

        PageUpdate pageUpdate = mPageUpdates.get(position);
        updateHolder.dateAdded.setText(pageUpdate.getCleanDateAdded());
        updateHolder.pages.setText(String.valueOf(pageUpdate.getPages()) + " page"  + (pageUpdate.getPages() == 1 ? "" : "s"));

        return row;
    }

    static class UpdateHolder {
        TextView dateAdded;
        TextView pages;
    }
}
