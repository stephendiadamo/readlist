package com.s_diadamo.readlist.sync;

import android.os.AsyncTask;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.s_diadamo.readlist.general.Utils;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class FixRelations extends AsyncTask<Void, Void, Void> {

    private final String mUserName;
    private final int mNewId;
    private final int mOldId;
    private final String mType;
    private final String mField;
    private final CountDownLatch mSignal;

    public FixRelations(String userName, int newId, int oldId, String type, String field, CountDownLatch signal) {
        mUserName = userName;
        mNewId = newId;
        mOldId = oldId;
        mType = type;
        mField = field;
        mSignal = signal;
    }

    @Override
    protected Void doInBackground(Void... params) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(mType);
        query.whereEqualTo(Utils.USER_NAME, mUserName);
        query.whereEqualTo(mField, mOldId);
        try {
            List<ParseObject> objects = query.find();
            for (ParseObject object : objects) {
                object.put(mField, mNewId);
            }
            ParseObject.saveAll(objects);
            mSignal.countDown();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
