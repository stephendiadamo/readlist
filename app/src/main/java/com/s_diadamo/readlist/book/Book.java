package com.s_diadamo.readlist.book;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import com.s_diadamo.readlist.general.Utils;
import com.s_diadamo.readlist.lent.LentBook;
import com.s_diadamo.readlist.lent.LentBookOperations;
import com.s_diadamo.readlist.shelf.Shelf;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Book {

    private int id;
    private String title;
    private String author;
    private int shelfId = 0;
    private String dateAdded;
    private int numPages;
    private int currentPage = 0;
    private int colour;
    private Boolean complete = false;
    private String completionDate = "";
    private String coverPictureUrl = "";
    private Boolean isDeleted = false;
    private double rating = -1;

    public Book() {
    }

    public Book(int id, String title, String author, int shelfId, String dateAdded, int numPages, int currentPage, int complete, String completionDate, String coverPictureUrl, double rating, int isDeleted) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.shelfId = shelfId;
        this.dateAdded = dateAdded;
        this.numPages = numPages;
        this.currentPage = currentPage;
        this.complete = (complete == 1);
        this.completionDate = completionDate;
        this.coverPictureUrl = coverPictureUrl;
        this.rating = rating;
        this.isDeleted = (isDeleted == 1);
    }

    public Book(String title, String author, int shelfId, String dateAdded, int numPages, int currentPage, int colour, int complete, String completionDate, String coverPictureUrl) {
        this.title = title;
        this.author = author;
        this.shelfId = shelfId;
        this.dateAdded = dateAdded;
        this.numPages = numPages;
        this.currentPage = currentPage;
        this.colour = colour;
        this.complete = (complete == 1);
        this.completionDate = completionDate;
        this.coverPictureUrl = coverPictureUrl;
    }

    public Book(String title, String author, int shelfId, String dateAdded, int numPages, int currentPage, int colour, String completionDate, String coverPictureUrl) {
        this.title = title;
        this.author = author;
        this.shelfId = shelfId;
        this.dateAdded = dateAdded;
        this.numPages = numPages;
        this.currentPage = currentPage;
        this.colour = colour;
        this.completionDate = completionDate;
        this.coverPictureUrl = coverPictureUrl;
    }

    public Book(int id, String title, String author, int shelfId, String dateAdded, int numPages, int currentPage, int complete, String completionDate, String coverPictureUrl, double rating) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.shelfId = shelfId;
        this.dateAdded = dateAdded;
        this.numPages = numPages;
        this.currentPage = currentPage;
        this.complete = (complete == 1);
        this.completionDate = completionDate;
        this.coverPictureUrl = coverPictureUrl;
        this.rating = rating;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title.trim();
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author.trim();
    }

    public int getShelfId() {
        return shelfId;
    }

    public void setShelfId(int shelfId) {
        this.shelfId = shelfId;
    }

    public String getDateAdded() {
        return this.dateAdded;
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
            Date d = simpleDateFormat.parse(dateAdded);
            simpleDateFormat.applyPattern(Utils.CLEAN_DATE_FORMAT);
            return simpleDateFormat.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    public int getNumPages() {
        return numPages;
    }

    public void setNumPages(int numPages) {
        this.numPages = numPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public void setColour(int colour) {
        this.colour = colour;
    }

    public Boolean isComplete() {
        return complete;
    }

    public void markComplete() {
        this.complete = true;
        this.setCompletionDate(Utils.getCurrentDate());
    }

    public String getCompletionDate() {
        return this.completionDate;
    }

    public String getCleanCompletionDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Utils.DATE_FORMAT, Locale.CANADA);
        try {
            Date d = simpleDateFormat.parse(completionDate);
            simpleDateFormat.applyPattern(Utils.CLEAN_DATE_FORMAT);
            return simpleDateFormat.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void setCompletionDate(String completionDate) {
        this.completionDate = completionDate;
    }

    public String getCoverPictureUrl() {
        return coverPictureUrl;
    }

    public void setCoverPictureUrl(String coverPictureUrl) {
        this.coverPictureUrl = coverPictureUrl;
    }

    public ColorDrawable getColorAsDrawable() {
        // For retention...
        if (this.shelfId == Shelf.DEFAULT_SHELF_ID) {
            return new ColorDrawable(Shelf.DEFAULT_COLOR);
        }

        return new ColorDrawable(this.colour);
    }

    public void reread() {
        this.completionDate = "";
        this.currentPage = 0;
        this.complete = false;
    }

    public void deleteLentBook(Context context) {
        LentBookOperations lentBookOperations = new LentBookOperations(context);
        LentBook lentBook = lentBookOperations.getLentBook(this);
        if (lentBook != null) {
            lentBook.delete();
            lentBookOperations.updateLentBook(lentBook);
        }
    }

    public LentBook getLentBook(Context context) {
        return new LentBookOperations(context).getLentBook(this);
    }

    public boolean isLent(Context context) {
        return new LentBookOperations(context).getLentBook(this) != null;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public double getRating() {
        return rating;
    }

}
