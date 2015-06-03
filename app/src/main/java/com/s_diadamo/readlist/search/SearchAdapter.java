package com.s_diadamo.readlist.search;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.s_diadamo.readlist.DownloadImageTask;
import com.s_diadamo.readlist.R;
import com.s_diadamo.readlist.book.Book;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


/**
 * Created by s-diadamo on 15-05-28.
 */
public class SearchAdapter extends ArrayAdapter<Book> {

    private Context context;
    private int layoutResourceID;
    private ArrayList<Book> results;

    public SearchAdapter(Context context, int layoutResourceID, ArrayList<Book> results) {
        super(context, layoutResourceID, results);
        this.context = context;
        this.layoutResourceID = layoutResourceID;
        this.results = results;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ResultHolder resultHolder;

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layoutResourceID, parent, false);

            resultHolder = new ResultHolder();
            resultHolder.resultBookCover = (ImageView) row.findViewById(R.id.search_book_cover);
            resultHolder.resultBookTitle = (TextView) row.findViewById(R.id.search_book_title);
            resultHolder.resultBookAuthor = (TextView) row.findViewById(R.id.search_book_author);
            //resultHolder.resultBookPages = (TextView) row.findViewById(R.id.search_book_num_pages);

            row.setTag(resultHolder);
        } else {
            resultHolder = (ResultHolder) row.getTag();
        }

        Book result = results.get(position);

        new DownloadImageTask(resultHolder.resultBookCover).execute(result.getCoverPictureURL());

        resultHolder.resultBookTitle.setText(result.getTitle());
        resultHolder.resultBookTitle.setText(result.getAuthor());
//        resultHolder.resultBookPages.setText(result.getNumPages());

        return row;
    }

    static class ResultHolder {
        ImageView resultBookCover;
        TextView resultBookTitle;
        TextView resultBookAuthor;
        TextView resultBookPages;
    }

}
