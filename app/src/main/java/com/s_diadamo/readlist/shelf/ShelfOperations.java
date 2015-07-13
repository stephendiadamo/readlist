package com.s_diadamo.readlist.shelf;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.s_diadamo.readlist.database.DatabaseHelper;
import com.s_diadamo.readlist.book.Book;
import com.s_diadamo.readlist.book.BookOperations;

import java.util.ArrayList;

public class ShelfOperations {
    private final String[] SHELF_TABLE_COLUMNS = {
            DatabaseHelper.KEY_ID,
            DatabaseHelper.SHELF_NAME,
            DatabaseHelper.SHELF_COLOR,
            DatabaseHelper.SHELF_IS_DELETED
    };

    private final DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private final Context context;

    public ShelfOperations(Context context) {
        dbHelper = new DatabaseHelper(context);
        this.context = context;
    }

    public void addShelf(Shelf shelf) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.SHELF_NAME, shelf.getName());
        values.put(DatabaseHelper.SHELF_COLOR, shelf.getColour());

        long id = db.insert(DatabaseHelper.TABLE_SHELVES, null, values);
        shelf.setId((int) id);
        db.close();
    }

    public Shelf getShelf(long id) {
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_SHELVES,
                SHELF_TABLE_COLUMNS,
                DatabaseHelper.KEY_ID + "=?", new String[]{String.valueOf(id)},
                null, null, null, null);

        Shelf shelf = null;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            shelf = parseShelf(cursor);
            cursor.close();
        }

        db.close();
        return shelf;
    }

    public ArrayList<Shelf> getAllShelves() {
        db = dbHelper.getReadableDatabase();
        ArrayList<Shelf> shelves = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_SHELVES;

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Shelf shelf = parseShelf(cursor);
                shelves.add(shelf);
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();
        return shelves;
    }

    public ArrayList<Shelf> getNonDefaultShelves() {
        db = dbHelper.getReadableDatabase();
        ArrayList<Shelf> shelves = new ArrayList<>();
        String selectQuery = String.format("SELECT * FROM %s WHERE %s!=%s AND %s=0",
                DatabaseHelper.TABLE_SHELVES,
                DatabaseHelper.KEY_ID,
                Shelf.DEFAULT_SHELF_ID,
                DatabaseHelper.SHELF_IS_DELETED);

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Shelf shelf = parseShelf(cursor);
                shelves.add(shelf);
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();
        return shelves;
    }

    public ArrayList<Book> getAllBooksWithShelf() {
        db = dbHelper.getReadableDatabase();
        ArrayList<Book> books = new ArrayList<>();
        String query = String.format("SELECT * FROM %s s INNER JOIN %s b ON s.%s=b.%s WHERE b.%s=0 ORDER BY %s",
                DatabaseHelper.TABLE_SHELVES,
                DatabaseHelper.TABLE_BOOKS,
                DatabaseHelper.KEY_ID,
                DatabaseHelper.BOOK_SHELF,
                DatabaseHelper.BOOK_IS_DELETED,
                DatabaseHelper.BOOK_COMPLETE);

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Shelf shelf = parseShelf(cursor);
                Book book = parseBookAfterJoin(cursor);
                book.setColour(shelf.getColour());
                books.add(book);
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();
        return books;
    }

    public ArrayList<Book> getBooksWithShelf(long id) {
        db = dbHelper.getReadableDatabase();
        ArrayList<Book> books = new ArrayList<>();
        String query = String.format("SELECT * FROM %s s INNER JOIN %s b ON s.%s=b.%s WHERE b.%s=%s AND b.%s=0 ORDER BY %s",
                DatabaseHelper.TABLE_SHELVES,
                DatabaseHelper.TABLE_BOOKS,
                DatabaseHelper.KEY_ID,
                DatabaseHelper.BOOK_SHELF,
                DatabaseHelper.BOOK_SHELF,
                String.valueOf(id),
                DatabaseHelper.BOOK_IS_DELETED,
                DatabaseHelper.BOOK_COMPLETE);

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Shelf shelf = parseShelf(cursor);
                Book book = parseBookAfterJoin(cursor);
                book.setColour(shelf.getColour());
                books.add(book);
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();
        return books;
    }

    public void updateShelf(Shelf shelf) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.SHELF_NAME, shelf.getName());
        values.put(DatabaseHelper.SHELF_COLOR, shelf.getColour());
        values.put(DatabaseHelper.SHELF_IS_DELETED, shelf.isDeleted());

        db.update(DatabaseHelper.TABLE_SHELVES, values, DatabaseHelper.KEY_ID + "=?",
                new String[]{String.valueOf(shelf.getId())});
        db.close();
    }

    public void deleteShelf(Shelf shelf) {
        ArrayList<Book> books = getShelf(shelf.getId()).fetchBooks(context);
        BookOperations bookOperations = new BookOperations(context);
        for (Book book : books) {
            book.setShelfId(Shelf.DEFAULT_SHELF_ID);
            bookOperations.updateBook(book);
        }
        db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_SHELVES, DatabaseHelper.KEY_ID + "=?",
                new String[]{String.valueOf(shelf.getId())});
        db.close();
    }

    private Shelf parseShelf(Cursor cursor) {
        return new Shelf(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getInt(2),
                cursor.getInt(3));
    }

    private Book parseBookAfterJoin(Cursor cursor) {
        return new Book(
                cursor.getInt(4),
                cursor.getString(5),
                cursor.getString(6),
                cursor.getInt(7),
                cursor.getString(8),
                cursor.getInt(9),
                cursor.getInt(10),
                cursor.getInt(11),
                cursor.getString(12),
                cursor.getString(13),
                cursor.getInt(14));
    }
}
