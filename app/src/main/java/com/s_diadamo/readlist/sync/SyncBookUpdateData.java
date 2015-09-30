package com.s_diadamo.readlist.sync;


import android.content.Context;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.s_diadamo.readlist.database.DatabaseHelper;
import com.s_diadamo.readlist.general.Utils;
import com.s_diadamo.readlist.updates.BookUpdate;
import com.s_diadamo.readlist.updates.BookUpdateOperations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class SyncBookUpdateData extends SyncData {
    private final BookUpdateOperations bookUpdateOperations;

    protected SyncBookUpdateData(Context context) {
        super(context, true);
        bookUpdateOperations = new BookUpdateOperations(context);
    }

    protected SyncBookUpdateData(Context context, boolean showSpinner) {
        super(context, showSpinner);
        bookUpdateOperations = new BookUpdateOperations(context);
    }

    void syncAllBookUpdates() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(TYPE_BOOK_UPDATE);
        query.whereEqualTo(Utils.USER_NAME, userName);
        if (showSpinner)
            syncSpinner.addThread();
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseBookUpdates, ParseException e) {
                if (showSpinner)
                    syncSpinner.endThread();
                ArrayList<BookUpdate> bookUpdatesOnDevice = bookUpdateOperations.getAllBookUpdates();
                ArrayList<BookUpdate> bookUpdatesFromParse = new ArrayList<>();
                if (parseBookUpdates != null) {
                    for (ParseObject parseBookUpdate : parseBookUpdates) {
                        BookUpdate bookUpdate = parseBookUpdateToBookUpdate(parseBookUpdate);
                        bookUpdatesFromParse.add(bookUpdate);
                    }
                }
                updateDeviceBookUpdates(bookUpdatesOnDevice, bookUpdatesFromParse, parseBookUpdates);
                updateParseBookUpdates(bookUpdatesOnDevice, bookUpdatesFromParse);
            }
        });
    }

    private void updateDeviceBookUpdates(ArrayList<BookUpdate> bookUpdatesOnDevice, ArrayList<BookUpdate> bookUpdatesFromParse, List<ParseObject> parseBookUdpates) {
        HashMap<Integer, Integer> deviceBookUpdateIds = new HashMap<>();
        int i = 0;
        for (BookUpdate bookUpdate : bookUpdatesOnDevice) {
            deviceBookUpdateIds.put(bookUpdate.getId(), i);
            ++i;
        }

        i = 0;
        for (BookUpdate bookUpdate : bookUpdatesFromParse) {
            if (!deviceBookUpdateIds.containsKey(bookUpdate.getId())) {
                int oldId = bookUpdate.getId();
                bookUpdateOperations.addBookUpdate(bookUpdate);
                if (bookUpdate.getId() != oldId) {
                    copyBookUpdateValues(parseBookUdpates.get(i), bookUpdate);
                    parseBookUdpates.get(i).saveEventually();
                }
            } else {
                BookUpdate comparisonBookUpdate = bookUpdatesOnDevice.get(deviceBookUpdateIds.get(bookUpdate.getId()));
                if (!bookUpdatesMatch(comparisonBookUpdate, bookUpdate)) {
                    bookUpdateOperations.addBookUpdate(bookUpdate);
                    copyBookUpdateValues(parseBookUdpates.get(i), bookUpdate);
                    parseBookUdpates.get(i).saveEventually();
                }
            }
            ++i;
        }
    }

    private void updateParseBookUpdates(ArrayList<BookUpdate> bookUpdatesOnDevice, ArrayList<BookUpdate> bookUpdatesFromParse) {
        HashSet<Integer> parseBookUpdateIds = new HashSet<>();
        for (BookUpdate bookUpdate : bookUpdatesFromParse) {
            parseBookUpdateIds.add(bookUpdate.getId());
        }

        ArrayList<ParseObject> bookUpdatesToSend = new ArrayList<>();

        for (final BookUpdate bookUpdate : bookUpdatesOnDevice) {
            if (!parseBookUpdateIds.contains(bookUpdate.getId())) {
                if (bookUpdate.isDeleted()) {
                    bookUpdateOperations.deleteBookUpdate(bookUpdate);
                } else {
                    bookUpdatesToSend.add(toParseBookUpdate(bookUpdate));
                }
            } else {
                if (bookUpdate.isDeleted()) {
                    deleteParseBookUpdate(bookUpdate);
                    bookUpdateOperations.deleteBookUpdate(bookUpdate);
                }
            }
        }

        ParseObject.saveAllInBackground(bookUpdatesToSend);
    }

    ParseObject toParseBookUpdate(BookUpdate bookUpdate) {
        ParseObject parseBookUpdate = new ParseObject(TYPE_BOOK_UPDATE);

        parseBookUpdate.put(Utils.USER_NAME, userName);
        parseBookUpdate.put(READLIST_ID, bookUpdate.getId());
        parseBookUpdate.put(DatabaseHelper.BOOK_UPDATE_BOOK_ID, bookUpdate.getBookId());
        parseBookUpdate.put(DatabaseHelper.BOOK_UPDATE_DATE, bookUpdate.getDate());

        return parseBookUpdate;
    }

    private boolean bookUpdatesMatch(BookUpdate deviceBookUpdate, BookUpdate parseBookUpdate) {
        boolean datesMatch = deviceBookUpdate.getDate().equals(parseBookUpdate.getDate());
        boolean booksMatch = deviceBookUpdate.getBookId() == parseBookUpdate.getBookId();
        return datesMatch && booksMatch;
    }

    private BookUpdate parseBookUpdateToBookUpdate(ParseObject parseBookUpdate) {
        return new BookUpdate(
                parseBookUpdate.getInt(READLIST_ID),
                parseBookUpdate.getInt(DatabaseHelper.BOOK_UPDATE_BOOK_ID),
                parseBookUpdate.getString(DatabaseHelper.BOOK_UPDATE_DATE));
    }

    void deleteParseBookUpdate(BookUpdate bookUpdate) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(TYPE_BOOK_UPDATE);
        query.whereEqualTo(Utils.USER_NAME, userName);
        query.whereEqualTo(READLIST_ID, bookUpdate.getId());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                ParseObject toDelete = list.get(0);
                toDelete.deleteEventually();
            }
        });
    }

    void copyBookUpdateValues(ParseObject parseBookUpdate, BookUpdate bookUpdate) {
        parseBookUpdate.put(READLIST_ID, bookUpdate.getId());
        parseBookUpdate.put(DatabaseHelper.BOOK_UPDATE_BOOK_ID, bookUpdate.getBookId());
        parseBookUpdate.put(DatabaseHelper.BOOK_UPDATE_DATE, bookUpdate.getDate());
    }
}
