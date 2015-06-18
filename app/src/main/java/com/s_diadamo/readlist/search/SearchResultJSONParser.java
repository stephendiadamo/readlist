package com.s_diadamo.readlist.search;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.s_diadamo.readlist.Utils;
import com.s_diadamo.readlist.book.Book;
import com.s_diadamo.readlist.shelf.Shelf;

import java.io.IOException;
import java.util.ArrayList;

class SearchResultJSONParser {

    public static ArrayList<Book> getBooksFromJSONResponse(String response, Shelf shelf) {
        ArrayList<Book> books = new ArrayList<>();
        try {
            JsonParser jsonParser = new JsonFactory().createParser(response);
            jsonParser.nextToken();
            while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                String attributeName = jsonParser.getCurrentName();
                if (attributeName.equals("kind")) {
                    jsonParser.nextToken();
                } else if (attributeName.equals("items")) {
                    jsonParser.nextToken();
                    while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                        jsonParser.nextToken();
                        attributeName = jsonParser.getCurrentName();
                        if (attributeName.equals("volumeInfo")) {
                            jsonParser.nextToken();
                            Book book = new Book();
                            book.setDateAdded(Utils.getCurrentDate());
                            book.setShelfId(shelf.getId());
                            book.setColour(shelf.getColour());
                            while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                                attributeName = jsonParser.getCurrentName();
                                switch (attributeName) {
                                    case "title":
                                        jsonParser.nextToken();
                                        book.setTitle(jsonParser.getText());
                                        break;
                                    case "authors":
                                        jsonParser.nextToken();
                                        StringBuilder stringBuilder = new StringBuilder();
                                        while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                                            stringBuilder.append(jsonParser.getText());
                                            stringBuilder.append(", ");
                                        }
                                        String authors = stringBuilder.toString();
                                        if (!authors.isEmpty()) {
                                            book.setAuthor(authors.substring(0, authors.length() - 2));
                                        }
                                        break;
                                    case "pageCount":
                                        jsonParser.nextToken();
                                        book.setNumPages(Integer.parseInt(jsonParser.getText()));
                                        break;
                                    case "imageLinks":
                                        jsonParser.nextToken();
                                        jsonParser.nextToken();
                                        jsonParser.nextToken();
                                        book.setCoverPictureUrl(jsonParser.getText());
                                        jsonParser.nextToken();
                                        break;
                                }
                            }
                            books.add(book);
                            jsonParser.nextToken();
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return books;
    }
}
