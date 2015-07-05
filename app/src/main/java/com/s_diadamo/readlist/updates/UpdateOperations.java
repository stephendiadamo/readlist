package com.s_diadamo.readlist.updates;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.s_diadamo.readlist.DatabaseHelper;
import com.s_diadamo.readlist.Utils;

import java.util.ArrayList;

public class UpdateOperations {

    private final DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public UpdateOperations(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void addUpdate(Update update) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.UPDATE_BOOK_ID, update.getBookId());
        values.put(DatabaseHelper.UPDATE_DATE, update.getDate());
        values.put(DatabaseHelper.UPDATE_PAGES, update.getPages());

        long id = db.insert(DatabaseHelper.TABLE_UPDATES, null, values);
        update.setId((int) id);
        db.close();
    }

    public int getAllTimePagesRead() {
        db = dbHelper.getReadableDatabase();
        int numPages = 0;
        String query = String.format("SELECT SUM(%s) FROM %s",
                DatabaseHelper.UPDATE_PAGES,
                DatabaseHelper.TABLE_UPDATES);

        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            numPages = cursor.getInt(0);
            cursor.close();
        }
        db.close();
        return numPages;
    }

    public int getNumberOfUpdates() {
        db = dbHelper.getReadableDatabase();
        int numUpdates = 0;
        String query = String.format("SELECT COUNT(*) FROM %s",
                DatabaseHelper.TABLE_UPDATES);

        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            numUpdates = cursor.getInt(0);
            cursor.close();
        }

        db.close();
        return numUpdates;
    }

    private int getNumberOfUpdatesBetweenDates(String start, String end) {
        int numUpdates = 0;
        db = dbHelper.getReadableDatabase();
        String query = String.format("SELECT COUNT(*) FROM %s WHERE %s BETWEEN '%s' AND '%s'",
                DatabaseHelper.TABLE_UPDATES,
                DatabaseHelper.UPDATE_DATE,
                start,
                end);
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            numUpdates = cursor.getInt(0);
            cursor.close();
        }

        db.close();
        return numUpdates;
    }

    public int getNumberOfPagesReadBetweenDates(String start, String end) {
        int numPagesRead = 0;
        db = dbHelper.getReadableDatabase();
        String query = String.format("SELECT SUM(%s) FROM %s WHERE %s BETWEEN '%s' AND '%s'",
                DatabaseHelper.UPDATE_PAGES,
                DatabaseHelper.TABLE_UPDATES,
                DatabaseHelper.UPDATE_DATE,
                start,
                end);
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            numPagesRead = cursor.getInt(0);
            cursor.close();
        }

        db.close();
        return numPagesRead;
    }

    public int getNumberOfUpdatesThisMonth() {
        String month = Utils.getCurrentMonth();
        String year = Utils.getCurrentYear();
        if (!month.isEmpty() && !year.isEmpty()) {
            String start = String.format("%s-%s-01 00:00:00", year, month);
            String end = String.format("%s-%s-31 23:59:59", year, month);
            return getNumberOfUpdatesBetweenDates(start, end);
        }
        return 0;
    }

    public int getNumberOfPagesReadThisMonth() {
        String month = Utils.getCurrentMonth();
        String year = Utils.getCurrentYear();
        if (!month.isEmpty() && !year.isEmpty()) {
            String start = String.format("%s-%s-01 00:00:00", year, month);
            String end = String.format("%s-%s-31 23:59:59", year, month);
            return getNumberOfPagesReadBetweenDates(start, end);
        }
        return 0;
    }

    public int getNumberOfUpdatesThisYear() {
        String year = Utils.getCurrentYear();
        if (!year.isEmpty()) {
            String start = String.format("%s-01-01 00:00:00", year);
            String end = String.format("%s-12-31 23:59:59", year);
            return getNumberOfUpdatesBetweenDates(start, end);
        }
        return 0;
    }

    public int getNumberOfPagesThisYear() {
        String year = Utils.getCurrentYear();
        if (!year.isEmpty()) {
            String start = String.format("%s-01-01 00:00:00", year);
            String end = String.format("%s-12-31 23:59:59", year);
            return getNumberOfPagesReadBetweenDates(start, end);
        }
        return 0;
    }

    private int calculateAverageWithQuery(Cursor cursor) {
        int updatesInWeek = 0;
        int numWeeks = 0;
        if (cursor != null && cursor.moveToFirst()) {
            do {
                updatesInWeek += cursor.getInt(1);
                numWeeks++;
            } while (cursor.moveToNext());
            cursor.close();
        }
        if (numWeeks != 0) {
            return updatesInWeek / numWeeks;
        }
        return 0;
    }

    public int getAverageWeeklyUpdates() {
        db = dbHelper.getReadableDatabase();
        String query = String.format(
                "SELECT strftime('%%Y-%%W', %s), count(*) FROM %s " +
                        "WHERE %s BETWEEN '2000-01-01 00:00:00' AND '2200-01-01 00:00:00' " +
                        "GROUP BY strftime('%%Y-%%W', %s);",
                DatabaseHelper.UPDATE_DATE,
                DatabaseHelper.TABLE_UPDATES,
                DatabaseHelper.UPDATE_DATE,
                DatabaseHelper.UPDATE_DATE);

        Cursor cursor = db.rawQuery(query, null);
        return calculateAverageWithQuery(cursor);
    }

    public int getAverageWeeklyPages() {
        db = dbHelper.getReadableDatabase();
        String query = String.format(
                "SELECT strftime('%%Y-%%W', %s), sum(%s) FROM %s " +
                        "WHERE %s BETWEEN '2000-01-01 00:00:00' AND '2200-01-01 00:00:00' " +
                        "GROUP BY strftime('%%Y-%%W', %s);",
                DatabaseHelper.UPDATE_DATE,
                DatabaseHelper.UPDATE_PAGES,
                DatabaseHelper.TABLE_UPDATES,
                DatabaseHelper.UPDATE_DATE,
                DatabaseHelper.UPDATE_DATE);

        Cursor cursor = db.rawQuery(query, null);
        return calculateAverageWithQuery(cursor);
    }

    private Update parseUpdate(Cursor cursor) {
        return new Update(
                cursor.getInt(0),
                cursor.getInt(1),
                cursor.getString(2),
                cursor.getInt(3)
        );
    }

    public void resetStatistics() {
        db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_UPDATES, null, null);
    }
}
