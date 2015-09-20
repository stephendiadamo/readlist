package com.s_diadamo.readlist.updates;

import com.s_diadamo.readlist.general.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PageUpdate {
    private int id;
    private final int bookId;
    private final String date;
    private final int pages;
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

    public String getCleanDateAdded() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Utils.DATE_FORMAT, Locale.CANADA);
        try {
            if (date.equals("")) {
                return "";
            }

            Date d = simpleDateFormat.parse(date);
            simpleDateFormat.applyPattern(Utils.CLEAN_DATE_FORMAT);
            return simpleDateFormat.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

}
