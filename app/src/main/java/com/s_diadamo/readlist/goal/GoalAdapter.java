package com.s_diadamo.readlist.goal;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.s_diadamo.readlist.R;

import java.util.ArrayList;
import java.util.Objects;

public class GoalAdapter extends ArrayAdapter<Goal> {

    private final Context context;
    private final int layoutResourceID;
    private final ArrayList<Goal> goals;


    public GoalAdapter(Context context, int layoutResourceID, ArrayList<Goal> goals) {
        super(context, layoutResourceID, goals);
        this.context = context;
        this.layoutResourceID = layoutResourceID;
        this.goals = goals;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        GoalHolder goalHolder;

        if (row == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            row = inflater.inflate(layoutResourceID, parent, false);

            goalHolder = new GoalHolder();
            goalHolder.goalIcon = (ImageView) row.findViewById(R.id.goal_icon);
            goalHolder.goalStartDate = (TextView) row.findViewById(R.id.goal_start_date);
            goalHolder.goalEndDate = (TextView) row.findViewById(R.id.goal_end_date);
            goalHolder.goalProgressMade = (TextView) row.findViewById(R.id.goal_progress_made);
            goalHolder.goalAmount = (TextView) row.findViewById(R.id.goal_amount);

            row.setTag(goalHolder);
        } else {
            goalHolder = (GoalHolder) row.getTag();
        }

        Goal goal = goals.get(position);
        if (goal.getType() == Goal.BOOK_GOAL) {
            goalHolder.goalIcon.setImageResource(R.drawable.ic_book);
        } else {
            goalHolder.goalIcon.setImageResource(R.drawable.ic_pages);
        }

        goalHolder.goalStartDate.setText(goal.getCleanStartDate());
        goalHolder.goalEndDate.setText(goal.getCleanEndDate());
        goalHolder.goalProgressMade.setText(String.valueOf(goal.getProgress(context)));
        goalHolder.goalAmount.setText(String.valueOf(goal.getAmount()));

        return row;
    }

    static class GoalHolder {
        ImageView goalIcon;
        TextView goalStartDate;
        TextView goalEndDate;
        TextView goalProgressMade;
        TextView goalAmount;
    }
}
