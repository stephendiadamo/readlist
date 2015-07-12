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
import java.util.HashSet;
import java.util.List;

public class SyncBookData extends SyncData {
    public SyncBookData(Context context) {
        super(context);
    }

    protected void syncAllBooks() {
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

    protected void updateDeviceBooks(ArrayList<Book> booksOnDevice, ArrayList<Book> booksFromParse) {
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
                updateParseBook(book);
            }
        }

        ParseObject.saveAllInBackground(booksToSend);
    }

    public void updateParseBook(final Book book) {
        queryForBook(book, new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> bookList, ParseException e) {
                if (bookList.size() > 0) {
                    ParseObject bookToUpdate = bookList.get(0);
                    copyBookValues(bookToUpdate, book);
                    bookToUpdate.saveEventually();
                }
            }
        });
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

    protected ParseObject toParseBook(Book book) {
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

    public void deleteParseBook(Book book) {
        queryForBook(book, new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> bookList, ParseException e) {
                if (bookList.size() > 0) {
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
}
