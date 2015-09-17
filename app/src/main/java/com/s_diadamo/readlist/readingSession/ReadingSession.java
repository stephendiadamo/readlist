package com.s_diadamo.readlist.readingSession;


import com.s_diadamo.readlist.general.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReadingSession {
    private long id;
    private final int bookId;
    private String date;
    private int lengthOfTime;
    private boolean isDeleted = false;

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

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public String getCleanDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Utils.DATE_FORMAT, Locale.CANADA);
        try {
            Date d = simpleDateFormat.parse(date);
            simpleDateFormat.applyPattern(Utils.CLEAN_DATE_FORMAT);
            return simpleDateFormat.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void setLengthOfTime(int lengthOfTime) {
        this.lengthOfTime = lengthOfTime;
    }

    public boolean isDeleted() {
        return isDeleted;
    }


    public void delete() {
        this.isDeleted = true;
    }
}
