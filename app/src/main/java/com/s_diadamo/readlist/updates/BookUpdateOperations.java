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

    public int getNumberOfBooksRead() {
        db = dbHelper.getReadableDatabase();
        int booksRead = 0;
        String query = String.format("SELECT COUNT(%s) FROM %s",
                DatabaseHelper.KEY_ID,
                DatabaseHelper.TABLE_BOOK_UPDATES);
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
        String query = String.format("SELECT COUNT(*) FROM %s WHERE %s BETWEEN '%s' AND '%s'",
                DatabaseHelper.TABLE_BOOK_UPDATES,
                DatabaseHelper.BOOK_UPDATE_DATE,
                start,
                end);
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

    public void resetStatistics() {
        db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_BOOK_UPDATES, null, null);
    }

    private BookUpdate parseBookUpdate(Cursor cursor) {
        return new BookUpdate(
                cursor.getInt(0),
                cursor.getInt(1),
                cursor.getString(2));
    }
}
