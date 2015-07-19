package com.s_diadamo.readlist.sync;

import android.content.Context;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.s_diadamo.readlist.database.DatabaseHelper;
import com.s_diadamo.readlist.general.Utils;
import com.s_diadamo.readlist.goal.Goal;
import com.s_diadamo.readlist.goal.GoalOperations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SyncGoalData extends SyncData {
    private final GoalOperations goalOperations;

    public SyncGoalData(Context context) {
        super(context, true);
        goalOperations = new GoalOperations(context);
    }

    public SyncGoalData(Context context, boolean showSpinner) {
        super(context, showSpinner);
        goalOperations = new GoalOperations(context);
    }

    void syncAllGoals() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(TYPE_GOAL);
        query.whereEqualTo(Utils.USER_NAME, userName);
        if (showSpinner)
            syncSpinner.addThread();
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseGoals, ParseException e) {
                if (showSpinner)
                    syncSpinner.endThread();

                ArrayList<Goal> goalsOnDevice = goalOperations.getAllGoals();
                ArrayList<Goal> goalsFromParse = new ArrayList<>();
                for (ParseObject parseGoal : parseGoals) {
                    Goal goal = parseGoalToGoal(parseGoal);
                    goalsFromParse.add(goal);
                }
                updateDeviceGoals(goalsOnDevice, goalsFromParse);
                updateParseGoals(goalsOnDevice, goalsFromParse);
            }
        });
    }

    private void updateDeviceGoals(ArrayList<Goal> goalsOnDevice, ArrayList<Goal> goalsFromParse) {
        HashSet<Integer> deviceGoalIds = new HashSet<>();
        for (Goal goal : goalsOnDevice) {
            deviceGoalIds.add(goal.getId());
        }

        for (Goal goal : goalsFromParse) {
            if (!deviceGoalIds.contains(goal.getId())) {
                goalOperations.addGoal(goal);
            }
        }
    }

    private void updateParseGoals(ArrayList<Goal> goalsOnDevice, ArrayList<Goal> goalsFromParse) {
        HashSet<Integer> parseGoalIds = new HashSet<>();
        for (Goal goal : goalsFromParse) {
            parseGoalIds.add(goal.getId());
        }

        ArrayList<ParseObject> goalsToSend = new ArrayList<>();

        for (final Goal goal : goalsOnDevice) {
            if (!parseGoalIds.contains(goal.getId())) {
                if (goal.isDeleted()) {
                    goalOperations.deleteGoal(goal);
                } else {
                    goalsToSend.add(toParseGoal(goal));
                }
            } else {
                if (goal.isDeleted()) {
                    deleteParseGoal(goal);
                    goalOperations.deleteGoal(goal);
                } else {
                    updateParseGoal(goal);
                }
            }
        }

        ParseObject.saveAllInBackground(goalsToSend);
    }

    public void updateParseGoal(final Goal goal) {
        queryForGoal(goal, new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> goalList, ParseException e) {
                if (goalList.size() > 0) {
                    ParseObject goalToUpdate = goalList.get(0);
                    copyGoalValues(goalToUpdate, goal);
                    goalToUpdate.saveEventually();
                }
            }
        });
    }

    public void deleteParseGoal(Goal goal) {
        queryForGoal(goal, new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> goalList, ParseException e) {
                if (goalList.size() > 0) {
                    ParseObject goalToDelete = goalList.get(0);
                    goalToDelete.deleteEventually();
                }
            }
        });
    }

    ParseObject toParseGoal(Goal goal) {
        ParseObject parseGoal = new ParseObject(TYPE_GOAL);

        parseGoal.put(Utils.USER_NAME, userName);
        parseGoal.put(READLIST_ID, goal.getId());
        parseGoal.put(DatabaseHelper.GOAL_TYPE, goal.getType());
        parseGoal.put(DatabaseHelper.GOAL_AMOUNT, goal.getAmount());
        parseGoal.put(DatabaseHelper.GOAL_START_DATE, goal.getStartDate());
        parseGoal.put(DatabaseHelper.GOAL_END_DATE, goal.getEndDate());
        parseGoal.put(DatabaseHelper.GOAL_IS_COMPLETE, goal.isComplete());

        return parseGoal;
    }

    private void copyGoalValues(ParseObject parseGoal, Goal goal) {
        parseGoal.put(DatabaseHelper.GOAL_TYPE, goal.getType());
        parseGoal.put(DatabaseHelper.GOAL_AMOUNT, goal.getAmount());
        parseGoal.put(DatabaseHelper.GOAL_START_DATE, goal.getStartDate());
        parseGoal.put(DatabaseHelper.GOAL_END_DATE, goal.getEndDate());
        parseGoal.put(DatabaseHelper.GOAL_IS_COMPLETE, goal.isComplete());
    }

    private Goal parseGoalToGoal(ParseObject parseGoal) {
        return new Goal(
                parseGoal.getInt(READLIST_ID),
                parseGoal.getInt(DatabaseHelper.GOAL_TYPE),
                parseGoal.getInt(DatabaseHelper.GOAL_AMOUNT),
                parseGoal.getString(DatabaseHelper.GOAL_START_DATE),
                parseGoal.getString(DatabaseHelper.GOAL_END_DATE),
                parseGoal.getInt(DatabaseHelper.GOAL_IS_COMPLETE));
    }

    private void queryForGoal(Goal goal, FindCallback<ParseObject> callback) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(TYPE_GOAL);
        query.whereEqualTo(Utils.USER_NAME, userName);
        query.whereEqualTo(READLIST_ID, goal.getId());
        query.findInBackground(callback);
    }
}
