package com.s_diadamo.readlist;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;


public class ShelvesFragment extends Fragment {
    View rootView;
    DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_shelves, container, false);
        setHasOptionsMenu(true);
        final ListView shelfListView = (ListView) rootView.findViewById(R.id.shelf_list_view);

        dbHelper = new DatabaseHelper(container.getContext());
        List<Shelf> shelves = dbHelper.getAllShelves();

        String[] values = new String[shelves.size()];

        for (int i = 0; i < shelves.size(); i++) {
            values[i] = shelves.get(i).getName();
        }

        ArrayAdapter<String> shelfArrayAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, values);
        shelfListView.setAdapter(shelfArrayAdapter);

        shelfListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String s = (String) shelfListView.getItemAtPosition(position);
                        Toast.makeText(view.getContext(), s, Toast.LENGTH_LONG).show();
                    }
                }
        );
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflator) {
        super.onCreateOptionsMenu(menu, inflator);
        menu.clear();
        inflator.inflate(R.menu.add_shelf, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        long id = item.getItemId();

        if (id == R.id.add_shelf) {
            addNewShelf();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addNewShelf() {
        final Dialog addShelfDialog = new Dialog(rootView.getContext());
        addShelfDialog.setContentView(R.layout.add_shelf);
        addShelfDialog.setTitle("New Shelf");

        final Button addShelfButton = (Button) addShelfDialog.findViewById(R.id.add_shelf_button);
        addShelfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText shelfNameEditText = (EditText) addShelfDialog.findViewById(R.id.shelf_name_edit_text);
                String shelfName = shelfNameEditText.getText().toString();

                if (!shelfName.isEmpty()) {
                    Shelf shelf = new Shelf(shelfName, Shelf.DEFAULT_COLOR);
                    dbHelper.addShelf(shelf);
                    addShelfDialog.dismiss();
                } else {
                    Toast.makeText(v.getContext(), "Please enter a shelf name", Toast.LENGTH_LONG).show();
                }
            }
        });
        addShelfDialog.show();
    }

}

