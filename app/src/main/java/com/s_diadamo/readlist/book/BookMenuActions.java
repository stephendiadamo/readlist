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
        final Dialog manuallyAddBookDialog = new Dialog(view.getContext());
        manuallyAddBookDialog.setContentView(R.layout.dialog_manually_add_book);
        manuallyAddBookDialog.setTitle("Add New Book");

        final EditText bookTitleEditText = (EditText) manuallyAddBookDialog.findViewById(R.id.manual_add_title);
        final EditText bookAuthorEditText = (EditText) manuallyAddBookDialog.findViewById(R.id.manual_add_author);
        final EditText bookPagesEditText = (EditText) manuallyAddBookDialog.findViewById(R.id.manual_add_pages);
        Button addButton = (Button) manuallyAddBookDialog.findViewById(R.id.manual_add_add_button);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bookTitle = bookTitleEditText.getText().toString();
                String bookAuthor = bookAuthorEditText.getText().toString();
                String pages = bookPagesEditText.getText().toString();

                if (bookTitle.isEmpty() || bookAuthor.isEmpty() || pages.isEmpty()) {
                    Toast.makeText(manuallyAddBookDialog.getContext(), "Please fill all information", Toast.LENGTH_LONG).show();
                } else {
                    Calendar calendar = Calendar.getInstance();
                    Book book = new Book(bookTitle, bookAuthor, 0, calendar.getTime().toString(), Integer.parseInt(pages), 0, "", 0, "");
                    bookAdapter.add(book);
                    bookAdapter.notifyDataSetChanged();
                    bookOperations.addBook(book);
                    manuallyAddBookDialog.dismiss();
                }
            }
        });

        manuallyAddBookDialog.show();
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
                String searchQuery = bookTitle.trim().replace(" ", "%20") + "+inauthor:" + bookAuthor.trim().replace(" ", "%20");
                String fields = "kind,items/volumeInfo(title,authors,pageCount,imageLinks/smallThumbnail)";

                String API_KEY = API.getGoogleBooksApiKey();

                Uri.Builder builder = new Uri.Builder();
                builder.scheme("https")
                        .authority("www.googleapis.com")
                        .appendPath("books")
                        .appendPath("v1")
                        .appendPath("volumes")
                        .encodedQuery("q=" + searchQuery)
                        .appendQueryParameter("key", API_KEY);

                String url = builder.build().toString();
                url += "&fields=" + fields;
                getSearchResultsAndShowSearchDialog(url);

                searchBookDialog.dismiss();
            }
        });
        searchBookDialog.show();
    }

    private void getSearchResultsAndShowSearchDialog(String url) {
        RequestQueue queue = Volley.newRequestQueue(view.getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ArrayList<Book> books = SearchResultJSONParser.getBooksFromJSONResponse(response);
                SearchResultDialog searchResultDialog = new SearchResultDialog(view.getContext(), books, bookAdapter, bookOperations);
                searchResultDialog.show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(view.getContext(), "Failed!", Toast.LENGTH_LONG).show();
            }
        });
        queue.add(stringRequest);
    }
}
