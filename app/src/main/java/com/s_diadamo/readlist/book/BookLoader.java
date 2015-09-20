package com.s_diadamo.readlist.book;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;

import com.s_diadamo.readlist.shelf.Shelf;
import com.s_diadamo.readlist.shelf.ShelfOperations;

import java.util.ArrayList;

class BookLoader extends AsyncTaskLoader<ArrayList<Book>> {
    private final Context context;
    private final int shelfId;

    public BookLoader(Context context, int shelfId) {
        super(context);
        this.context = context;
        this.shelfId = shelfId;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
        super.onStartLoading();
    }

    @Override
    public ArrayList<Book> loadInBackground() {
        if (isReset()) {
            return null;
        }
        Shelf shelf = new ShelfOperations(context).getShelf(shelfId);
        return shelf.fetchBooks(context);
    }

    @Override
    public void deliverResult(ArrayList<Book> books) {
        if (isStarted()) {
            super.deliverResult(books);
        }
    }
}
