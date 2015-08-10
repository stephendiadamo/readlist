package com.s_diadamo.readlist.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 5;
    private static final String DATABASE_NAME = "readList";

    // Tables
    public static final String TABLE_BOOKS = "books";
    public static final String TABLE_SHELVES = "shelves";
    public static final String TABLE_PAGE_UPDATES = "page_updates";
    public static final String TABLE_BOOK_UPDATES = "book_updates";
    public static final String TABLE_GOALS = "goals";
    public static final String TABLE_LENT_BOOKS = "lent_books";
    public static final String TABLE_COMMENTS = "comments";

    // Common columns
    public static final String KEY_ID = "id";
    public static final String IS_DELETED = "is_deleted";

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
    public static final String BOOK_RATING = "rating";

    // Shelves table columns
    public static final String SHELF_NAME = "name";
    public static final String SHELF_COLOR = "color";

    // Page Updates table columns
    public static final String PAGE_UPDATE_BOOK_ID = "book_id";
    public static final String PAGE_UPDATE_DATE = "date";
    public static final String PAGE_UPDATE_PAGES = "pages";

    // Book Updates table columns
    public static final String BOOK_UPDATE_BOOK_ID = "book_id";
    public static final String BOOK_UPDATE_DATE = "date";

    // Goals table columns
    public static final String GOAL_TYPE = "type";
    public static final String GOAL_AMOUNT = "amount";
    public static final String GOAL_START_DATE = "start_date";
    public static final String GOAL_END_DATE = "end_date";
    public static final String GOAL_IS_COMPLETE = "complete";

    // Lent Book table columns
    public static final String LENT_BOOK_BOOK_ID = "book_id";
    public static final String LENT_BOOK_LENT_TO = "lent_to";
    public static final String LENT_BOOK_DATE_LENT = "date_lent";

    //Comment table columns
    public static final String COMMENT_BOOK_ID = "book_id";
    public static final String COMMENT_COMMENT = "comment";
    public static final String COMMENT_DATE_ADDED = "date_added";

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
            BOOK_COVER_PICTURE_URL + " TEXT, " +
            BOOK_RATING + " REAL DEFAULT -1, " +
            IS_DELETED + " INTEGER" +
            ")";

    private static final String CREATE_SHELVES_TABLE = "CREATE TABLE " + TABLE_SHELVES +
            "(" +
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            SHELF_NAME + " TEXT, " +
            SHELF_COLOR + " TEXT, " +
            IS_DELETED + " INTEGER" +
            ")";

    private static final String CREATE_PAGE_UPDATES_TABLE = "CREATE TABLE " + TABLE_PAGE_UPDATES +
            "(" +
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            PAGE_UPDATE_BOOK_ID + " INTEGER, " +
            PAGE_UPDATE_DATE + " TEXT, " +
            PAGE_UPDATE_PAGES + " INTEGER, " +
            IS_DELETED + " INTEGER" +
            ")";

    private static final String CREATE_BOOK_UPDATES_TABLE = "CREATE TABLE " + TABLE_BOOK_UPDATES +
            "(" +
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            BOOK_UPDATE_BOOK_ID + " INTEGER, " +
            BOOK_UPDATE_DATE + " TEXT, " +
            IS_DELETED + " INTEGER" +
            ")";

    private static final String CREATE_GOALS_TABLE = "CREATE TABLE " + TABLE_GOALS +
            "(" +
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            GOAL_TYPE + " INTEGER, " +
            GOAL_AMOUNT + " INTEGER, " +
            GOAL_START_DATE + " TEXT, " +
            GOAL_END_DATE + " TEXT, " +
            GOAL_IS_COMPLETE + " INTEGER, " +
            IS_DELETED + " INTEGER" +
            ")";

    private static final String CREATE_LENT_BOOKS_TABLE = "CREATE TABLE " + TABLE_LENT_BOOKS +
            "(" +
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            LENT_BOOK_BOOK_ID + " INTEGER, " +
            LENT_BOOK_LENT_TO + " TEXT, " +
            LENT_BOOK_DATE_LENT + " TEXT, " +
            IS_DELETED + " INTEGER" +
            ")";

    private static final String CREATE_COMMENT_TABLE = "CREATE TABLE " + TABLE_COMMENTS +
            "(" +
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COMMENT_BOOK_ID + " INTEGER, " +
            COMMENT_COMMENT + " TEXT, " +
            COMMENT_DATE_ADDED + " TEXT, " +
            IS_DELETED + " INTEGER" +
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
        db.execSQL(CREATE_LENT_BOOKS_TABLE);
        db.execSQL(CREATE_COMMENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            String addBookColumn = String.format("ALTER TABLE %s ADD COLUMN %s INTEGER DEFAULT 0",
                    TABLE_BOOKS,
                    IS_DELETED);

            String addShelfColumn = String.format("ALTER TABLE %s ADD COLUMN %s INTEGER DEFAULT 0",
                    TABLE_SHELVES,
                    IS_DELETED);

            String addGoalColumn = String.format("ALTER TABLE %s ADD COLUMN %s INTEGER DEFAULT 0",
                    TABLE_GOALS,
                    IS_DELETED);

            db.execSQL(addBookColumn);
            db.execSQL(addShelfColumn);
            db.execSQL(addGoalColumn);
        }

        if (oldVersion < 3) {
            String addPageUpdateColumn = String.format("ALTER TABLE %s ADD COLUMN %s INTEGER DEFAULT 0",
                    TABLE_PAGE_UPDATES,
                    IS_DELETED);

            String addBookUpdateColumn = String.format("ALTER TABLE %s ADD COLUMN %s INTEGER DEFAULT 0",
                    TABLE_BOOK_UPDATES,
                    IS_DELETED);

            db.execSQL(addPageUpdateColumn);
            db.execSQL(addBookUpdateColumn);
        }

        if (oldVersion < 4) {
            db.execSQL(CREATE_LENT_BOOKS_TABLE);
        }

        if (oldVersion < 5) {
            String addRatingColumn = String.format("ALTER TABLE %s ADD COLUMN %s REAL DEFAULT -1",
                    TABLE_BOOKS,
                    BOOK_RATING);

            db.execSQL(addRatingColumn);
            db.execSQL(CREATE_COMMENT_TABLE);
        }
    }
}
