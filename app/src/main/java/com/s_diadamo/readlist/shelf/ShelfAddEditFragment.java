package com.s_diadamo.readlist.shelf;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.s_diadamo.readlist.MainActivity;
import com.s_diadamo.readlist.R;
import com.s_diadamo.readlist.Utils;
import com.s_diadamo.readlist.navigationDrawer.NavigationDrawerFragment;

public class ShelfAddEditFragment extends Fragment {

    public static final String EDIT_MODE = "EDIT_MODE";

    private Shelf shelf;
    private ShelfOperations shelfOperations;
    private EditText shelfEditText;
    private boolean isEditMode = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_edit_shelf, container, false);

        shelfOperations = new ShelfOperations(rootView.getContext());

        setHasOptionsMenu(true);
        setUpShelf();
        setMode();

        shelfEditText = (EditText) rootView.findViewById(R.id.edit_shelf_shelf_name);

        final ImageView colours[] = {
                (ImageView) rootView.findViewById(R.id.color_pallet_dark_red),
                (ImageView) rootView.findViewById(R.id.color_pallet_dark_blue),
                (ImageView) rootView.findViewById(R.id.color_pallet_purple),
                (ImageView) rootView.findViewById(R.id.color_pallet_dark_green),
                (ImageView) rootView.findViewById(R.id.color_pallet_dark_orange),
                (ImageView) rootView.findViewById(R.id.color_pallet_light_red),
                (ImageView) rootView.findViewById(R.id.color_pallet_light_blue),
                (ImageView) rootView.findViewById(R.id.color_pallet_dark_gray),
                (ImageView) rootView.findViewById(R.id.color_pallet_light_green),
                (ImageView) rootView.findViewById(R.id.color_pallet_light_orange)
        };

        final ViewGroup.LayoutParams defaultParams = colours[0].getLayoutParams();
        final ViewGroup.LayoutParams selectedParams = colours[1].getLayoutParams();
        selectedParams.width = selectedParams.width + 15;
        selectedParams.height = selectedParams.height + 15;
        colours[1].setLayoutParams(defaultParams);

        View.OnClickListener listener = (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (ImageView colour : colours) {
                    colour.setLayoutParams(defaultParams);
                }

                v.setLayoutParams(selectedParams);

                int colourId = ((ColorDrawable) v.getBackground()).getColor();
                shelf.setColour(colourId);
            }
        });

        for (ImageView colour : colours) {
            colour.setOnClickListener(listener);
        }

        if (isEditMode) {
            shelfEditText.setText(shelf.getName());
        }

        ((MainActivity) getActivity()).closeDrawer();

        ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (ab != null) {
            if (isEditMode){
                ab.setTitle(shelf.getName());
            } else {
                ab.setTitle("Add Shelf");
            }
        }

        return rootView;
    }

    private void setUpShelf() {
        Bundle args = getArguments();
        String stringShelfId = "";
        int shelfId;
        if (args != null) {
            stringShelfId = args.getString(Shelf.SHELF_ID);
        }
        if (!stringShelfId.isEmpty()) {
            shelfId = Integer.parseInt(stringShelfId);
            shelf = shelfOperations.getShelf(shelfId);
        } else {
            shelf = new Shelf();
        }
    }

    private void setMode() {
        Bundle args = getArguments();
        if (args != null) {
            if (!args.getString(EDIT_MODE).isEmpty()) {
                isEditMode = true;
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_general_add, menu);
        Bundle args = getArguments();
        if (args != null) {
            if (!args.getString(EDIT_MODE).isEmpty()) {
                menu.findItem(R.id.add).setTitle(R.string.done);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        long id = item.getItemId();

        if (id == R.id.add) {
            addShelf();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addShelf() {
        shelf.setName(shelfEditText.getText().toString());
        if (isEditMode) {
            shelfOperations.updateShelf(shelf);
            ((NavigationDrawerFragment) getActivity().getSupportFragmentManager().
                    findFragmentById(R.id.navigation_drawer)).updateShelfFromExpandableList(shelf);

        } else {
            shelfOperations.addShelf(shelf);
            ((NavigationDrawerFragment) getActivity().getSupportFragmentManager().
                    findFragmentById(R.id.navigation_drawer)).addShelf(shelf);

        }

        Utils.hideKeyBoard(getActivity());
        Utils.launchBookFragment(getActivity().getSupportFragmentManager());
    }
}
