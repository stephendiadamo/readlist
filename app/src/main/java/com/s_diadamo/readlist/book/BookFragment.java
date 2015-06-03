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
import com.s_diadamo.readlist.search.SearchResultDialog;

import java.io.IOException;
import java.util.ArrayList;

public class BookFragment extends Fragment {
    View rootView;
    BookOperations bookOperations;
    ListView bookListView;
    BookAdapter bookAdapter;
    ArrayList<Book> userBooks;
    BookMenuActions bookMenuActions;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_listview, container, false);

        setHasOptionsMenu(true);

        bookListView = (ListView) rootView.findViewById(R.id.general_list_view);
        bookOperations = new BookOperations(container.getContext());

        userBooks = bookOperations.getAllBooks();

        bookAdapter = new BookAdapter(container.getContext(), R.layout.row_book_element, userBooks);
        bookListView.setAdapter(bookAdapter);
        registerForContextMenu(bookListView);
        bookListView.setLongClickable(false);

        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getActivity().openContextMenu(view);
            }
        });

        bookMenuActions = new BookMenuActions(rootView, bookOperations, bookAdapter);

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
                bookMenuActions.setCurrentPage(userBooks.get(info.position));
                return true;
            case R.id.mark_complete:
                // TODO: rip this out
                b = userBooks.get(info.position);
                b.setComplete(true);
                bookAdapter.notifyDataSetChanged();
                bookOperations.updateBook(b);
                return true;
            case R.id.set_color:
                return true;
            case R.id.edit_num_pages:
                bookMenuActions.editNumberOfPages(userBooks.get(info.position));
                return true;
            case R.id.delete_book:
                // TODO: rip this out
                b = userBooks.remove(info.position);
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
            bookMenuActions.searchBook();
            return true;
        } else if (id == R.id.add_book_manually) {
            bookMenuActions.manuallyAddBook();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
