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
import java.util.HashSet;
import java.util.List;

public class SyncBookUpdateData extends SyncData {
    public SyncBookUpdateData(Context context) {
        super(context);
    }

    protected void syncAllBookUpdates() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(TYPE_BOOK_UPDATE);
        query.whereEqualTo(Utils.USER_NAME, userName);
        syncSpinner.addThread();
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseBookUpdates, ParseException e) {
                syncSpinner.endThread();
                ArrayList<BookUpdate> bookUpdatesOnDevice = new BookUpdateOperations(context).getAllBookUpdates();
                ArrayList<BookUpdate> bookUpdatesFromParse = new ArrayList<>();
                for (ParseObject parseBookUpdate : parseBookUpdates) {
                    BookUpdate bookUpdate = parseBookUpdateToBookUpdate(parseBookUpdate);
                    bookUpdatesFromParse.add(bookUpdate);
                }
                updateDeviceBookUpdates(bookUpdatesOnDevice, bookUpdatesFromParse);
                updateParseBookUpdates(bookUpdatesOnDevice, bookUpdatesFromParse);
            }
        });
    }

    private void updateDeviceBookUpdates(ArrayList<BookUpdate> bookUpdatesOnDevice, ArrayList<BookUpdate> bookUpdatesFromParse) {
        HashSet<Integer> deviceBookUpdateIds = new HashSet<>();
        for (BookUpdate bookUpdate : bookUpdatesOnDevice) {
            deviceBookUpdateIds.add(bookUpdate.getId());
        }

        BookUpdateOperations bookUpdateOperations = new BookUpdateOperations(context);

        for (BookUpdate bookUpdate : bookUpdatesFromParse) {
            if (!deviceBookUpdateIds.contains(bookUpdate.getId())) {
                bookUpdateOperations.addBookUpdate(bookUpdate);
            }
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
                bookUpdatesToSend.add(toParseBookUpdate(bookUpdate));
            }
        }

        ParseObject.saveAllInBackground(bookUpdatesToSend);
    }

    protected ParseObject toParseBookUpdate(BookUpdate bookUpdate) {
        ParseObject parseBookUpdate = new ParseObject(TYPE_BOOK_UPDATE);

        parseBookUpdate.put(Utils.USER_NAME, userName);
        parseBookUpdate.put(READLIST_ID, bookUpdate.getId());
        parseBookUpdate.put(DatabaseHelper.BOOK_UPDATE_BOOK_ID, bookUpdate.getBookId());
        parseBookUpdate.put(DatabaseHelper.BOOK_UPDATE_DATE, bookUpdate.getDate());

        return parseBookUpdate;
    }

    private BookUpdate parseBookUpdateToBookUpdate(ParseObject parseBookUpdate) {
        return new BookUpdate(
                parseBookUpdate.getInt(READLIST_ID),
                parseBookUpdate.getInt(DatabaseHelper.BOOK_UPDATE_BOOK_ID),
                parseBookUpdate.getString(DatabaseHelper.BOOK_UPDATE_DATE));
    }

}
