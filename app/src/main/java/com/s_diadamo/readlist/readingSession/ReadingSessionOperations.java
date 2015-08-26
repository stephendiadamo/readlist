package com.s_diadamo.readlist.readingSession;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.s_diadamo.readlist.database.DatabaseHelper;

import java.util.ArrayList;

public class ReadingSessionOperations {
    private final DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public ReadingSessionOperations(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void addReadingSession(ReadingSession readingSession) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.READING_SESSION_BOOK_ID, readingSession.getBookId());
        values.put(DatabaseHelper.READING_SESSION_DATE, readingSession.getDate());
        values.put(DatabaseHelper.READING_SESSION_LENGTH, readingSession.getLengthOfTime());
        values.put(DatabaseHelper.IS_DELETED, false);

        long id = db.insert(DatabaseHelper.TABLE_READING_SESSIONS, null, values);
        readingSession.setId(id);
        db.close();
    }

    public ArrayList<ReadingSession> getReadingSessions() {
        db = dbHelper.getReadableDatabase();
        ArrayList<ReadingSession> sessions = new ArrayList<>();
        String query = String.format("SELECT * FROM %s", DatabaseHelper.TABLE_READING_SESSIONS);

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                ReadingSession session = parseReadingSession(cursor);
                sessions.add(session);
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();
        return sessions;
    }

    public ArrayList<ReadingSession> getReadingSessionsForBook(int bookId) {
        db = dbHelper.getReadableDatabase();
        ArrayList<ReadingSession> sessions = new ArrayList<>();
        String query = String.format("SELECT * FROM %s WHERE %s=%d AND %s=0",
                DatabaseHelper.TABLE_READING_SESSIONS,
                DatabaseHelper.READING_SESSION_BOOK_ID,
                bookId,
                DatabaseHelper.IS_DELETED);

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                ReadingSession session = parseReadingSession(cursor);
                sessions.add(session);
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();
        return sessions;
    }

    public void deleteSession(ReadingSession readingSession) {
        db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_READING_SESSIONS, DatabaseHelper.KEY_ID + "=?",
                new String[]{String.valueOf(readingSession.getId())});
        db.close();
    }

    private ReadingSession parseReadingSession(Cursor cursor) {
        return new ReadingSession(
                cursor.getInt(0),
                cursor.getInt(1),
                cursor.getString(2),
                cursor.getInt(3),
                cursor.getInt(4));
    }
}
