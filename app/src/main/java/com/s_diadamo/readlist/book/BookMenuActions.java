package com.s_diadamo.readlist.book;


import android.app.AlertDialog;
import android.app.Dialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.s_diadamo.readlist.R;
import com.s_diadamo.readlist.general.Utils;
import com.s_diadamo.readlist.navigationDrawer.NavigationDrawerFragment;
import com.s_diadamo.readlist.search.Search;
import com.s_diadamo.readlist.shelf.Shelf;
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
        Intent intent = new Intent(view.getContext(), BookManualAddActivity.class);
        intent.putExtra(Shelf.SHELF_ID, String.valueOf(shelf.getId()));
        view.getContext().startActivity(intent);
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

    public void deleteShelf(final Shelf shelf, final NavigationDrawerFragment shelfDrawer, final FragmentManager manager) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

        builder.setTitle("Delete Shelf");
        builder.setMessage("Delete " + shelf.getName() + "?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                new ShelfOperations(view.getContext()).deleteShelf(shelf);
                shelfDrawer.deleteItemFromExpandableList(shelf);
                Utils.launchBookFragment(manager);
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
