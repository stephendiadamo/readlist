package com.s_diadamo.readlist.shelf;

public class Shelf {

    public static final String DEFAULT_COLOR = "#111";

    int _id;
    String _name;
    String _color;

    public Shelf() {
    }

    public Shelf(int id, String name, String color) {
        this._id = id;
        this._name = name;
        this._color = color;
    }

    public Shelf(String name, String color) {
        this._name = name;
        this._color = color;
    }

    public int getID() {
        return _id;
    }

    public void setID(int id) {
        this._id = id;
    }

    public String getName() {
        return this._name;
    }

    public void setName(String name) {
        this._name = name;
    }

    public String getColor() {
        return this._color;
    }

    public void setColor(String color) {
        this._color = color;
    }
}
