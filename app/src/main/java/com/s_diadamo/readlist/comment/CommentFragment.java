package com.s_diadamo.readlist.comment;

import android.content.Context;
import android.support.v4.content.Loader;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.s_diadamo.readlist.R;

import java.util.ArrayList;

public class CommentFragment extends Fragment implements LoaderManager.LoaderCallbacks {
    private Context context;
    private ListView commentListView;
    private CommentAdapter commentAdapter;
    private CommentOperations commentOperations;
    private int bookId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_comment_book, container, false);
        context = rootView.getContext();

        setHasOptionsMenu(false);

        commentListView = (ListView) rootView.findViewById(R.id.comment_book_list);
        getLoaderManager().initLoader(CommentLoader.ID, null, this);


        //TODO: Fetch bookId from bundle

        return rootView;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.clear();
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_general_delete, menu);
    }


    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        switch (id) {
            case CommentLoader.ID:
                return new CommentLoader(context, bookId);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        int id = loader.getId();
        switch (id) {
            case CommentLoader.ID:
                ArrayList<Comment> comments = (ArrayList<Comment>) data;
                commentAdapter = new CommentAdapter(context, R.layout.row_comment_element, comments);
                commentListView.setAdapter(commentAdapter);

                registerForContextMenu(commentListView);
                commentListView.setLongClickable(false);
                commentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        getActivity().openContextMenu(view);
                    }
                });
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
    }
}
