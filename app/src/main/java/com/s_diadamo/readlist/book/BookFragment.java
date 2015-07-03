package com.s_diadamo.readlist.book;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.LoaderManager;
import android.content.Intent;
import android.support.v4.content.Loader;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
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
import com.s_diadamo.readlist.shelf.ShelfLoader;
import com.s_diadamo.readlist.updates.Update;
import com.s_diadamo.readlist.updates.UpdateOperations;

import java.util.ArrayList;

public class BookFragment extends Fragment implements LoaderManager.LoaderCallbacks {
    private View rootView;
    private ListView bookListView;
    private ArrayList<Book> userBooks;
    private BookOperations bookOperations;
    private BookAdapter bookAdapter;
    private Shelf shelf;
    private int shelfId;
    private MenuItem hideCompletedBooks;
    private SharedPreferences prefs;

    private static final String HIDE_COMPLETED_BOOKS = "HIDE_COMPLETED_BOOKS";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_listview, container, false);

        setHasOptionsMenu(true);

        bookListView = (ListView) rootView.findViewById(R.id.general_list_view);
        bookOperations = new BookOperations(container.getContext());

        setShelfId();

        getLoaderManager().initLoader(BookLoader.ID, null, this);
        getLoaderManager().initLoader(ShelfLoader.ID, null, this);

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

    private void setShelfId() {
        Bundle args = getArguments();
        String stringShelfId = "";
        if (args != null) {
            stringShelfId = args.getString(Shelf.SHELF_ID);
        }
        if (!stringShelfId.isEmpty()) {
            shelfId = Integer.parseInt(stringShelfId);
        } else {
            shelfId = Shelf.DEFAULT_SHELF_ID;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_book, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        hideCompletedBooks = menu.findItem(R.id.hide_completed_books);
        hideCompletedBooks.setChecked(prefs.getBoolean(HIDE_COMPLETED_BOOKS, false));
        updateVisibleBooks();

        if (shelfId == Shelf.DEFAULT_SHELF_ID) {
            menu.findItem(R.id.edit_shelf).setVisible(false);
            menu.findItem(R.id.delete_shelf).setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        long id = item.getItemId();
        BookMenuActions bookMenuActions = (new BookMenuActions(rootView, bookOperations, bookAdapter, shelf));

        if (id == R.id.add_book_search) {
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
                bookMenuActions.deleteShelf(shelf, ((NavigationDrawerFragment) getActivity().getSupportFragmentManager().
                        findFragmentById(R.id.navigation_drawer)));
            } else {
                Toast.makeText(rootView.getContext(), "You cannot delete this shelf", Toast.LENGTH_LONG).show();
            }
            return true;
        } else if (id == R.id.hide_completed_books) {
            toggleHideCompletedBooks();
            updateVisibleBooks();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_book_actions, menu);

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        if (userBooks.get(info.position).isComplete()) {
            menu.findItem(R.id.set_current_page).setTitle("Reread");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Book book = userBooks.get(info.position);

        switch (item.getItemId()) {
            case R.id.set_current_page:
                if (book.isComplete()) {
                    book.reread();
                    bookAdapter.notifyDataSetChanged();
                    bookOperations.updateBook(book);
                } else {
                    (new BookMenuActions(rootView, bookOperations, bookAdapter, shelf)).setCurrentPage(book);
                }
                return true;
            case R.id.mark_complete:
                addRemainingPagesAndCompleteBook(book);
                return true;
            case R.id.edit_book:
                launchEditBookFragment(userBooks.get(info.position));
                return true;
            case R.id.delete_book:
                deleteBook(userBooks.get(info.position));
                return true;
        }

        return super.onContextItemSelected(item);
    }

    private void deleteBook(final Book book) {
        new AlertDialog.Builder(rootView.getContext())
                .setMessage("Delete \"" + book.getTitle() + "\"?")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        bookOperations.deleteBook(book);
                        userBooks.remove(book);
                        bookAdapter.notifyDataSetChanged();
                        bookAdapter.notifyDataSetInvalidated();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        l.showContextMenuForChild(v);
    }

    private void launchEditBookFragment(Book book) {
        Fragment fragment = new BookEditFragment();
        Bundle bundle = new Bundle();
        String bookId = String.valueOf(book.getId());
        bundle.putString("BOOK_ID", bookId);
        fragment.setArguments(bundle);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .addToBackStack("EDIT_BOOK")
                .replace(R.id.container, fragment)
                .commit();
    }

    private void addRemainingPagesAndCompleteBook(Book book) {
        int remainingPages = book.getNumPages() - book.getCurrentPage();
        new UpdateOperations(rootView.getContext()).
                addUpdate(new Update(book.getId(), remainingPages));

        book.markComplete();
        book.setCurrentPage(book.getNumPages());

        bookAdapter.notifyDataSetChanged();
        bookOperations.updateBook(book);
    }

    private void toggleHideCompletedBooks() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(HIDE_COMPLETED_BOOKS, !hideCompletedBooks.isChecked());
        editor.apply();
        hideCompletedBooks.setChecked(!hideCompletedBooks.isChecked());
    }

    private void updateVisibleBooks() {
        if (hideCompletedBooks.isChecked()) {
            bookAdapter.hideCompletedBooks();
        } else {
            getLoaderManager().initLoader(BookLoader.ID, null, this);
        }
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
            Toast.makeText(rootView.getContext(), "Scan Failed", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        switch (id) {
            case BookLoader.ID:
                return new BookLoader(rootView.getContext(), shelfId);
            case ShelfLoader.ID:
                return new ShelfLoader(rootView.getContext(), shelfId);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        int id = loader.getId();
        switch (id) {
            case BookLoader.ID:
                userBooks = (ArrayList<Book>) data;
                bookAdapter = new BookAdapter(rootView.getContext(), R.layout.row_book_element, userBooks);
                bookListView.setAdapter(bookAdapter);
                break;
            case ShelfLoader.ID:
                shelf = (Shelf) data;
                ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
                if (ab != null) {
                    ab.setTitle(shelf.getName());
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
    }
}
