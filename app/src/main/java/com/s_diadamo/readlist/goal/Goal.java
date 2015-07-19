package com.s_diadamo.readlist.goal;

import android.content.Context;

import com.s_diadamo.readlist.general.Utils;
import com.s_diadamo.readlist.sync.SyncGoalData;
import com.s_diadamo.readlist.updates.BookUpdateOperations;
import com.s_diadamo.readlist.updates.PageUpdateOperations;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Goal {

    private int id;
    private final int type;
    private final int amount;
    private final String startDate;
    private final String endDate;
    private boolean isComplete;
    private boolean isDeleted = false;
    public static final int BOOK_GOAL = 0;
    private static final int PAGE_GOAL = 1;
    private static final int END_DATE = 0;
    private static final int START_DATE = 1;


    public Goal(int id, int type, int amount, String startDate, String endDate, int isComplete, int isDeleted) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isComplete = (isComplete == 1);
        this.isDeleted = (isDeleted == 1);
    }

    public Goal(int id, int type, int amount, String startDate, String endDate, int isComplete) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isComplete = (isComplete == 1);
    }

    public Goal(int type, int amount, String startDate, String endDate) {
        this.type = type;
        this.amount = amount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isComplete = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public int getAmount() {
        return amount;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public boolean isComplete() {
        return isComplete;
    }

    private void markComplete() {
        isComplete = true;
    }

    public void delete() {
        this.isDeleted = true;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public int getProgress(Context context) {
        int progress;
        if (type == PAGE_GOAL) {
            progress = new PageUpdateOperations(context).getNumberOfPagesReadBetweenDates(startDate, endDate);
        } else {
            progress = new BookUpdateOperations(context).getNumberOfBooksReadBetweenDates(startDate, endDate);
        }
        if (progress >= amount && !isComplete) {
            markComplete();
            new GoalOperations(context).updateGoal(this);
            if (Utils.checkUserIsLoggedIn(context)) {
                new SyncGoalData(context).updateParseGoal(this);
            }
        }
        return progress;
    }

    public String getCleanEndDate() {
        return getCleanDate(END_DATE);
    }

    public String getCleanStartDate() {
        return getCleanDate(START_DATE);
    }

    private String getCleanDate(int type) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Utils.DATE_FORMAT, Locale.CANADA);
        try {
            Date d;
            if (type == END_DATE) {
                d = simpleDateFormat.parse(endDate);
            } else {
                d = simpleDateFormat.parse(startDate);
            }
            simpleDateFormat.applyPattern(Utils.CLEAN_DATE_FORMAT);
            return simpleDateFormat.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

}
