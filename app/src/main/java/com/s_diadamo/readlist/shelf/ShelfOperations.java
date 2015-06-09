package com.s_diadamo.readlist.shelf;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.s_diadamo.readlist.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by s-diadamo on 15-05-27.
 */
public class ShelfOperations {
    private String[] SHELF_TABLE_COLUMNS = {
            DatabaseHelper.KEY_ID,
            DatabaseHelper.SHELF_NAME,
            DatabaseHelper.SHELF_COLOR
    };

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public ShelfOperations(Context context) {
        dbHelper = new DatabaseHelper(context);
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
                new String[]{
                        DatabaseHelper.KEY_ID,
                        DatabaseHelper.SHELF_NAME,
                        DatabaseHelper.SHELF_COLOR
                }, DatabaseHelper.KEY_ID + "=?", new String[]{String.valueOf(id)},
                null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            Shelf shelf = parseShelf(cursor);
            db.close();
            return shelf;
        }
        db.close();
        return null;
    }

    public List<Shelf> getAllShelves() {
        db = dbHelper.getReadableDatabase();
        List<Shelf> shelves = new ArrayList<Shelf>();
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

    public int getShelvesCount() {
        db = dbHelper.getReadableDatabase();
        String countQuery = "SELECT * FROM " + DatabaseHelper.TABLE_SHELVES;
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
        db.close();
        return cursor.getCount();
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
        db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_SHELVES, DatabaseHelper.KEY_ID + "=?",
                new String[]{String.valueOf(shelf.getId())});
        db.close();
    }

    private Shelf parseShelf(Cursor cursor) {
        Shelf shelf = new Shelf(
                Integer.parseInt(cursor.getString(0)),
                cursor.getString(1),
                cursor.getString(2));
        return shelf;
    }
}
