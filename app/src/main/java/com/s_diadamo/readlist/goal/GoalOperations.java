package com.s_diadamo.readlist.goal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.s_diadamo.readlist.DatabaseHelper;

import java.util.ArrayList;

public class GoalOperations {

    private final DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public GoalOperations(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void addGoal(Goal goal) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.GOAL_TYPE, goal.getType());
        values.put(DatabaseHelper.GOAL_AMOUNT, goal.getAmount());
        values.put(DatabaseHelper.GOAL_START_DATE, goal.getStartDate());
        values.put(DatabaseHelper.GOAL_END_DATE, goal.getEndDate());
        values.put(DatabaseHelper.GOAL_IS_COMPLETE, goal.isComplete());

        long id = db.insert(DatabaseHelper.TABLE_GOALS, null, values);
        goal.setId((int) id);
        db.close();
    }

    public ArrayList<Goal> getGoals() {
        db = dbHelper.getReadableDatabase();
        ArrayList<Goal> goals = new ArrayList<>();
        String query = "SELECT * FROM " + DatabaseHelper.TABLE_GOALS;

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                Goal goal = parseGoal(cursor);
                goals.add(goal);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return goals;
    }

    public ArrayList<Goal> getBookGoals() {
        return getGoalsByType(Goal.BOOK_GOAL);
    }

    public ArrayList<Goal> getPageGoals() {
        return getGoalsByType(Goal.PAGE_GOAL);
    }

    private ArrayList<Goal> getGoalsByType(int type) {
        db = dbHelper.getReadableDatabase();
        ArrayList<Goal> goals = new ArrayList<>();

        String query = String.format("SELECT * FROM %s WHERE %s=%s",
                DatabaseHelper.TABLE_GOALS,
                DatabaseHelper.GOAL_TYPE,
                String.valueOf(type)
        );

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                Goal goal = parseGoal(cursor);
                goals.add(goal);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return goals;
    }

    public void updateGoal(Goal goal) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.GOAL_TYPE, goal.getType());
        values.put(DatabaseHelper.GOAL_AMOUNT, goal.getAmount());
        values.put(DatabaseHelper.GOAL_START_DATE, goal.getStartDate());
        values.put(DatabaseHelper.GOAL_END_DATE, goal.getEndDate());
        values.put(DatabaseHelper.GOAL_IS_COMPLETE, goal.isComplete());

        db.update(DatabaseHelper.TABLE_GOALS, values, DatabaseHelper.KEY_ID + "=?",
                new String[]{String.valueOf(goal.getId())});
        db.close();
    }

    public void deleteGoal(Goal goal) {
        db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_GOALS, DatabaseHelper.KEY_ID + "=?",
                new String[]{String.valueOf(goal.getId())});
        db.close();
    }

    private Goal parseGoal(Cursor cursor) {
        return new Goal(
                cursor.getInt(0),
                cursor.getInt(1),
                cursor.getInt(2),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getInt(5)
        );
    }
}
