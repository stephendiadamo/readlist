package com.s_diadamo.readlist.search;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.s_diadamo.readlist.book.Book;

import java.io.IOException;
import java.util.ArrayList;

public class SearchResultJSONParser {

    public static ArrayList<Book> getBooksFromJSONResponse(String response) {
        ArrayList<Book> books = new ArrayList<Book>();
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
                            while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                                attributeName = jsonParser.getCurrentName();
                                if (attributeName.equals("title")) {
                                    jsonParser.nextToken();
                                    book.setTitle(jsonParser.getText());
                                } else if (attributeName.equals("authors")) {
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
                                } else if (attributeName.equals("pageCount")) {
                                    jsonParser.nextToken();
                                    book.setNumPages(Integer.parseInt(jsonParser.getText()));
                                } else if (attributeName.equals("imageLinks")) {
                                    jsonParser.nextToken();
                                    jsonParser.nextToken();
                                    jsonParser.nextToken();
                                    book.setCoverPictureUrl(jsonParser.getText());
                                    jsonParser.nextToken();
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
