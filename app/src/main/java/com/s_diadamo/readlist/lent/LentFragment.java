package com.s_diadamo.readlist.lent;


import android.support.v4.app.LoaderManager;
import android.content.Context;
import android.support.v4.content.Loader;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.s_diadamo.readlist.R;
import com.s_diadamo.readlist.general.Utils;
import com.s_diadamo.readlist.sync.SyncData;

import java.util.ArrayList;

public class LentFragment extends Fragment implements LoaderManager.LoaderCallbacks {
    private Context context;
    private ListView lentBookListView;
    private LentBookAdapter lentBookAdapter;
    private LentBookOperations lentBookOperations;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_listview, container, false);
        context = rootView.getContext();

        setHasOptionsMenu(false);
        lentBookOperations = new LentBookOperations(context);

        lentBookListView = (ListView) rootView.findViewById(R.id.general_list_view);
        getLoaderManager().initLoader(LentBookLoader.ID, null, this);

        ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (ab != null) {
            ab.setTitle(getResources().getString(R.string.lent_books));
        }

        return rootView;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.clear();
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_general_delete_edit, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (lentBookAdapter == null) {
            Utils.showToast(context, "Loading has not finished, please try again");
            return false;
        }

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        LentBook lentBook = lentBookAdapter.getItem(info.position);
        switch (item.getItemId()) {
            case R.id.edit:
                LentBookDialog lentBookDialog = new LentBookDialog(context, lentBook);
                lentBookDialog.setLentBookAdapter(lentBookAdapter);
                lentBookDialog.show();
                return true;
            case R.id.delete:
                if (Utils.checkUserIsLoggedIn(context)) {
                    new SyncData(context).delete(lentBook);
                    lentBookOperations.deleteLentBook(lentBook);
                } else {
                    lentBook.delete();
                    lentBookOperations.updateLentBook(lentBook);
                }

                lentBookAdapter.remove(lentBook);

                return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LentBookLoader.ID:
                return new LentBookLoader(context);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        int id = loader.getId();
        switch (id) {
            case LentBookLoader.ID:
                ArrayList<LentBook> lentBooks = (ArrayList<LentBook>) data;
                lentBookAdapter = new LentBookAdapter(context, lentBooks);
                lentBookListView.setAdapter(lentBookAdapter);

                registerForContextMenu(lentBookListView);
                lentBookListView.setLongClickable(false);
                lentBookListView.setOnItemClickListener(
                        new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                getActivity().openContextMenu(view);
                            }
                        }
                );

                break;
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
    }
}
