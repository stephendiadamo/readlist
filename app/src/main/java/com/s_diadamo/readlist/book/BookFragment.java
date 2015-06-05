package com.s_diadamo.readlist.book;

import android.content.Intent;
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
import android.widget.ListView;
import android.widget.Toast;

import com.google.zxing.client.android.Intents;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.CompoundBarcodeView;
import com.s_diadamo.readlist.R;
import com.s_diadamo.readlist.ScanActivity;
import com.s_diadamo.readlist.search.Search;

import java.util.ArrayList;

public class BookFragment extends Fragment {
    View rootView;
    ListView bookListView;
    ArrayList<Book> userBooks;
    BookMenuActions bookMenuActions;
    BookOperations bookOperations;
    BookAdapter bookAdapter;

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

        if (getArguments() != null) {
            String bookISBN = getArguments().getString("BOOK_ISBN");
            if (!bookISBN.isEmpty()) {
                Search search = new Search(rootView.getContext(), bookAdapter, bookOperations);
                search.searchWithISBN(bookISBN);
            }
        }
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
        } else if (id == R.id.add_book_scan) {
            // TODO: Make a separate fragment for this
            launchScanner();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void launchScanner() {
        ScanActivity scanActivity = new ScanActivity(rootView.getContext(), bookAdapter, bookOperations);
        Intent intent = new Intent(rootView.getContext(), scanActivity.getClass());
        startActivity(intent);
    }
}
