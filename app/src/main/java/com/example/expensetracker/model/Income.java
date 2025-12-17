package com.example.expensetracker.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "incomes")
public class Income {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public double amount;
    public String source;
    public long date;

    public Income(double amount, String source, long date) {
        this.amount = amount;
        this.source = source;
        this.date = date;
    }

    public Income() {}
}

