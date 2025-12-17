package com.example.expensetracker.model;

public class ListItem {
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_EXPENSE = 1;

    public int type;
    public String header; // date string for header
    public Expense expense;

    public static ListItem header(String header) {
        ListItem li = new ListItem();
        li.type = TYPE_HEADER;
        li.header = header;
        return li;
    }

    public static ListItem item(Expense expense) {
        ListItem li = new ListItem();
        li.type = TYPE_EXPENSE;
        li.expense = expense;
        return li;
    }
}

