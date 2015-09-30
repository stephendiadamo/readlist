package com.s_diadamo.readlist.sync;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.s_diadamo.readlist.book.Book;
import com.s_diadamo.readlist.comment.Comment;
import com.s_diadamo.readlist.general.MultiProcessSpinner;
import com.s_diadamo.readlist.general.Utils;
import com.s_diadamo.readlist.lent.LentBook;
import com.s_diadamo.readlist.readingSession.ReadingSession;
import com.s_diadamo.readlist.shelf.Shelf;
import com.s_diadamo.readlist.goal.Goal;
import com.s_diadamo.readlist.updates.BookUpdate;
import com.s_diadamo.readlist.updates.PageUpdate;

import java.util.List;

public class SyncData {

    static final String READLIST_ID = "readlist_id";
    static final String TYPE_BOOK = "book";
    static final String TYPE_SHELF = "shelf";
    static final String TYPE_GOAL = "goal";
    static final String TYPE_BOOK_UPDATE = "book_update";
    static final String TYPE_PAGE_UPDATE = "page_update";
    static final String TYPE_LENT_BOOK = "lent_book";
    static final String TYPE_COMMENT = "comment";
    static final String TYPE_READING_SESSION = "reading_session";

    private final Context context;
    final String userName;
    final MultiProcessSpinner syncSpinner;
    final boolean showSpinner;

    public SyncData(Context context, boolean showSpinner) {
        this.context = context;
        this.userName = Utils.getUserName(context);
        this.syncSpinner = MultiProcessSpinner.getInstance();
        this.syncSpinner.setInfo(context, "Syncing data...");
        this.showSpinner = showSpinner;
    }

    public SyncData(Context context) {
        this.context = context;
        this.userName = Utils.getUserName(context);
        this.syncSpinner = MultiProcessSpinner.getInstance();
        this.syncSpinner.setInfo(context, "Syncing data...");
        this.showSpinner = true;
    }

    public void syncAllData() {
        new SyncShelfData(context, showSpinner).syncAllShelves();
        new SyncBookData(context, showSpinner).syncAllBooks();

        new SyncGoalData(context, showSpinner).syncAllGoals();
        new SyncBookUpdateData(context, showSpinner).syncAllBookUpdates();
        new SyncPageUpdateData(context, showSpinner).syncAllPageUpdates();
        new SyncLentBookData(context, showSpinner).syncAllLentBooks();
        new SyncCommentData(context, showSpinner).syncAllComments();
        new SyncReadingSessionData(context, showSpinner).syncAllReadingSessions();
    }

    public void syncAllData(AppCompatActivity activity) {
        new SyncShelfData(context).syncAllShelves(activity);
        new SyncBookData(context).syncAllBooks();

        new SyncGoalData(context).syncAllGoals();
        new SyncBookUpdateData(context).syncAllBookUpdates();
        new SyncPageUpdateData(context).syncAllPageUpdates();
        new SyncLentBookData(context).syncAllLentBooks();
        new SyncCommentData(context).syncAllComments();
        new SyncReadingSessionData(context).syncAllReadingSessions();
    }

    public void add(Book book) {
        ParseObject parseBook = new SyncBookData(context).toParseBook(book);
        parseBook.saveEventually();
    }

    public void add(Shelf shelf) {
        ParseObject parseShelf = new SyncShelfData(context).toParseShelf(shelf);
        parseShelf.saveEventually();
    }

    public void add(Goal goal) {
        ParseObject parseGoal = new SyncGoalData(context).toParseGoal(goal);
        parseGoal.saveEventually();
    }

    public void add(BookUpdate bookUpdate) {
        ParseObject parseBookUpdate = new SyncBookUpdateData(context).toParseBookUpdate(bookUpdate);
        parseBookUpdate.saveEventually();
    }

    public void add(PageUpdate pageUpdate) {
        ParseObject parsePageUpdate = new SyncPageUpdateData(context).toParsePageUpdate(pageUpdate);
        parsePageUpdate.saveEventually();
    }

    public void add(LentBook lentBook) {
        ParseObject parseLentBook = new SyncLentBookData(context).toParseLentBook(lentBook);
        parseLentBook.saveEventually();
    }

    public void add(Comment comment) {
        ParseObject parseComment = new SyncCommentData(context).toParseComment(comment);
        parseComment.saveEventually();
    }

    public void add(ReadingSession readingSession) {
        ParseObject parseReadingSession = new SyncReadingSessionData(context).toParseReadingSession(readingSession);
        parseReadingSession.saveEventually();
    }

    public void delete(Book book) {
        new SyncBookData(context).deleteParseBook(book);
    }

    public void delete(Shelf shelf) {
        new SyncShelfData(context).deleteParseShelf(shelf);
    }

    public void delete(Goal goal) {
        new SyncGoalData(context).deleteParseGoal(goal);
    }

    public void delete(PageUpdate pageUpdate) {
        new SyncPageUpdateData(context).deleteParsePageUpdate(pageUpdate);
    }

    public void delete(BookUpdate bookUpdate) {
        new SyncBookUpdateData(context).deleteParseBookUpdate(bookUpdate);
    }

    public void delete(LentBook lentBook) {
        new SyncLentBookData(context).deleteParseLentBook(lentBook);
    }

    public void delete(Comment comment) {
        new SyncCommentData(context).deleteParseComment(comment);
    }

    public void delete(ReadingSession readingSession) {
        new SyncReadingSessionData(context).deleteParseReadingSession(readingSession);
    }

    public void update(Book book) {
        new SyncBookData(context).updateParseBook(book);
    }

    public void update(Shelf shelf) {
        new SyncShelfData(context).updateParseShelf(shelf);
    }

    public void update(Goal goal) {
        new SyncGoalData(context).updateParseGoal(goal);
    }

    public void update(LentBook lentBook) {
        new SyncLentBookData(context).updateParseLentBook(lentBook);
    }

    public void update(Comment comment) {
        new SyncCommentData(context).updateParseComment(comment);
    }

    public void update(ReadingSession readingSession) {
        new SyncReadingSessionData(context).updateParseReadingSession(readingSession);
    }

    public void deleteReadingSessions() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(TYPE_READING_SESSION);
        query.whereEqualTo(Utils.USER_NAME, userName);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                ParseObject.deleteAllInBackground(list);
            }
        });
    }

    public void deleteBookUpdates() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(TYPE_BOOK_UPDATE);
        query.whereEqualTo(Utils.USER_NAME, userName);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                ParseObject.deleteAllInBackground(list);
            }
        });
    }

    public void deletePageUpdates() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(TYPE_PAGE_UPDATE);
        query.whereEqualTo(Utils.USER_NAME, userName);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                ParseObject.deleteAllInBackground(list);
            }
        });
    }


}
