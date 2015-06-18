package com.s_diadamo.readlist.book;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.s_diadamo.readlist.R;
import com.s_diadamo.readlist.navigationDrawer.NavigationDrawerFragment;
import com.s_diadamo.readlist.scan.ScanActivity;
import com.s_diadamo.readlist.search.Search;
import com.s_diadamo.readlist.shelf.Shelf;
import com.s_diadamo.readlist.shelf.ShelfOperations;
import com.s_diadamo.readlist.updates.Update;
import com.s_diadamo.readlist.updates.UpdateOperations;

import java.util.ArrayList;

public class BookFragment extends Fragment {
    View rootView;
    ListView bookListView;
    ArrayList<Book> userBooks;
    BookMenuActions bookMenuActions;
    BookOperations bookOperations;
    BookAdapter bookAdapter;

    Shelf shelf;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_listview, container, false);

        setHasOptionsMenu(true);

        bookListView = (ListView) rootView.findViewById(R.id.general_list_view);
        bookOperations = new BookOperations(container.getContext());

        setUpShelf();
        userBooks = getBooks();

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

        bookMenuActions = new BookMenuActions(rootView, bookOperations, bookAdapter, shelf);

        ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (ab != null) {
            ab.setTitle(shelf.getName());
        }

        return rootView;
    }

    private void setUpShelf() {
        Bundle args = getArguments();
        String stringShelfId = "";
        int shelfId;
        if (args != null) {
            stringShelfId = args.getString(Shelf.SHELF_ID);
        }
        if (!stringShelfId.isEmpty()) {
            shelfId = Integer.parseInt(stringShelfId);
        } else {
            shelfId = Shelf.DEFAULT_SHELF_ID;

        }
        shelf = new ShelfOperations(rootView.getContext()).getShelf(shelfId);
    }

    private ArrayList<Book> getBooks() {
        return shelf.fetchBooks(rootView.getContext());
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
        Book book;
        switch (item.getItemId()) {
            case R.id.set_current_page:
                bookMenuActions.setCurrentPage(userBooks.get(info.position));
                return true;
            case R.id.mark_complete:
                book = userBooks.get(info.position);
                completeBook(book);
                return true;
            case R.id.edit_shelf:
                bookMenuActions.editShelf(userBooks.get(info.position));
                return true;
            case R.id.edit_num_pages:
                bookMenuActions.editNumberOfPages(userBooks.get(info.position));
                return true;
            case R.id.delete_book:
                book = userBooks.remove(info.position);
                bookAdapter.notifyDataSetChanged();
                bookOperations.deleteBook(book);
                return true;
        }

        return super.onContextItemSelected(item);
    }

    private void completeBook(Book book) {
        int remainingPages = book.getNumPages() - book.getCurrentPage();
        new UpdateOperations(rootView.getContext()).
                addUpdate(new Update(book.getId(), remainingPages));

        book.setComplete(true);
        book.setCurrentPage(book.getNumPages());

        bookAdapter.notifyDataSetChanged();
        bookOperations.updateBook(book);
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
            launchScanner();
            return true;
        } else if (id == R.id.edit_shelf) {
            bookMenuActions.editShelfInfo(shelf, ((AppCompatActivity) getActivity()).getSupportActionBar());
            ((NavigationDrawerFragment) getActivity().getSupportFragmentManager().
                    findFragmentById(R.id.navigation_drawer)).notifyChanges();
            return true;
        } else if (id == R.id.delete_shelf) {
            if (shelf.getId() != Shelf.DEFAULT_SHELF_ID) {
                bookMenuActions.deleteShelf(shelf);
                ((NavigationDrawerFragment) getActivity().getSupportFragmentManager().
                        findFragmentById(R.id.navigation_drawer)).deleteItemFromExpandableList(shelf);
            } else {
                Toast.makeText(rootView.getContext(), "You cannot delete this shelf", Toast.LENGTH_LONG).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void launchScanner() {
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);
        integrator.setCaptureActivity(ScanActivity.class);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
        integrator.setOrientationLocked(false);
        integrator.setPrompt("");
        integrator.initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            String bookISBN = result.getContents();
            if (bookISBN != null && !bookISBN.isEmpty()) {
                Search search = new Search(rootView.getContext(), bookAdapter, bookOperations, shelf);
                search.searchWithISBN(bookISBN);
            }
        } else {
            showToast("Scan failed");
        }
    }

    public void showToast(String message) {
        Toast.makeText(rootView.getContext(), message, Toast.LENGTH_LONG).show();
    }
}
