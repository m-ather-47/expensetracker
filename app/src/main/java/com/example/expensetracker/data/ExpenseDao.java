package com.example.expensetracker.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.expensetracker.model.Expense;
import com.example.expensetracker.model.CategorySummary;

import java.util.List;

@Dao
public interface ExpenseDao {
    @Insert
    long insert(Expense expense);

    @Update
    void update(Expense expense);

    @Delete
    void delete(Expense expense);

    @Query("SELECT * FROM expenses ORDER BY date DESC")
    List<Expense> getAll();

    @Query("SELECT * FROM expenses WHERE id = :id LIMIT 1")
    Expense getById(int id);

    @Query("SELECT SUM(amount) FROM expenses")
    Double getTotalExpenses();

    @Query("SELECT SUM(amount) FROM expenses WHERE date BETWEEN :start AND :end")
    Double getTotalBetween(long start, long end);

    @Query("SELECT category AS category, SUM(amount) AS total FROM expenses WHERE date BETWEEN :start AND :end GROUP BY category")
    List<CategorySummary> getCategoryTotalsBetween(long start, long end);
}
