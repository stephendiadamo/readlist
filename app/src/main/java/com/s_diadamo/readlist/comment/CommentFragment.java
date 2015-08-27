package com.s_diadamo.readlist.comment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.Loader;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.s_diadamo.readlist.R;
import com.s_diadamo.readlist.general.Utils;
import com.s_diadamo.readlist.sync.SyncData;

import java.util.ArrayList;

public class CommentFragment extends Fragment implements LoaderManager.LoaderCallbacks {
    private View rootView;
    private Context context;
    private ListView commentListView;
    private CommentAdapter commentAdapter;
    private CommentOperations commentOperations;
    private int bookId;
    private ArrayList<Comment> comments;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_comment_book, container, false);
        context = rootView.getContext();

        setHasOptionsMenu(false);

        commentListView = (ListView) rootView.findViewById(R.id.comment_book_comments_list);
        commentOperations = new CommentOperations(context);

        setBookId();

        final EditText commentBox = (EditText) rootView.findViewById(R.id.comment_book_comment_box);
        Button addComment = (Button) rootView.findViewById(R.id.comment_book_add_comment);

        addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentString = commentBox.getText().toString();
                if (commentString.isEmpty()) {
                    Utils.showToast(context, "Please add a comment");
                    return;
                }

                Comment comment = new Comment(bookId, commentString);
                comments.add(comment);
                commentAdapter.notifyDataSetChanged();
                commentOperations.addComment(comment);
                commentBox.setText("");

                if (Utils.checkUserIsLoggedIn(context)) {
                    new SyncData(context).add(comment);
                }
            }
        });


        getLoaderManager().initLoader(CommentLoader.ID, null, this);
        return rootView;
    }

    private void setBookId() {
        Bundle args = getArguments();
        if (args != null) {
            bookId = args.getInt(Comment.COMMENT_BOOK_ID);
        } else {
            Utils.showToast(context, "Something bad happened");
        }
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
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final Comment comment = commentAdapter.getItem(info.position);
        switch (item.getItemId()) {
            case R.id.edit:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                LayoutInflater inflater = LayoutInflater.from(context);
                final View editCommentDialog = inflater.inflate(R.layout.dialog_edit_comment, null);
                final EditText editCommentBox = (EditText) editCommentDialog.findViewById(R.id.edit_comment);
                editCommentBox.setText(comment.getComment());
                alertDialog.setView(editCommentDialog);
                alertDialog.setTitle(R.string.edit_comment);
                alertDialog
                        .setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                comment.setComment(editCommentBox.getText().toString());
                                commentOperations.updateComment(comment);
                                if (Utils.checkUserIsLoggedIn(context)) {
                                    new SyncData(context).update(comment);
                                }
                                commentAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .show();
                return true;
            case R.id.delete:
                new AlertDialog.Builder(context)
                        .setMessage(getString(R.string.delete_comment) + "?")
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (commentAdapter != null) {
                                    if (Utils.checkUserIsLoggedIn(context)) {
                                        new SyncData(context).delete(comment);
                                        commentOperations.deleteComment(comment);
                                    } else {
                                        comment.delete();
                                        commentOperations.updateComment(comment);
                                    }
                                    commentAdapter.remove(comment);
                                }
                            }
                        })
                        .setNegativeButton(R.string.no, null)
                        .show();
                return true;
        }
        return super.onContextItemSelected(item);
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
                comments = (ArrayList<Comment>) data;
                commentAdapter = new CommentAdapter(context, comments);
                commentListView.setEmptyView(rootView.findViewById(R.id.comment_book_empty_view));
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
