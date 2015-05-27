package com.s_diadamo.readlist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "readList";

    // Tables
    private static final String TABLE_BOOKS = "books";
    private static final String TABLE_SHELVES = "shelves";

    // Common columns
    private static final String KEY_ID = "id";

    // Books table columns
    private static final String BOOK_TITLE = "title";
    private static final String BOOK_SHELF = "shelf";
    private static final String BOOK_DATE_ADDED = "date_added";
    private static final String BOOK_NUM_PAGES = "num_pages";
    private static final String BOOK_CURRENT_PAGE = "current_page";
    private static final String BOOK_TILE_COLOR = "tile_color";
    private static final String BOOK_COMPLETE = "complete";
    private static final String BOOK_COVER_PICTURE_URL = "cover_picture_url";

    // Shelves table columns
    private static final String SHELF_NAME = "name";
    private static final String SHELF_COLOR = "color";


    private static final String CREATE_BOOKS_TABLE = "CREATE TABLE " + TABLE_BOOKS +
            "(" +
            KEY_ID + " INTEGER PRIMARY KEY, " +
            BOOK_TITLE + " TEXT, " +
            BOOK_SHELF + " INTEGER, " +
            BOOK_DATE_ADDED + " TEXT, " +
            BOOK_NUM_PAGES + " INTEGER, " +
            BOOK_CURRENT_PAGE + " INTEGER, " +
            BOOK_TILE_COLOR + " TEXT, " +
            BOOK_COMPLETE + " INTEGER, " +
            BOOK_COVER_PICTURE_URL + " TEXT" +
            ")";

    private static final String CREATE_SHELVES_TABLE = "CREATE TABLE " + TABLE_SHELVES +
            "(" +
            KEY_ID + " INTEGER PRIMARY KEY, " +
            SHELF_NAME + " TEXT, " +
            SHELF_COLOR + " TEXT" +
            ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_BOOKS_TABLE);
        db.execSQL(CREATE_SHELVES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SHELVES);
        onCreate(db);
    }

    public void addBook(Book book) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(BOOK_TITLE, book.getTitle());
        values.put(BOOK_SHELF, book.getShelf());
        values.put(BOOK_DATE_ADDED, book.getDateAdded());
        values.put(BOOK_NUM_PAGES, book.getNumPages());
        values.put(BOOK_CURRENT_PAGE, book.getCurrentPage());
        values.put(BOOK_TILE_COLOR, book.getTileColor());
        values.put(BOOK_COMPLETE, book.getComplete());
        values.put(BOOK_COVER_PICTURE_URL, book.getCoverPictureURL());

        db.insert(TABLE_BOOKS, null, values);
        db.close();
    }

    public Book getBook(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_BOOKS,
                new String[]{
                        KEY_ID,
                        BOOK_TITLE,
                        BOOK_SHELF,
                        BOOK_DATE_ADDED,
                        BOOK_NUM_PAGES,
                        BOOK_CURRENT_PAGE,
                        BOOK_TILE_COLOR,
                        BOOK_COMPLETE,
                        BOOK_COVER_PICTURE_URL
                }, KEY_ID + "=?", new String[]{String.valueOf(id)},
                null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            Book book = new Book(
                    Integer.parseInt(cursor.getString(0)),
                    cursor.getString(1),
                    Integer.parseInt(cursor.getString(2)),
                    cursor.getString(3),
                    Integer.parseInt(cursor.getString(4)),
                    Integer.parseInt(cursor.getString(5)),
                    cursor.getString(6),
                    Integer.parseInt(cursor.getString(7)),
                    cursor.getString(8));
            return book;
        }
        return null;
    }

    public List<Book> getAllBooks() {
        SQLiteDatabase db = this.getWritableDatabase();

        List<Book> books = new ArrayList<Book>();
        String selectQuery = "SELECT * FROM " + TABLE_BOOKS;

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Book book = new Book(
                        Integer.parseInt(cursor.getString(0)),
                        cursor.getString(1),
                        Integer.parseInt(cursor.getString(2)),
                        cursor.getString(3),
                        Integer.parseInt(cursor.getString(4)),
                        Integer.parseInt(cursor.getString(5)),
                        cursor.getString(6),
                        Integer.parseInt(cursor.getString(7)),
                        cursor.getString(8));
                books.add(book);
            } while (cursor.moveToNext());
        }
        return books;
    }

    public int getBooksCount() {
        SQLiteDatabase db = this.getReadableDatabase();

        String countQuery = "SELECT * FROM " + TABLE_BOOKS;
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        return cursor.getCount();
    }

    public int updateBook(Book book) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(BOOK_TITLE, book.getTitle());
        values.put(BOOK_SHELF, book.getShelf());
        values.put(BOOK_DATE_ADDED, book.getDateAdded());
        values.put(BOOK_NUM_PAGES, book.getNumPages());
        values.put(BOOK_CURRENT_PAGE, book.getCurrentPage());
        values.put(BOOK_TILE_COLOR, book.getTileColor());
        values.put(BOOK_COVER_PICTURE_URL, book.getCoverPictureURL());

        return db.update(TABLE_BOOKS, values, KEY_ID + "=?",
                new String[]{String.valueOf(book.getID())});
    }

    public void deleteBook(Book book) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BOOKS, KEY_ID + "=?",
                new String[]{String.valueOf(book.getID())});
        db.close();
    }

    public void addShelf(Shelf shelf) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(SHELF_NAME, shelf.getName());
        values.put(SHELF_COLOR, shelf.getColor());

        db.insert(TABLE_SHELVES, null, values);
        db.close();
    }

    public Shelf getShelf(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_SHELVES,
                new String[]{
                        KEY_ID,
                        SHELF_NAME,
                        SHELF_COLOR
                }, KEY_ID + "=?", new String[]{String.valueOf(id)},
                null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            Shelf shelf = new Shelf(
                    Integer.parseInt(cursor.getString(0)),
                    cursor.getString(1),
                    cursor.getString(2));
            return shelf;
        }
        return null;
    }

    public List<Shelf> getAllShelves() {
        SQLiteDatabase db = this.getReadableDatabase();

        List<Shelf> shelves = new ArrayList<Shelf>();
        String selectQuery = "SELECT * FROM " + TABLE_SHELVES;

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Shelf shelf = new Shelf(
                        Integer.parseInt(cursor.getString(0)),
                        cursor.getString(1),
                        cursor.getString(2));
                shelves.add(shelf);
            } while (cursor.moveToNext());
        }
        return shelves;
    }

    public int getShelvesCount() {
        SQLiteDatabase db = this.getReadableDatabase();

        String countQuery = "SELECT * FROM " + TABLE_SHELVES;
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        return cursor.getCount();
    }

    public int updateShelf(Shelf shelf) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(SHELF_NAME, shelf.getName());
        values.put(SHELF_COLOR, shelf.getColor());

        return db.update(TABLE_SHELVES, values, KEY_ID + "=?",
                new String[]{String.valueOf(shelf.getID())});
    }

    public void deleteShelf(Shelf shelf) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SHELVES, KEY_ID + "=?",
                new String[]{String.valueOf(shelf.getID())});
        db.close();
    }

    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}
