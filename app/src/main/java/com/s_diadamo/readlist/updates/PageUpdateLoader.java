package com.s_diadamo.readlist.updates;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.ArrayList;

public class PageUpdateLoader extends AsyncTaskLoader<ArrayList<PageUpdate>> {
    private final Context mContext;
    private final int mBookId;

    public PageUpdateLoader(Context context, int bookId) {
        super(context);
        mContext = context;
        mBookId = bookId;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
        super.onStartLoading();
    }

    @Override
    public ArrayList<PageUpdate> loadInBackground() {
        if (isReset()) {
            return null;
        }
        return new PageUpdateOperations(mContext).getPageUpdatesForBook(mBookId);
    }

    @Override
    public void deliverResult(ArrayList<PageUpdate> pageUpdates) {
        if (isStarted()) {
            super.deliverResult(pageUpdates);
        }
    }
}
