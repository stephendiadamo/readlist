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
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.s_diadamo.readlist.API;
import com.s_diadamo.readlist.BookSearchRequest;
import com.s_diadamo.readlist.R;

import java.net.URI;
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
            //searchBook();
            return true;
        } else if (id == R.id.add_book_manually) {
            BookMenuActions.manuallyAddBook(rootView, bookOperations, bookAdapter);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

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

                String API_KEY = API.getGoogleBooksApiKey();

                Uri.Builder builder = new Uri.Builder();
                builder.scheme("https")
                        .authority("www.googleapis.com")
                        .appendPath("books")
                        .appendPath("v1")
                        .appendPath("volumes")
                        .encodedQuery("q=" + searchQuery)
                        .appendQueryParameter("key", API_KEY);

                ArrayList<Book> bookResults = new ArrayList<Book>();
                String url = builder.build().toString();

                BookSearchRequest bookSearchRequest = new BookSearchRequest(bookResults);
                bookSearchRequest.execute(url);

                searchBookDialog.dismiss();
            }
        });
        searchBookDialog.show();
    }
}
