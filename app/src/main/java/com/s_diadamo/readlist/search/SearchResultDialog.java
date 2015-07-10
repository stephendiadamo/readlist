package com.s_diadamo.readlist.search;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.s_diadamo.readlist.R;
import com.s_diadamo.readlist.book.Book;
import com.s_diadamo.readlist.book.BookAdapter;
import com.s_diadamo.readlist.book.BookOperations;
import com.s_diadamo.readlist.general.SyncData;
import com.s_diadamo.readlist.general.Utils;

import java.util.ArrayList;

class SearchResultDialog extends AlertDialog {
    public SearchResultDialog(final Context context, final ArrayList<Book> books, final FragmentManager manager) {
        super(context);

        if (books.isEmpty()) {
            setTitle("No results found");
            setMessage("Please try again with different search parameters.");
        } else {
            setTitle("Search Results");
            ListView bookListView = new ListView(context);

            SearchAdapter searchAdapter = new SearchAdapter(context, R.layout.row_search_result, books);

            bookListView.setAdapter(searchAdapter);
            bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Book book = books.get(position);
                    (new BookOperations(context)).addBook(book);
                    if (Utils.checkUserIsLoggedIn(context)) {
                        (new SyncData(context)).syncBook(book);
                    }
                    Toast.makeText(context, "Added book", Toast.LENGTH_SHORT).show();
                    Utils.launchBookFragment(manager);
                    dismiss();
                }
            });
            this.setView(bookListView);
        }
    }
}
