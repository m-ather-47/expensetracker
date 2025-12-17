package com.example.expensetracker;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.expensetracker.data.AppDatabase;
import com.example.expensetracker.model.Income;

import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class IncomeActivity extends AppCompatActivity {

    private EditText etAmount, etSource, etDate;
    private long selectedDateMillis = System.currentTimeMillis();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);

        etAmount = findViewById(R.id.etIncomeAmount);
        etSource = findViewById(R.id.etIncomeSource);
        etDate = findViewById(R.id.etIncomeDate);
        Button btnSave = findViewById(R.id.btnIncomeSave);

        etDate.setText(android.text.format.DateFormat.getDateFormat(this).format(selectedDateMillis));
        etDate.setOnClickListener(v -> showDatePicker());

        btnSave.setOnClickListener(v -> saveIncome());
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(selectedDateMillis);
        int y = c.get(Calendar.YEAR);
        int m = c.get(Calendar.MONTH);
        int d = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dp = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            Calendar sel = Calendar.getInstance();
            sel.set(year, month, dayOfMonth, 0, 0, 0);
            selectedDateMillis = sel.getTimeInMillis();
            etDate.setText(android.text.format.DateFormat.getDateFormat(this).format(selectedDateMillis));
        }, y, m, d);
        dp.show();
    }

    private void saveIncome() {
        String amt = etAmount.getText().toString().trim();
        if (TextUtils.isEmpty(amt)) {
            etAmount.setError("Amount required");
            return;
        }
        double amount;
        try {
            amount = Double.parseDouble(amt);
        } catch (NumberFormatException ex) {
            etAmount.setError("Invalid amount");
            return;
        }
        if (amount <= 0) {
            etAmount.setError("Amount must be greater than 0");
            return;
        }
        String src = etSource.getText().toString().trim();
        Income i = new Income(amount, src, selectedDateMillis);
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            db.incomeDao().insert(i);
            runOnUiThread(() -> {
                Toast.makeText(this, "Income saved", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }
}

