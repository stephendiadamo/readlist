package com.s_diadamo.readlist.goal;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.ArrayList;

class GoalLoader extends AsyncTaskLoader<ArrayList<Goal>> {

    public static final int ID = 2;
    private final Context context;

    public GoalLoader(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
        super.onStartLoading();
    }

    @Override
    public ArrayList<Goal> loadInBackground() {
        return new GoalOperations(context).getGoals();
    }

    @Override
    public void deliverResult(ArrayList<Goal> goals) {
        if (isStarted()) {
            super.deliverResult(goals);
        }
    }
}
