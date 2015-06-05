package com.s_diadamo.readlist.search;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.s_diadamo.readlist.R;
import com.s_diadamo.readlist.book.Book;
import com.s_diadamo.readlist.book.BookAdapter;
import com.s_diadamo.readlist.book.BookOperations;

import java.util.ArrayList;

public class SearchResultDialog extends AlertDialog {


    public SearchResultDialog(Context context, final ArrayList<Book> books) {
        super(context);

        setTitle("Search Results");
        ListView bookListView = new ListView(context);

        SearchAdapter searchAdapter = new SearchAdapter(context, R.layout.row_search_result, books);
        final BookOperations bookOperations = new BookOperations(context);

        bookListView.setAdapter(searchAdapter);
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                bookOperations.addBook(books.get(position));
                dismiss();
            }
        });
        this.setView(bookListView);
    }

    public SearchResultDialog(Context context, final ArrayList<Book> books, final BookAdapter bookAdapter, final BookOperations bookOperations) {
        super(context);

        setTitle("Search Results");
        ListView bookListView = new ListView(context);

        SearchAdapter searchAdapter = new SearchAdapter(context, R.layout.row_search_result, books);

        bookListView.setAdapter(searchAdapter);
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                bookAdapter.add(books.get(position));
                bookAdapter.notifyDataSetChanged();
                bookOperations.addBook(books.get(position));
                dismiss();
            }
        });
        this.setView(bookListView);
    }
}
