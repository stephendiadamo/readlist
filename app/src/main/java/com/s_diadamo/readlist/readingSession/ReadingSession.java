package com.s_diadamo.readlist.readingSession;


import com.s_diadamo.readlist.general.Utils;

public class ReadingSession {
    private int id;
    private final int bookId;
    private String date;
    private int lengthOfTime;
    private boolean isDeleted;

    public ReadingSession(int id, int bookId, String date, int lengthOfTIme, int isDeleted) {
        this.id = id;
        this.bookId = bookId;
        this.date = date;
        this.lengthOfTime = lengthOfTIme;
        this.isDeleted = (isDeleted == 1);
    }

    public ReadingSession(int bookId, int lengthOfTime) {
        this.bookId = bookId;
        this.lengthOfTime = lengthOfTime;
        this.date = Utils.getCurrentDate();
    }

    public int getId() {
        return id;
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

    public void setDate(String date) {
        this.date = date;
    }

    public int getLengthOfTime() {
        return lengthOfTime;
    }

    public void setLengthOfTime(int lengthOfTime) {
        this.lengthOfTime = lengthOfTime;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}
