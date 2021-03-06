package com.s_diadamo.readlist.book;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

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
            DatabaseHelper.BOOK_RATING,
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
        values.put(DatabaseHelper.BOOK_RATING, book.getRating());
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

        Book book = null;
        if (cursor.moveToFirst()) {
            book = parseBook(cursor);
            cursor.close();
        }

        db.close();
        return book;
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

    public ArrayList<Book> getAllValidBooks() {
        ArrayList<Book> books = new ArrayList<>();
        db = dbHelper.getReadableDatabase();
        String query = String.format("SELECT * FROM %s WHERE %s=0",
                DatabaseHelper.TABLE_BOOKS,
                DatabaseHelper.IS_DELETED);
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
        values.put(DatabaseHelper.BOOK_RATING, book.getRating());
        values.put(DatabaseHelper.IS_DELETED, book.isDeleted());

        db.update(DatabaseHelper.TABLE_BOOKS, values, DatabaseHelper.KEY_ID + "=?",
                new String[]{String.valueOf(book.getId())});
        db.close();
    }

    public void deleteBook(Book book) {
        deleteAssociatedLentBooks(book);
        deleteAssociatedComments(book);
        removeAssoicationWithReadingSessions(book);
        db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_BOOKS, DatabaseHelper.KEY_ID + "=?",
                new String[]{String.valueOf(book.getId())});
        db.close();
    }

    public void deleteAssociatedLentBooks(Book book) {
        db = dbHelper.getWritableDatabase();
        String flagLentBooksDeletedQuery = String.format("UPDATE %s SET %s=%d WHERE %s=%d",
                DatabaseHelper.TABLE_LENT_BOOKS,
                DatabaseHelper.IS_DELETED,
                1,
                DatabaseHelper.LENT_BOOK_BOOK_ID,
                book.getId());
        db.execSQL(flagLentBooksDeletedQuery);
        db.close();
    }

    private void deleteAssociatedComments(Book book) {
        db = dbHelper.getWritableDatabase();
        String flagCommentsAsDeletedQuery = String.format("UPDATE %s SET %s=%d WHERE %s=%d",
                DatabaseHelper.TABLE_COMMENTS,
                DatabaseHelper.IS_DELETED,
                1,
                DatabaseHelper.COMMENT_BOOK_ID,
                book.getId());
        db.execSQL(flagCommentsAsDeletedQuery);
        db.close();
    }

    private void removeAssoicationWithReadingSessions(Book book) {
        db = dbHelper.getWritableDatabase();
        String updateQuery = String.format("UPDATE %s SET %s=%d WHERE %s=%d",
                DatabaseHelper.TABLE_READING_SESSIONS,
                DatabaseHelper.READING_SESSION_BOOK_ID,
                -1,
                DatabaseHelper.READING_SESSION_BOOK_ID,
                book.getId());
        db.execSQL(updateQuery);
        db.close();
    }

    public boolean hasSimilarBook(Book book) {
        db = dbHelper.getReadableDatabase();
        String checkForData = String.format("SELECT * FROM %s WHERE %s=\"%s\" AND %s=\"%s\"",
                DatabaseHelper.TABLE_BOOKS,
                DatabaseHelper.BOOK_TITLE,
                book.getTitle(),
                DatabaseHelper.BOOK_AUTHOR,
                book.getAuthor());

        Cursor cursor = db.rawQuery(checkForData, null);
        boolean hasElement = cursor.moveToFirst();
        cursor.close();
        db.close();
        return hasElement;
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
                cursor.getDouble(10),
                cursor.getInt(11));
    }
}
