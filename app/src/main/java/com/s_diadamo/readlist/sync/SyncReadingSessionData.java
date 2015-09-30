package com.s_diadamo.readlist.sync;

import android.content.Context;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.s_diadamo.readlist.database.DatabaseHelper;
import com.s_diadamo.readlist.general.Utils;
import com.s_diadamo.readlist.readingSession.ReadingSession;
import com.s_diadamo.readlist.readingSession.ReadingSessionOperations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class SyncReadingSessionData extends SyncData {

    private final ReadingSessionOperations readingSessionOperations;

    public SyncReadingSessionData(Context context, boolean showSpinner) {
        super(context, showSpinner);
        readingSessionOperations = new ReadingSessionOperations(context);
    }

    public SyncReadingSessionData(Context context) {
        super(context);
        readingSessionOperations = new ReadingSessionOperations(context);
    }

    public void syncAllReadingSessions() {
        if (showSpinner) {
            syncSpinner.addThread();
        }

        ParseQuery<ParseObject> query = ParseQuery.getQuery(TYPE_READING_SESSION);
        query.whereEqualTo(Utils.USER_NAME, userName);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseReadingSessions, ParseException e) {
                if (showSpinner)
                    syncSpinner.endThread();

                ArrayList<ReadingSession> readingSessionsOnDevice = readingSessionOperations.getReadingSessions();
                ArrayList<ReadingSession> readingSessionsFromParse = new ArrayList<>();
                for (ParseObject parseReadingSession : parseReadingSessions) {
                    ReadingSession readingSession = parseReadingSessionToReadingSession(parseReadingSession);
                    readingSessionsFromParse.add(readingSession);
                }
                updateDeviceReadingSessions(readingSessionsOnDevice, readingSessionsFromParse, parseReadingSessions);
                updateParseReadingSessions(readingSessionsOnDevice, readingSessionsFromParse);
            }
        });
    }

    private void updateDeviceReadingSessions(ArrayList<ReadingSession> readingSessionsOnDevice, ArrayList<ReadingSession> readingSessionsFromParse, List<ParseObject> parseReadingSessions) {
        HashMap<Long, Integer> deviceReadingSessionIds = new HashMap<>();
        int i = 0;
        for (ReadingSession readingSession : readingSessionsOnDevice) {
            deviceReadingSessionIds.put(readingSession.getId(), i);
            ++i;
        }

        i = 0;
        for (ReadingSession readingSession : readingSessionsFromParse) {
            if (!deviceReadingSessionIds.containsKey(readingSession.getId())) {
                long oldId = readingSession.getId();
                readingSessionOperations.addReadingSession(readingSession);
                if (readingSession.getId() != oldId) {
                    copyReadingSessionValues(parseReadingSessions.get(i), readingSession);
                    parseReadingSessions.get(i).saveEventually();
                }
            } else {
                ReadingSession comparison = readingSessionsOnDevice.get(deviceReadingSessionIds.get(readingSession.getId()));
                if (!comparison.getDate().equals(readingSession.getDate())) {
                    readingSessionOperations.addReadingSession(readingSession);
                    copyReadingSessionValues(parseReadingSessions.get(i), readingSession);
                    parseReadingSessions.get(i).saveEventually();
                }
            }
            ++i;
        }
    }

    private void updateParseReadingSessions(ArrayList<ReadingSession> readingSessionsOnDevice, ArrayList<ReadingSession> readingSessionsFromParse) {
        HashSet<Long> parseReadingSessionIds = new HashSet<>();
        for (ReadingSession readingSession : readingSessionsFromParse) {
            parseReadingSessionIds.add(readingSession.getId());
        }

        ArrayList<ParseObject> readingSessionsToSend = new ArrayList<>();

        for (final ReadingSession readingSession : readingSessionsOnDevice) {
            if (!parseReadingSessionIds.contains(readingSession.getId())) {
                if (readingSession.isDeleted()) {
                    readingSessionOperations.deleteSession(readingSession);
                } else {
                    readingSessionsToSend.add(toParseReadingSession(readingSession));
                }
            } else {
                if (readingSession.isDeleted()) {
                    deleteParseReadingSession(readingSession);
                    readingSessionOperations.deleteSession(readingSession);
                } else {
                    updateParseReadingSession(readingSession);
                }
            }
        }

        ParseObject.saveAllInBackground(readingSessionsToSend);
    }

    void updateParseReadingSession(final ReadingSession readingSession) {
        queryForReadingSession(readingSession, new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (list != null && list.size() > 0) {
                    ParseObject readingSessionToUpdate = list.get(0);
                    copyReadingSessionValues(readingSessionToUpdate, readingSession);
                    readingSessionToUpdate.saveEventually();
                }
            }
        });
    }

    private void copyReadingSessionValues(ParseObject readingSessionToUpdate, ReadingSession readingSession) {
        readingSessionToUpdate.put(READLIST_ID, readingSession.getId());
        readingSessionToUpdate.put(DatabaseHelper.READING_SESSION_BOOK_ID, readingSession.getBookId());
        readingSessionToUpdate.put(DatabaseHelper.READING_SESSION_DATE, readingSession.getDate());
        readingSessionToUpdate.put(DatabaseHelper.READING_SESSION_LENGTH, readingSession.getLengthOfTime());
    }

    void deleteParseReadingSession(ReadingSession readingSession) {
        queryForReadingSession(readingSession, new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                ParseObject readingSessionToDelete = list.get(0);
                readingSessionToDelete.deleteEventually();
            }
        });
    }

    private void queryForReadingSession(ReadingSession readingSession, FindCallback<ParseObject> callback) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(TYPE_READING_SESSION);
        query.whereEqualTo(Utils.USER_NAME, userName);
        query.whereEqualTo(READLIST_ID, readingSession.getId());
        query.findInBackground(callback);
    }

    private ReadingSession parseReadingSessionToReadingSession(ParseObject parseReadingSession) {
        return new ReadingSession(parseReadingSession.getInt(READLIST_ID),
                parseReadingSession.getInt(DatabaseHelper.READING_SESSION_BOOK_ID),
                parseReadingSession.getString(DatabaseHelper.READING_SESSION_DATE),
                parseReadingSession.getInt(DatabaseHelper.READING_SESSION_LENGTH), 0);
    }

    ParseObject toParseReadingSession(ReadingSession readingSession) {
        ParseObject parseReadingSession = new ParseObject(TYPE_READING_SESSION);

        parseReadingSession.put(Utils.USER_NAME, userName);
        parseReadingSession.put(READLIST_ID, readingSession.getId());
        parseReadingSession.put(DatabaseHelper.READING_SESSION_BOOK_ID, readingSession.getBookId());
        parseReadingSession.put(DatabaseHelper.READING_SESSION_DATE, readingSession.getDate());
        parseReadingSession.put(DatabaseHelper.READING_SESSION_LENGTH, readingSession.getLengthOfTime());

        return parseReadingSession;
    }
}
