package com.example.expensetracker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.adapter.ExpenseAdapter;
import com.example.expensetracker.data.AppDatabase;
import com.example.expensetracker.model.Expense;
import com.example.expensetracker.model.ListItem;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExpenseListActivity extends BaseActivity {

    private ExpenseAdapter adapter;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private TextView tvEmpty;
    private TextView tvBalance;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // inflate into the shared base that contains the BottomNavigationView
        setActivityLayout(R.layout.activity_expense_list);

        RecyclerView rv = findViewById(R.id.rvExpenses);
        tvEmpty = findViewById(R.id.tvEmpty);
        tvBalance = findViewById(R.id.tvBalance);
        adapter = new ExpenseAdapter();
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rv.setAdapter(adapter);

        //noinspection deprecation
        findViewById(R.id.fabAddExpense).setOnClickListener(v -> startActivityForResult(new Intent(this, AddExpenseActivity.class), 100));

        findViewById(R.id.btnManageIncome).setOnClickListener(v -> startActivity(new Intent(this, IncomeActivity.class)));

        adapter.setOnItemActionListener(new ExpenseAdapter.OnItemActionListener() {
            @Override
            public void onEdit(Expense expense) {
                Intent i = new Intent(ExpenseListActivity.this, AddExpenseActivity.class);
                i.putExtra(AddExpenseActivity.EXTRA_EXPENSE_ID, expense.id);
                //noinspection deprecation
                startActivityForResult(i, 100);
            }

            @Override
            public void onDelete(Expense expense) {
                executor.execute(() -> {
                    AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                    db.expenseDao().delete(expense);
                    runOnUiThread(() -> loadExpenses());
                });
            }
        });

        loadExpenses();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            loadExpenses();
        }
    }

    @SuppressLint("DefaultLocale")
    private void loadExpenses() {
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            List<Expense> list = db.expenseDao().getAll();
            Double totalExpenses = db.expenseDao().getTotalExpenses();
            Double totalIncome = db.incomeDao().getTotalIncome();
            if (totalExpenses == null) totalExpenses = 0.0;
            if (totalIncome == null) totalIncome = 0.0;
            double finalBalance = totalIncome - totalExpenses;

            // Group by start-of-day millis (normalized) to avoid locale formatting differences
            List<ListItem> grouped = new ArrayList<>();
            long lastDay = Long.MIN_VALUE;
            if (list != null) {
                for (Expense e : list) {
                    long day = normalizedDayMillis(e.date);
                    if (day != lastDay) {
                        String header = friendlyDateLabel(day);
                        grouped.add(ListItem.header(header));
                        lastDay = day;
                    }
                    grouped.add(ListItem.item(e));
                }
            }

            runOnUiThread(() -> {
                adapter.setItems(grouped);
                tvEmpty.setVisibility(list == null || list.isEmpty() ? View.VISIBLE : View.GONE);
                tvBalance.setText(String.format("Balance: $%.2f", finalBalance));
            });
        });
    }

    private long normalizedDayMillis(long timeMillis) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timeMillis);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    private String friendlyDateLabel(long dayMillis) {
        Calendar c = Calendar.getInstance();
        long todayStart = normalizedDayMillis(c.getTimeInMillis());
        c.add(Calendar.DAY_OF_MONTH, -1);
        long yesterdayStart = normalizedDayMillis(c.getTimeInMillis());

        if (dayMillis == todayStart) return "Today";
        if (dayMillis == yesterdayStart) return "Yesterday";
        return DateFormat.getDateInstance().format(new Date(dayMillis));
    }

    @Override
    protected int getNavMenuItemId() {
        return R.id.nav_expenses;
    }
}
