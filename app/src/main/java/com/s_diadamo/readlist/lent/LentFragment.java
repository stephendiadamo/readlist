package com.s_diadamo.readlist.lent;


import android.support.v4.app.LoaderManager;
import android.content.Context;
import android.support.v4.content.Loader;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.s_diadamo.readlist.R;

import java.util.ArrayList;

public class LentFragment extends Fragment implements LoaderManager.LoaderCallbacks {

    private Context context;
    private ArrayList<LentBook> lentBooks;
    private LentBookAdapter lentBookAdapter;
    private ListView lentBookListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_listview, container, false);
        context = rootView.getContext();

        setHasOptionsMenu(false);

        lentBookListView = (ListView) rootView.findViewById(R.id.general_list_view);
        getLoaderManager().initLoader(LentBookLoader.ID, null, this);

        new LentBookOperations(context).getLentBooks();


        return rootView;
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
                lentBooks = (ArrayList<LentBook>) data;
                lentBookAdapter = new LentBookAdapter(context, R.layout.row_lent_book_element, lentBooks);
                lentBookListView.setAdapter(lentBookAdapter);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
    }
}
