package com.s_diadamo.readlist.readingSession;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.s_diadamo.readlist.database.DatabaseHelper;
import com.s_diadamo.readlist.general.Utils;

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

    public void update(ReadingSession readingSession) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.READING_SESSION_BOOK_ID, readingSession.getBookId());
        values.put(DatabaseHelper.READING_SESSION_DATE, readingSession.getDate());
        values.put(DatabaseHelper.READING_SESSION_LENGTH, readingSession.getLengthOfTime());
        values.put(DatabaseHelper.IS_DELETED, readingSession.isDeleted());

        db.update(DatabaseHelper.TABLE_READING_SESSIONS, values, DatabaseHelper.KEY_ID + "=?",
                new String[]{String.valueOf(readingSession.getId())});
        db.close();

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

    public int getAverageWeeklyReadingSessions() {
        db = dbHelper.getReadableDatabase();
        String query = String.format(
                "SELECT strftime('%%Y-%%W', %s), count(*) FROM %s " +
                        "WHERE (%s BETWEEN '2000-01-01 00:00:00' AND '2200-01-01 00:00:00') AND %s=0 " +
                        "GROUP BY strftime('%%Y-%%W', %s);",
                DatabaseHelper.READING_SESSION_DATE,
                DatabaseHelper.TABLE_READING_SESSIONS,
                DatabaseHelper.READING_SESSION_DATE,
                DatabaseHelper.IS_DELETED,
                DatabaseHelper.READING_SESSION_DATE);

        Cursor cursor = db.rawQuery(query, null);
        return Utils.calculateAverageWithQuery(cursor);
    }

    public int getAverageWeeklyTimeSpentReading() {
        db = dbHelper.getReadableDatabase();
        String query = String.format(
                "SELECT strftime('%%Y-%%W', %s), sum(%s) FROM %s " +
                        "WHERE %s BETWEEN '2000-01-01 00:00:00' AND '2200-01-01 00:00:00' AND %s=0 " +
                        "GROUP BY strftime('%%Y-%%W', %s);",
                DatabaseHelper.READING_SESSION_DATE,
                DatabaseHelper.READING_SESSION_LENGTH,
                DatabaseHelper.TABLE_READING_SESSIONS,
                DatabaseHelper.READING_SESSION_DATE,
                DatabaseHelper.IS_DELETED,
                DatabaseHelper.READING_SESSION_DATE);

        Cursor cursor = db.rawQuery(query, null);
        return Utils.calculateAverageWithQuery(cursor);
    }

    public int getNumberOfReadingSessionsThisMonth() {
        String month = Utils.getCurrentMonth();
        String year = Utils.getCurrentYear();
        if (year != null && !year.isEmpty() && month != null && !month.isEmpty()) {
            String start = String.format("%s-%s-01 00:00:00", year, month);
            String end = String.format("%s-%s-31 23:59:59", year, month);
            return getNumberOfReadingSessionsBetweenDates(start, end);
        }
        return 0;
    }

    public int getTimeSpentReadingThisMonth() {
        String month = Utils.getCurrentMonth();
        String year = Utils.getCurrentYear();
        if (year != null && !year.isEmpty() && month != null && !month.isEmpty()) {
            String start = String.format("%s-%s-01 00:00:00", year, month);
            String end = String.format("%s-%s-31 23:59:59", year, month);
            return getTimeSpentReadingBetweenDates(start, end);
        }
        return 0;
    }

    public int getNumberOfReadingSessionsThisYear() {
        String year = Utils.getCurrentYear();
        if (year != null && !year.isEmpty()) {
            String start = String.format("%s-01-01 00:00:00", year);
            String end = String.format("%s-12-31 23:59:59", year);
            return getNumberOfReadingSessionsBetweenDates(start, end);
        }
        return 0;
    }

    public int getTimeSpentReadingThisYear() {
        String year = Utils.getCurrentYear();
        if (year != null && !year.isEmpty()) {
            String start = String.format("%s-01-01 00:00:00", year);
            String end = String.format("%s-12-31 23:59:59", year);
            return getTimeSpentReadingBetweenDates(start, end);
        }
        return 0;
    }

    public int getNumberOfReadingSessions() {
        db = dbHelper.getReadableDatabase();
        int numReadingSessions = 0;
        String query = String.format("SELECT COUNT(*) FROM %s WHERE %s=0",
                DatabaseHelper.TABLE_READING_SESSIONS,
                DatabaseHelper.IS_DELETED);

        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            numReadingSessions = cursor.getInt(0);
            cursor.close();
        }

        db.close();
        return numReadingSessions;
    }

    public int getTimeSpentReading() {
        db = dbHelper.getReadableDatabase();
        int timeSpentReading = 0;

        String query = String.format("SELECT SUM(%s) FROM %s WHERE %s=0",
                DatabaseHelper.READING_SESSION_LENGTH,
                DatabaseHelper.TABLE_READING_SESSIONS,
                DatabaseHelper.IS_DELETED);

        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            timeSpentReading = cursor.getInt(0);
            cursor.close();
        }
        db.close();
        return timeSpentReading;
    }

    public int getTimeSpentReadingForBook(int bookId) {
        db = dbHelper.getReadableDatabase();
        int timeSpentReading = 0;

        String query = String.format("SELECT SUM(%s) FROM %s WHERE %s=%d AND %s=0",
                DatabaseHelper.READING_SESSION_LENGTH,
                DatabaseHelper.TABLE_READING_SESSIONS,
                DatabaseHelper.READING_SESSION_BOOK_ID,
                bookId,
                DatabaseHelper.IS_DELETED);

        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            timeSpentReading = cursor.getInt(0);
            cursor.close();
        }
        db.close();
        return timeSpentReading;
    }

    private int getNumberOfReadingSessionsBetweenDates(String start, String end) {
        int numReadingSessions = 0;
        db = dbHelper.getReadableDatabase();
        String query = String.format("SELECT COUNT(*) FROM %s WHERE (%s BETWEEN '%s' AND '%s') AND %s=0",
                DatabaseHelper.TABLE_READING_SESSIONS,
                DatabaseHelper.READING_SESSION_DATE,
                start,
                end,
                DatabaseHelper.IS_DELETED);
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            numReadingSessions = cursor.getInt(0);
            cursor.close();
        }

        db.close();
        return numReadingSessions;
    }

    private int getTimeSpentReadingBetweenDates(String start, String end) {
        int numReadingSessions = 0;
        db = dbHelper.getReadableDatabase();
        String query = String.format("SELECT SUM(%s) FROM %s WHERE (%s BETWEEN '%s' AND '%s') AND %s=0",
                DatabaseHelper.READING_SESSION_LENGTH,
                DatabaseHelper.TABLE_READING_SESSIONS,
                DatabaseHelper.READING_SESSION_DATE,
                start,
                end,
                DatabaseHelper.IS_DELETED);
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            numReadingSessions = cursor.getInt(0);
            cursor.close();
        }

        db.close();
        return numReadingSessions;
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
