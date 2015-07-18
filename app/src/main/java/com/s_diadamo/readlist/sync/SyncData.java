package com.s_diadamo.readlist.sync;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.parse.ParseObject;
import com.s_diadamo.readlist.book.Book;
import com.s_diadamo.readlist.general.MultiProcessSpinner;
import com.s_diadamo.readlist.general.Utils;
import com.s_diadamo.readlist.shelf.Shelf;
import com.s_diadamo.readlist.goal.Goal;
import com.s_diadamo.readlist.updates.BookUpdate;
import com.s_diadamo.readlist.updates.PageUpdate;

public class SyncData {

    public static final String READLIST_ID = "readlist_id";
    public static final String TYPE_BOOK = "book";
    public static final String TYPE_SHELF = "shelf";
    public static final String TYPE_GOAL = "goal";
    public static final String TYPE_BOOK_UPDATE = "book_update";
    public static final String TYPE_PAGE_UPDATE = "page_update";

    Context context;
    String userName;
    MultiProcessSpinner syncSpinner;
    boolean showSpinner;

    public SyncData(Context context, boolean showSpinner) {
        this.context = context;
        this.userName = Utils.getUserName(context);
        this.syncSpinner = MultiProcessSpinner.getInstance();
        this.syncSpinner.setInfo(context, "Syncing data...", "Syncing complete");
        this.showSpinner = showSpinner;
    }

    public SyncData(Context context) {
        this.context = context;
        this.userName = Utils.getUserName(context);
        this.syncSpinner = MultiProcessSpinner.getInstance();
        this.syncSpinner.setInfo(context, "Syncing data...", "Syncing complete");
        this.showSpinner = true;
    }

    public void syncAllData() {
        new SyncBookData(context, showSpinner).syncAllBooks();
        new SyncShelfData(context, showSpinner).syncAllShelves();
        new SyncGoalData(context, showSpinner).syncAllGoals();
        new SyncBookUpdateData(context, showSpinner).syncAllBookUpdates();
        new SyncPageUpdateData(context, showSpinner).syncAllPageUpdates();
    }

    public void syncAllData(AppCompatActivity activity) {
        new SyncBookData(context).syncAllBooks();
        new SyncShelfData(context).syncAllShelves(activity);
        new SyncGoalData(context).syncAllGoals();
        new SyncBookUpdateData(context).syncAllBookUpdates();
        new SyncPageUpdateData(context).syncAllPageUpdates();
    }

    public void addBookToParse(Book book) {
        ParseObject parseBook = new SyncBookData(context).toParseBook(book);
        parseBook.saveEventually();
    }

    public void addShelfToParse(Shelf shelf) {
        ParseObject parseShelf = new SyncShelfData(context).toParseShelf(shelf);
        parseShelf.saveEventually();
    }

    public void addGoalToParse(Goal goal) {
        ParseObject parseGoal = new SyncGoalData(context).toParseGoal(goal);
        parseGoal.saveEventually();
    }

    public void addBookUpdateToParse(BookUpdate bookUpdate) {
        ParseObject parseBookUpdate = new SyncBookUpdateData(context).toParseBookUpdate(bookUpdate);
        parseBookUpdate.saveEventually();
    }

    public void addPageUpdateToParse(PageUpdate pageUpdate) {
        ParseObject parsePageUpdate = new SyncPageUpdateData(context).toParsePageUpdate(pageUpdate);
        parsePageUpdate.saveEventually();
    }
}
