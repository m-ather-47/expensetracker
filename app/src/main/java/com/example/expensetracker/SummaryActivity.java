package com.example.expensetracker;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.expensetracker.data.AppDatabase;
import com.example.expensetracker.model.CategorySummary;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SummaryActivity extends BaseActivity {

    private TextView tvStart, tvEnd, tvTotals;
    private LinearLayout llCategories;
    private long startMillis, endMillis;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // inflate activity_summary into base layout that contains bottom nav
        setActivityLayout(R.layout.activity_summary);

        tvStart = findViewById(R.id.tvStart);
        tvEnd = findViewById(R.id.tvEnd);
        tvTotals = findViewById(R.id.tvTotals);
        llCategories = findViewById(R.id.llCategories);
        Button btnLoad = findViewById(R.id.btnLoad);
        Button btnChart = findViewById(R.id.btnChart);

        // default: this month's start/end
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 0); c.set(Calendar.MINUTE, 0); c.set(Calendar.SECOND, 0);
        startMillis = c.getTimeInMillis();
        c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 23); c.set(Calendar.MINUTE, 59); c.set(Calendar.SECOND, 59);
        endMillis = c.getTimeInMillis();

        tvStart.setText(android.text.format.DateFormat.getDateFormat(this).format(startMillis));
        tvEnd.setText(android.text.format.DateFormat.getDateFormat(this).format(endMillis));

        tvStart.setOnClickListener(v -> pickDate(true));
        tvEnd.setOnClickListener(v -> pickDate(false));

        btnLoad.setOnClickListener(v -> loadSummary());

        btnChart.setOnClickListener(v -> {
            // open ChartActivity with start/end
            android.content.Intent i = new android.content.Intent(SummaryActivity.this, ChartActivity.class);
            i.putExtra("start", startMillis);
            i.putExtra("end", endMillis);
            startActivity(i);
        });

        loadSummary();
    }

    private void pickDate(boolean isStart) {
        Calendar c = Calendar.getInstance();
        if (isStart) c.setTimeInMillis(startMillis); else c.setTimeInMillis(endMillis);
        DatePickerDialog dp = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            Calendar sel = Calendar.getInstance();
            sel.set(year, month, dayOfMonth, isStart ? 0 : 23, isStart ? 0 : 59, isStart ? 0 : 59);
            if (isStart) startMillis = sel.getTimeInMillis(); else endMillis = sel.getTimeInMillis();
            tvStart.setText(android.text.format.DateFormat.getDateFormat(this).format(startMillis));
            tvEnd.setText(android.text.format.DateFormat.getDateFormat(this).format(endMillis));
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        dp.show();
    }

    @SuppressLint("DefaultLocale")
    private void loadSummary() {
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            Double totalExpenses = db.expenseDao().getTotalBetween(startMillis, endMillis);
            Double totalIncome = db.incomeDao().getTotalBetween(startMillis, endMillis);
            if (totalExpenses == null) totalExpenses = 0.0;
            if (totalIncome == null) totalIncome = 0.0;
            List<CategorySummary> cats = db.expenseDao().getCategoryTotalsBetween(startMillis, endMillis);
            double balance = totalIncome - totalExpenses;
            double finalTotalExpenses = totalExpenses;
            double finalTotalIncome = totalIncome;
            runOnUiThread(() -> {
                tvTotals.setText(String.format("Expenses: $%.2f  Income: $%.2f  Balance: $%.2f", finalTotalExpenses, finalTotalIncome, balance));
                llCategories.removeAllViews();
                if (cats != null) {
                    for (CategorySummary cs : cats) {
                        TextView t = new TextView(SummaryActivity.this);
                        t.setText(String.format("%s: $%.2f", cs.category, cs.total == null ? 0.0 : cs.total));
                        llCategories.addView(t);
                    }
                }
            });
        });
    }

    @Override
    protected int getNavMenuItemId() {
        return R.id.nav_summary;
    }
}
