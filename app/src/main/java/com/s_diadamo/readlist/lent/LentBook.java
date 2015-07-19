package com.s_diadamo.readlist.lent;

import com.s_diadamo.readlist.book.Book;
import com.s_diadamo.readlist.general.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LentBook extends Book {
    private int bookId;
    private String lentTo;
    private String dateLent;
    private boolean isDeleted = false;

    public LentBook(int id, int bookId, String lentTo, String dateLent, int isDeleted) {
        super.setId(id);
        this.bookId = bookId;
        this.lentTo = lentTo;
        this.dateLent = dateLent;
        this.isDeleted = (isDeleted == 1);
    }

    public LentBook(int bookId, String lentTo, String date) {
        this.bookId = bookId;
        this.lentTo = lentTo;
        this.dateLent = date;
    }

    public LentBook(int id, String bookTitle, String bookCoverUrl, String lentTo, String dateLent) {
        super.setId(id);
        super.setTitle(bookTitle);
        super.setCoverPictureUrl(bookCoverUrl);
        this.lentTo = lentTo;
        this.dateLent = dateLent;
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

    public String getCleanDateLent() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Utils.DATE_FORMAT, Locale.CANADA);
        try {
            Date d = simpleDateFormat.parse(dateLent);
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

    public void setLentTo(String lentTo) {
        this.lentTo = lentTo;
    }

    public void setDateLent(String dateLent) {
        this.dateLent = dateLent;
    }
}
