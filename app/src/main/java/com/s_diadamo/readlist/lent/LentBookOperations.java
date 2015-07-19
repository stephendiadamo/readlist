package com.s_diadamo.readlist.lent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.s_diadamo.readlist.database.DatabaseHelper;

import java.util.ArrayList;

public class LentBookOperations {
    private final DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public LentBookOperations(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void addLentBook(LentBook lentBook) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.LENT_BOOK_BOOK_ID, lentBook.getBookId());
        values.put(DatabaseHelper.LENT_BOOK_LENT_TO, lentBook.getLentTo());
        values.put(DatabaseHelper.LENT_BOOK_DATE_LENT, lentBook.getDateLent());
        values.put(DatabaseHelper.IS_DELETED, lentBook.isDeleted());

        long id = db.insert(DatabaseHelper.TABLE_LENT_BOOKS, null, values);
        lentBook.setId((int) id);
        db.close();
    }

    public ArrayList<LentBook> getLentBooks() {
        db = dbHelper.getReadableDatabase();
        ArrayList<LentBook> lentBooks = new ArrayList<>();
        String query = String.format("SELECT * FROM %s", DatabaseHelper.TABLE_LENT_BOOKS);

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                LentBook lentBook = parseLentBook(cursor);
                lentBooks.add(lentBook);
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();
        return lentBooks;
    }

    public ArrayList<LentBook> getValidLentBooks() {
        db = dbHelper.getReadableDatabase();
        ArrayList<LentBook> lentBooks = new ArrayList<>();
        String query = String.format(
                "SELECT l.%s, b.%s, b.%s, l.%s, l.%s FROM %s b INNER JOIN %s l ON b.%s=l.%s WHERE l.%s=0",
                DatabaseHelper.KEY_ID,
                DatabaseHelper.BOOK_TITLE,
                DatabaseHelper.BOOK_COVER_PICTURE_URL,
                DatabaseHelper.LENT_BOOK_LENT_TO,
                DatabaseHelper.LENT_BOOK_DATE_LENT,
                DatabaseHelper.TABLE_BOOKS,
                DatabaseHelper.TABLE_LENT_BOOKS,
                DatabaseHelper.KEY_ID,
                DatabaseHelper.LENT_BOOK_BOOK_ID,
                DatabaseHelper.IS_DELETED);

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                LentBook lentBook = parseJoinedLentBook(cursor);
                lentBooks.add(lentBook);
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();
        return lentBooks;
    }

    public void updateLentBook(LentBook lentBook) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.LENT_BOOK_BOOK_ID, lentBook.getId());
        values.put(DatabaseHelper.LENT_BOOK_LENT_TO, lentBook.getLentTo());
        values.put(DatabaseHelper.LENT_BOOK_DATE_LENT, lentBook.getDateLent());
        values.put(DatabaseHelper.IS_DELETED, lentBook.isDeleted());

        db.update(DatabaseHelper.TABLE_LENT_BOOKS, values, DatabaseHelper.KEY_ID + "=?",
                new String[]{String.valueOf(lentBook.getId())});
        db.close();
    }

    public void deleteLentBook(LentBook lentBook) {
        db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_LENT_BOOKS, DatabaseHelper.KEY_ID + "=?",
                new String[]{String.valueOf(lentBook.getId())});
        db.close();
    }

    private LentBook parseLentBook(Cursor cursor) {
        return new LentBook(
                cursor.getInt(0),
                cursor.getInt(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getInt(4));
    }

    private LentBook parseJoinedLentBook(Cursor cursor) {
        return new LentBook(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4));
    }

}
