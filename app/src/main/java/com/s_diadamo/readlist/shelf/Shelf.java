package com.s_diadamo.readlist.shelf;

public class Shelf {

    public static final String DEFAULT_COLOR = "#111";

    int id;
    String name;
    String colour;

    public Shelf() {
    }

    public Shelf(int id, String name, String color) {
        this.id = id;
        this.name = name;
        this.colour = color;
    }

    public Shelf(String name, String color) {
        this.name = name;
        this.colour = color;
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
        this.name = name;
    }

    public String getColour() {
        return this.colour;
    }

    public void setColour(String color) {
        this.colour = color;
    }
}
