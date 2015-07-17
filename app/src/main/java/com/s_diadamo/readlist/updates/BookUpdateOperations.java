package com.s_diadamo.readlist.updates;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.s_diadamo.readlist.database.DatabaseHelper;
import com.s_diadamo.readlist.general.Utils;

import java.util.ArrayList;

public class BookUpdateOperations {

    private final DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public BookUpdateOperations(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void addBookUpdate(BookUpdate bookUpdate) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.BOOK_UPDATE_BOOK_ID, bookUpdate.getBookId());
        values.put(DatabaseHelper.BOOK_UPDATE_DATE, bookUpdate.getDate());
        values.put(DatabaseHelper.BOOK_UPDATE_IS_DELETED, bookUpdate.isDeleted());

        long id = db.insert(DatabaseHelper.TABLE_BOOK_UPDATES, null, values);
        bookUpdate.setId((int) id);
        db.close();
    }

    public ArrayList<BookUpdate> getAllBookUpdates() {
        db = dbHelper.getReadableDatabase();
        ArrayList<BookUpdate> bookUpdates = new ArrayList<>();
        String query = String.format("SELECT * FROM %s", DatabaseHelper.TABLE_BOOK_UPDATES);
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                BookUpdate bookUpdate = parseBookUpdate(cursor);
                bookUpdates.add(bookUpdate);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return bookUpdates;
    }

    public ArrayList<BookUpdate> getAllValidBookUpdates() {
        db = dbHelper.getReadableDatabase();
        ArrayList<BookUpdate> bookUpdates = new ArrayList<>();
        String query = String.format("SELECT * FROM %s WHERE %s=0",
                DatabaseHelper.TABLE_BOOK_UPDATES,
                DatabaseHelper.BOOK_IS_DELETED);
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                BookUpdate bookUpdate = parseBookUpdate(cursor);
                bookUpdates.add(bookUpdate);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return bookUpdates;
    }

    public int getNumberOfBooksRead() {
        db = dbHelper.getReadableDatabase();
        int booksRead = 0;
        String query = String.format("SELECT COUNT(%s) FROM %s WHERE %s=0",
                DatabaseHelper.KEY_ID,
                DatabaseHelper.TABLE_BOOK_UPDATES,
                DatabaseHelper.BOOK_UPDATE_IS_DELETED);
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            booksRead = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return booksRead;
    }

    public int getNumberOfBooksReadBetweenDates(String start, String end) {
        int numBooksRead = 0;
        db = dbHelper.getReadableDatabase();
        String query = String.format("SELECT COUNT(*) FROM %s WHERE (%s BETWEEN '%s' AND '%s') AND %s=0",
                DatabaseHelper.TABLE_BOOK_UPDATES,
                DatabaseHelper.BOOK_UPDATE_DATE,
                start,
                end,
                DatabaseHelper.BOOK_UPDATE_IS_DELETED);
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            numBooksRead = cursor.getInt(0);
            cursor.close();
        }

        db.close();
        return numBooksRead;
    }

    public int getNumberOfBooksReadThisMonth() {
        String month = Utils.getCurrentMonth();
        String year = Utils.getCurrentYear();
        if (!month.isEmpty() && !year.isEmpty()) {
            String start = String.format("%s-%s-01 00:00:00", year, month);
            String end = String.format("%s-%s-31 23:59:59", year, month);
            return getNumberOfBooksReadBetweenDates(start, end);
        }
        return 0;
    }

    public int getNumberOfBooksReadThisYear() {
        String year = Utils.getCurrentYear();
        if (!year.isEmpty()) {
            String start = String.format("%s-01-01 00:00:00", year);
            String end = String.format("%s-12-31 23:59:59", year);
            return getNumberOfBooksReadBetweenDates(start, end);
        }
        return 0;
    }

    public void updateBookUpdate(BookUpdate bookUpdate) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.BOOK_UPDATE_BOOK_ID, bookUpdate.getBookId());
        values.put(DatabaseHelper.BOOK_UPDATE_DATE, bookUpdate.getDate());
        values.put(DatabaseHelper.BOOK_UPDATE_IS_DELETED, bookUpdate.isDeleted());

        db.update(DatabaseHelper.TABLE_BOOK_UPDATES, values, DatabaseHelper.KEY_ID + "=?",
                new String[]{String.valueOf(bookUpdate.getId())});
        db.close();
    }

    public void resetStatistics() {
        db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_BOOK_UPDATES, null, null);
    }

    public void deleteBookUpdate(BookUpdate bookUpdate) {
        db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_BOOK_UPDATES, DatabaseHelper.KEY_ID + "=?",
                new String[]{String.valueOf(bookUpdate.getId())});
        db.close();
    }

    private BookUpdate parseBookUpdate(Cursor cursor) {
        return new BookUpdate(
                cursor.getInt(0),
                cursor.getInt(1),
                cursor.getString(2),
                cursor.getInt(3));
    }
}
