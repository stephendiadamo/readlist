package com.s_diadamo.readlist.shelf;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.s_diadamo.readlist.DatabaseHelper;
import com.s_diadamo.readlist.book.Book;
import com.s_diadamo.readlist.book.BookOperations;

import java.util.ArrayList;
import java.util.List;

public class ShelfOperations {
    private String[] SHELF_TABLE_COLUMNS = {
            DatabaseHelper.KEY_ID,
            DatabaseHelper.SHELF_NAME,
            DatabaseHelper.SHELF_COLOR
    };

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private Context context;

    public ShelfOperations(Context context) {
        dbHelper = new DatabaseHelper(context);
        this.context = context;
    }

    public Shelf addShelf(Shelf shelf) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.SHELF_NAME, shelf.getName());
        values.put(DatabaseHelper.SHELF_COLOR, shelf.getColour());

        long shelfID = db.insert(DatabaseHelper.TABLE_SHELVES, null, values);
        db.close();
        return getShelf(shelfID);
    }

    public Shelf getShelf(long id) {
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_SHELVES,
                SHELF_TABLE_COLUMNS,
                DatabaseHelper.KEY_ID + "=?", new String[]{String.valueOf(id)},
                null, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            Shelf shelf = parseShelf(cursor);
            db.close();
            return shelf;
        }
        db.close();
        return null;
    }

    public ArrayList<Shelf> getAllShelves() {
        db = dbHelper.getReadableDatabase();
        ArrayList<Shelf> shelves = new ArrayList<Shelf>();
        String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_SHELVES;

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Shelf shelf = parseShelf(cursor);
                shelves.add(shelf);
            } while (cursor.moveToNext());
        }
        db.close();
        return shelves;
    }

    public ArrayList<Book> getAllBooksWithShelf() {
        db = dbHelper.getReadableDatabase();
        ArrayList<Book> books = new ArrayList<Book>();
        String query = String.format("SELECT * FROM %s s INNER JOIN %s b ON s.%s=b.%s",
                DatabaseHelper.TABLE_SHELVES,
                DatabaseHelper.TABLE_BOOKS,
                DatabaseHelper.KEY_ID,
                DatabaseHelper.BOOK_SHELF);

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Shelf shelf = parseShelf(cursor);
                Book book = parseBookAfterJoin(cursor);
                book.setColour(shelf.getColour());
                books.add(book);
            } while (cursor.moveToNext());
        }
        db.close();
        return books;
    }

    public ArrayList<Book> getBooksWithShelf(long id) {
        db = dbHelper.getReadableDatabase();
        ArrayList<Book> books = new ArrayList<Book>();
        String query = String.format("SELECT * FROM %s s INNER JOIN %s b ON s.%s=b.%s WHERE b.%s=%s",
                DatabaseHelper.TABLE_SHELVES,
                DatabaseHelper.TABLE_BOOKS,
                DatabaseHelper.KEY_ID,
                DatabaseHelper.BOOK_SHELF,
                DatabaseHelper.BOOK_SHELF,
                String.valueOf(id));

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Shelf shelf = parseShelf(cursor);
                Book book = parseBookAfterJoin(cursor);
                book.setColour(shelf.getColour());
                books.add(book);
            } while (cursor.moveToNext());
        }
        db.close();
        return books;
    }


    public int updateShelf(Shelf shelf) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.SHELF_NAME, shelf.getName());
        values.put(DatabaseHelper.SHELF_COLOR, shelf.getColour());

        int updateInt = db.update(DatabaseHelper.TABLE_SHELVES, values, DatabaseHelper.KEY_ID + "=?",
                new String[]{String.valueOf(shelf.getId())});
        db.close();
        return updateInt;
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
        Shelf shelf = new Shelf(
                Integer.parseInt(cursor.getString(0)),
                cursor.getString(1),
                Integer.parseInt(cursor.getString(2)));
        return shelf;
    }

    private Book parseBookAfterJoin(Cursor cursor) {
        Book book = new Book(
                Integer.parseInt(cursor.getString(3)),
                cursor.getString(4),
                cursor.getString(5),
                Integer.parseInt(cursor.getString(6)),
                cursor.getString(7),
                Integer.parseInt(cursor.getString(8)),
                Integer.parseInt(cursor.getString(9)),
                Integer.parseInt(cursor.getString(10)),
                cursor.getString(11),
                cursor.getString(12));
        return book;
    }

}
