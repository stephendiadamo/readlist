package com.s_diadamo.readlist.book;

import java.util.Calendar;

public class Book {

    int id;
    String title;
    String author;
    int shelf = 0;
    String dateAdded;
    int numPages;
    int currentPage = 0;
    String tileColor;
    Boolean complete = false;
    String completionDate = "";
    String coverPictureUrl = "";

    public Book() {
    }

    public Book(int id, String title, String author, int shelf, String dateAdded, int numPages, int currentPage, String tileColor, int complete, String coverPictureUrl) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.shelf = shelf;
        this.dateAdded = dateAdded;
        this.numPages = numPages;
        this.currentPage = currentPage;
        this.tileColor = tileColor;
        this.complete = (complete == 1);
        this.coverPictureUrl = coverPictureUrl;
    }

    public Book(String title, String author, int shelf, String dateAdded, int numPages, int currentPage, String tileColor, int complete, String coverPictureUrl) {
        this.title = title;
        this.author = author;
        this.shelf = shelf;
        this.dateAdded = dateAdded;
        this.numPages = numPages;
        this.currentPage = currentPage;
        this.tileColor = tileColor;
        this.complete = (complete == 1);
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

    public int getShelf() {
        return shelf;
    }

    public void setShelf(int shelf) {
        this.shelf = shelf;
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

    public String getTileColor() {
        return tileColor;
    }

    public void setTileColor(String tileColor) {
        this.tileColor = tileColor;
    }

    public Boolean getComplete() {
        return complete;
    }

    public void setComplete(Boolean complete) {
        this.complete = complete;
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

//    private String getCurrentDate() {
//        Calendar cal = Calendar.getInstance();
//        return cal.getTime().toString();
//    }

}
