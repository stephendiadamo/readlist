package com.s_diadamo.readlist.shelf;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import android.widget.Toast;

import com.s_diadamo.readlist.book.BookFragment;
import com.s_diadamo.readlist.general.MainActivity;
import com.s_diadamo.readlist.R;
import com.s_diadamo.readlist.general.Utils;
import com.s_diadamo.readlist.navigationDrawer.NavigationDrawerFragment;

public class ShelfAddEditFragment extends Fragment {

    public static final String EDIT_MODE = "EDIT_MODE";

    private View rootView;
    private Shelf shelf;
    private ShelfOperations shelfOperations;
    private EditText shelfEditText;
    private boolean isEditMode = false;
    private int currentSelectedColour;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_add_edit_shelf, container, false);

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
                (ImageView) rootView.findViewById(R.id.color_pallet_white),
                (ImageView) rootView.findViewById(R.id.color_pallet_light_green),
                (ImageView) rootView.findViewById(R.id.color_pallet_light_orange)
        };

        final ImageView selectors[] = {
                (ImageView) rootView.findViewById(R.id.color_pallet_dark_red_selected),
                (ImageView) rootView.findViewById(R.id.color_pallet_dark_blue_selected),
                (ImageView) rootView.findViewById(R.id.color_pallet_purple_selected),
                (ImageView) rootView.findViewById(R.id.color_pallet_dark_green_selected),
                (ImageView) rootView.findViewById(R.id.color_pallet_dark_orange_selected),
                (ImageView) rootView.findViewById(R.id.color_pallet_light_red_selected),
                (ImageView) rootView.findViewById(R.id.color_pallet_light_blue_selected),
                (ImageView) rootView.findViewById(R.id.color_pallet_white_selected),
                (ImageView) rootView.findViewById(R.id.color_pallet_light_green_selected),
                (ImageView) rootView.findViewById(R.id.color_pallet_light_orange_selected)
        };

        View.OnClickListener listener = (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectors[currentSelectedColour].setVisibility(View.GONE);
                currentSelectedColour = colourToSelectorMap(v.getId());
                selectors[currentSelectedColour].setVisibility(View.VISIBLE);

                int colourId;
                if (v.getId() != R.id.color_pallet_white) {
                    colourId = ((ColorDrawable) v.getBackground()).getColor();
                } else {
                    colourId = Shelf.DEFAULT_COLOR;
                }
                shelf.setColour(colourId);
            }
        });

        int i = 0;
        for (ImageView colour : colours) {
            colour.setOnClickListener(listener);
            if (((ColorDrawable) colour.getBackground()).getColor() == shelf.getColour()) {
                selectors[i].setVisibility(View.VISIBLE);
            }
            i++;
        }

        if (isEditMode) {
            shelfEditText.setText(shelf.getName());
        }

        ((MainActivity) getActivity()).closeDrawer();

        ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (ab != null) {
            if (isEditMode) {
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
        String shelfName = shelfEditText.getText().toString();

        if (shelfName.isEmpty()) {
            Toast.makeText(rootView.getContext(), "Please enter a shelf name.", Toast.LENGTH_LONG).show();
            return;
        }

        shelf.setName(shelfName);
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

        Fragment bookFragment = new BookFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Shelf.SHELF_ID, String.valueOf(shelf.getId()));
        bookFragment.setArguments(bundle);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        for (int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
            fragmentManager.popBackStack();
        }

        fragmentManager.beginTransaction()
                .replace(R.id.container, bookFragment)
                .commit();
    }

    private int colourToSelectorMap(int colourId) {
        switch (colourId) {
            case R.id.color_pallet_dark_red:
                return 0;
            case R.id.color_pallet_dark_blue:
                return 1;
            case R.id.color_pallet_purple:
                return 2;
            case R.id.color_pallet_dark_green:
                return 3;
            case R.id.color_pallet_dark_orange:
                return 4;
            case R.id.color_pallet_light_red:
                return 5;
            case R.id.color_pallet_light_blue:
                return 6;
            case R.id.color_pallet_white:
                return 7;
            case R.id.color_pallet_light_green:
                return 8;
            case R.id.color_pallet_light_orange:
                return 9;
            default:
                return 7;
        }
    }
}
