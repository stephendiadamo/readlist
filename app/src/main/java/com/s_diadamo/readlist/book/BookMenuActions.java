package com.s_diadamo.readlist.book;


import android.app.AlertDialog;
import android.app.Dialog;

import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.s_diadamo.readlist.R;
import com.s_diadamo.readlist.search.Search;
import com.s_diadamo.readlist.shelf.Shelf;
import com.s_diadamo.readlist.shelf.ShelfEditInfoDialog;
import com.s_diadamo.readlist.shelf.ShelfOperations;
import com.s_diadamo.readlist.shelf.ShelfSpinnerAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class BookMenuActions {

    View view;
    BookOperations bookOperations;
    BookAdapter bookAdapter;
    int shelfId;

    public BookMenuActions(View view, BookOperations bookOperations, BookAdapter bookAdapter, int shelfId) {
        this.view = view;
        this.bookOperations = bookOperations;
        this.bookAdapter = bookAdapter;
        this.shelfId = shelfId;
    }

    public void editNumberOfPages(final Book book) {
        final Dialog editNumberOfPagesDialog = new Dialog(view.getContext());
        editNumberOfPagesDialog.setContentView(R.layout.dialog_edit_book_pages);
        editNumberOfPagesDialog.setTitle("Edit Book Pages");

        final Button updatePagesButton = (Button) editNumberOfPagesDialog.findViewById(R.id.update_page_button);
        updatePagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newNumberOfPages = ((EditText) editNumberOfPagesDialog.findViewById(R.id.update_page_num_value)).getText().toString();
                if (!newNumberOfPages.isEmpty()) {
                    int pages = Integer.parseInt(newNumberOfPages);
                    book.setNumPages(pages);
                    bookOperations.updateBook(book);
                    bookAdapter.notifyDataSetChanged();
                }
                editNumberOfPagesDialog.dismiss();
            }
        });
        editNumberOfPagesDialog.show();
    }

    public void setCurrentPage(final Book book) {
        BookUpdatePageDialog bookUpdatePageDialog = new BookUpdatePageDialog(view.getContext(), book, bookAdapter, bookOperations);
        bookUpdatePageDialog.show();
    }

    public void manuallyAddBook() {
        BookManuallyAddBookDialog bookManuallyAddBookDialog = new BookManuallyAddBookDialog(view.getContext(), bookAdapter, bookOperations, shelfId);
        bookManuallyAddBookDialog.show();
    }

    public void searchBook() {
        final Dialog searchBookDialog = new Dialog(view.getContext());
        searchBookDialog.setContentView(R.layout.dialog_search_book);
        searchBookDialog.setTitle("Search");

        final Button searchBookButton = (Button) searchBookDialog.findViewById(R.id.search_book);
        searchBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bookTitle = ((EditText) searchBookDialog.findViewById(R.id.book_search_title)).getText().toString();
                String bookAuthor = ((EditText) searchBookDialog.findViewById(R.id.book_search_author)).getText().toString();
                Search search = new Search(view.getContext(), bookAdapter, bookOperations, shelfId);
                search.searchWithAuthorAndTitle(bookAuthor, bookTitle);
                searchBookDialog.dismiss();
            }
        });
        searchBookDialog.show();
    }

    public void editShelfInfo(final Shelf shelf) {
        ShelfEditInfoDialog editShelfDialog = new ShelfEditInfoDialog(view.getContext(), shelf);
        editShelfDialog.show();
    }

    public void deleteShelf(final Shelf shelf) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

        builder.setTitle("Delete Shelf");
        builder.setMessage("Delete " + shelf.getName() + "?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                new ShelfOperations(view.getContext()).deleteShelf(shelf);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void editShelf(final Book book) {
        final Dialog setShelfDialog = new Dialog(view.getContext());
        setShelfDialog.setContentView(R.layout.dialog_set_book_shelf);
        setShelfDialog.setTitle("Edit Shelf");

        final ShelfOperations shelfOperations = new ShelfOperations(view.getContext());
        final Spinner shelfSpinner = (Spinner) setShelfDialog.findViewById(R.id.set_shelf_shelf_spinner);
        final ArrayList<Shelf> shelves = shelfOperations.getAllShelves();

        ShelfSpinnerAdapter adapter = new ShelfSpinnerAdapter(setShelfDialog.getContext(),
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

        Button setShelf = (Button) setShelfDialog.findViewById(R.id.set_shelf_done);

        final int currentShelfId = book.getShelfId();
        setShelf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedShelfPosition = shelfSpinner.getSelectedItemPosition();
                book.setShelfId(shelves.get(selectedShelfPosition).getId());
                if (book.getShelfId() != currentShelfId && currentShelfId != Shelf.DEFAULT_SHELF_ID) {
                    bookAdapter.remove(book);
                }
                bookOperations.updateBook(book);
                setShelfDialog.dismiss();
            }
        });

        setShelfDialog.show();
    }
}
