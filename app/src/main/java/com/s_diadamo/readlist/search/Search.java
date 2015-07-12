package com.s_diadamo.readlist.search;


import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.s_diadamo.readlist.general.API;
import com.s_diadamo.readlist.book.Book;
import com.s_diadamo.readlist.book.BookAdapter;
import com.s_diadamo.readlist.book.BookOperations;
import com.s_diadamo.readlist.shelf.Shelf;

import java.util.ArrayList;


public class Search {

    private final Context context;
    private final String fields = "kind,items/volumeInfo(title,authors,pageCount,imageLinks/smallThumbnail)";
    private final Shelf shelf;
    private final FragmentManager manager;
    private final String API_KEY = API.getGoogleBooksApiKey();

    public Search(Context context, FragmentManager manager, Shelf shelf) {
        this.context = context;
        this.manager = manager;
        this.shelf = shelf;
    }

    public void searchWithAuthorAndTitle(String author, String title) {
        String searchQuery;
        int MAX_RESULTS = 20;

        if (!title.isEmpty() && !author.isEmpty()) {
            String inTitle = title.trim().replace(" ", "%20");
            String inAuthor = author.trim().replace(" ", "%20");
            searchQuery = inTitle + "+intitle:" + inTitle + "+inauthor:" + inAuthor;
        } else if (!title.isEmpty()) {
            String inTitle = title.trim().replace(" ", "%20");
            searchQuery = inTitle + "+intitle:" + inTitle;
        } else {
            String inAuthor = author.trim().replace(" ", "%20");
            searchQuery = inAuthor + "+inauthor:" + inAuthor;
        }

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("www.googleapis.com")
                .appendPath("books")
                .appendPath("v1")
                .appendPath("volumes")
                .encodedQuery("q=" + searchQuery)
                .appendQueryParameter("key", API_KEY)
                .appendQueryParameter("maxResults", String.valueOf(MAX_RESULTS))
                .appendQueryParameter("printType", "books");

        String url = builder.build().toString();
        url += "&fields=" + fields;
        performSearchAndShowResults(url);
    }

    public void searchWithISBN(String ISBN) {
        String searchQuery = "isbn:" + ISBN;
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
        performSearchAndShowResults(url);
    }

    private void performSearchAndShowResults(String url) {
        RequestQueue queue = Volley.newRequestQueue(context);
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Searching...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ArrayList<Book> books = SearchResultJSONParser.getBooksFromJSONResponse(response, shelf);
                SearchResultDialog searchResultDialog = new SearchResultDialog(context, books, manager);
                progressDialog.dismiss();
                searchResultDialog.show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                progressDialog.dismiss();
                Toast toast = Toast.makeText(context, "Search failed. Check internet connection and try again.", Toast.LENGTH_LONG);
                TextView textView = (TextView) toast.getView().findViewById(android.R.id.message);
                if (textView != null) {
                    textView.setGravity(Gravity.CENTER);
                }
                toast.show();
            }
        });
        queue.add(stringRequest);
    }
}
