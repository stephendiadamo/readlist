package com.s_diadamo.readlist.goal;

import android.content.Context;

import com.s_diadamo.readlist.Utils;
import com.s_diadamo.readlist.book.BookOperations;
import com.s_diadamo.readlist.updates.UpdateOperations;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Goal {

    private int id;
    private int type;
    private int amount;
    private String startDate;
    private String endDate;
    private boolean isComplete;
    public static final int BOOK_GOAL = 0;
    public static final int PAGE_GOAL = 1;
    private static final int END_DATE = 0;
    private static final int START_DATE = 1;

    public Goal(int id, int type, int amount, String startDate, String endDate, int isComplete) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isComplete = (isComplete == 1);
    }

    public Goal(int type, int amount, String startDate, String endDate, int isComplete) {
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

    public void markComplete() {
        isComplete = true;
    }

    public void markIncomplete() {
        isComplete = false;
    }

    public int getProgress(Context context) {
        int progress;
        if (type == PAGE_GOAL) {
            progress = (new UpdateOperations(context)).getNumberOfPagesReadBetweenDates(startDate, endDate);
        } else {
            progress = (new BookOperations(context)).getNumberOfBooksReadBetweenDates(startDate, endDate);
        }
        if (progress >= amount && !isComplete) {
            markComplete();
            (new GoalOperations(context)).updateGoal(this);
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
