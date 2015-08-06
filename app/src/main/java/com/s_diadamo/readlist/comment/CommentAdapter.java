package com.s_diadamo.readlist.comment;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.s_diadamo.readlist.R;

import java.util.ArrayList;

public class CommentAdapter extends ArrayAdapter<Comment> {

    private final Context context;
    private final int resourceId;
    private final ArrayList<Comment> comments;

    public CommentAdapter(Context context, int resourceId, ArrayList<Comment> comments) {
        super(context, resourceId, comments);
        this.context = context;
        this.resourceId = resourceId;
        this.comments = comments;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        CommentHolder commentHolder;
        if (row == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            row = inflater.inflate(resourceId, parent, false);

            commentHolder = new CommentHolder();
            commentHolder.dateAdded = (TextView) row.findViewById(R.id.comment_date_added);
            commentHolder.comment = (TextView) row.findViewById(R.id.comment_comment);

            row.setTag(commentHolder);
        } else {
            commentHolder = (CommentHolder) row.getTag();
        }

        Comment comment = comments.get(position);
        commentHolder.dateAdded.setText(comment.getCleanDate());
        commentHolder.comment.setText(comment.getComment());

        return row;
    }

    static class CommentHolder {
        TextView dateAdded;
        TextView comment;
    }
}
