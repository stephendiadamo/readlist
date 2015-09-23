package com.s_diadamo.readlist.syncExternalData;

import android.content.Context;

import com.s_diadamo.readlist.book.Book;
import com.s_diadamo.readlist.book.BookOperations;
import com.s_diadamo.readlist.general.Utils;
import com.s_diadamo.readlist.shelf.Shelf;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GoogleJSONResultParser {
    public static void parseGoogleJSONBookData(Context context, JSONObject jsonData) {
        try {
            JSONArray items = jsonData.getJSONArray("items");
            BookOperations bookOperations = new BookOperations(context);
            for (int i = 0; i < items.length(); i++) {
                JSONObject jsonBook = items.getJSONObject(i).getJSONObject("volumeInfo");
                Book book = new Book();

                //TODO: Use the bloody ISBN code you fool!

                book.setTitle(jsonBook.getString("title"));
                book.setAuthor(cleanAuthors(jsonBook.getJSONArray("authors")));
                if (jsonBook.has("pageCount")) {
                    book.setNumPages(jsonBook.getInt("pageCount"));
                } else {
                    book.setNumPages(0);
                }
                book.setCoverPictureUrl(jsonBook.getJSONObject("imageLinks").getString("smallThumbnail"));
                book.setDateAdded(Utils.getCurrentDate());
                book.setShelfId(Shelf.DEFAULT_SHELF_ID);
                book.setColour(Shelf.DEFAULT_COLOR);

                if (!bookOperations.hasSimilarBook(book)) {
                    bookOperations.addBook(book);
                }
            }
            Utils.showToast(context, "Your Google Play books have been added");
        } catch (JSONException e) {
            e.printStackTrace();
            Utils.showToast(context, "Your Google Play books failed to load");
        }
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
