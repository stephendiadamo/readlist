package com.s_diadamo.readlist.lent;

import com.s_diadamo.readlist.general.Utils;

public class LentBook {
    private int id;
    private int bookId;
    private String lentTo;
    private String dateLent;
    private boolean isDeleted = false;

    public LentBook(int id, int bookId, String lentTo, String dateLent, int isDeleted) {
        this.id = id;
        this.bookId = bookId;
        this.lentTo = lentTo;
        this.dateLent = dateLent;
        this.isDeleted = (isDeleted == 1);
    }

    public LentBook(int id, int bookId, String lentTo) {
        this.id = id;
        this.bookId = bookId;
        this.lentTo = lentTo;
        this.dateLent = Utils.getCurrentDate();
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getBookId() {
        return bookId;
    }

    public String getLentTo() {
        return lentTo;
    }

    public String getDateLent() {
        return dateLent;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void delete() {
        this.isDeleted = true;
    }
}
