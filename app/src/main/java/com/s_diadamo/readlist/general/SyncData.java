package com.s_diadamo.readlist.general;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.s_diadamo.readlist.book.Book;
import com.s_diadamo.readlist.book.BookOperations;
import com.s_diadamo.readlist.database.DatabaseHelper;
import com.s_diadamo.readlist.goal.GoalOperations;
import com.s_diadamo.readlist.shelf.Shelf;
import com.s_diadamo.readlist.goal.Goal;
import com.s_diadamo.readlist.shelf.ShelfOperations;
import com.s_diadamo.readlist.updates.BookUpdate;
import com.s_diadamo.readlist.updates.BookUpdateOperations;
import com.s_diadamo.readlist.updates.PageUpdate;
import com.s_diadamo.readlist.updates.PageUpdateOperations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SyncData {

    private static final String READLIST_ID = "readlist_id";
    private static final String TYPE_BOOK = "book";
    private static final String TYPE_SHELF = "shelf";
    private static final String TYPE_GOAL = "goal";
    private static final String TYPE_BOOK_UPDATE = "book_update";
    private static final String TYPE_PAGE_UPDATE = "page_update";

    Context context;
    String userName;
    MultiProcessSpinner syncSpinner;

    public SyncData(Context context) {
        this.context = context;
        this.userName = getUserName();
        this.syncSpinner = new MultiProcessSpinner(context, "Syncing data...", "Syncing complete");
    }

    public void syncAllData() {
        syncAllBooks();
        syncAllShelves();
        syncAllGoals();
        syncAllBookUpdates();
        syncAllPageUpdates();
    }

    private void syncAllBooks() {
        syncSpinner.addThread();
        ParseQuery<ParseObject> query = ParseQuery.getQuery(TYPE_BOOK);
        query.whereEqualTo(Utils.USER_NAME, userName);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseBooks, ParseException e) {
                syncSpinner.endThread();
                ArrayList<Book> booksOnDevice = new BookOperations(context).getAllBooks();
                ArrayList<Book> booksFromParse = new ArrayList<>();
                for (ParseObject parseBook : parseBooks) {
                    Book book = parseBookToBook(parseBook);
                    booksFromParse.add(book);
                }
                updateDeviceBooks(booksOnDevice, booksFromParse);
                updateParseBooks(booksOnDevice, booksFromParse);
            }
        });
    }

    private void updateDeviceBooks(ArrayList<Book> booksOnDevice, ArrayList<Book> booksFromParse) {
        HashSet<Integer> deviceBookIds = new HashSet<>();
        for (Book book : booksOnDevice) {
            deviceBookIds.add(book.getId());
        }

        BookOperations bookOperations = new BookOperations(context);

        for (Book book : booksFromParse) {
            if (!deviceBookIds.contains(book.getId())) {
                bookOperations.addBook(book);
            }
        }
    }

    private void updateParseBooks(ArrayList<Book> booksOnDevice, ArrayList<Book> booksFromParse) {
        HashSet<Integer> parseBookIds = new HashSet<>();
        for (Book book : booksFromParse) {
            parseBookIds.add(book.getId());
        }

        ArrayList<ParseObject> booksToSend = new ArrayList<>();

        for (final Book book : booksOnDevice) {
            if (!parseBookIds.contains(book.getId())) {
                booksToSend.add(toParseBook(book));
            } else {
                ParseQuery<ParseObject> query = ParseQuery.getQuery(TYPE_BOOK);
                query.whereEqualTo(Utils.USER_NAME, userName);
                query.whereEqualTo(READLIST_ID, book.getId());
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> bookList, ParseException e) {
                        if (bookList.size() > 0) {
                            ParseObject bookToUpdate = bookList.get(0);
                            copyBookValues(bookToUpdate, book);
                            bookToUpdate.saveInBackground();
                        }
                    }
                });
            }
        }

        ParseObject.saveAllInBackground(booksToSend);
    }

    private void syncAllShelves() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(TYPE_SHELF);
        query.whereEqualTo(Utils.USER_NAME, userName);
        syncSpinner.addThread();
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseShelves, ParseException e) {
                syncSpinner.endThread();
                ArrayList<Shelf> shelvesOnDevice = new ShelfOperations(context).getAllShelves();
                ArrayList<Shelf> shelvesFromParse = new ArrayList<>();
                for (ParseObject parseShelf : parseShelves) {
                    Shelf shelf = parseShelfToShelf(parseShelf);
                    shelvesFromParse.add(shelf);
                }
                updateDeviceShelves(shelvesOnDevice, shelvesFromParse);
                updateParseShelves(shelvesOnDevice, shelvesFromParse);
            }
        });
    }

    private void updateDeviceShelves(ArrayList<Shelf> shelvesOnDevice, ArrayList<Shelf> shelvesFromParse) {
        HashSet<Integer> deviceShelfIds = new HashSet<>();
        for (Shelf shelf : shelvesOnDevice) {
            deviceShelfIds.add(shelf.getId());
        }

        ShelfOperations shelfOperations = new ShelfOperations(context);

        for (Shelf shelf : shelvesFromParse) {
            if (!deviceShelfIds.contains(shelf.getId())) {
                shelfOperations.addShelf(shelf);
            }
        }
    }

    private void updateParseShelves(ArrayList<Shelf> shelvesOnDevice, ArrayList<Shelf> shelvesFromParse) {
        HashSet<Integer> parseShelfIds = new HashSet<>();
        for (Shelf shelf : shelvesFromParse) {
            parseShelfIds.add(shelf.getId());
        }

        ArrayList<ParseObject> shelvesToSend = new ArrayList<>();

        for (final Shelf shelf : shelvesOnDevice) {
            if (!parseShelfIds.contains(shelf.getId())) {
                shelvesToSend.add(toParseShelf(shelf));
            } else {
                ParseQuery<ParseObject> query = ParseQuery.getQuery(TYPE_SHELF);
                query.whereEqualTo(Utils.USER_NAME, userName);
                query.whereEqualTo(READLIST_ID, shelf.getId());
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> shelfList, ParseException e) {
                        if (shelfList.size() > 0) {
                            ParseObject shelfToUpdate = shelfList.get(0);
                            copyShelfValues(shelfToUpdate, shelf);
                            shelfToUpdate.saveInBackground();
                        }
                    }
                });
            }
        }

        ParseObject.saveAllInBackground(shelvesToSend);
    }

    private void syncAllGoals() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(TYPE_GOAL);
        query.whereEqualTo(Utils.USER_NAME, userName);
        syncSpinner.addThread();
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseGoals, ParseException e) {
                syncSpinner.endThread();
                ArrayList<Goal> goalsOnDevice = new GoalOperations(context).getAllGoals();
                ArrayList<Goal> goalsFromParse = new ArrayList<>();
                for (ParseObject parseGoal : parseGoals) {
                    Goal goal = parseGoalToGoal(parseGoal);
                    goalsFromParse.add(goal);
                }
                updateDeviceGoals(goalsOnDevice, goalsFromParse);
                updateParseGoals(goalsOnDevice, goalsFromParse);
            }
        });
    }

    private void updateDeviceGoals(ArrayList<Goal> goalsOnDevice, ArrayList<Goal> goalsFromParse) {
        HashSet<Integer> deviceGoalIds = new HashSet<>();
        for (Goal goal : goalsOnDevice) {
            deviceGoalIds.add(goal.getId());
        }

        GoalOperations goalOperations = new GoalOperations(context);

        for (Goal goal : goalsFromParse) {
            if (!deviceGoalIds.contains(goal.getId())) {
                goalOperations.addGoal(goal);
            }
        }
    }

    private void updateParseGoals(ArrayList<Goal> goalsOnDevice, ArrayList<Goal> goalsFromParse) {
        HashSet<Integer> parseGoalIds = new HashSet<>();
        for (Goal goal : goalsFromParse) {
            parseGoalIds.add(goal.getId());
        }

        ArrayList<ParseObject> goalsToSend = new ArrayList<>();

        for (final Goal goal : goalsOnDevice) {
            if (!parseGoalIds.contains(goal.getId())) {
                goalsToSend.add(toParseGoal(goal));
            } else {
                ParseQuery<ParseObject> query = ParseQuery.getQuery(TYPE_GOAL);
                query.whereEqualTo(Utils.USER_NAME, userName);
                query.whereEqualTo(READLIST_ID, goal.getId());
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> goalList, ParseException e) {
                        if (goalList.size() > 0) {
                            ParseObject goalToUpdate = goalList.get(0);
                            copyGoalValues(goalToUpdate, goal);
                            goalToUpdate.saveInBackground();
                        }
                    }
                });
            }
        }

        ParseObject.saveAllInBackground(goalsToSend);
    }

    private void syncAllBookUpdates() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(TYPE_BOOK_UPDATE);
        query.whereEqualTo(Utils.USER_NAME, userName);
        syncSpinner.addThread();
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseBookUpdates, ParseException e) {
                syncSpinner.endThread();
                ArrayList<BookUpdate> bookUpdatesOnDevice = new BookUpdateOperations(context).getAllBookUpdates();
                ArrayList<BookUpdate> bookUpdatesFromParse = new ArrayList<>();
                for (ParseObject parseBookUpdate : parseBookUpdates) {
                    BookUpdate bookUpdate = parseBookUpdateToBookUpdate(parseBookUpdate);
                    bookUpdatesFromParse.add(bookUpdate);
                }
                updateDeviceBookUpdates(bookUpdatesOnDevice, bookUpdatesFromParse);
                updateParseBookUpdates(bookUpdatesOnDevice, bookUpdatesFromParse);
            }
        });
    }

    private void updateDeviceBookUpdates(ArrayList<BookUpdate> bookUpdatesOnDevice, ArrayList<BookUpdate> bookUpdatesFromParse) {
        HashSet<Integer> deviceBookUpdateIds = new HashSet<>();
        for (BookUpdate bookUpdate : bookUpdatesOnDevice) {
            deviceBookUpdateIds.add(bookUpdate.getId());
        }

        BookUpdateOperations bookUpdateOperations = new BookUpdateOperations(context);

        for (BookUpdate bookUpdate : bookUpdatesFromParse) {
            if (!deviceBookUpdateIds.contains(bookUpdate.getId())) {
                bookUpdateOperations.addBookUpdate(bookUpdate);
            }
        }
    }

    private void updateParseBookUpdates(ArrayList<BookUpdate> bookUpdatesOnDevice, ArrayList<BookUpdate> bookUpdatesFromParse) {
        HashSet<Integer> parseBookUpdateIds = new HashSet<>();
        for (BookUpdate bookUpdate : bookUpdatesFromParse) {
            parseBookUpdateIds.add(bookUpdate.getId());
        }

        ArrayList<ParseObject> bookUpdatesToSend = new ArrayList<>();

        for (final BookUpdate bookUpdate : bookUpdatesOnDevice) {
            if (!parseBookUpdateIds.contains(bookUpdate.getId())) {
                bookUpdatesToSend.add(toParseBookUpdate(bookUpdate));
            }
        }

        ParseObject.saveAllInBackground(bookUpdatesToSend);
    }

    private void syncAllPageUpdates() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(TYPE_PAGE_UPDATE);
        query.whereEqualTo(Utils.USER_NAME, userName);
        syncSpinner.addThread();
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parsePageUpdates, ParseException e) {
                syncSpinner.endThread();
                ArrayList<PageUpdate> pageUpdatesOnDevice = new PageUpdateOperations(context).getAllPageUpdates();
                ArrayList<PageUpdate> pageUpdatesFromParse = new ArrayList<>();
                for (ParseObject parsePageUpdate : parsePageUpdates) {
                    PageUpdate pageUpdate = parsePageUpdateToPageUpdate(parsePageUpdate);
                    pageUpdatesFromParse.add(pageUpdate);
                }
                updateDevicePageUpdates(pageUpdatesOnDevice, pageUpdatesFromParse);
                updateParsePageUpdates(pageUpdatesOnDevice, pageUpdatesFromParse);
            }
        });
    }

    private void updateDevicePageUpdates(ArrayList<PageUpdate> pageUpdatesOnDevice, ArrayList<PageUpdate> pageUpdatesFromParse) {
        HashSet<Integer> devicePageUpdateIds = new HashSet<>();
        for (PageUpdate pageUpdate : pageUpdatesOnDevice) {
            devicePageUpdateIds.add(pageUpdate.getId());
        }

        PageUpdateOperations pageUpdateOperations = new PageUpdateOperations(context);

        for (PageUpdate pageUpdate : pageUpdatesFromParse) {
            if (!devicePageUpdateIds.contains(pageUpdate.getId())) {
                pageUpdateOperations.addPageUpdate(pageUpdate);
            }
        }
    }

    private void updateParsePageUpdates(ArrayList<PageUpdate> pageUpdatesOnDevice, ArrayList<PageUpdate> pageUpdatesFromParse) {
        HashSet<Integer> parsepageUpdateIds = new HashSet<>();
        for (PageUpdate pageUpdate : pageUpdatesFromParse) {
            parsepageUpdateIds.add(pageUpdate.getId());
        }

        ArrayList<ParseObject> pageUpdatesToSend = new ArrayList<>();

        for (final PageUpdate pageUpdate : pageUpdatesOnDevice) {
            if (!parsepageUpdateIds.contains(pageUpdate.getId())) {
                pageUpdatesToSend.add(toParsePageUpdate(pageUpdate));
            }
        }

        ParseObject.saveAllInBackground(pageUpdatesToSend);
    }

    public void syncBook(Book book) {
        ParseObject parseBook = toParseBook(book);
        parseBook.saveEventually();
    }

    public void syncShelf(Shelf shelf) {
        ParseObject parseShelf = toParseShelf(shelf);
        parseShelf.saveEventually();
    }

    public void syncGoal(Goal goal) {
        ParseObject parseGoal = toParseGoal(goal);
        parseGoal.saveEventually();
    }

    public void syncBookUpdate(BookUpdate bookUpdate) {
        ParseObject parseBookUpdate = toParseBookUpdate(bookUpdate);
        parseBookUpdate.saveEventually();
    }

    public void syncPageUpdate(PageUpdate pageUpdate) {
        ParseObject parsePageUpdate = toParsePageUpdate(pageUpdate);
        parsePageUpdate.saveEventually();
    }

    private ParseObject toParseBook(Book book) {
        ParseObject parseBook = new ParseObject(TYPE_BOOK);

        parseBook.put(Utils.USER_NAME, userName);
        parseBook.put(READLIST_ID, book.getId());
        parseBook.put(DatabaseHelper.BOOK_TITLE, book.getTitle());
        parseBook.put(DatabaseHelper.BOOK_AUTHOR, book.getAuthor());
        parseBook.put(DatabaseHelper.BOOK_SHELF, book.getShelfId());
        parseBook.put(DatabaseHelper.BOOK_DATE_ADDED, book.getDateAdded());
        parseBook.put(DatabaseHelper.BOOK_NUM_PAGES, book.getNumPages());
        parseBook.put(DatabaseHelper.BOOK_CURRENT_PAGE, book.getCurrentPage());
        parseBook.put(DatabaseHelper.BOOK_COMPLETE, book.isComplete());
        parseBook.put(DatabaseHelper.BOOK_COMPLETION_DATE, book.getCompletionDate());
        parseBook.put(DatabaseHelper.BOOK_COVER_PICTURE_URL, book.getCoverPictureUrl());

        return parseBook;
    }

    private void copyBookValues(ParseObject parseBook, Book book) {
        parseBook.put(DatabaseHelper.BOOK_TITLE, book.getTitle());
        parseBook.put(DatabaseHelper.BOOK_AUTHOR, book.getAuthor());
        parseBook.put(DatabaseHelper.BOOK_SHELF, book.getShelfId());
        parseBook.put(DatabaseHelper.BOOK_DATE_ADDED, book.getDateAdded());
        parseBook.put(DatabaseHelper.BOOK_NUM_PAGES, book.getNumPages());
        parseBook.put(DatabaseHelper.BOOK_CURRENT_PAGE, book.getCurrentPage());
        parseBook.put(DatabaseHelper.BOOK_COMPLETE, book.isComplete());
        parseBook.put(DatabaseHelper.BOOK_COMPLETION_DATE, book.getCompletionDate());
        parseBook.put(DatabaseHelper.BOOK_COVER_PICTURE_URL, book.getCoverPictureUrl());
    }

    private ParseObject toParseShelf(Shelf shelf) {
        ParseObject parseShelf = new ParseObject(TYPE_SHELF);

        parseShelf.put(Utils.USER_NAME, userName);
        parseShelf.put(READLIST_ID, shelf.getId());
        parseShelf.put(DatabaseHelper.SHELF_NAME, shelf.getName());
        parseShelf.put(DatabaseHelper.SHELF_COLOR, shelf.getColour());

        return parseShelf;
    }

    private void copyShelfValues(ParseObject parseShelf, Shelf shelf) {
        parseShelf.put(DatabaseHelper.SHELF_NAME, shelf.getName());
        parseShelf.put(DatabaseHelper.SHELF_COLOR, shelf.getColour());
    }

    private ParseObject toParseGoal(Goal goal) {
        ParseObject parseGoal = new ParseObject(TYPE_GOAL);

        parseGoal.put(Utils.USER_NAME, userName);
        parseGoal.put(READLIST_ID, goal.getId());
        parseGoal.put(DatabaseHelper.GOAL_TYPE, goal.getType());
        parseGoal.put(DatabaseHelper.GOAL_AMOUNT, goal.getAmount());
        parseGoal.put(DatabaseHelper.GOAL_START_DATE, goal.getStartDate());
        parseGoal.put(DatabaseHelper.GOAL_END_DATE, goal.getEndDate());
        parseGoal.put(DatabaseHelper.GOAL_IS_COMPLETE, goal.isComplete());

        return parseGoal;
    }

    private void copyGoalValues(ParseObject parseGoal, Goal goal) {
        parseGoal.put(DatabaseHelper.GOAL_TYPE, goal.getType());
        parseGoal.put(DatabaseHelper.GOAL_AMOUNT, goal.getAmount());
        parseGoal.put(DatabaseHelper.GOAL_START_DATE, goal.getStartDate());
        parseGoal.put(DatabaseHelper.GOAL_END_DATE, goal.getEndDate());
        parseGoal.put(DatabaseHelper.GOAL_IS_COMPLETE, goal.isComplete());
    }

    private ParseObject toParseBookUpdate(BookUpdate bookUpdate) {
        ParseObject parseBookUpdate = new ParseObject(TYPE_BOOK_UPDATE);

        parseBookUpdate.put(Utils.USER_NAME, userName);
        parseBookUpdate.put(READLIST_ID, bookUpdate.getId());
        parseBookUpdate.put(DatabaseHelper.BOOK_UPDATE_BOOK_ID, bookUpdate.getBookId());
        parseBookUpdate.put(DatabaseHelper.BOOK_UPDATE_DATE, bookUpdate.getDate());

        return parseBookUpdate;
    }

    private ParseObject toParsePageUpdate(PageUpdate pageUpdate) {
        ParseObject parsePageUpdate = new ParseObject(TYPE_PAGE_UPDATE);

        parsePageUpdate.put(Utils.USER_NAME, userName);
        parsePageUpdate.put(READLIST_ID, pageUpdate.getId());
        parsePageUpdate.put(DatabaseHelper.PAGE_UPDATE_BOOK_ID, pageUpdate.getBookId());
        parsePageUpdate.put(DatabaseHelper.PAGE_UPDATE_DATE, pageUpdate.getDate());
        parsePageUpdate.put(DatabaseHelper.PAGE_UPDATE_PAGES, pageUpdate.getPages());

        return parsePageUpdate;
    }

    private Book parseBookToBook(ParseObject parseBook) {
        return new Book(
                parseBook.getInt(READLIST_ID),
                parseBook.getString(DatabaseHelper.BOOK_TITLE),
                parseBook.getString(DatabaseHelper.BOOK_AUTHOR),
                parseBook.getInt(DatabaseHelper.BOOK_SHELF),
                parseBook.getString(DatabaseHelper.BOOK_DATE_ADDED),
                parseBook.getInt(DatabaseHelper.BOOK_NUM_PAGES),
                parseBook.getInt(DatabaseHelper.BOOK_CURRENT_PAGE),
                parseBook.getInt(DatabaseHelper.BOOK_COMPLETE),
                parseBook.getString(DatabaseHelper.BOOK_COMPLETION_DATE),
                parseBook.getString(DatabaseHelper.BOOK_COVER_PICTURE_URL));
    }

    private Shelf parseShelfToShelf(ParseObject parseShelf) {
        return new Shelf(
                parseShelf.getInt(READLIST_ID),
                parseShelf.getString(DatabaseHelper.SHELF_NAME),
                parseShelf.getInt(DatabaseHelper.SHELF_COLOR));
    }

    private Goal parseGoalToGoal(ParseObject parseGoal) {
        return new Goal(
                parseGoal.getInt(READLIST_ID),
                parseGoal.getInt(DatabaseHelper.GOAL_TYPE),
                parseGoal.getInt(DatabaseHelper.GOAL_AMOUNT),
                parseGoal.getString(DatabaseHelper.GOAL_START_DATE),
                parseGoal.getString(DatabaseHelper.GOAL_END_DATE),
                parseGoal.getInt(DatabaseHelper.GOAL_IS_COMPLETE));
    }

    private BookUpdate parseBookUpdateToBookUpdate(ParseObject parseBookUpdate) {
        return new BookUpdate(
                parseBookUpdate.getInt(READLIST_ID),
                parseBookUpdate.getInt(DatabaseHelper.BOOK_UPDATE_BOOK_ID),
                parseBookUpdate.getString(DatabaseHelper.BOOK_UPDATE_DATE));
    }

    private PageUpdate parsePageUpdateToPageUpdate(ParseObject parsePageUpdate) {
        return new PageUpdate(
                parsePageUpdate.getInt(READLIST_ID),
                parsePageUpdate.getInt(DatabaseHelper.BOOK_UPDATE_BOOK_ID),
                parsePageUpdate.getString(DatabaseHelper.BOOK_UPDATE_DATE),
                parsePageUpdate.getInt(DatabaseHelper.PAGE_UPDATE_PAGES));
    }

    private String getUserName() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String userName = prefs.getString(Utils.USER_NAME, "");
        if (userName == null || userName.isEmpty()) {
            return "";
        }
        return userName;
    }
}
