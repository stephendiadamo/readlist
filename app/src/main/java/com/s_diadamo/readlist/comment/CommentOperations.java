package com.s_diadamo.readlist.comment;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.s_diadamo.readlist.database.DatabaseHelper;

import java.util.ArrayList;

public class CommentOperations {
    private final DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public CommentOperations(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void addComment(Comment comment) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.COMMENT_BOOK_ID, comment.getBookId());
        values.put(DatabaseHelper.COMMENT_COMMENT, comment.getComment());
        values.put(DatabaseHelper.COMMENT_DATE_ADDED, comment.getDateAdded());
        values.put(DatabaseHelper.IS_DELETED, comment.isDeleted());

        long id = db.insert(DatabaseHelper.TABLE_COMMENTS, null, values);
        comment.setId((int) id);
        db.close();
    }

    public ArrayList<Comment> getComments() {
        db = dbHelper.getReadableDatabase();
        ArrayList<Comment> comments = new ArrayList<>();
        String query = String.format("SELECT * FROM %s", DatabaseHelper.TABLE_COMMENTS);

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                Comment comment = parseComment(cursor);
                comments.add(comment);
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();
        return comments;
    }

    public ArrayList<Comment> getCommentsForBook(int bookId) {
        db = dbHelper.getReadableDatabase();
        ArrayList<Comment> comments = new ArrayList<>();
        String query = String.format("SELECT * FROM %s WHERE %s=%d AND %s=0",
                DatabaseHelper.TABLE_COMMENTS,
                DatabaseHelper.COMMENT_BOOK_ID,
                bookId,
                DatabaseHelper.IS_DELETED);

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                Comment comment = parseComment(cursor);
                comments.add(comment);
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();
        return comments;
    }

    public void updateComment(Comment comment) {
        db = dbHelper.getReadableDatabase();
        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.COMMENT_BOOK_ID, comment.getBookId());
        values.put(DatabaseHelper.COMMENT_COMMENT, comment.getComment());
        values.put(DatabaseHelper.COMMENT_DATE_ADDED, comment.getDateAdded());
        values.put(DatabaseHelper.IS_DELETED, comment.isDeleted());

        db.update(DatabaseHelper.TABLE_COMMENTS, values, DatabaseHelper.KEY_ID + "=?",
                new String[]{String.valueOf(comment.getId())});
        db.close();
    }

    public void deleteComment(Comment comment) {
        db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_COMMENTS, DatabaseHelper.KEY_ID + "=?",
                new String[]{String.valueOf(comment.getId())});
        db.close();
    }

    private Comment parseComment(Cursor cursor) {
        return new Comment(cursor.getInt(0),
                cursor.getInt(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getInt(4));
    }
}
