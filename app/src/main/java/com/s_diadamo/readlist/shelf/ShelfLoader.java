package com.s_diadamo.readlist.shelf;


import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

public class ShelfLoader extends AsyncTaskLoader<Shelf> {

    public static final int ID = 1;
    private final Context context;
    private final int shelfId;

    public ShelfLoader(Context context, int shelfId) {
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
    public Shelf loadInBackground() {
        if (isReset()) {
            return null;
        }
        return new ShelfOperations(context).getShelf(shelfId);
    }

    @Override
    public void deliverResult(Shelf shelf) {
        if (isStarted()) {
            super.deliverResult(shelf);
        }
    }
}

