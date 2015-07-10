package com.s_diadamo.readlist.general;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.parse.ParseObject;
import com.s_diadamo.readlist.book.Book;
import com.s_diadamo.readlist.database.DatabaseHelper;
import com.s_diadamo.readlist.shelf.Shelf;
import com.s_diadamo.readlist.goal.Goal;
import com.s_diadamo.readlist.updates.BookUpdate;
import com.s_diadamo.readlist.updates.PageUpdate;

public class SyncData {

    private static final String TYPE_BOOK = "book";
    private static final String TYPE_SHELF = "shelf";
    private static final String TYPE_GOAL = "goal";
    private static final String TYPE_BOOK_UPDATE = "book_update";
    private static final String TYPE_PAGE_UPDATE = "page_update";

    Context context;
    String userName;

    public SyncData(Context context) {
        this.context = context;
        this.userName = getUserName();
    }

    public int syncAllData() {
        int numElementsSynced = 0;

        if (userName.isEmpty()) {
            return -1;
        }

        return numElementsSynced;
    }

    private String getUserName() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String userName = prefs.getString(Utils.USER_NAME, "");
        if (userName == null || userName.isEmpty()) {
            return "";
        }
        return userName;
    }

    public void syncBook(Book book) {
        ParseObject parseBook = new ParseObject(TYPE_BOOK);

        parseBook.put(Utils.USER_NAME, userName);
        parseBook.put(DatabaseHelper.KEY_ID, book.getId());
        parseBook.put(DatabaseHelper.BOOK_TITLE, book.getTitle());
        parseBook.put(DatabaseHelper.BOOK_AUTHOR, book.getAuthor());
        parseBook.put(DatabaseHelper.BOOK_SHELF, book.getShelfId());
        parseBook.put(DatabaseHelper.BOOK_DATE_ADDED, book.getDateAdded());
        parseBook.put(DatabaseHelper.BOOK_NUM_PAGES, book.getNumPages());
        parseBook.put(DatabaseHelper.BOOK_CURRENT_PAGE, book.getCurrentPage());
        parseBook.put(DatabaseHelper.BOOK_COMPLETE, book.isComplete());
        parseBook.put(DatabaseHelper.BOOK_COMPLETION_DATE, book.getCompletionDate());
        parseBook.put(DatabaseHelper.BOOK_COVER_PICTURE_URL, book.getCoverPictureUrl());

        parseBook.saveInBackground();

    }

    public void syncShelf(Shelf shelf) {
        ParseObject parseShelf = new ParseObject(TYPE_SHELF);

        parseShelf.put(Utils.USER_NAME, userName);
        parseShelf.put(DatabaseHelper.SHELF_NAME, shelf.getName());
        parseShelf.put(DatabaseHelper.SHELF_COLOR, shelf.getColour());

        parseShelf.saveInBackground();
    }

    public void syncGoal(Goal goal) {
        ParseObject parseGoal = new ParseObject(TYPE_GOAL);

        parseGoal.put(Utils.USER_NAME, userName);
        parseGoal.put(DatabaseHelper.GOAL_TYPE, goal.getType());
        parseGoal.put(DatabaseHelper.GOAL_AMOUNT, goal.getAmount());
        parseGoal.put(DatabaseHelper.GOAL_START_DATE, goal.getStartDate());
        parseGoal.put(DatabaseHelper.GOAL_END_DATE, goal.getEndDate());
        parseGoal.put(DatabaseHelper.GOAL_IS_COMPLETE, goal.isComplete());

        parseGoal.saveInBackground();
    }

    public void syncBookUpdate(BookUpdate bookUpdate) {
        ParseObject parseBookUpdate = new ParseObject(TYPE_BOOK_UPDATE);

        parseBookUpdate.put(Utils.USER_NAME, userName);
        parseBookUpdate.put(DatabaseHelper.BOOK_UPDATE_BOOK_ID, bookUpdate.getBookId());
        parseBookUpdate.put(DatabaseHelper.BOOK_UPDATE_DATE, bookUpdate.getDate());

        parseBookUpdate.saveInBackground();
    }

    public void syncPageUpdate(PageUpdate pageUpdate) {
        ParseObject parsePageUpdate = new ParseObject(TYPE_PAGE_UPDATE);

        parsePageUpdate.put(Utils.USER_NAME, userName);
        parsePageUpdate.put(DatabaseHelper.PAGE_UPDATE_BOOK_ID, pageUpdate.getBookId());
        parsePageUpdate.put(DatabaseHelper.PAGE_UPDATE_DATE, pageUpdate.getDate());
        parsePageUpdate.put(DatabaseHelper.PAGE_UPDATE_PAGES, pageUpdate.getPages());

        parsePageUpdate.saveInBackground();
    }
}
