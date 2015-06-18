package com.s_diadamo.readlist.book;


import android.app.AlertDialog;
import android.app.Dialog;

import android.content.DialogInterface;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.s_diadamo.readlist.R;
import com.s_diadamo.readlist.search.Search;
import com.s_diadamo.readlist.shelf.Shelf;
import com.s_diadamo.readlist.shelf.ShelfEditInfoDialog;
import com.s_diadamo.readlist.shelf.ShelfOperations;

class BookMenuActions {

    private final View view;
    private final BookOperations bookOperations;
    private final BookAdapter bookAdapter;
    private final Shelf shelf;

    public BookMenuActions(View view, BookOperations bookOperations, BookAdapter bookAdapter, Shelf shelf) {
        this.view = view;
        this.bookOperations = bookOperations;
        this.bookAdapter = bookAdapter;
        this.shelf = shelf;
    }

    public void setCurrentPage(final Book book) {
        BookUpdatePageDialog bookUpdatePageDialog = new BookUpdatePageDialog(view.getContext(), book, bookAdapter, bookOperations);
        bookUpdatePageDialog.show();
    }

    public void manuallyAddBook() {
        BookManuallyAddBookDialog bookManuallyAddBookDialog = new BookManuallyAddBookDialog(view.getContext(), bookAdapter, bookOperations, shelf);
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
                Search search = new Search(view.getContext(), bookAdapter, bookOperations, shelf);
                search.searchWithAuthorAndTitle(bookAuthor, bookTitle);
                searchBookDialog.dismiss();
            }
        });
        searchBookDialog.show();
    }

    public void editShelfInfo(final Shelf shelf, ActionBar actionBar) {
        ShelfEditInfoDialog editShelfDialog = new ShelfEditInfoDialog(view.getContext(), shelf, actionBar);
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
}
