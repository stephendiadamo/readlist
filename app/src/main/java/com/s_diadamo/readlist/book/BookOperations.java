package com.s_diadamo.readlist.book;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.s_diadamo.readlist.database.DatabaseHelper;

import java.util.ArrayList;

public class BookOperations {
    private final String[] BOOK_TABLE_COLUMNS = {
            DatabaseHelper.KEY_ID,
            DatabaseHelper.BOOK_TITLE,
            DatabaseHelper.BOOK_AUTHOR,
            DatabaseHelper.BOOK_SHELF,
            DatabaseHelper.BOOK_DATE_ADDED,
            DatabaseHelper.BOOK_NUM_PAGES,
            DatabaseHelper.BOOK_CURRENT_PAGE,
            DatabaseHelper.BOOK_COMPLETE,
            DatabaseHelper.BOOK_COMPLETION_DATE,
            DatabaseHelper.BOOK_COVER_PICTURE_URL,
            DatabaseHelper.IS_DELETED
    };
    private final DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public BookOperations(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void addBook(Book book) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.BOOK_TITLE, book.getTitle());
        values.put(DatabaseHelper.BOOK_AUTHOR, book.getAuthor());
        values.put(DatabaseHelper.BOOK_SHELF, book.getShelfId());
        values.put(DatabaseHelper.BOOK_DATE_ADDED, book.getDateAdded());
        values.put(DatabaseHelper.BOOK_NUM_PAGES, book.getNumPages());
        values.put(DatabaseHelper.BOOK_CURRENT_PAGE, book.getCurrentPage());
        values.put(DatabaseHelper.BOOK_COMPLETE, book.isComplete());
        values.put(DatabaseHelper.BOOK_COMPLETION_DATE, book.getCompletionDate());
        values.put(DatabaseHelper.BOOK_COVER_PICTURE_URL, book.getCoverPictureUrl());
        values.put(DatabaseHelper.IS_DELETED, book.isDeleted());

        long id = db.insert(DatabaseHelper.TABLE_BOOKS, null, values);
        book.setId((int) id);
        db.close();
    }

    public Book getBook(long id) {
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_BOOKS,
                BOOK_TABLE_COLUMNS
                , DatabaseHelper.KEY_ID + "=?", new String[]{String.valueOf(id)},
                null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            Book book = parseBook(cursor);
            cursor.close();
            db.close();
            return book;
        }

        db.close();
        return null;
    }

    public int getBooksCount() {
        db = dbHelper.getReadableDatabase();
        int books = 0;
        String query = String.format("SELECT COUNT(%s) FROM %s WHERE %s=0 ",
                DatabaseHelper.KEY_ID,
                DatabaseHelper.TABLE_BOOKS,
                DatabaseHelper.IS_DELETED);
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            books = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return books;
    }

    public ArrayList<Book> getAllBooks() {
        ArrayList<Book> books = new ArrayList<>();
        db = dbHelper.getReadableDatabase();
        String query = String.format("SELECT * FROM %s",
                DatabaseHelper.TABLE_BOOKS);

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                Book book = parseBook(cursor);
                books.add(book);
            } while (cursor.moveToNext());
            cursor.close();
        }
        cursor.close();

        db.close();
        return books;
    }

    public void updateBook(Book book) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.BOOK_TITLE, book.getTitle());
        values.put(DatabaseHelper.BOOK_AUTHOR, book.getAuthor());
        values.put(DatabaseHelper.BOOK_SHELF, book.getShelfId());
        values.put(DatabaseHelper.BOOK_DATE_ADDED, book.getDateAdded());
        values.put(DatabaseHelper.BOOK_NUM_PAGES, book.getNumPages());
        values.put(DatabaseHelper.BOOK_CURRENT_PAGE, book.getCurrentPage());
        values.put(DatabaseHelper.BOOK_COMPLETE, book.isComplete());
        values.put(DatabaseHelper.BOOK_COMPLETION_DATE, book.getCompletionDate());
        values.put(DatabaseHelper.BOOK_COVER_PICTURE_URL, book.getCoverPictureUrl());
        values.put(DatabaseHelper.IS_DELETED, book.isDeleted());

        db.update(DatabaseHelper.TABLE_BOOKS, values, DatabaseHelper.KEY_ID + "=?",
                new String[]{String.valueOf(book.getId())});
        db.close();
    }

    public void deleteBook(Book book) {
        db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_BOOKS, DatabaseHelper.KEY_ID + "=?",
                new String[]{String.valueOf(book.getId())});
        db.close();
    }

    private Book parseBook(Cursor cursor) {
        return new Book(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getInt(3),
                cursor.getString(4),
                cursor.getInt(5),
                cursor.getInt(6),
                cursor.getInt(7),
                cursor.getString(8),
                cursor.getString(9),
                cursor.getInt(10));
    }
}
