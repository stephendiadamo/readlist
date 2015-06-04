package com.s_diadamo.readlist.book;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.s_diadamo.readlist.DownloadImageTask;
import com.s_diadamo.readlist.R;

import java.util.ArrayList;

public class BookAdapter extends ArrayAdapter<Book> {

    private Context context;
    private int layoutResourceID;
    private ArrayList<Book> books;

    public BookAdapter(Context context, int layoutResourceID, ArrayList<Book> books) {
        super(context, layoutResourceID, books);
        this.context = context;
        this.layoutResourceID = layoutResourceID;
        this.books = books;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        BookHolder bookHolder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceID, parent, false);

            bookHolder = new BookHolder();
            bookHolder.bookCover = (ImageView) row.findViewById(R.id.book_cover);
            bookHolder.bookTitle = (TextView) row.findViewById(R.id.book_title);
            bookHolder.bookAuthor = (TextView) row.findViewById(R.id.book_author);
            bookHolder.currentPage = (TextView) row.findViewById(R.id.book_current_page);
            bookHolder.pages = (TextView) row.findViewById(R.id.book_pages);

            row.setTag(bookHolder);
        } else {
            bookHolder = (BookHolder) row.getTag();
        }
        Book book = books.get(position);
        if (!book.getCoverPictureUrl().isEmpty()) {
            new DownloadImageTask(bookHolder.bookCover).execute(book.getCoverPictureUrl());
        }
        bookHolder.bookTitle.setText(book.getTitle());
        bookHolder.bookAuthor.setText(book.getAuthor());
        bookHolder.currentPage.setText(String.valueOf(book.getCurrentPage()));
        bookHolder.pages.setText(String.valueOf(book.getNumPages()));

        return row;
    }

    static class BookHolder {
        ImageView bookCover;
        TextView bookTitle;
        TextView bookAuthor;
        TextView currentPage;
        TextView pages;
    }
}
