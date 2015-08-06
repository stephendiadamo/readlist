package com.s_diadamo.readlist.comment;


import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;

import java.util.ArrayList;

public class CommentLoader extends AsyncTaskLoader<ArrayList<Comment>> {

    public static final int ID = 6;
    private final Context context;
    private final int bookId;

    public CommentLoader(Context context, int bookId) {
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
    public ArrayList<Comment> loadInBackground() {
        if (isReset()) {
            return null;
        }
        return new CommentOperations(context).getCommentsForBook(bookId);
    }

    @Override
    public void deliverResult(ArrayList<Comment> comments) {
        if (isStarted()) {
            super.deliverResult(comments);
        }
    }
}
