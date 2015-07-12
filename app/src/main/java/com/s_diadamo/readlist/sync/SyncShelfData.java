package com.s_diadamo.readlist.sync;


import android.content.Context;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.s_diadamo.readlist.database.DatabaseHelper;
import com.s_diadamo.readlist.general.Utils;
import com.s_diadamo.readlist.shelf.Shelf;
import com.s_diadamo.readlist.shelf.ShelfOperations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SyncShelfData extends SyncData {

    public SyncShelfData(Context context) {
        super(context);
    }

    protected void syncAllShelves() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(TYPE_SHELF);
        query.whereEqualTo(Utils.USER_NAME, userName);
        syncSpinner.addThread();
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseShelves, ParseException e) {
                syncSpinner.endThread();
                ArrayList<Shelf> shelvesOnDevice = new ShelfOperations(context).getAllShelves();
                ArrayList<Shelf> shelvesFromParse = new ArrayList<>();
                for (ParseObject parseShelf : parseShelves) {
                    Shelf shelf = parseShelfToShelf(parseShelf);
                    shelvesFromParse.add(shelf);
                }
                updateDeviceShelves(shelvesOnDevice, shelvesFromParse);
                updateParseShelves(shelvesOnDevice, shelvesFromParse);
            }
        });
    }

    private void updateDeviceShelves(ArrayList<Shelf> shelvesOnDevice, ArrayList<Shelf> shelvesFromParse) {
        HashSet<Integer> deviceShelfIds = new HashSet<>();
        for (Shelf shelf : shelvesOnDevice) {
            deviceShelfIds.add(shelf.getId());
        }

        ShelfOperations shelfOperations = new ShelfOperations(context);

        for (Shelf shelf : shelvesFromParse) {
            if (!deviceShelfIds.contains(shelf.getId())) {
                shelfOperations.addShelf(shelf);
            }
        }
    }

    private void updateParseShelves(ArrayList<Shelf> shelvesOnDevice, ArrayList<Shelf> shelvesFromParse) {
        HashSet<Integer> parseShelfIds = new HashSet<>();
        for (Shelf shelf : shelvesFromParse) {
            parseShelfIds.add(shelf.getId());
        }

        ArrayList<ParseObject> shelvesToSend = new ArrayList<>();

        for (final Shelf shelf : shelvesOnDevice) {
            if (!parseShelfIds.contains(shelf.getId()) && shelf.getId() != Shelf.DEFAULT_SHELF_ID) {
                shelvesToSend.add(toParseShelf(shelf));
            } else {
                updateParseShelf(shelf);
            }
        }

        ParseObject.saveAllInBackground(shelvesToSend);
    }

    public void updateParseShelf(final Shelf shelf) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(TYPE_SHELF);
        query.whereEqualTo(Utils.USER_NAME, userName);
        query.whereEqualTo(READLIST_ID, shelf.getId());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> shelfList, ParseException e) {
                if (shelfList.size() > 0) {
                    ParseObject shelfToUpdate = shelfList.get(0);
                    copyShelfValues(shelfToUpdate, shelf);
                    shelfToUpdate.saveEventually();
                }
            }
        });
    }

    protected ParseObject toParseShelf(Shelf shelf) {
        ParseObject parseShelf = new ParseObject(TYPE_SHELF);

        parseShelf.put(Utils.USER_NAME, userName);
        parseShelf.put(READLIST_ID, shelf.getId());
        parseShelf.put(DatabaseHelper.SHELF_NAME, shelf.getName());
        parseShelf.put(DatabaseHelper.SHELF_COLOR, shelf.getColour());

        return parseShelf;
    }

    private Shelf parseShelfToShelf(ParseObject parseShelf) {
        return new Shelf(
                parseShelf.getInt(READLIST_ID),
                parseShelf.getString(DatabaseHelper.SHELF_NAME),
                parseShelf.getInt(DatabaseHelper.SHELF_COLOR));
    }

    private void copyShelfValues(ParseObject parseShelf, Shelf shelf) {
        parseShelf.put(DatabaseHelper.SHELF_NAME, shelf.getName());
        parseShelf.put(DatabaseHelper.SHELF_COLOR, shelf.getColour());
    }
}
