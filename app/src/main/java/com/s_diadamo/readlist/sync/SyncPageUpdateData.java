package com.s_diadamo.readlist.sync;

import android.content.Context;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.s_diadamo.readlist.database.DatabaseHelper;
import com.s_diadamo.readlist.general.Utils;
import com.s_diadamo.readlist.updates.PageUpdate;
import com.s_diadamo.readlist.updates.PageUpdateOperations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SyncPageUpdateData extends SyncData {
    public SyncPageUpdateData(Context context) {
        super(context);
    }

    protected void syncAllPageUpdates() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(TYPE_PAGE_UPDATE);
        query.whereEqualTo(Utils.USER_NAME, userName);
        syncSpinner.addThread();
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parsePageUpdates, ParseException e) {
                syncSpinner.endThread();
                ArrayList<PageUpdate> pageUpdatesOnDevice = new PageUpdateOperations(context).getAllPageUpdates();
                ArrayList<PageUpdate> pageUpdatesFromParse = new ArrayList<>();
                for (ParseObject parsePageUpdate : parsePageUpdates) {
                    PageUpdate pageUpdate = parsePageUpdateToPageUpdate(parsePageUpdate);
                    pageUpdatesFromParse.add(pageUpdate);
                }
                updateDevicePageUpdates(pageUpdatesOnDevice, pageUpdatesFromParse);
                updateParsePageUpdates(pageUpdatesOnDevice, pageUpdatesFromParse);
            }
        });
    }

    private void updateDevicePageUpdates(ArrayList<PageUpdate> pageUpdatesOnDevice, ArrayList<PageUpdate> pageUpdatesFromParse) {
        HashSet<Integer> devicePageUpdateIds = new HashSet<>();
        for (PageUpdate pageUpdate : pageUpdatesOnDevice) {
            devicePageUpdateIds.add(pageUpdate.getId());
        }

        PageUpdateOperations pageUpdateOperations = new PageUpdateOperations(context);

        for (PageUpdate pageUpdate : pageUpdatesFromParse) {
            if (!devicePageUpdateIds.contains(pageUpdate.getId())) {
                pageUpdateOperations.addPageUpdate(pageUpdate);
            }
        }
    }

    private void updateParsePageUpdates(ArrayList<PageUpdate> pageUpdatesOnDevice, ArrayList<PageUpdate> pageUpdatesFromParse) {
        HashSet<Integer> parsepageUpdateIds = new HashSet<>();
        for (PageUpdate pageUpdate : pageUpdatesFromParse) {
            parsepageUpdateIds.add(pageUpdate.getId());
        }

        ArrayList<ParseObject> pageUpdatesToSend = new ArrayList<>();

        for (final PageUpdate pageUpdate : pageUpdatesOnDevice) {
            if (!parsepageUpdateIds.contains(pageUpdate.getId())) {
                pageUpdatesToSend.add(toParsePageUpdate(pageUpdate));
            }
        }

        ParseObject.saveAllInBackground(pageUpdatesToSend);
    }

    private PageUpdate parsePageUpdateToPageUpdate(ParseObject parsePageUpdate) {
        return new PageUpdate(
                parsePageUpdate.getInt(READLIST_ID),
                parsePageUpdate.getInt(DatabaseHelper.BOOK_UPDATE_BOOK_ID),
                parsePageUpdate.getString(DatabaseHelper.BOOK_UPDATE_DATE),
                parsePageUpdate.getInt(DatabaseHelper.PAGE_UPDATE_PAGES));
    }

    protected ParseObject toParsePageUpdate(PageUpdate pageUpdate) {
        ParseObject parsePageUpdate = new ParseObject(TYPE_PAGE_UPDATE);

        parsePageUpdate.put(Utils.USER_NAME, userName);
        parsePageUpdate.put(READLIST_ID, pageUpdate.getId());
        parsePageUpdate.put(DatabaseHelper.PAGE_UPDATE_BOOK_ID, pageUpdate.getBookId());
        parsePageUpdate.put(DatabaseHelper.PAGE_UPDATE_DATE, pageUpdate.getDate());
        parsePageUpdate.put(DatabaseHelper.PAGE_UPDATE_PAGES, pageUpdate.getPages());

        return parsePageUpdate;
    }
}
