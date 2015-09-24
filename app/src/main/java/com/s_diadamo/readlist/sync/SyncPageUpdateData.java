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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class SyncPageUpdateData extends SyncData {
    private final PageUpdateOperations pageUpdateOperations;

    protected SyncPageUpdateData(Context context) {
        super(context, true);
        pageUpdateOperations = new PageUpdateOperations(context);
    }

    protected SyncPageUpdateData(Context context, boolean showSpinner) {
        super(context, showSpinner);
        pageUpdateOperations = new PageUpdateOperations(context);
    }

    void syncAllPageUpdates() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(TYPE_PAGE_UPDATE);
        query.whereEqualTo(Utils.USER_NAME, userName);

        if (showSpinner)
            syncSpinner.addThread();
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parsePageUpdates, ParseException e) {
                if (showSpinner)
                    syncSpinner.endThread();
                ArrayList<PageUpdate> pageUpdatesOnDevice = pageUpdateOperations.getAllPageUpdates();
                ArrayList<PageUpdate> pageUpdatesFromParse = new ArrayList<>();
                for (ParseObject parsePageUpdate : parsePageUpdates) {
                    PageUpdate pageUpdate = parsePageUpdateToPageUpdate(parsePageUpdate);
                    pageUpdatesFromParse.add(pageUpdate);
                }
                updateDevicePageUpdates(pageUpdatesOnDevice, pageUpdatesFromParse, parsePageUpdates);
                updateParsePageUpdates(pageUpdatesOnDevice, pageUpdatesFromParse);
            }
        });
    }

    private void updateDevicePageUpdates(ArrayList<PageUpdate> pageUpdatesOnDevice, ArrayList<PageUpdate> pageUpdatesFromParse, List<ParseObject> parsePageUpdates) {
        HashMap<Integer, Integer> devicePageUpdateIds = new HashMap<>();
        int i = 0;
        for (PageUpdate pageUpdate : pageUpdatesOnDevice) {
            devicePageUpdateIds.put(pageUpdate.getId(), i);
            ++i;
        }

        i = 0;
        for (PageUpdate pageUpdate : pageUpdatesFromParse) {
            if (!devicePageUpdateIds.containsKey(pageUpdate.getId())) {
                pageUpdateOperations.addPageUpdate(pageUpdate);
                copyPageUpdateValues(parsePageUpdates.get(i), pageUpdate);
                parsePageUpdates.get(i).saveEventually();
            } else {
                PageUpdate comparison = pageUpdatesOnDevice.get(devicePageUpdateIds.get(pageUpdate.getId()));
                if (!comparison.getDate().equals(pageUpdate.getDate())) {
                    pageUpdateOperations.addPageUpdate(pageUpdate);
                    copyPageUpdateValues(parsePageUpdates.get(i), pageUpdate);
                    parsePageUpdates.get(i).saveEventually();
                }
            }
            i++;
        }
    }

    private void updateParsePageUpdates(ArrayList<PageUpdate> pageUpdatesOnDevice, ArrayList<PageUpdate> pageUpdatesFromParse) {
        HashSet<Integer> parsePageUpdateIds = new HashSet<>();
        for (PageUpdate pageUpdate : pageUpdatesFromParse) {
            parsePageUpdateIds.add(pageUpdate.getId());
        }

        ArrayList<ParseObject> pageUpdatesToSend = new ArrayList<>();

        for (final PageUpdate pageUpdate : pageUpdatesOnDevice) {
            if (!parsePageUpdateIds.contains(pageUpdate.getId())) {
                if (pageUpdate.isDeleted()) {
                    pageUpdateOperations.deletePageUpdate(pageUpdate);
                } else {
                    pageUpdatesToSend.add(toParsePageUpdate(pageUpdate));
                }
            } else {
                if (pageUpdate.isDeleted()) {
                    deleteParsePageUpdate(pageUpdate);
                    pageUpdateOperations.deletePageUpdate(pageUpdate);
                }
            }
        }

        ParseObject.saveAllInBackground(pageUpdatesToSend);
    }

    private PageUpdate parsePageUpdateToPageUpdate(ParseObject parsePageUpdate) {
        return new PageUpdate(
                parsePageUpdate.getInt(READLIST_ID),
                parsePageUpdate.getInt(DatabaseHelper.PAGE_UPDATE_BOOK_ID),
                parsePageUpdate.getString(DatabaseHelper.PAGE_UPDATE_DATE),
                parsePageUpdate.getInt(DatabaseHelper.PAGE_UPDATE_PAGES));
    }

    ParseObject toParsePageUpdate(PageUpdate pageUpdate) {
        ParseObject parsePageUpdate = new ParseObject(TYPE_PAGE_UPDATE);

        parsePageUpdate.put(Utils.USER_NAME, userName);
        parsePageUpdate.put(READLIST_ID, pageUpdate.getId());
        parsePageUpdate.put(DatabaseHelper.PAGE_UPDATE_BOOK_ID, pageUpdate.getBookId());
        parsePageUpdate.put(DatabaseHelper.PAGE_UPDATE_DATE, pageUpdate.getDate());
        parsePageUpdate.put(DatabaseHelper.PAGE_UPDATE_PAGES, pageUpdate.getPages());

        return parsePageUpdate;
    }

    void deleteParsePageUpdate(PageUpdate pageUpdate) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(TYPE_PAGE_UPDATE);
        query.whereEqualTo(Utils.USER_NAME, userName);
        query.whereEqualTo(READLIST_ID, pageUpdate.getId());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                ParseObject toDelete = list.get(0);
                toDelete.deleteEventually();
            }
        });
    }

    void copyPageUpdateValues(ParseObject parsePageUpdate, PageUpdate pageUpdate) {
        parsePageUpdate.put(READLIST_ID, pageUpdate.getId());
        parsePageUpdate.put(DatabaseHelper.PAGE_UPDATE_BOOK_ID, pageUpdate.getBookId());
        parsePageUpdate.put(DatabaseHelper.PAGE_UPDATE_DATE, pageUpdate.getDate());
        parsePageUpdate.put(DatabaseHelper.PAGE_UPDATE_PAGES, pageUpdate.getPages());
    }
}
