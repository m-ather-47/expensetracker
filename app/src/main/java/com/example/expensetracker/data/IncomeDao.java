package com.example.expensetracker.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.expensetracker.model.Income;

import java.util.List;

@Dao
public interface IncomeDao {
    @Insert
    long insert(Income income);

    @Query("SELECT SUM(amount) FROM incomes")
    Double getTotalIncome();

    @Query("SELECT * FROM incomes ORDER BY date DESC")
    List<Income> getAll();

    @Query("SELECT SUM(amount) FROM incomes WHERE date BETWEEN :start AND :end")
    Double getTotalBetween(long start, long end);
}
