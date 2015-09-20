package com.s_diadamo.readlist.sync;


import android.content.Context;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.s_diadamo.readlist.comment.Comment;
import com.s_diadamo.readlist.comment.CommentOperations;
import com.s_diadamo.readlist.database.DatabaseHelper;
import com.s_diadamo.readlist.general.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

class SyncCommentData extends SyncData {

    private final CommentOperations commentOperations;

    public SyncCommentData(Context context) {
        super(context);
        commentOperations = new CommentOperations(context);
    }

    public SyncCommentData(Context context, boolean showSpinner) {
        super(context, showSpinner);
        commentOperations = new CommentOperations(context);
    }

    public void syncAllComments() {
        if (showSpinner)
            syncSpinner.addThread();

        ParseQuery<ParseObject> query = ParseQuery.getQuery(TYPE_COMMENT);
        query.whereEqualTo(Utils.USER_NAME, userName);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseComments, ParseException e) {
                if (showSpinner)
                    syncSpinner.endThread();

                ArrayList<Comment> commentsOnDevice = commentOperations.getComments();
                ArrayList<Comment> commentsFromParse = new ArrayList<>();
                for (ParseObject parseComment : parseComments) {
                    Comment comment = parseCommentToComment(parseComment);
                    commentsFromParse.add(comment);
                }
                updateDeviceComments(commentsOnDevice, commentsFromParse, parseComments);
                updateParseComments(commentsOnDevice, commentsFromParse);
            }
        });
    }

    private void updateDeviceComments(ArrayList<Comment> commentsOnDevice, ArrayList<Comment> commentsFromParse, List<ParseObject> parseComments) {
        HashMap<Integer, Integer> deviceCommentIds = new HashMap<>();
        int i = 0;
        for (Comment comment : commentsOnDevice) {
            deviceCommentIds.put(comment.getId(), i);
            ++i;
        }

        i = 0;
        for (Comment comment : commentsFromParse) {
            if (!deviceCommentIds.containsKey(comment.getId())) {
                commentOperations.addComment(comment);
                copyCommentValues(parseComments.get(i), comment);
                parseComments.get(i).saveEventually();
            } else {
                Comment comparison = commentsOnDevice.get(deviceCommentIds.get(comment.getId()));
                if (!comment.getDateAdded().equals(comparison.getDateAdded())) {
                    commentOperations.addComment(comment);
                    copyCommentValues(parseComments.get(i), comment);
                    parseComments.get(i).saveEventually();
                }
            }
            ++i;
        }
    }

    private void updateParseComments(ArrayList<Comment> commentsOnDevice, ArrayList<Comment> commentsFromParse) {
        HashSet<Integer> parseCommentIds = new HashSet<>();
        for (Comment comment : commentsFromParse) {
            parseCommentIds.add(comment.getId());
        }

        ArrayList<ParseObject> commentsToSend = new ArrayList<>();

        for (final Comment comment : commentsOnDevice) {
            if (!parseCommentIds.contains(comment.getId())) {
                if (comment.isDeleted()) {
                    commentOperations.deleteComment(comment);
                } else {
                    commentsToSend.add(toParseComment(comment));
                }
            } else {
                if (comment.isDeleted()) {
                    deleteParseComment(comment);
                    commentOperations.deleteComment(comment);
                } else {
                    updateParseComment(comment);
                }
            }
        }

        ParseObject.saveAllInBackground(commentsToSend);
    }

    void updateParseComment(final Comment comment) {
        queryForComment(comment, new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (list != null && list.size() > 0) {
                    ParseObject commentToUpdate = list.get(0);
                    copyCommentValues(commentToUpdate, comment);
                    commentToUpdate.saveEventually();
                }
            }
        });
    }

    void deleteParseComment(Comment comment) {
        queryForComment(comment, new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (list != null && list.size() > 0) {
                    ParseObject commentToDelete = list.get(0);
                    commentToDelete.deleteEventually();
                }
            }
        });
    }

    private void queryForComment(Comment comment, FindCallback<ParseObject> callback) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(TYPE_COMMENT);
        query.whereEqualTo(Utils.USER_NAME, userName);
        query.whereEqualTo(READLIST_ID, comment.getId());
        query.findInBackground(callback);
    }

    private void copyCommentValues(ParseObject parseComment, Comment comment) {
        parseComment.put(READLIST_ID, comment.getId());
        parseComment.put(DatabaseHelper.COMMENT_BOOK_ID, comment.getBookId());
        parseComment.put(DatabaseHelper.COMMENT_COMMENT, comment.getComment());
        parseComment.put(DatabaseHelper.COMMENT_DATE_ADDED, comment.getDateAdded());
    }

    private Comment parseCommentToComment(ParseObject parseComment) {
        return new Comment(
                parseComment.getInt(READLIST_ID),
                parseComment.getInt(DatabaseHelper.COMMENT_BOOK_ID),
                parseComment.getString(DatabaseHelper.COMMENT_COMMENT),
                parseComment.getString(DatabaseHelper.COMMENT_DATE_ADDED));
    }

    ParseObject toParseComment(Comment comment) {
        ParseObject parseComment = new ParseObject(TYPE_COMMENT);

        parseComment.put(Utils.USER_NAME, userName);
        parseComment.put(READLIST_ID, comment.getId());
        parseComment.put(DatabaseHelper.COMMENT_BOOK_ID, comment.getBookId());
        parseComment.put(DatabaseHelper.COMMENT_COMMENT, comment.getComment());
        parseComment.put(DatabaseHelper.COMMENT_DATE_ADDED, comment.getDateAdded());

        return parseComment;
    }
}
