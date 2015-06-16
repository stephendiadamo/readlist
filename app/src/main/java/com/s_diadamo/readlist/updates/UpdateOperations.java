package com.s_diadamo.readlist.updates;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.s_diadamo.readlist.DatabaseHelper;

import java.util.ArrayList;

public class UpdateOperations {

    private String[] UPDATE_TABLE_COLUMNS = {
            DatabaseHelper.KEY_ID,
            DatabaseHelper.UPDATE_BOOK_ID,
            DatabaseHelper.UPDATE_DATE,
            DatabaseHelper.UPDATE_PAGES
    };

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private Context context;

    public UpdateOperations(Context context) {
        dbHelper = new DatabaseHelper(context);
        this.context = context;
    }

    public void addUpdate(Update update) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.UPDATE_BOOK_ID, update.getBookId());
        values.put(DatabaseHelper.UPDATE_DATE, update.getDate());
        values.put(DatabaseHelper.UPDATE_PAGES, update.getPages());

        db.insert(DatabaseHelper.TABLE_UPDATES, null, values);
        db.close();
    }

    public ArrayList<Update> getAllUpdates() {
        db = dbHelper.getReadableDatabase();
        ArrayList<Update> updates = new ArrayList<Update>();
        String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_UPDATES;

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Update update = parseUpdate(cursor);
                updates.add(update);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return updates;
    }

    public int getAllTimePagesRead() {
        db = dbHelper.getReadableDatabase();
        int pages = 0;
        String query = String.format("SELECT SUM(%s) FROM %s",
                DatabaseHelper.UPDATE_PAGES,
                DatabaseHelper.TABLE_UPDATES);

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            pages = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return pages;
    }


    private Update parseUpdate(Cursor cursor) {
        Update update = new Update(
                Integer.parseInt(cursor.getString(0)),
                cursor.getInt(1),
                cursor.getString(2),
                cursor.getInt(3)
        );
        return update;
    }


}
