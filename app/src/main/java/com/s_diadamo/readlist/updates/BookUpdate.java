package com.s_diadamo.readlist.updates;

import com.s_diadamo.readlist.general.Utils;

public class BookUpdate {
    private int id;
    private int bookId;
    private String date;
    private boolean isDeleted = false;

    public BookUpdate(int id, int bookId, String date, int isDeleted) {
        this.id = id;
        this.bookId = bookId;
        this.date = date;
        this.isDeleted = (isDeleted == 1);
    }

    public BookUpdate(int id, int bookId, String date) {
        this.id = id;
        this.bookId = bookId;
        this.date = date;
    }

    public BookUpdate(int bookId) {
        this.bookId = bookId;
        this.date = Utils.getCurrentDate();
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBookId() {
        return bookId;
    }

    public String getDate() {
        return date;
    }

    public void delete() {
        this.isDeleted = true;
    }

    public boolean isDeleted() {
        return isDeleted;
    }
}
