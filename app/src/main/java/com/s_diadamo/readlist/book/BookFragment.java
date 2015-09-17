package com.s_diadamo.readlist.book;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.fortysevendeg.swipelistview.SwipeListView;
import com.fortysevendeg.swipelistview.SwipeListViewListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.parse.ParseAnalytics;
import com.s_diadamo.readlist.R;
import com.s_diadamo.readlist.general.Analytics;
import com.s_diadamo.readlist.general.LoaderIDs;
import com.s_diadamo.readlist.general.Utils;
import com.s_diadamo.readlist.navigationDrawer.NavigationDrawerFragment;
import com.s_diadamo.readlist.scan.ScanActivity;
import com.s_diadamo.readlist.search.Search;
import com.s_diadamo.readlist.shelf.Shelf;
import com.s_diadamo.readlist.shelf.ShelfAddEditFragment;
import com.s_diadamo.readlist.shelf.ShelfLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class BookFragment extends Fragment implements LoaderManager.LoaderCallbacks {
    private View rootView;
    private Context context;
    private SwipeListView bookListView;
    private ArrayList<Book> userBooks;
    private BookOperations bookOperations;
    private BookAdapter bookAdapter;
    private Shelf shelf;
    private int shelfId;
    private MenuItem hideCompletedBooksMenuItem;
    private MenuItem hideShelvedBooksMenuItem;
    private SharedPreferences prefs;
    private boolean loadingBooks = true;
    private boolean loadingShelf = true;
    private static final String HIDE_COMPLETED_BOOKS = "HIDE_COMPLETED_BOOKS";
    private static final String HIDE_SHELVED_BOOKS = "HIDE_SHELVED_BOOKS";
    private static final String EDIT_SHELF = "EDIT_SHELF";
    private static final String SORT_TYPE = "SORT_TYPE";
    private static final int SORT_DATE_ADDED = 0;
    private static final int SORT_TITLE = 1;
    private static final int SORT_AUTHOR = 2;
    private static final int SORT_SHELF = 3;
    private static final int SORT_COMPLETION = 4;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_book_swipe_listview, container, false);
        context = rootView.getContext();
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        setHasOptionsMenu(true);

        bookListView = (SwipeListView) rootView.findViewById(R.id.book_swipe_listview);
        bookOperations = new BookOperations(container.getContext());

        setShelfId();

        getLoaderManager().initLoader(LoaderIDs.BOOK_LOADER_ID, null, this);
        getLoaderManager().initLoader(LoaderIDs.SHELF_LOADER_ID, null, this);

        bookListView.setSwipeCloseAllItemsWhenMoveList(true);
        bookListView.setSwipeListViewListener(new SwipeListViewListener() {

            @Override
            public void onOpened(int i, boolean b) {
            }

            @Override
            public void onClosed(int i, boolean b) {
            }

            @Override
            public void onListChanged() {
                bookListView.closeOpenedItems();
            }

            @Override
            public void onMove(int i, float v) {
            }

            @Override
            public void onStartOpen(int i, int i1, boolean b) {
            }

            @Override
            public void onStartClose(int i, boolean b) {
            }

            @Override
            public void onClickFrontView(int i) {
                bookListView.closeOpenedItems();
                bookListView.openAnimate(i);
            }

            @Override
            public void onClickBackView(int i) {
                bookListView.closeOpenedItems();
            }

            @Override
            public void onDismiss(int[] ints) {
                bookListView.closeOpenedItems();
            }

            @Override
            public int onChangeSwipeMode(int i) {
                return 0;
            }

            @Override
            public void onChoiceChanged(int i, boolean b) {
            }

            @Override
            public void onChoiceStarted() {
            }

            @Override
            public void onChoiceEnded() {
            }

            @Override
            public void onFirstListItem() {
            }

            @Override
            public void onLastListItem() {
            }
        });

        ParseAnalytics.trackEventInBackground(Analytics.OPENED_BOOK_FRAGMENT);
        return rootView;
    }


    private void setShelfId() {
        Bundle args = getArguments();
        String stringShelfId = "";
        if (args != null) {
            stringShelfId = args.getString(Shelf.SHELF_ID);
        }

        if (stringShelfId != null && !stringShelfId.isEmpty()) {
            ParseAnalytics.trackEventInBackground(Analytics.VIEWED_PARTICULAR_SHELF);
            shelfId = Integer.parseInt(stringShelfId);
        } else {
            ParseAnalytics.trackEventInBackground(Analytics.VIEWED_DEFAULT_SHELF);
            shelfId = Shelf.DEFAULT_SHELF_ID;
        }
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

        hideCompletedBooksMenuItem = menu.findItem(R.id.hide_completed_books);
        if (hideCompletedBooksMenuItem != null) {
            hideCompletedBooksMenuItem.setChecked(prefs.getBoolean(HIDE_COMPLETED_BOOKS, false));
            hideCompletedBooksMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    toggleHideCompletedBooks();
                    return true;
                }
            });
        }

        hideShelvedBooksMenuItem = menu.findItem(R.id.hide_shelved_books);
        if (hideShelvedBooksMenuItem != null) {
            hideShelvedBooksMenuItem.setChecked(prefs.getBoolean(HIDE_SHELVED_BOOKS, false));
            hideShelvedBooksMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    toggleHideShelvedBooks();
                    return true;
                }
            });
        }

        if (shelfId == Shelf.DEFAULT_SHELF_ID) {
            menu.findItem(R.id.edit_shelf).setVisible(false);
            menu.findItem(R.id.delete_shelf).setVisible(false);
        } else {
            menu.findItem(R.id.hide_shelved_books).setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        long id = item.getItemId();
        BookMenuActions bookMenuActions = new BookMenuActions(context, bookOperations, bookAdapter, shelf);

        if (id == R.id.add_book_search) {
            ParseAnalytics.trackEventInBackground(Analytics.SEARCHED_BOOK);
            if (Utils.isNetworkAvailable(getActivity())) {
                bookMenuActions.searchBook(getActivity().getSupportFragmentManager());
            } else {
                Utils.showToast(context, Utils.CHECK_INTERNET_MESSAGE);
            }
            return true;
        } else if (id == R.id.add_book_manually) {
            ParseAnalytics.trackEventInBackground(Analytics.MANUALLY_ADDED_BOOK);
            bookMenuActions.manuallyAddBook();
            return true;
        } else if (id == R.id.add_book_scan) {
            ParseAnalytics.trackEventInBackground(Analytics.SCANNED_BOOK);
            if (Utils.isNetworkAvailable(getActivity())) {
                launchScanner();
            } else {
                Utils.showToast(context, Utils.CHECK_INTERNET_MESSAGE);
            }
            return true;
        } else if (id == R.id.edit_shelf) {
            ParseAnalytics.trackEventInBackground(Analytics.EDITED_SHELF);
            launchEditShelfFragment();
            return true;
        } else if (id == R.id.delete_shelf) {
            ParseAnalytics.trackEventInBackground(Analytics.DELETED_SHELF);
            if (shelf.getId() != Shelf.DEFAULT_SHELF_ID) {
                bookMenuActions.deleteShelf(shelf, ((NavigationDrawerFragment) getActivity().getSupportFragmentManager().
                        findFragmentById(R.id.navigation_drawer)), getActivity().getSupportFragmentManager());
            } else {
                Utils.showToast(context, getString(R.string.you_cannot_delete_this_shelf));
            }
            return true;
        } else if (id == R.id.sort_by_title) {
            sortBooksByTitle();
            return true;
        } else if (id == R.id.sort_by_date) {
            sortBooksByDate();
            return true;
        } else if (id == R.id.sort_by_shelf) {
            sortBooksByShelf();
            return true;
        } else if (id == R.id.sort_by_author) {
            sortBooksByAuthor();
            return true;
        } else if (id == R.id.sort_by_completion) {
            sortBooksByCompletion();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void sortBooksByAuthor() {
        Collections.sort(userBooks, new Comparator<Book>() {
            @Override
            public int compare(Book lhs, Book rhs) {
                return lhs.getAuthor().compareTo(rhs.getAuthor());
            }
        });
        bookAdapter.notifyDataSetChanged();
        saveSortingType(SORT_AUTHOR);
    }

    private void sortBooksByTitle() {
        Collections.sort(userBooks, new Comparator<Book>() {
            @Override
            public int compare(Book lhs, Book rhs) {
                return lhs.getTitle().compareTo(rhs.getTitle());
            }
        });
        bookAdapter.notifyDataSetChanged();
        saveSortingType(SORT_TITLE);
    }

    private void sortBooksByShelf() {
        Collections.sort(userBooks, new Comparator<Book>() {
            @Override
            public int compare(Book lhs, Book rhs) {
                return lhs.getShelfId() - rhs.getShelfId();
            }
        });
        bookAdapter.notifyDataSetChanged();
        saveSortingType(SORT_SHELF);
    }

    private void sortBooksByDate() {
        Collections.sort(userBooks, new Comparator<Book>() {
            @Override
            public int compare(Book lhs, Book rhs) {
                Date bookOneDate = Utils.getDateFromString(lhs.getDateAdded());
                Date bookTwoDate = Utils.getDateFromString(rhs.getDateAdded());
                if (bookOneDate != null && bookTwoDate != null) {
                    return -1 * bookOneDate.compareTo(bookTwoDate);
                } else {
                    return 0;
                }
            }
        });
        bookAdapter.notifyDataSetChanged();
        saveSortingType(SORT_DATE_ADDED);
    }

    private void sortBooksByCompletion() {
        Collections.sort(userBooks, new Comparator<Book>() {
            @Override
            public int compare(Book lhs, Book rhs) {
                int leftHandCompletion = lhs.getNumPages() <= 0 ? 101 : (100 * lhs.getCurrentPage() / lhs.getNumPages());
                int rightHandCompletion = rhs.getNumPages() <= 0 ? 101 : (100 * rhs.getCurrentPage() / rhs.getNumPages());
                return -1 * (leftHandCompletion - rightHandCompletion);
            }
        });
        bookAdapter.notifyDataSetChanged();
        saveSortingType(SORT_COMPLETION);
    }

    private void saveSortingType(int type) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(SORT_TYPE, type);
        editor.apply();
    }

    private void sortBooks() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int type = prefs.getInt(SORT_TYPE, SORT_DATE_ADDED);
        switch (type) {
            case SORT_DATE_ADDED:
                sortBooksByDate();
                break;
            case SORT_TITLE:
                sortBooksByTitle();
                break;
            case SORT_AUTHOR:
                sortBooksByAuthor();
                break;
            case SORT_SHELF:
                sortBooksByShelf();
                break;
            case SORT_COMPLETION:
                sortBooksByCompletion();
                break;
            default:
                sortBooksByDate();
                break;
        }
    }

    private void launchEditShelfFragment() {
        Bundle bundle = new Bundle();
        bundle.putString(Shelf.SHELF_ID, String.valueOf(shelf.getId()));
        bundle.putString(ShelfAddEditFragment.EDIT_MODE, getString(R.string.yes));

        Fragment fragment = new ShelfAddEditFragment();
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .addToBackStack(EDIT_SHELF)
                .replace(R.id.container, fragment)
                .commit();
    }

    private void toggleHideCompletedBooks() {
        hideCompletedBooksMenuItem.setChecked(!hideCompletedBooksMenuItem.isChecked());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(HIDE_COMPLETED_BOOKS, hideCompletedBooksMenuItem.isChecked());
        editor.apply();
        bookAdapter.toggleHideComplete();
        updateVisibleBooks();

        ParseAnalytics.trackEventInBackground(Analytics.TOGGLED_COMPLETED_BOOKS);
    }

    private void toggleHideShelvedBooks() {
        hideShelvedBooksMenuItem.setChecked(!hideShelvedBooksMenuItem.isChecked());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(HIDE_SHELVED_BOOKS, hideShelvedBooksMenuItem.isChecked());
        editor.apply();
        bookAdapter.toggleHideShelved();
        updateVisibleBooks();

        ParseAnalytics.trackEventInBackground(Analytics.TOGGLED_SHELVED_BOOKS);
    }

    private void updateVisibleBooks() {
        if (!loadingShelf && !loadingBooks && hideCompletedBooksMenuItem != null && hideShelvedBooksMenuItem != null) {
            userBooks = shelf.fetchBooks(context);
            if (shelf.getId() == Shelf.DEFAULT_SHELF_ID) {
                bookAdapter = new BookAdapter(context, userBooks,
                        hideCompletedBooksMenuItem.isChecked(),
                        hideShelvedBooksMenuItem.isChecked(),
                        getActivity().getSupportFragmentManager());
            } else {
                bookAdapter = new BookAdapter(context, userBooks,
                        hideCompletedBooksMenuItem.isChecked(),
                        false, getActivity().getSupportFragmentManager());
            }
            bookAdapter.updateVisibleBooks();
            bookListView.setAdapter(bookAdapter);
            sortBooks();
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
                Search search = new Search(context, getActivity().getSupportFragmentManager(), shelf);
                search.searchWithISBN(bookISBN);
            }
        } else {
            Utils.showToast(context, getString(R.string.scan_failed));
        }
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LoaderIDs.BOOK_LOADER_ID:
                return new BookLoader(context, shelfId);
            case LoaderIDs.SHELF_LOADER_ID:
                return new ShelfLoader(context, shelfId);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        int id = loader.getId();
        switch (id) {
            case LoaderIDs.BOOK_LOADER_ID:
                userBooks = (ArrayList<Book>) data;
                bookAdapter = new BookAdapter(context, userBooks,
                        prefs.getBoolean(HIDE_COMPLETED_BOOKS, false),
                        prefs.getBoolean(HIDE_SHELVED_BOOKS, false),
                        getActivity().getSupportFragmentManager());

                bookListView.setEmptyView(rootView.findViewById(R.id.book_empty_list_view));
                bookListView.setAdapter(bookAdapter);
                loadingBooks = false;
                break;
            case LoaderIDs.SHELF_LOADER_ID:
                shelf = (Shelf) data;
                ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
                if (ab != null) {
                    ab.setTitle(shelf.getName());
                    if (shelf.getColour() != Shelf.DEFAULT_COLOR) {
                        ab.setBackgroundDrawable(new ColorDrawable(shelf.getColour()));
                    } else {
                        ab.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.ActionBarColor)));
                    }
                }
                loadingShelf = false;
                break;
        }
        updateVisibleBooks();
    }

    @Override
    public void onLoaderReset(Loader loader) {
    }
}
