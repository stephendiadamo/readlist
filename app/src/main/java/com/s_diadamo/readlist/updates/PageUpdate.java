package com.s_diadamo.readlist.updates;

import com.s_diadamo.readlist.general.Utils;

public class PageUpdate {
    private int id;
    private int bookId;
    private String date;
    private int pages;
    private boolean isDeleted = false;

    public PageUpdate(int id, int bookId, String date, int pages, int isDeleted) {
        this.id = id;
        this.bookId = bookId;
        this.date = date;
        this.pages = pages;
        this.isDeleted = (isDeleted == 1);
    }

    public PageUpdate(int id, int bookId, String date, int pages) {
        this.id = id;
        this.bookId = bookId;
        this.date = date;
        this.pages = pages;
    }

    public PageUpdate(int bookId, String date, int pages) {
        this.bookId = bookId;
        this.date = date;
        this.pages = pages;
    }

    public PageUpdate(int bookId, int pages) {
        this.bookId = bookId;
        this.date = Utils.getCurrentDate();
        this.pages = pages;
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

    public int getPages() {
        return pages;
    }

    public void delete() {
        this.isDeleted = true;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

}
