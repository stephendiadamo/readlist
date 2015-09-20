package com.s_diadamo.readlist.search;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.s_diadamo.readlist.general.Utils;
import com.s_diadamo.readlist.book.Book;
import com.s_diadamo.readlist.shelf.Shelf;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

class SearchResultJSONParser {

    public static ArrayList<Book> getBooksFromJSONResponse(JSONObject jsonData, Shelf shelf) {
        ArrayList<Book> books = new ArrayList<>();
        try {
            JSONArray items = jsonData.getJSONArray("items");

            for (int i = 0; i < items.length(); i++) {
                JSONObject jsonBook = items.getJSONObject(i).getJSONObject("volumeInfo");
                Book book = new Book();

                book.setTitle(jsonBook.getString("title"));
                book.setAuthor(cleanAuthors(jsonBook.getJSONArray("authors")));
                if (jsonBook.has("pageCount")) {
                    book.setNumPages(jsonBook.getInt("pageCount"));
                } else {
                    book.setNumPages(0);
                }
                book.setCoverPictureUrl(jsonBook.getJSONObject("imageLinks").getString("smallThumbnail"));
                book.setDateAdded(Utils.getCurrentDate());
                book.setShelfId(shelf.getId());
                book.setColour(shelf.getColour());
                books.add(book);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return books;
    }

    private static String cleanAuthors(JSONArray jsonAuthors) throws JSONException {
        StringBuilder authors = new StringBuilder();
        for (int i = 0; i < jsonAuthors.length() - 1; i++) {
            authors.append(jsonAuthors.getString(i));
            authors.append(", ");
        }
        authors.append(jsonAuthors.getString(jsonAuthors.length() - 1));
        return authors.toString();
    }
}
