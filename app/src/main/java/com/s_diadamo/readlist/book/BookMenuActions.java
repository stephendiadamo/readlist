package com.s_diadamo.readlist.book;


import android.app.AlertDialog;
import android.app.Dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.s_diadamo.readlist.R;
import com.s_diadamo.readlist.general.Utils;
import com.s_diadamo.readlist.lent.LentBook;
import com.s_diadamo.readlist.lent.LentBookDialog;
import com.s_diadamo.readlist.navigationDrawer.NavigationDrawerFragment;
import com.s_diadamo.readlist.search.Search;
import com.s_diadamo.readlist.shelf.Shelf;
import com.s_diadamo.readlist.shelf.ShelfOperations;
import com.s_diadamo.readlist.sync.SyncData;
import com.s_diadamo.readlist.sync.SyncShelfData;

class BookMenuActions {

    private final Context context;
    private BookOperations bookOperations;
    private BookAdapter bookAdapter;
    private Shelf shelf;

    public BookMenuActions(Context context, BookOperations bookOperations, BookAdapter bookAdapter, Shelf shelf) {
        this.context = context;
        this.bookOperations = bookOperations;
        this.bookAdapter = bookAdapter;
        this.shelf = shelf;
    }

    public BookMenuActions(Context context) {
        this.context = context;
    }

    public void setCurrentPage(final Book book) {
        BookUpdatePageDialog bookUpdatePageDialog = new BookUpdatePageDialog(context, book, bookAdapter, bookOperations);
        bookUpdatePageDialog.show();
    }

    public void manuallyAddBook() {
        Intent intent = new Intent(context, BookManualAddActivity.class);
        intent.putExtra(Shelf.SHELF_ID, String.valueOf(shelf.getId()));
        context.startActivity(intent);
    }

    public void searchBook(final FragmentManager manager) {
        final Dialog searchBookDialog = new Dialog(context);
        searchBookDialog.setContentView(R.layout.dialog_search_book);
        searchBookDialog.setTitle("Search");

        final Button searchBookButton = (Button) searchBookDialog.findViewById(R.id.search_book);
        searchBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bookTitle = ((EditText) searchBookDialog.findViewById(R.id.book_search_title)).getText().toString();
                String bookAuthor = ((EditText) searchBookDialog.findViewById(R.id.book_search_author)).getText().toString();
                Search search = new Search(context, manager, shelf);
                search.searchWithAuthorAndTitle(bookAuthor, bookTitle);
                searchBookDialog.dismiss();
            }
        });
        searchBookDialog.show();
    }

    public void deleteShelf(final Shelf shelf, final NavigationDrawerFragment shelfDrawer, final FragmentManager manager) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("Delete Shelf");
        builder.setMessage("Delete " + shelf.getName() + "?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (Utils.checkUserIsLoggedIn(context)) {
                    new SyncData(context).delete(shelf);
                    new ShelfOperations(context).deleteShelf(shelf);
                } else {
                    ShelfOperations shelfOperations = new ShelfOperations(context);
                    shelfOperations.removeAssociatedBooks(shelf);
                    shelf.delete();
                    new ShelfOperations(context).updateShelf(shelf);
                }

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

    public void lendBook(final Book book) {
        LentBook lentBook = book.getLentBook(context);
        if (lentBook == null) {
            LentBookDialog lentBookDialog = new LentBookDialog(context, book);
            lentBookDialog.show();
        } else {
            Utils.showToast(context, "This book has already been lent to " + lentBook.getLentTo());
        }
    }
}
