package com.example.expensetracker.model;

import androidx.room.ColumnInfo;

public class CategorySummary {
    @ColumnInfo(name = "category")
    public String category;

    @ColumnInfo(name = "total")
    public Double total;

    public CategorySummary(String category, Double total) {
        this.category = category;
        this.total = total;
    }
}

