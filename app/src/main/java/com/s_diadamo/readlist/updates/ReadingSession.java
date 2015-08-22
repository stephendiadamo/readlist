package com.s_diadamo.readlist.updates;


import com.s_diadamo.readlist.general.Utils;

public class ReadingSession {
    private int id;
    private final int bookId;
    private String startTime;
    private String endTime;
    private boolean isDeleted;

    public ReadingSession(int id, int bookId, String startTime, String endTime, int isDeleted) {
        this.id = id;
        this.bookId = bookId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isDeleted = (isDeleted == 1);
    }

    public ReadingSession(int bookId) {
        this.bookId = bookId;
        this.startTime = Utils.getCurrentDate();
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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}
