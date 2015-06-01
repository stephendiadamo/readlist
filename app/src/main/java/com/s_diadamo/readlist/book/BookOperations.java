package com.s_diadamo.readlist.book;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.s_diadamo.readlist.DatabaseHelper;

import java.util.ArrayList;

public class BookOperations {
    private String[] BOOK_TABLE_COLUMNS = {
            DatabaseHelper.KEY_ID,
            DatabaseHelper.BOOK_TITLE,
            DatabaseHelper.BOOK_AUTHOR,
            DatabaseHelper.BOOK_SHELF,
            DatabaseHelper.BOOK_DATE_ADDED,
            DatabaseHelper.BOOK_NUM_PAGES,
            DatabaseHelper.BOOK_CURRENT_PAGE,
            DatabaseHelper.BOOK_TILE_COLOR,
            DatabaseHelper.BOOK_COMPLETE,
            DatabaseHelper.BOOK_COVER_PICTURE_URL
    };
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public BookOperations(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public Book addBook(Book book) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.BOOK_TITLE, book.getTitle());
        values.put(DatabaseHelper.BOOK_AUTHOR, book.getAuthor());
        values.put(DatabaseHelper.BOOK_SHELF, book.getShelf());
        values.put(DatabaseHelper.BOOK_DATE_ADDED, book.getDateAdded());
        values.put(DatabaseHelper.BOOK_NUM_PAGES, book.getNumPages());
        values.put(DatabaseHelper.BOOK_CURRENT_PAGE, book.getCurrentPage());
        values.put(DatabaseHelper.BOOK_TILE_COLOR, book.getTileColor());
        values.put(DatabaseHelper.BOOK_COMPLETE, book.getComplete());
        values.put(DatabaseHelper.BOOK_COVER_PICTURE_URL, book.getCoverPictureURL());

        long bookID = db.insert(DatabaseHelper.TABLE_BOOKS, null, values);
        db.close();
        return getBook(bookID);
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
            return book;
        }
        return null;
    }

    public ArrayList<Book> getAllBooks() {
        db = dbHelper.getReadableDatabase();
        ArrayList<Book> books = new ArrayList<Book>();
        String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_BOOKS;

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Book book = parseBook(cursor);
                books.add(book);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return books;
    }

    public int getBooksCount() {
        db = dbHelper.getReadableDatabase();
        String countQuery = "SELECT * FROM " + DatabaseHelper.TABLE_BOOKS;
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
        return cursor.getCount();
    }

    public int updateBook(Book book) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.BOOK_TITLE, book.getTitle());
        values.put(DatabaseHelper.BOOK_AUTHOR, book.getAuthor());
        values.put(DatabaseHelper.BOOK_SHELF, book.getShelf());
        values.put(DatabaseHelper.BOOK_DATE_ADDED, book.getDateAdded());
        values.put(DatabaseHelper.BOOK_NUM_PAGES, book.getNumPages());
        values.put(DatabaseHelper.BOOK_CURRENT_PAGE, book.getCurrentPage());
        values.put(DatabaseHelper.BOOK_TILE_COLOR, book.getTileColor());
        values.put(DatabaseHelper.BOOK_COMPLETE, book.getComplete());
        values.put(DatabaseHelper.BOOK_COVER_PICTURE_URL, book.getCoverPictureURL());

        int updateInt = db.update(DatabaseHelper.TABLE_BOOKS, values, DatabaseHelper.KEY_ID + "=?",
                new String[]{String.valueOf(book.getID())});
        db.close();
        return updateInt;
    }

    public void deleteBook(Book book) {
        db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_BOOKS, DatabaseHelper.KEY_ID + "=?",
                new String[]{String.valueOf(book.getID())});
        db.close();
    }

    private Book parseBook(Cursor cursor) {
        Book book = new Book(
                Integer.parseInt(cursor.getString(0)),
                cursor.getString(1),
                cursor.getString(2),
                Integer.parseInt(cursor.getString(3)),
                cursor.getString(4),
                Integer.parseInt(cursor.getString(5)),
                Integer.parseInt(cursor.getString(6)),
                cursor.getString(7),
                Integer.parseInt(cursor.getString(8)),
                cursor.getString(9));
        return book;
    }
}