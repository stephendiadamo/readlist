package com.s_diadamo.readlist.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "readList";

    // Tables
    public static final String TABLE_BOOKS = "books";
    public static final String TABLE_SHELVES = "shelves";
    public static final String TABLE_PAGE_UPDATES = "page_updates";
    public static final String TABLE_BOOK_UPDATES = "book_updates";
    public static final String TABLE_GOALS = "goals";

    // Common columns
    public static final String KEY_ID = "id";

    // Books table columns
    public static final String BOOK_TITLE = "title";
    public static final String BOOK_AUTHOR = "author";
    public static final String BOOK_SHELF = "shelf";
    public static final String BOOK_DATE_ADDED = "date_added";
    public static final String BOOK_NUM_PAGES = "num_pages";
    public static final String BOOK_CURRENT_PAGE = "current_page";
    public static final String BOOK_COMPLETE = "complete";
    public static final String BOOK_COMPLETION_DATE = "completion_date";
    public static final String BOOK_COVER_PICTURE_URL = "cover_picture_url";

    // Shelves table columns
    public static final String SHELF_NAME = "name";
    public static final String SHELF_COLOR = "color";

    // Page Updates table columns
    public static final String PAGE_UPDATE_BOOK_ID = "book_id";
    public static final String PAGE_UPDATE_DATE = "date";
    public static final String PAGE_UPDATE_PAGES = "pages";

    // Book Updates table colums
    public static final String BOOK_UPDATE_BOOK_ID = "book_id";
    public static final String BOOK_UPDATE_DATE = "date";

    // Goals table columns
    public static final String GOAL_TYPE = "type";
    public static final String GOAL_AMOUNT = "amount";
    public static final String GOAL_START_DATE = "start_date";
    public static final String GOAL_END_DATE = "end_date";
    public static final String GOAL_IS_COMPLETE = "complete";

    private static final String CREATE_BOOKS_TABLE = "CREATE TABLE " + TABLE_BOOKS +
            "(" +
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            BOOK_TITLE + " TEXT, " +
            BOOK_AUTHOR + " TEXT, " +
            BOOK_SHELF + " INTEGER, " +
            BOOK_DATE_ADDED + " TEXT, " +
            BOOK_NUM_PAGES + " INTEGER, " +
            BOOK_CURRENT_PAGE + " INTEGER, " +
            BOOK_COMPLETE + " INTEGER, " +
            BOOK_COMPLETION_DATE + " TEXT, " +
            BOOK_COVER_PICTURE_URL + " TEXT" +
            ")";

    private static final String CREATE_SHELVES_TABLE = "CREATE TABLE " + TABLE_SHELVES +
            "(" +
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            SHELF_NAME + " TEXT, " +
            SHELF_COLOR + " TEXT" +
            ")";

    private static final String CREATE_PAGE_UPDATES_TABLE = "CREATE TABLE " + TABLE_PAGE_UPDATES +
            "(" +
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            PAGE_UPDATE_BOOK_ID + " INTEGER, " +
            PAGE_UPDATE_DATE + " TEXT, " +
            PAGE_UPDATE_PAGES + " INTEGER" +
            ")";

    private static final String CREATE_BOOK_UPDATES_TABLE = "CREATE TABLE " + TABLE_BOOK_UPDATES +
            "(" +
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            BOOK_UPDATE_BOOK_ID + " INTEGER, " +
            BOOK_UPDATE_DATE + " TEXT" +
            ")";

    private static final String CREATE_GOALS_TABLE = "CREATE TABLE " + TABLE_GOALS +
            "(" +
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            GOAL_TYPE + " INTEGER, " +
            GOAL_AMOUNT + " INTEGER, " +
            GOAL_START_DATE + " TEXT, " +
            GOAL_END_DATE + " TEXT, " +
            GOAL_IS_COMPLETE + " INTEGER" +
            ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_BOOKS_TABLE);
        db.execSQL(CREATE_SHELVES_TABLE);
        db.execSQL(CREATE_PAGE_UPDATES_TABLE);
        db.execSQL(CREATE_BOOK_UPDATES_TABLE);
        db.execSQL(CREATE_GOALS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SHELVES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PAGE_UPDATES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOK_UPDATES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GOALS);
        onCreate(db);
    }
}