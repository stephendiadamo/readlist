package com.s_diadamo.readlist.sync;


import android.content.Context;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.s_diadamo.readlist.book.Book;
import com.s_diadamo.readlist.database.DatabaseHelper;
import com.s_diadamo.readlist.general.Utils;
import com.s_diadamo.readlist.lent.LentBook;
import com.s_diadamo.readlist.lent.LentBookOperations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SyncLentBookData extends SyncData {
    private final LentBookOperations lentBookOperations;

    public SyncLentBookData(Context context) {
        super(context);
        lentBookOperations = new LentBookOperations(context);
    }

    SyncLentBookData(Context context, boolean showSpinner) {
        super(context, showSpinner);
        lentBookOperations = new LentBookOperations(context);
    }

    void syncAllLentBooks() {
        if (showSpinner)
            syncSpinner.addThread();

        ParseQuery<ParseObject> query = ParseQuery.getQuery(TYPE_LENT_BOOK);
        query.whereEqualTo(Utils.USER_NAME, userName);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseLentBooks, ParseException e) {
                if (showSpinner)
                    syncSpinner.endThread();

                ArrayList<LentBook> lentBooksOnDevice = lentBookOperations.getLentBooks();
                ArrayList<LentBook> lentBooksFromParse = new ArrayList<>();
                for (ParseObject parseLentBook : parseLentBooks) {
                    LentBook lentBook = parseLentBookToLentBook(parseLentBook);
                    lentBooksFromParse.add(lentBook);
                }
                updateDeviceLentBooks(lentBooksOnDevice, lentBooksFromParse);
                updateParseLentBooks(lentBooksOnDevice, lentBooksFromParse);
            }
        });
    }

    private void updateDeviceLentBooks(ArrayList<LentBook> lentBooksOnDevice, ArrayList<LentBook> lentBooksFromParse) {
        HashSet<Integer> deviceLentBookIds = new HashSet<>();
        for (LentBook lentBook : lentBooksOnDevice) {
            deviceLentBookIds.add(lentBook.getId());
        }

        for (LentBook lentBook : lentBooksFromParse) {
            if (!deviceLentBookIds.contains(lentBook.getId())) {
                lentBookOperations.addLentBook(lentBook);
            }
        }
    }

    private void updateParseLentBooks(ArrayList<LentBook> lentBooksOnDevice, ArrayList<LentBook> lentBooksFromParse) {
        HashSet<Integer> parseLentBookIds = new HashSet<>();
        for (LentBook lentBook : lentBooksFromParse) {
            parseLentBookIds.add(lentBook.getId());
        }

        ArrayList<ParseObject> lentBooksToSend = new ArrayList<>();

        for (final LentBook lentBook : lentBooksOnDevice) {
            if (!parseLentBookIds.contains(lentBook.getId())) {
                if (lentBook.isDeleted()) {
                    lentBookOperations.deleteLentBook(lentBook);
                } else {
                    lentBooksToSend.add(toParseLentBook(lentBook));
                }
            } else {
                if (lentBook.isDeleted()) {
                    deleteParseLentBook(lentBook);
                    lentBookOperations.deleteLentBook(lentBook);
                } else {
                    updateParseLentBook(lentBook);
                }
            }
        }

        ParseObject.saveAllInBackground(lentBooksToSend);
    }

    public ParseObject toParseLentBook(LentBook lentBook) {
        ParseObject parseLentBook = new ParseObject(TYPE_LENT_BOOK);

        parseLentBook.put(Utils.USER_NAME, userName);
        parseLentBook.put(READLIST_ID, lentBook.getId());
        parseLentBook.put(DatabaseHelper.LENT_BOOK_BOOK_ID, lentBook.getBookId());
        parseLentBook.put(DatabaseHelper.LENT_BOOK_LENT_TO, lentBook.getLentTo());
        parseLentBook.put(DatabaseHelper.LENT_BOOK_DATE_LENT, lentBook.getDateLent());

        return parseLentBook;
    }

    private LentBook parseLentBookToLentBook(ParseObject parseLentBook) {
        return new LentBook(
                parseLentBook.getInt(READLIST_ID),
                parseLentBook.getInt(DatabaseHelper.LENT_BOOK_BOOK_ID),
                parseLentBook.getString(DatabaseHelper.LENT_BOOK_LENT_TO),
                parseLentBook.getString(DatabaseHelper.LENT_BOOK_DATE_LENT));
    }

    protected void updateParseLentBook(final LentBook lentBook) {
        queryForLentBook(lentBook, new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (list != null && list.size() > 0) {
                    ParseObject lentBookToUpdate = list.get(0);
                    copyLentBookValues(lentBookToUpdate, lentBook);
                    lentBookToUpdate.saveEventually();
                }
            }
        });
    }

    protected void deleteParseLentBook(LentBook lentBook) {
        queryForLentBook(lentBook, new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (list != null && list.size() > 0) {
                    ParseObject lentBookToDelete = list.get(0);
                    lentBookToDelete.deleteEventually();
                }
            }
        });
    }

    private void queryForLentBook(LentBook lentBook, FindCallback<ParseObject> callback) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(TYPE_LENT_BOOK);
        query.whereEqualTo(Utils.USER_NAME, userName);
        query.whereEqualTo(READLIST_ID, lentBook.getId());
        query.findInBackground(callback);
    }

    private void copyLentBookValues(ParseObject parseLentBook, LentBook lentBook) {
        parseLentBook.put(READLIST_ID, lentBook.getId());
        parseLentBook.put(DatabaseHelper.LENT_BOOK_BOOK_ID, lentBook.getBookId());
        parseLentBook.put(DatabaseHelper.LENT_BOOK_LENT_TO, lentBook.getLentTo());
        parseLentBook.put(DatabaseHelper.LENT_BOOK_DATE_LENT, lentBook.getDateLent());
    }
}
