package com.s_diadamo.readlist.shelf;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.s_diadamo.readlist.R;
import com.s_diadamo.readlist.book.Book;
import com.s_diadamo.readlist.general.MainActivity;

import java.util.ArrayList;
import java.util.HashSet;

class ShelfAddBookAdapter extends ArrayAdapter<Book> {

    private final ArrayList<Book> books;
    private final int layoutResourceID;
    private final LayoutInflater layoutInflater;
    public final HashSet<Integer> selectedBooks;

    public ShelfAddBookAdapter(Context context, ArrayList<Book> books) {
        super(context, R.layout.row_search_result, books);
        this.layoutResourceID = R.layout.row_search_result;
        this.books = books;
        this.layoutInflater = LayoutInflater.from(context);
        this.selectedBooks = new HashSet<>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        final SearchBookHolder searchBookHolder;

        if (row == null) {
            row = layoutInflater.inflate(layoutResourceID, parent, false);
            searchBookHolder = new SearchBookHolder();
            searchBookHolder.resultBookCover = (ImageView) row.findViewById(R.id.search_book_cover);
            searchBookHolder.resultBookTitle = (TextView) row.findViewById(R.id.search_book_title);
            searchBookHolder.resultBookAuthor = (TextView) row.findViewById(R.id.search_book_author);
            row.setTag(searchBookHolder);
        } else {
            searchBookHolder = (SearchBookHolder) row.getTag();
        }

        Book result = books.get(position);

        if (!result.getCoverPictureUrl().isEmpty()) {
            MainActivity.imageLoader.DisplayImage(result.getCoverPictureUrl(), searchBookHolder.resultBookCover);
        }

        searchBookHolder.resultBookTitle.setText(result.getTitle());
        searchBookHolder.resultBookAuthor.setText(result.getAuthor());

        if (selectedBooks.contains(position)) {
            row.setBackground(new ColorDrawable(Color.LTGRAY));
        } else {
            row.setBackground(new ColorDrawable(Color.TRANSPARENT));
        }

        return row;
    }

    static class SearchBookHolder {
        ImageView resultBookCover;
        TextView resultBookTitle;
        TextView resultBookAuthor;
    }
}
