package com.s_diadamo.readlist;

import java.util.Calendar;

public class Book {

    int _id;
    String _title;
    int _shelf;
    String _date_added;
    int _num_pages;
    int _current_page;
    String _tile_color;
    Boolean _complete;
    String _cover_picture_url;

    public Book() {
    }

    public Book(int id, String title, int shelf, String dateAdded, int numPages, int currentPage, String tileColor, int complete, String coverPictureUrl) {
        this._id = id;
        this._title = title;
        this._shelf = shelf;
        this._date_added = dateAdded;
        this._num_pages = numPages;
        this._current_page = currentPage;
        this._tile_color = tileColor;
        this._complete = (complete == 1);
        this._cover_picture_url = coverPictureUrl;
    }

    public Book(String title, int shelf, String dateAdded, int numPages, int currentPage, String tileColor, int complete, String coverPictureUrl) {
        this._title = title;
        this._shelf = shelf;
        this._date_added = dateAdded;
        this._num_pages = numPages;
        this._current_page = currentPage;
        this._tile_color = tileColor;
        this._complete = (complete == 1);
        this._cover_picture_url = coverPictureUrl;
    }

    public String getTitle() {
        return _title;
    }

    public void setTitle(String _title) {
        this._title = _title;
    }

    public int getID() {
        return _id;
    }

    public void setID(int id) {
        this._id = id;
    }

    public int getShelf() {
        return _shelf;
    }

    public void setShelf(int _shelf) {
        this._shelf = _shelf;
    }

    public int getNumPages() {
        return _num_pages;
    }

    public void setNumPages(int _num_pages) {
        this._num_pages = _num_pages;
    }

    public int getCurrentPage() {
        return _current_page;
    }

    public void setCurrentPage(int _current_page) {
        this._current_page = _current_page;
    }

    public String getTileColor() {
        return _tile_color;
    }

    public void setTileColor(String _tile_color) {
        this._tile_color = _tile_color;
    }

    public int getComplete() {
        return _complete ? 1 : 0;
    }

    public void setComplete(Boolean _complete) {
        this._complete = _complete;
    }

    public String getCoverPictureURL() {
        return _cover_picture_url;
    }

    public void setCoverPictureURL(String _cover_picture_url) {
        this._cover_picture_url = _cover_picture_url;
    }

    public String getDateAdded() {
        return this._date_added;
    }

//    private String getCurrentDate() {
//        Calendar cal = Calendar.getInstance();
//        return cal.getTime().toString();
//    }

}
