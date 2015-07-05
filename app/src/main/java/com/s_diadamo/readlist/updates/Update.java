package com.s_diadamo.readlist.updates;

import com.s_diadamo.readlist.Utils;

public class Update {

    private int id;
    private int bookId;
    private String date;
    private int pages;

    public Update(int id, int bookId, String date, int pages) {
        this.id = id;
        this.bookId = bookId;
        this.date = date;
        this.pages = pages;
    }

    public Update(int bookId, String date, int pages) {
        this.bookId = bookId;
        this.date = date;
        this.pages = pages;
    }

    public Update(int bookId, int pages) {
        this.bookId = bookId;
        this.date = Utils.getCurrentDate();
        this.pages = pages;
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

}
