package com.s_diadamo.readlist.shelf;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.s_diadamo.readlist.R;
import com.s_diadamo.readlist.navigationDrawer.NavigationExpandableListAdapter;

public class ShelfAddDialog extends AlertDialog {
    public ShelfAddDialog(Context context, final NavigationExpandableListAdapter shelfArrayAdapter) {
        super(context);

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        final View content = layoutInflater.inflate(R.layout.dialog_add_shelf, null);

        final ShelfOperations shelfOperations = new ShelfOperations(context);

        setTitle("New Shelf");

        final EditText shelfNameEditText = (EditText) content.findViewById(R.id.shelf_name_edit_text);
        final Button addShelfButton = (Button) content.findViewById(R.id.shelf_add_button);

        addShelfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String shelfName = shelfNameEditText.getText().toString();
                if (!shelfName.isEmpty()) {
                    Shelf shelf = new Shelf(shelfName, Shelf.DEFAULT_COLOR);
                    shelfOperations.addShelf(shelf);
                    shelfArrayAdapter.addShelf(shelf.getName());
                    dismiss();
                } else {
                    Toast.makeText(content.getContext(), "Please enter a shelf name", Toast.LENGTH_LONG).show();
                }
            }
        });

        setView(content);
    }

}
