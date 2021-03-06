package com.s_diadamo.readlist.search;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.s_diadamo.readlist.general.MainActivity;
import com.s_diadamo.readlist.R;
import com.s_diadamo.readlist.book.Book;

import java.util.ArrayList;
import java.util.HashSet;

class SearchAdapter extends ArrayAdapter<Book> {

    private final int layoutResourceID;
    private final ArrayList<Book> results;
    private final LayoutInflater layoutInflater;
    public final HashSet<Integer> selectedBooks;

    public SearchAdapter(Context context, ArrayList<Book> results) {
        super(context, R.layout.row_search_result, results);
        this.layoutResourceID = R.layout.row_search_result;
        this.results = results;
        this.layoutInflater = LayoutInflater.from(context);
        this.selectedBooks = new HashSet<>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        final ResultHolder resultHolder;

        if (row == null) {
            row = layoutInflater.inflate(layoutResourceID, parent, false);
            resultHolder = new ResultHolder();
            resultHolder.resultBookCover = (ImageView) row.findViewById(R.id.search_book_cover);
            resultHolder.resultBookTitle = (TextView) row.findViewById(R.id.search_book_title);
            resultHolder.resultBookAuthor = (TextView) row.findViewById(R.id.search_book_author);
            row.setTag(resultHolder);
        } else {
            resultHolder = (ResultHolder) row.getTag();
        }

        Book result = results.get(position);

        if (!result.getCoverPictureUrl().isEmpty()) {
            MainActivity.imageLoader.DisplayImage(result.getCoverPictureUrl(), resultHolder.resultBookCover);
        }

        resultHolder.resultBookTitle.setText(result.getTitle());
        resultHolder.resultBookAuthor.setText(result.getAuthor());

        if (selectedBooks.contains(position)) {
            row.setBackground(new ColorDrawable(Color.LTGRAY));
        } else {
            row.setBackground(new ColorDrawable(Color.TRANSPARENT));
        }

        return row;
    }

    static class ResultHolder {
        ImageView resultBookCover;
        TextView resultBookTitle;
        TextView resultBookAuthor;
    }
}
