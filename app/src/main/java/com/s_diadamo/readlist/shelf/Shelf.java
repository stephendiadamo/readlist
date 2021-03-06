package com.s_diadamo.readlist.shelf;

import android.content.Context;
import android.graphics.Color;

import com.s_diadamo.readlist.book.Book;

import java.util.ArrayList;

public class Shelf {

    public static final int DEFAULT_COLOR = Color.WHITE;
    public static final String SHELF_ID = "SHELF_ID";
    public static final int DEFAULT_SHELF_ID = 1;

    private int id;
    private String name;
    private int colour;
    private boolean isDeleted;

    public Shelf() {
    }

    public Shelf(int id, String name, int color) {
        this.id = id;
        this.name = name;
        this.colour = color;
        this.isDeleted = false;
    }

    public Shelf(int id, String name, int color, int isDeleted) {
        this.id = id;
        this.name = name;
        this.colour = color;
        this.isDeleted = (isDeleted == 1);
    }

    public Shelf(String name, int color) {
        this.name = name;
        this.colour = color;
        this.isDeleted = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name.trim();
    }

    public int getColour() {
        return this.colour;
    }

    public void setColour(int color) {
        this.colour = color;
    }

    public void delete() {
        this.isDeleted = true;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public ArrayList<Book> fetchBooks(Context context) {
        ShelfOperations shelfOperations = new ShelfOperations(context);
        ArrayList<Book> books;
        if (id == DEFAULT_SHELF_ID) {
            books = shelfOperations.getAllBooksWithShelf();
        } else {
            books = shelfOperations.getBooksWithShelf(this.id);
        }

        return books;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
