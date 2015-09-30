package com.s_diadamo.readlist.sync;


import android.content.Context;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.s_diadamo.readlist.book.Book;
import com.s_diadamo.readlist.book.BookOperations;
import com.s_diadamo.readlist.database.DatabaseHelper;
import com.s_diadamo.readlist.general.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

class SyncBookData extends SyncData {
    private final BookOperations bookOperations;

    SyncBookData(Context context) {
        super(context, true);
        bookOperations = new BookOperations(context);
    }

    SyncBookData(Context context, boolean showSpinner) {
        super(context, showSpinner);
        bookOperations = new BookOperations(context);
    }

    void syncAllBooks() {
        if (showSpinner) {
            syncSpinner.addThread();
        }

        ParseQuery<ParseObject> query = ParseQuery.getQuery(TYPE_BOOK);
        query.whereEqualTo(Utils.USER_NAME, userName);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseBooks, ParseException e) {
                if (showSpinner) {
                    syncSpinner.endThread();
                }

                ArrayList<Book> booksOnDevice = bookOperations.getAllBooks();
                ArrayList<Book> booksFromParse = new ArrayList<>();
                for (ParseObject parseBook : parseBooks) {
                    Book book = parseBookToBook(parseBook);
                    booksFromParse.add(book);
                }
                updateDeviceBooks(booksOnDevice, booksFromParse, parseBooks);
                updateParseBooks(booksOnDevice, booksFromParse);
            }
        });
    }

    private void updateDeviceBooks(ArrayList<Book> booksOnDevice, ArrayList<Book> booksFromParse, List<ParseObject> parseBooks) {
        HashMap<Integer, Integer> deviceBookIds = new HashMap<>();
        int i = 0;
        for (Book book : booksOnDevice) {
            deviceBookIds.put(book.getId(), i);
            ++i;
        }

        i = 0;
        for (Book book : booksFromParse) {
            if (!deviceBookIds.containsKey(book.getId())) {
                int oldBookId = book.getId();

                if (book.getCurrentPage() == book.getNumPages() && book.getNumPages() != 0) {
                    book.markComplete();
                }

                bookOperations.addBook(book);
                if (book.getId() != oldBookId) {
                    fixBookRelations(book.getId(), oldBookId);
                    copyBookValues(parseBooks.get(i), book);
                    parseBooks.get(i).saveEventually();
                }
            } else {
                Book comparison = booksOnDevice.get(deviceBookIds.get(book.getId()));
                if (!booksMatch(comparison, book)) {
                    int oldBookId = book.getId();

                    if (book.getCurrentPage() == book.getNumPages() && book.getNumPages() != 0) {
                        book.markComplete();
                    }

                    bookOperations.addBook(book);
                    fixBookRelations(book.getId(), oldBookId);
                    copyBookValues(parseBooks.get(i), book);
                    parseBooks.get(i).saveEventually();
                }
            }
            ++i;
        }
    }

    // TODO: Save ISBN numbers... Or some unique identifier
    private boolean booksMatch(Book deviceBook, Book parseBook) {
        return deviceBook.getDateAdded().equals(parseBook.getDateAdded())
                && deviceBook.getTitle().equals(parseBook.getTitle());

    }

    private void updateParseBooks(ArrayList<Book> booksOnDevice, ArrayList<Book> booksFromParse) {
        HashSet<Integer> parseBookIds = new HashSet<>();
        for (Book book : booksFromParse) {
            parseBookIds.add(book.getId());
        }

        ArrayList<ParseObject> booksToSend = new ArrayList<>();

        for (final Book book : booksOnDevice) {
            if (!parseBookIds.contains(book.getId())) {
                if (book.isDeleted()) {
                    bookOperations.deleteBook(book);
                } else {
                    booksToSend.add(toParseBook(book));
                }
            } else {
                if (book.isDeleted()) {
                    deleteParseBook(book);
                    bookOperations.deleteBook(book);
                } else {
                    updateParseBook(book);
                }
            }
        }

        ParseObject.saveAllInBackground(booksToSend);
    }

    private void fixBookRelations(int newBookId, int oldBookId) {
        final CountDownLatch signalBookSync = new CountDownLatch(3);

        fixRelation(newBookId, oldBookId, TYPE_LENT_BOOK, DatabaseHelper.LENT_BOOK_BOOK_ID, signalBookSync);
        fixRelation(newBookId, oldBookId, TYPE_COMMENT, DatabaseHelper.COMMENT_BOOK_ID, signalBookSync);
        fixRelation(newBookId, oldBookId, TYPE_READING_SESSION, DatabaseHelper.READING_SESSION_BOOK_ID, signalBookSync);
        try {
            signalBookSync.await(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void fixRelation(int newBookId, int oldBookId, String type, String field, CountDownLatch signal) {
        new FixRelations(userName, newBookId, oldBookId, type, field, signal).execute();
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
                parseBook.getString(DatabaseHelper.BOOK_COVER_PICTURE_URL),
                parseBook.getDouble(DatabaseHelper.BOOK_RATING));
    }


    ParseObject toParseBook(Book book) {
        ParseObject parseBook = new ParseObject(TYPE_BOOK);

        parseBook.put(Utils.USER_NAME, userName);
        parseBook.put(READLIST_ID, book.getId());
        if (book.getTitle() != null) {
            parseBook.put(DatabaseHelper.BOOK_TITLE, book.getTitle());
        }
        if (book.getAuthor() != null) {
            parseBook.put(DatabaseHelper.BOOK_AUTHOR, book.getAuthor());
        }
        parseBook.put(DatabaseHelper.BOOK_SHELF, book.getShelfId());
        parseBook.put(DatabaseHelper.BOOK_DATE_ADDED, book.getDateAdded());
        parseBook.put(DatabaseHelper.BOOK_NUM_PAGES, book.getNumPages());
        parseBook.put(DatabaseHelper.BOOK_CURRENT_PAGE, book.getCurrentPage());
        parseBook.put(DatabaseHelper.BOOK_COMPLETE, book.isComplete());
        parseBook.put(DatabaseHelper.BOOK_COMPLETION_DATE, book.getCompletionDate());
        parseBook.put(DatabaseHelper.BOOK_COVER_PICTURE_URL, book.getCoverPictureUrl());
        parseBook.put(DatabaseHelper.BOOK_RATING, book.getRating());

        return parseBook;
    }

    void updateParseBook(final Book book) {
        queryForBook(book, new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> bookList, ParseException e) {
                if (bookList != null && bookList.size() > 0) {
                    ParseObject bookToUpdate = bookList.get(0);
                    copyBookValues(bookToUpdate, book);
                    bookToUpdate.saveEventually();
                }
            }
        });
    }

    void deleteParseBook(Book book) {
        queryForBook(book, new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> bookList, ParseException e) {
                if (bookList != null && bookList.size() > 0) {
                    ParseObject bookToDelete = bookList.get(0);
                    bookToDelete.deleteEventually();
                }
            }
        });
    }

    private void queryForBook(Book book, FindCallback<ParseObject> callback) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(TYPE_BOOK);
        query.whereEqualTo(Utils.USER_NAME, userName);
        query.whereEqualTo(READLIST_ID, book.getId());
        query.findInBackground(callback);
    }

    private void copyBookValues(ParseObject parseBook, Book book) {
        parseBook.put(READLIST_ID, book.getId());
        if (book.getTitle() != null) {
            parseBook.put(DatabaseHelper.BOOK_TITLE, book.getTitle());
        }
        if (book.getAuthor() != null) {
            parseBook.put(DatabaseHelper.BOOK_AUTHOR, book.getAuthor());
        }
        parseBook.put(DatabaseHelper.BOOK_SHELF, book.getShelfId());
        parseBook.put(DatabaseHelper.BOOK_DATE_ADDED, book.getDateAdded());
        parseBook.put(DatabaseHelper.BOOK_NUM_PAGES, book.getNumPages());
        parseBook.put(DatabaseHelper.BOOK_CURRENT_PAGE, book.getCurrentPage());
        parseBook.put(DatabaseHelper.BOOK_COMPLETE, book.isComplete());
        parseBook.put(DatabaseHelper.BOOK_COMPLETION_DATE, book.getCompletionDate());
        parseBook.put(DatabaseHelper.BOOK_COVER_PICTURE_URL, book.getCoverPictureUrl());
        parseBook.put(DatabaseHelper.BOOK_RATING, book.getRating());
    }
}
