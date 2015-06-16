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

    public Update getUpdate(long id) {
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_UPDATES,
                UPDATE_TABLE_COLUMNS,
                DatabaseHelper.KEY_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null, null);

        Update update = null;
        if (cursor != null) {
            cursor.moveToFirst();
            update = parseUpdate(cursor);
        }

        db.close();
        return update;
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
        db.close();
        return updates;
    }

    private Update parseUpdate(Cursor cursor) {
        Update update = new Update(
                Integer.parseInt(cursor.getString(0)),
                Integer.parseInt(cursor.getString(1)),
                cursor.getString(2),
                Integer.parseInt(cursor.getString(3))
        );
        return update;
    }


}
