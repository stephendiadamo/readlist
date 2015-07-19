package com.s_diadamo.readlist.lent;


import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;

import java.util.ArrayList;

class LentBookLoader extends AsyncTaskLoader<ArrayList<LentBook>> {
    public static final int ID = 5;
    private final Context context;


    public LentBookLoader(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
        super.onStartLoading();
    }

    @Override
    public ArrayList<LentBook> loadInBackground() {
        if (isReset()) {
            return null;
        }
        return new LentBookOperations(context).getValidLentBooks();
    }

    @Override
    public void deliverResult(ArrayList<LentBook> lentBooks) {
        if (isStarted()) {
            super.deliverResult(lentBooks);
        }
    }
}
