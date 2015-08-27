package com.s_diadamo.readlist.readingSession;


import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;

import java.util.ArrayList;

public class ReadingSessionLoader extends AsyncTaskLoader<ArrayList<ReadingSession>> {

    public static final int ID = 7;
    private final Context context;
    private final int bookId;

    public ReadingSessionLoader(Context context, int bookId) {
        super(context);
        this.context = context;
        this.bookId = bookId;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
        super.onStartLoading();
    }

    @Override
    public ArrayList<ReadingSession> loadInBackground() {
        if (isReset()) {
            return null;
        }
        return new ReadingSessionOperations(context).getReadingSessionsForBook(bookId);
    }

    @Override
    public void deliverResult(ArrayList<ReadingSession> readingSessions) {
        if (isStarted()) {
            super.deliverResult(readingSessions);
        }
    }
}
