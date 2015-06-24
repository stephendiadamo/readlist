package com.s_diadamo.readlist.goal;

public class Goal {

    private int id;
    private String type;
    private int amount;
    private String deadline;
    private boolean isComplete;
    public static final String BOOK_GOAL = "book_goal";
    public static final String PAGE_GOAL = "page_goal";

    public Goal(int id, String type, int amount, String deadline, int isComplete) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.deadline = deadline;
        this.isComplete = (isComplete == 1);
    }

    public Goal(String type, int amount, String deadline, int isComplete) {
        this.type = type;
        this.amount = amount;
        this.deadline = deadline;
        this.isComplete = (isComplete == 1);
    }

    public Goal(String type, int amount, String deadline) {
        this.type = type;
        this.amount = amount;
        this.deadline = deadline;
        this.isComplete = false;
    }

    public Goal newBookGoal(int amount, String deadline) {
        return new Goal(BOOK_GOAL, amount, deadline);
    }

    public Goal newPageGoal(int amount, String deadline) {
        return new Goal(PAGE_GOAL, amount, deadline);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
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

}
