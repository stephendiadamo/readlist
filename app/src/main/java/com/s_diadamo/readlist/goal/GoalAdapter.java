package com.s_diadamo.readlist.goal;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.s_diadamo.readlist.R;

import java.util.ArrayList;

class GoalAdapter extends ArrayAdapter<Goal> {

    private final Context context;
    private final int layoutResourceID;
    private final ArrayList<Goal> goals;


    public GoalAdapter(Context context, ArrayList<Goal> goals) {
        super(context, R.layout.row_goal_element, goals);
        this.context = context;
        this.layoutResourceID = R.layout.row_goal_element;
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
            goalHolder.goalProgressBar = (ProgressBar) row.findViewById(R.id.goal_progress);
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
        if (goal.getType() == Goal.BOOK_GOAL) {
            goalHolder.goalAmount.setText(String.valueOf(goal.getAmount()) + " books");
        } else {
            goalHolder.goalAmount.setText(String.valueOf(goal.getAmount()) + " pages");
        }

        if (goal.getAmount() > 0) {
            int normalizedProgress = goal.getProgress(context) * 100 / goal.getAmount();
            if (normalizedProgress <= 100) {
                goalHolder.goalProgressBar.setProgress(normalizedProgress);
            } else {
                goalHolder.goalProgressBar.setProgress(100);
            }
        } else {
            goalHolder.goalProgressBar.setProgress(0);
        }
        return row;
    }

    public void hideCompletedGoals() {
        for (int i = 0; i < goals.size(); i++) {
            if (goals.get(i).isComplete()) {
                goals.remove(i);
                i--;
            }
        }
        notifyDataSetChanged();
        notifyDataSetInvalidated();
    }

    static class GoalHolder {
        ImageView goalIcon;
        TextView goalStartDate;
        TextView goalEndDate;
        TextView goalAmount;
        ProgressBar goalProgressBar;
    }
}
