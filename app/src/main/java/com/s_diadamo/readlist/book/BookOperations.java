package com.s_diadamo.readlist.book;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.s_diadamo.readlist.DatabaseHelper;
import com.s_diadamo.readlist.Utils;

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
            DatabaseHelper.BOOK_COMPLETE,
            DatabaseHelper.BOOK_COMPLETION_DATE,
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
        values.put(DatabaseHelper.BOOK_SHELF, book.getShelfId());
        values.put(DatabaseHelper.BOOK_DATE_ADDED, book.getDateAdded());
        values.put(DatabaseHelper.BOOK_NUM_PAGES, book.getNumPages());
        values.put(DatabaseHelper.BOOK_CURRENT_PAGE, book.getCurrentPage());
        values.put(DatabaseHelper.BOOK_COMPLETE, book.getComplete());
        values.put(DatabaseHelper.BOOK_COMPLETION_DATE, book.getCompletionDate());
        values.put(DatabaseHelper.BOOK_COVER_PICTURE_URL, book.getCoverPictureUrl());

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
            db.close();
            return book;
        }

        db.close();
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
        db.close();
        return books;
    }

    public int getBooksCount() {
        db = dbHelper.getReadableDatabase();
        int books = 0;
        String query = String.format("SELECT COUNT(%s) FROM %s",
                DatabaseHelper.KEY_ID,
                DatabaseHelper.TABLE_BOOKS);
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            books = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return books;
    }

    public int getNumberOfBooksRead() {
        db = dbHelper.getReadableDatabase();
        int booksRead = 0;
        String query = String.format("SELECT COUNT(%s) FROM %s WHERE %s=1",
                DatabaseHelper.KEY_ID,
                DatabaseHelper.TABLE_BOOKS,
                DatabaseHelper.BOOK_COMPLETE);
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
        String query = String.format("SELECT COUNT(*) FROM %s WHERE (%s BETWEEN '%s' AND '%s') AND %s=1",
                DatabaseHelper.TABLE_BOOKS,
                DatabaseHelper.BOOK_DATE_ADDED,
                start,
                end,
                DatabaseHelper.BOOK_COMPLETE);
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

    public int updateBook(Book book) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.BOOK_TITLE, book.getTitle());
        values.put(DatabaseHelper.BOOK_AUTHOR, book.getAuthor());
        values.put(DatabaseHelper.BOOK_SHELF, book.getShelfId());
        values.put(DatabaseHelper.BOOK_DATE_ADDED, book.getDateAdded());
        values.put(DatabaseHelper.BOOK_NUM_PAGES, book.getNumPages());
        values.put(DatabaseHelper.BOOK_CURRENT_PAGE, book.getCurrentPage());
        values.put(DatabaseHelper.BOOK_COMPLETE, book.getComplete());
        values.put(DatabaseHelper.BOOK_COMPLETION_DATE, book.getCompletionDate());
        values.put(DatabaseHelper.BOOK_COVER_PICTURE_URL, book.getCoverPictureUrl());

        int updateInt = db.update(DatabaseHelper.TABLE_BOOKS, values, DatabaseHelper.KEY_ID + "=?",
                new String[]{String.valueOf(book.getId())});
        db.close();
        return updateInt;
    }

    public void deleteBook(Book book) {
        db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_BOOKS, DatabaseHelper.KEY_ID + "=?",
                new String[]{String.valueOf(book.getId())});
        db.close();
    }

    private Book parseBook(Cursor cursor) {
        Book book = new Book(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getInt(3),
                cursor.getString(4),
                cursor.getInt(5),
                cursor.getInt(6),
                cursor.getInt(7),
                cursor.getString(8),
                cursor.getString(9));
        return book;
    }
}
