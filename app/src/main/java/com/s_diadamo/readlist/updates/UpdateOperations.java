package com.s_diadamo.readlist.updates;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.s_diadamo.readlist.DatabaseHelper;
import com.s_diadamo.readlist.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class UpdateOperations {


    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public UpdateOperations(Context context) {
        dbHelper = new DatabaseHelper(context);
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
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Update update = parseUpdate(cursor);
                updates.add(update);
            } while (cursor.moveToNext());
            cursor.close();
        }
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
        if (cursor != null && cursor.moveToFirst()) {
            pages = cursor.getInt(0);
            cursor.close();
        }
        db.close();
        return pages;
    }

    public int getNumberOfUpdates() {
        db = dbHelper.getReadableDatabase();
        int numUpdates = 0;
        String query = String.format("SELECT COUNT(*) FROM %s",
                DatabaseHelper.TABLE_UPDATES);

        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            numUpdates = cursor.getInt(0);
            cursor.close();
        }
        db.close();
        return numUpdates;
    }

    public int getNumberOfUpdatesThisMonth() {
        int numUpdates = 0;
        db = dbHelper.getReadableDatabase();
        try {
            String stringDate = Utils.getCurrentDate();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Utils.DATE_FORMAT, Locale.CANADA);
            Date date = simpleDateFormat.parse(stringDate);
            String month = (String) android.text.format.DateFormat.format("MM", date);
            String year = (String) android.text.format.DateFormat.format("yyyy", date);
            String query = String.format("SELECT COUNT(*) FROM %s WHERE %s BETWEEN '%s-%s-%s 00:00:00' AND '%s-%s-%s 23:59:59'",
                    DatabaseHelper.TABLE_UPDATES,
                    DatabaseHelper.UPDATE_DATE,
                    year,
                    month,
                    "01",
                    year,
                    month,
                    "31");
            Cursor cursor = db.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                numUpdates = cursor.getInt(0);
                cursor.close();
            }
            db.close();
        } catch (ParseException e) {
        }

        return numUpdates;
    }

    public int getNumberOfPagesReadThisMonth() {
        return 0;
    }

    public int getNumberOfUpdatesThisYear() {
        return 0;
    }

    public int getNumberOfPagesThisYear() {
        return 0;
    }

    public int getAverageWeeklyUpdates() {
        return 0;
    }

    public int getAverageWeeklyPages() {
        return 0;
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
