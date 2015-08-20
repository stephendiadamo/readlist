package com.s_diadamo.readlist.comment;


import com.s_diadamo.readlist.general.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Comment {

    public static final String COMMENT_BOOK_ID = "COMMENT_BOOK_ID";

    private int id;
    private int bookId;
    private String comment;
    private final String dateAdded;
    private boolean isDeleted = false;

    public Comment(int id, int bookId, String comment, String dateAdded, int isDeleted) {
        this.id = id;
        this.bookId = bookId;
        this.comment = comment;
        this.dateAdded = dateAdded;
        this.isDeleted = (isDeleted == 1);
    }

    public Comment(int id, int bookId, String comment, String dateAdded) {
        this.id = id;
        this.bookId = bookId;
        this.comment = comment;
        this.dateAdded = dateAdded;
    }

    public Comment(int bookId, String comment) {
        this.bookId = bookId;
        this.comment = comment;
        this.dateAdded = Utils.getCurrentDate();
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public String getCleanDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Utils.DATE_FORMAT, Locale.CANADA);
        try {
            Date d = simpleDateFormat.parse(dateAdded);
            simpleDateFormat.applyPattern(Utils.CLEAN_DATE_FORMAT);
            return simpleDateFormat.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void delete() {
        this.isDeleted = true;
    }

}
