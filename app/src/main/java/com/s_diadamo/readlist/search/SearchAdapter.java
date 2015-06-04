package com.s_diadamo.readlist.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.s_diadamo.readlist.DownloadImageTask;
import com.s_diadamo.readlist.R;
import com.s_diadamo.readlist.book.Book;

import java.util.ArrayList;

public class SearchAdapter extends ArrayAdapter<Book> {

    private Context context;
    private int layoutResourceID;
    private ArrayList<Book> results;
    private LayoutInflater layoutInflater;

    public SearchAdapter(Context context, int layoutResourceID, ArrayList<Book> results) {
        super(context, layoutResourceID, results);
        this.context = context;
        this.layoutResourceID = layoutResourceID;
        this.results = results;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ResultHolder resultHolder;

        if (row == null) {
            row = layoutInflater.inflate(layoutResourceID, parent, false);
            resultHolder = new ResultHolder();
            resultHolder.resultBookCover = (ImageView) row.findViewById(R.id.search_book_cover);
            resultHolder.resultBookTitle = (TextView) row.findViewById(R.id.search_book_title);
            resultHolder.resultBookAuthor = (TextView) row.findViewById(R.id.search_book_author);
            resultHolder.resultBookPages = (TextView) row.findViewById(R.id.search_book_num_pages);

            row.setTag(resultHolder);
        } else {
            resultHolder = (ResultHolder) row.getTag();
        }

        Book result = results.get(position);

        new DownloadImageTask(resultHolder.resultBookCover).execute(result.getCoverPictureUrl());

        resultHolder.resultBookTitle.setText(result.getTitle());
        resultHolder.resultBookAuthor.setText(result.getAuthor());
        resultHolder.resultBookPages.setText(String.valueOf(result.getNumPages()));

        return row;
    }

    static class ResultHolder {
        ImageView resultBookCover;
        TextView resultBookTitle;
        TextView resultBookAuthor;
        TextView resultBookPages;
    }

}
