package com.s_diadamo.readlist.updates;

import com.s_diadamo.readlist.general.Utils;

public class BookUpdate {
    private int id;
    private int bookId;
    private String date;

    public BookUpdate(int id, int bookId, String date) {
        this.id = id;
        this.bookId = bookId;
        this.date = date;
    }

    public BookUpdate(int bookId) {
        this.bookId = bookId;
        this.date = Utils.getCurrentDate();
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
}
