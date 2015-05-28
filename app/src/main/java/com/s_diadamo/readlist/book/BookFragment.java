package com.s_diadamo.readlist.book;

import android.app.Dialog;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.s_diadamo.readlist.R;

import java.util.ArrayList;


public class BookFragment extends Fragment {
    View rootView;
    BookOperations bookOperations;
    ListView bookListView;
    BookAdapter bookAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_listview, container, false);

        setHasOptionsMenu(true);

        bookListView = (ListView) rootView.findViewById(R.id.general_list_view);
        bookOperations = new BookOperations(container.getContext());

        ArrayList<Book> books = bookOperations.getAllBooks();

        final BookAdapter bookAdapter = new BookAdapter(container.getContext(), R.layout.book_row_element, books);
        bookListView.setAdapter(bookAdapter);

        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Book book = bookAdapter.getItem(position);

                // TODO: Pop up some settings thing

                Toast.makeText(view.getContext(), book.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_book, menu);
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
        }

        return super.onOptionsItemSelected(item);
    }

    private void searchBook() {
        final Dialog searchBookDialog = new Dialog(rootView.getContext());
        searchBookDialog.setContentView(R.layout.search_book);
        searchBookDialog.setTitle("Search");

        final Button searchBookButton = (Button) searchBookDialog.findViewById(R.id.search_book);
        searchBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Search Book", Toast.LENGTH_SHORT).show();
                searchBookDialog.dismiss();
            }
        });
        searchBookDialog.show();
    }
}
