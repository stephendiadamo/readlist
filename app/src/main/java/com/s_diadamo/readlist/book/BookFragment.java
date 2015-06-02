package com.s_diadamo.readlist.book;

import android.app.Dialog;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.s_diadamo.readlist.API;
import com.s_diadamo.readlist.R;

import java.io.IOException;
import java.util.ArrayList;

public class BookFragment extends Fragment {
    View rootView;
    BookOperations bookOperations;
    ListView bookListView;
    BookAdapter bookAdapter;
    ArrayList<Book> books;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_listview, container, false);

        setHasOptionsMenu(true);

        bookListView = (ListView) rootView.findViewById(R.id.general_list_view);
        bookOperations = new BookOperations(container.getContext());

        books = bookOperations.getAllBooks();

        bookAdapter = new BookAdapter(container.getContext(), R.layout.row_book_element, books);
        bookListView.setAdapter(bookAdapter);
        registerForContextMenu(bookListView);
        bookListView.setLongClickable(false);

        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getActivity().openContextMenu(view);
            }
        });

        return rootView;
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_book_actions, menu);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_book, menu);
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        l.showContextMenuForChild(v);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Book b;
        switch (item.getItemId()) {
            case R.id.set_current_page:
                BookMenuActions.setCurrentPage(books.get(info.position), rootView, bookOperations, bookAdapter);
                return true;
            case R.id.mark_complete:
                // TODO: rip this out
                b = books.get(info.position);
                b.setComplete(true);
                bookAdapter.notifyDataSetChanged();
                bookOperations.updateBook(b);
                return true;
            case R.id.set_color:
                return true;
            case R.id.edit_num_pages:
                BookMenuActions.editNumberOfPages(books.get(info.position), rootView, bookOperations, bookAdapter);
                return true;
            case R.id.delete_book:
                // TODO: rip this out
                b = books.remove(info.position);
                bookAdapter.notifyDataSetChanged();
                bookOperations.deleteBook(b);
                return true;
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        long id = item.getItemId();

        if (id == R.id.add_book) {
            searchBook();
            return true;
        } else if (id == R.id.add_book_manually) {
            BookMenuActions.manuallyAddBook(rootView, bookOperations, bookAdapter);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // TODO: Clean this up

    private void searchBook() {
        final Dialog searchBookDialog = new Dialog(rootView.getContext());
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

    // TODO: Rip this out

    private void getSearchResultsAndShowSearchDialog(String url) {
        RequestQueue queue = Volley.newRequestQueue(rootView.getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ArrayList<Book> books = getBooksFromJSONResponse(response);
                displaySearchResults(books);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(rootView.getContext(), "Failed!", Toast.LENGTH_LONG).show();
            }
        });
        queue.add(stringRequest);
    }

    private ArrayList<Book> getBooksFromJSONResponse(String response) {
        ArrayList<Book> books = new ArrayList<Book>();
        try {
            JsonParser jsonParser = new JsonFactory().createParser(response);
            jsonParser.nextToken();
            while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                String attributeName = jsonParser.getCurrentName();
                if (attributeName.equals("kind")) {
                    jsonParser.nextToken();
                } else if (attributeName.equals("items")) {
                    jsonParser.nextToken();
                    while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                        jsonParser.nextToken();
                        attributeName = jsonParser.getCurrentName();
                        if (attributeName.equals("volumeInfo")) {
                            jsonParser.nextToken();
                            Book book = new Book();
                            while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                                attributeName = jsonParser.getCurrentName();
                                if (attributeName.equals("title")) {
                                    jsonParser.nextToken();
                                    book.setTitle(jsonParser.getText());
                                } else if (attributeName.equals("authors")) {
                                    jsonParser.nextToken();
                                    StringBuilder stringBuilder = new StringBuilder();
                                    while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                                        stringBuilder.append(jsonParser.getText());
                                        stringBuilder.append(", ");
                                    }
                                    String authors = stringBuilder.toString();
                                    if (!authors.isEmpty()) {
                                        book.setAuthor(authors.substring(0, authors.length() - 2));
                                    }
                                } else if (attributeName.equals("pageCount")) {
                                    jsonParser.nextToken();
                                    book.setNumPages(Integer.parseInt(jsonParser.getText()));
                                } else if (attributeName.equals("imageLinks")) {
                                    jsonParser.nextToken();
                                    jsonParser.nextToken();
                                    jsonParser.nextToken();
                                    book.setCoverPictureURL(jsonParser.getText());
                                    jsonParser.nextToken();
                                }
                            }
                            books.add(book);
                            jsonParser.nextToken();
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return books;
    }

    private void displaySearchResults(ArrayList<Book> books) {

    }
}
