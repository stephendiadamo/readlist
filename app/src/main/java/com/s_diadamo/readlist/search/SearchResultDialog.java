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

class SearchResultDialog extends AlertDialog {

    public SearchResultDialog(Context context, final ArrayList<Book> books, final BookAdapter bookAdapter, final BookOperations bookOperations) {
        super(context);

        setTitle("Search Results");
        ListView bookListView = new ListView(context);

        SearchAdapter searchAdapter = new SearchAdapter(context, R.layout.row_search_result, books);

        bookListView.setAdapter(searchAdapter);
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Book book = books.get(position);
                bookAdapter.add(book);
                bookAdapter.notifyDataSetChanged();
                bookOperations.addBook(book);
                dismiss();
            }
        });
        this.setView(bookListView);
    }
}
