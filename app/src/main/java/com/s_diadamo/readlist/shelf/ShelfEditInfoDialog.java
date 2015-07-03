package com.s_diadamo.readlist.shelf;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.s_diadamo.readlist.ColourPickerDialog;
import com.s_diadamo.readlist.R;
import com.s_diadamo.readlist.book.Book;
import com.s_diadamo.readlist.book.BookAdapter;
import com.s_diadamo.readlist.navigationDrawer.NavigationDrawerFragment;


public class ShelfEditInfoDialog extends AlertDialog {

    public ShelfEditInfoDialog(final Context context, final Shelf shelf, final ActionBar actionBar, final BookAdapter adapter, final NavigationDrawerFragment navDrawer) {
        super(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        final View content = inflater.inflate(R.layout.dialog_edit_shelf, null);

        setTitle("Edit Shelf");

        final ImageView colourView = (ImageView) content.findViewById(R.id.edit_shelf_colour_selector);
        colourView.setBackground(new ColorDrawable(shelf.getColour()));
        colourView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColourPickerDialog colourPickerDialog = new ColourPickerDialog(context, shelf, colourView);
                colourPickerDialog.show();
            }
        });

        final EditText shelfEditText = (EditText) content.findViewById(R.id.edit_shelf_shelf_name);
        shelfEditText.setText(shelf.getName());

        Button done = (Button) content.findViewById(R.id.edit_shelf_done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shelf.setName(shelfEditText.getText().toString());
                ShelfOperations operations = new ShelfOperations(context);
                operations.updateShelf(shelf);
                actionBar.setTitle(shelf.getName());

                for (int i = 0; i < adapter.getCount(); i++) {
                    adapter.getItem(i).setColour(shelf.getColour());
                }

                adapter.notifyDataSetChanged();
                navDrawer.updateShelfFromExpandableList(shelf);

                dismiss();
            }
        });

        setView(content);
    }
}
