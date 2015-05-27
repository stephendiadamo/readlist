package com.s_diadamo.readlist;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;


public class ShelvesFragment extends Fragment {
    View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_shelves, container, false);
        final ListView shelfListView = (ListView) rootView.findViewById(R.id.shelf_list_view);

        DatabaseHelper databaseHelper = new DatabaseHelper(container.getContext());
        List<Shelf> shelves = databaseHelper.getAllShelves();

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
}

