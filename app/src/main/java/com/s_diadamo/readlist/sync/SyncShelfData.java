package com.s_diadamo.readlist.sync;


import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.s_diadamo.readlist.R;
import com.s_diadamo.readlist.database.DatabaseHelper;
import com.s_diadamo.readlist.general.Utils;
import com.s_diadamo.readlist.navigationDrawer.NavigationDrawerFragment;
import com.s_diadamo.readlist.shelf.Shelf;
import com.s_diadamo.readlist.shelf.ShelfOperations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

class SyncShelfData extends SyncData {
    private final ShelfOperations shelfOperations;

    SyncShelfData(Context context) {
        super(context, true);
        shelfOperations = new ShelfOperations(context);
    }

    SyncShelfData(Context context, boolean showSpinner) {
        super(context, showSpinner);
        shelfOperations = new ShelfOperations(context);
    }

    void syncAllShelves() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(TYPE_SHELF);
        query.whereEqualTo(Utils.USER_NAME, userName);
        if (showSpinner)
            syncSpinner.addThread();
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseShelves, ParseException e) {
                if (showSpinner)
                    syncSpinner.endThread();
                ArrayList<Shelf> shelvesOnDevice = shelfOperations.getAllShelves();
                ArrayList<Shelf> shelvesFromParse = new ArrayList<>();
                for (ParseObject parseShelf : parseShelves) {
                    Shelf shelf = parseShelfToShelf(parseShelf);
                    shelvesFromParse.add(shelf);
                }
                updateDeviceShelves(shelvesOnDevice, shelvesFromParse, parseShelves);
                updateParseShelves(shelvesOnDevice, shelvesFromParse);
            }
        });
    }

    void syncAllShelves(final AppCompatActivity activity) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(TYPE_SHELF);
        query.whereEqualTo(Utils.USER_NAME, userName);
        if (showSpinner)
            syncSpinner.addThread();
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseShelves, ParseException e) {
                if (showSpinner)
                    syncSpinner.endThread();
                ArrayList<Shelf> shelvesOnDevice = shelfOperations.getAllShelves();
                ArrayList<Shelf> shelvesFromParse = new ArrayList<>();
                for (ParseObject parseShelf : parseShelves) {
                    Shelf shelf = parseShelfToShelf(parseShelf);
                    shelvesFromParse.add(shelf);
                }
                updateDeviceShelves(shelvesOnDevice, shelvesFromParse, parseShelves);
                updateParseShelves(shelvesOnDevice, shelvesFromParse);
                ((NavigationDrawerFragment) activity.getSupportFragmentManager().
                        findFragmentById(R.id.navigation_drawer)).resetAdapter();
            }
        });
    }

    private void updateDeviceShelves(ArrayList<Shelf> shelvesOnDevice, ArrayList<Shelf> shelvesFromParse, List<ParseObject> parseShelves) {
        HashSet<Integer> deviceShelfIds = new HashSet<>();
        for (Shelf shelf : shelvesOnDevice) {
            deviceShelfIds.add(shelf.getId());
        }

        int i = 0;
        for (Shelf shelf : shelvesFromParse) {
            if (!deviceShelfIds.contains(shelf.getId())) {
                shelfOperations.addShelf(shelf);
                copyShelfValues(parseShelves.get(i), shelf);
                parseShelves.get(i).saveEventually();
            }
            i++;
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
                if (shelf.isDeleted()) {
                    shelfOperations.deleteShelf(shelf);
                } else {
                    shelvesToSend.add(toParseShelf(shelf));
                }
            } else {
                if (shelf.isDeleted()) {
                    deleteParseShelf(shelf);
                    shelfOperations.deleteShelf(shelf);
                } else {
                    updateParseShelf(shelf);
                }
            }
        }

        ParseObject.saveAllInBackground(shelvesToSend);
    }

    void updateParseShelf(final Shelf shelf) {
        queryForShelf(shelf, new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> shelfList, ParseException e) {
                if (shelfList != null && shelfList.size() > 0) {
                    ParseObject shelfToUpdate = shelfList.get(0);
                    copyShelfValues(shelfToUpdate, shelf);
                    shelfToUpdate.saveEventually();
                }
            }
        });
    }

    void deleteParseShelf(Shelf shelf) {
        queryForShelf(shelf, new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> shelfList, ParseException e) {
                if (shelfList != null && shelfList.size() > 0) {
                    ParseObject shelfToDelete = shelfList.get(0);
                    shelfToDelete.deleteEventually();
                }
            }
        });
    }

    ParseObject toParseShelf(Shelf shelf) {
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

    private void queryForShelf(Shelf shelf, FindCallback<ParseObject> callback) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(TYPE_SHELF);
        query.whereEqualTo(Utils.USER_NAME, userName);
        query.whereEqualTo(READLIST_ID, shelf.getId());
        query.findInBackground(callback);
    }
}
