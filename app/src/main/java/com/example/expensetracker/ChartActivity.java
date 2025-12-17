package com.example.expensetracker;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.expensetracker.data.AppDatabase;
import com.example.expensetracker.model.CategorySummary;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChartActivity extends AppCompatActivity {

    private PieChart pieChart;
    private long start, end;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        pieChart = findViewById(R.id.pieChart);
        start = getIntent().getLongExtra("start", 0);
        end = getIntent().getLongExtra("end", System.currentTimeMillis());

        loadChart();
    }

    private void loadChart() {
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            List<CategorySummary> cs = db.expenseDao().getCategoryTotalsBetween(start, end);
            ArrayList<PieEntry> entries = new ArrayList<>();
            if (cs != null) {
                for (CategorySummary c : cs) {
                    entries.add(new PieEntry(c.total == null ? 0f : c.total.floatValue(), c.category));
                }
            }
            PieDataSet set = new PieDataSet(entries, "Categories");
            set.setColors(ColorTemplate.MATERIAL_COLORS);
            PieData data = new PieData(set);
            runOnUiThread(() -> {
                pieChart.setData(data);
                pieChart.invalidate();
            });
        });
    }
}
