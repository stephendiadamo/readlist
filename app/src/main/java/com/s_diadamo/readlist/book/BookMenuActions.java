package com.s_diadamo.readlist.book;

import android.app.Dialog;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.s_diadamo.readlist.API;
import com.s_diadamo.readlist.R;
import com.s_diadamo.readlist.search.Search;
import com.s_diadamo.readlist.search.SearchResultDialog;
import com.s_diadamo.readlist.search.SearchResultJSONParser;

import java.util.ArrayList;
import java.util.Calendar;

public class BookMenuActions {

    View view;
    BookOperations bookOperations;
    BookAdapter bookAdapter;

    public BookMenuActions(View view, BookOperations bookOperations, BookAdapter bookAdapter) {
        this.view = view;
        this.bookOperations = bookOperations;
        this.bookAdapter = bookAdapter;
    }

    public void editNumberOfPages(final Book book) {
        final Dialog editNumberOfPagesDialog = new Dialog(view.getContext());
        editNumberOfPagesDialog.setContentView(R.layout.dialog_edit_book_pages);
        editNumberOfPagesDialog.setTitle("Edit Book Pages");

        final Button updatePagesButton = (Button) editNumberOfPagesDialog.findViewById(R.id.update_page_button);
        updatePagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newNumberOfPages = ((EditText) editNumberOfPagesDialog.findViewById(R.id.update_page_num_value)).getText().toString();
                if (!newNumberOfPages.isEmpty()) {
                    int pages = Integer.parseInt(newNumberOfPages);
                    book.setNumPages(pages);
                    bookOperations.updateBook(book);
                    bookAdapter.notifyDataSetChanged();
                }
                editNumberOfPagesDialog.dismiss();
            }
        });
        editNumberOfPagesDialog.show();
    }

    public void setCurrentPage(final Book book) {
        BookUpdatePageDialog bookUpdatePageDialog = new BookUpdatePageDialog(view.getContext(), book, bookAdapter, bookOperations);
        bookUpdatePageDialog.show();
    }

    public void manuallyAddBook() {
        BookManuallyAddBookDialog bookManuallyAddBookDialog = new BookManuallyAddBookDialog(view.getContext(), bookAdapter, bookOperations);
        bookManuallyAddBookDialog.show();
    }

    public void searchBook() {
        final Dialog searchBookDialog = new Dialog(view.getContext());
        searchBookDialog.setContentView(R.layout.dialog_search_book);
        searchBookDialog.setTitle("Search");

        final Button searchBookButton = (Button) searchBookDialog.findViewById(R.id.search_book);
        searchBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bookTitle = ((EditText) searchBookDialog.findViewById(R.id.book_search_title)).getText().toString();
                String bookAuthor = ((EditText) searchBookDialog.findViewById(R.id.book_search_author)).getText().toString();
                Search search = new Search(view.getContext(), bookAdapter, bookOperations);
                search.searchWithAuthorAndTitle(bookAuthor, bookTitle);
                searchBookDialog.dismiss();
            }
        });
        searchBookDialog.show();
    }
}
