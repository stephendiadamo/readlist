package com.s_diadamo.readlist.book;

import android.graphics.drawable.ColorDrawable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Book {

    int id;
    String title;
    String author;
    int shelfId = 0;
    String dateAdded;
    int numPages;
    int currentPage = 0;
    int colour;
    Boolean complete = false;
    String completionDate = "";
    String coverPictureUrl = "";

    public Book() {
    }

    public Book(int id, String title, String author, int shelfId, String dateAdded, int numPages, int currentPage, int colour, int complete, String completionDate, String coverPictureUrl) {
        this.id = id;
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

    public Book(int id, String title, String author, int shelfId, String dateAdded, int numPages, int currentPage, int complete, String completionDate, String coverPictureUrl) {
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
    }

    public Book(String title, String author, int shelfId, String dateAdded, int numPages, int currentPage, int complete, String completionDate, String coverPictureUrl) {
        this.title = title;
        this.author = author;
        this.shelfId = shelfId;
        this.dateAdded = dateAdded;
        this.numPages = numPages;
        this.currentPage = currentPage;
        this.complete = (complete == 1);
        this.completionDate = completionDate;
        this.coverPictureUrl = coverPictureUrl;
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
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getShelfId() {
        return shelfId;
    }

    public void setShelfId(int shelfId) {
        this.shelfId = shelfId;
    }

    public String getDateAdded() {
        return dateAdded;
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

    public int getColour() {
        return colour;
    }

    public void setColour(int colour) {
        this.colour = colour;
    }

    public Boolean getComplete() {
        return complete;
    }

    public void setComplete(Boolean complete) {
        this.complete = complete;
        this.setCompletionDate(getCurrentDate());
    }

    public String getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(String completionDate) {
        this.completionDate = completionDate;
    }

    public String getCoverPictureUrl() {
        return coverPictureUrl;
    }

    public void setCoverPictureUrl(String coverPictureUrl) {
        this.coverPictureUrl = coverPictureUrl;
    }

    public static String getCurrentDate() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.CANADA);
        return simpleDateFormat.format(cal.getTime());
    }

    public ColorDrawable getColorAsDrawalbe() {
        return new ColorDrawable(this.colour);
    }
}
