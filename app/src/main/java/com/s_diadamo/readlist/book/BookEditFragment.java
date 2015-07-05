package com.s_diadamo.readlist.book;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;

import com.s_diadamo.readlist.R;
import com.s_diadamo.readlist.shelf.Shelf;
import com.s_diadamo.readlist.shelf.ShelfOperations;
import com.s_diadamo.readlist.shelf.ShelfSpinnerAdapter;

import java.util.ArrayList;


public class BookEditFragment extends Fragment {

    private View rootView;
    private Book book;
    private BookOperations bookOperations;
    private EditText bookTitle;
    private EditText bookAuthor;
    private EditText bookPages;
    private Spinner shelfSpinner;
    private ArrayList<Shelf> shelves;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_edit_book, container, false);

        setHasOptionsMenu(true);

        bookOperations = new BookOperations(rootView.getContext());
        int bookId = Integer.parseInt(getArguments().getString("BOOK_ID"));
        book = bookOperations.getBook(bookId);

        bookTitle = (EditText) rootView.findViewById(R.id.edit_book_title);
        bookAuthor = (EditText) rootView.findViewById(R.id.edit_book_author);
        bookPages = (EditText) rootView.findViewById(R.id.edit_book_number_of_pages);
        shelfSpinner = (Spinner) rootView.findViewById(R.id.edit_book_shelf_spinner);

        bookTitle.setText(book.getTitle());
        bookTitle.requestFocus();
        bookAuthor.setText(book.getAuthor());
        bookPages.setText(String.valueOf(book.getNumPages()));

        ShelfOperations shelfOperations = new ShelfOperations(rootView.getContext());
        shelves = shelfOperations.getAllShelves();
        ShelfSpinnerAdapter adapter = new ShelfSpinnerAdapter(rootView.getContext(),
                android.R.layout.simple_spinner_dropdown_item, shelves);
        shelfSpinner.setAdapter(adapter);

        int shelfIndex = 0;
        for (Shelf s : shelves) {
            if (s.getId() == book.getShelfId()) {
                break;
            }
            shelfIndex++;
        }
        shelfSpinner.setSelection(shelfIndex);

        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_edit_book, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        long id = item.getItemId();

        if (id == R.id.edit_book_done) {
            updateBookValues();
            launchBookFragment();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateBookValues() {
        book.setTitle(bookTitle.getText().toString());
        book.setAuthor(bookAuthor.getText().toString());
        book.setNumPages(Integer.parseInt(bookPages.getText().toString()));
        int selectedShelfPosition = shelfSpinner.getSelectedItemPosition();
        book.setShelfId(shelves.get(selectedShelfPosition).getId());
        bookOperations.updateBook(book);
    }

    private void launchBookFragment() {
        Fragment fragment = new BookFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }
}
