package com.s_diadamo.readlist.shelf;

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

import com.s_diadamo.readlist.R;

import java.util.ArrayList;
import java.util.List;


public class ShelfFragment extends Fragment {
    View rootView;
    ShelfOperations shelfOperations;
    ListView shelfListView;
    ArrayAdapter<String> shelfArrayAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_listview, container, false);

        setHasOptionsMenu(true);

        shelfListView = (ListView) rootView.findViewById(R.id.general_list_view);
        shelfOperations = new ShelfOperations(container.getContext());
        List<Shelf> shelves = shelfOperations.getAllShelves();

        ArrayList<String> values = new ArrayList<String>();

        // TODO: Going to need to modify this when shelves are more than names

        for (int i = 0; i < shelves.size(); i++) {
            values.add(shelves.get(i).getName());
        }

        shelfArrayAdapter = new ArrayAdapter<String>(getActivity(),
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_shelf, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        long id = item.getItemId();

        if (id == R.id.add_shelf) {
            ShelfAddDialog shelfAddDialog = new ShelfAddDialog(rootView.getContext(), shelfOperations, shelfArrayAdapter);
            shelfAddDialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

