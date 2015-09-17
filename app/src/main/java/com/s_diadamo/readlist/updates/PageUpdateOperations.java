package com.s_diadamo.readlist.updates;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.s_diadamo.readlist.database.DatabaseHelper;
import com.s_diadamo.readlist.general.Utils;

import java.util.ArrayList;

public class PageUpdateOperations {

    private final DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public PageUpdateOperations(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void addPageUpdate(PageUpdate pageUpdate) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.PAGE_UPDATE_BOOK_ID, pageUpdate.getBookId());
        values.put(DatabaseHelper.PAGE_UPDATE_DATE, pageUpdate.getDate());
        values.put(DatabaseHelper.PAGE_UPDATE_PAGES, pageUpdate.getPages());
        values.put(DatabaseHelper.IS_DELETED, pageUpdate.isDeleted());

        long id = db.insert(DatabaseHelper.TABLE_PAGE_UPDATES, null, values);
        pageUpdate.setId((int) id);
        db.close();
    }

    public ArrayList<PageUpdate> getAllPageUpdates() {
        db = dbHelper.getReadableDatabase();
        ArrayList<PageUpdate> pageUpdates = new ArrayList<>();
        String query = String.format("SELECT * FROM %s", DatabaseHelper.TABLE_PAGE_UPDATES);
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                PageUpdate pageUpdate = parsePageUpdate(cursor);
                pageUpdates.add(pageUpdate);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return pageUpdates;
    }

    public ArrayList<PageUpdate> getAllValidPageUpdates() {
        db = dbHelper.getReadableDatabase();
        ArrayList<PageUpdate> pageUpdates = new ArrayList<>();
        String query = String.format("SELECT * FROM %s WHERE %s=0",
                DatabaseHelper.TABLE_PAGE_UPDATES,
                DatabaseHelper.IS_DELETED);
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                PageUpdate pageUpdate = parsePageUpdate(cursor);
                pageUpdates.add(pageUpdate);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return pageUpdates;
    }

    public ArrayList<PageUpdate> getPageUpdatesForBook(int bookId) {
        db = dbHelper.getReadableDatabase();
        ArrayList<PageUpdate> pageUpdates = new ArrayList<>();
        String query = String.format("SELECT * FROM %s WHERE %s=0 AND %s=%d",
                DatabaseHelper.TABLE_PAGE_UPDATES,
                DatabaseHelper.IS_DELETED,
                DatabaseHelper.PAGE_UPDATE_BOOK_ID,
                bookId);
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                PageUpdate pageUpdate = parsePageUpdate(cursor);
                pageUpdates.add(pageUpdate);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return pageUpdates;
    }

    public int getAllTimePagesRead() {
        db = dbHelper.getReadableDatabase();
        int numPages = 0;
        String query = String.format("SELECT SUM(%s) FROM %s WHERE %s=0",
                DatabaseHelper.PAGE_UPDATE_PAGES,
                DatabaseHelper.TABLE_PAGE_UPDATES,
                DatabaseHelper.IS_DELETED);

        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            numPages = cursor.getInt(0);
            cursor.close();
        }
        db.close();
        return numPages;
    }

    public int getNumberOfPagesReadBetweenDates(String start, String end) {
        int numPagesRead = 0;
        db = dbHelper.getReadableDatabase();
        String query = String.format("SELECT SUM(%s) FROM %s WHERE (%s BETWEEN '%s' AND '%s') AND %s=0",
                DatabaseHelper.PAGE_UPDATE_PAGES,
                DatabaseHelper.TABLE_PAGE_UPDATES,
                DatabaseHelper.PAGE_UPDATE_DATE,
                start,
                end,
                DatabaseHelper.IS_DELETED);
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            numPagesRead = cursor.getInt(0);
            cursor.close();
        }

        db.close();
        return numPagesRead;
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

    public int getNumberOfPagesThisYear() {
        String year = Utils.getCurrentYear();
        if (!year.isEmpty()) {
            String start = String.format("%s-01-01 00:00:00", year);
            String end = String.format("%s-12-31 23:59:59", year);
            return getNumberOfPagesReadBetweenDates(start, end);
        }
        return 0;
    }

    public int getAverageWeeklyPages() {
        db = dbHelper.getReadableDatabase();
        String query = String.format(
                "SELECT strftime('%%Y-%%W', %s), sum(%s) FROM %s " +
                        "WHERE %s BETWEEN '2000-01-01 00:00:00' AND '2200-01-01 00:00:00' AND %s=0 " +
                        "GROUP BY strftime('%%Y-%%W', %s);",
                DatabaseHelper.PAGE_UPDATE_DATE,
                DatabaseHelper.PAGE_UPDATE_PAGES,
                DatabaseHelper.TABLE_PAGE_UPDATES,
                DatabaseHelper.PAGE_UPDATE_DATE,
                DatabaseHelper.IS_DELETED,
                DatabaseHelper.PAGE_UPDATE_DATE);

        Cursor cursor = db.rawQuery(query, null);
        return Utils.calculateAverageWithQuery(cursor);
    }

    private PageUpdate parsePageUpdate(Cursor cursor) {
        return new PageUpdate(
                cursor.getInt(0),
                cursor.getInt(1),
                cursor.getString(2),
                cursor.getInt(3),
                cursor.getInt(4)
        );
    }

    public void resetStatistics() {
        db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_PAGE_UPDATES, null, null);
    }

    public void updatePageUpdate(PageUpdate pageUpdate) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.PAGE_UPDATE_BOOK_ID, pageUpdate.getBookId());
        values.put(DatabaseHelper.PAGE_UPDATE_DATE, pageUpdate.getDate());
        values.put(DatabaseHelper.PAGE_UPDATE_PAGES, pageUpdate.getPages());
        values.put(DatabaseHelper.IS_DELETED, pageUpdate.isDeleted());

        db.update(DatabaseHelper.TABLE_PAGE_UPDATES, values, DatabaseHelper.KEY_ID + "=?",
                new String[]{String.valueOf(pageUpdate.getId())});
        db.close();
    }

    public void deletePageUpdate(PageUpdate pageUpdate) {
        db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_PAGE_UPDATES, DatabaseHelper.KEY_ID + "=?",
                new String[]{String.valueOf(pageUpdate.getId())});
        db.close();
    }
}
