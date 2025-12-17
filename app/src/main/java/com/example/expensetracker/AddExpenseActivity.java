package com.example.expensetracker;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.expensetracker.data.AppDatabase;
import com.example.expensetracker.model.Expense;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddExpenseActivity extends AppCompatActivity {

    public static final String EXTRA_EXPENSE_ID = "expense_id";

    private EditText etAmount, etDescription, etDate;
    private Spinner spCategory;
    private long selectedDateMillis = System.currentTimeMillis();

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private int editingId = -1;

    private ArrayAdapter<String> categoryAdapter;
    private ArrayList<String> categoriesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        etAmount = findViewById(R.id.etAmount);
        etDescription = findViewById(R.id.etDescription);
        etDate = findViewById(R.id.etDate);
        spCategory = findViewById(R.id.spCategory);
        Button btnSave = findViewById(R.id.btnSave);

        categoriesList = new ArrayList<>();
        categoriesList.add("Food");
        categoriesList.add("Transport");
        categoriesList.add("Bills");
        categoriesList.add("Shopping");
        categoriesList.add("Other");

        categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categoriesList);
        spCategory.setAdapter(categoryAdapter);

        etDate.setText(android.text.format.DateFormat.getDateFormat(this).format(selectedDateMillis));
        etDate.setOnClickListener(v -> showDatePicker());

        // Check if editing
        if (getIntent() != null && getIntent().hasExtra(EXTRA_EXPENSE_ID)) {
            editingId = getIntent().getIntExtra(EXTRA_EXPENSE_ID, -1);
            if (editingId != -1) {
                loadExpense(editingId);
            }
        }

        spCategory.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String sel = categoryAdapter.getItem(position);
                if ("Other".equalsIgnoreCase(sel)) {
                    promptForCustomCategory();
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        btnSave.setOnClickListener(v -> saveExpense());
    }

    private void promptForCustomCategory() {
        final EditText input = new EditText(this);
        AlertDialog.Builder b = new AlertDialog.Builder(this)
                .setTitle("Custom category")
                .setMessage("Enter category name:")
                .setView(input)
                .setPositiveButton("Add", (dialog, which) -> {
                    String c = input.getText().toString().trim();
                    if (!TextUtils.isEmpty(c)) {
                        // insert before the last 'Other' item
                        int pos = categoriesList.size() - 1;
                        categoriesList.add(pos, c);
                        categoryAdapter.notifyDataSetChanged();
                        spCategory.setSelection(pos);
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // reset selection to first item
                    spCategory.setSelection(0);
                    dialog.dismiss();
                });
        b.show();
    }

    private void loadExpense(int id) {
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            Expense e = db.expenseDao().getById(id);
            if (e != null) {
                selectedDateMillis = e.date;
                runOnUiThread(() -> {
                    etAmount.setText(String.valueOf(e.amount));
                    etDescription.setText(e.description);
                    etDate.setText(android.text.format.DateFormat.getDateFormat(this).format(selectedDateMillis));
                    // Try to set category if present in spinner
                    for (int i = 0; i < categoryAdapter.getCount(); i++) {
                        if (Objects.requireNonNull(categoryAdapter.getItem(i)).equalsIgnoreCase(e.category)) {
                            spCategory.setSelection(i);
                            return;
                        }
                    }
                    // if not found, add it
                    categoriesList.add(categoriesList.size() - 1, e.category);
                    categoryAdapter.notifyDataSetChanged();
                    spCategory.setSelection(categoriesList.size() - 2);
                });
            }
        });
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

    private void saveExpense() {
        String amtStr = etAmount.getText().toString().trim();
        if (TextUtils.isEmpty(amtStr)) {
            etAmount.setError("Amount required");
            return;
        }
        double amount;
        try {
            amount = Double.parseDouble(amtStr);
        } catch (NumberFormatException ex) {
            etAmount.setError("Invalid amount");
            return;
        }
        if (amount <= 0) {
            etAmount.setError("Amount must be greater than 0");
            return;
        }

        String category = spCategory.getSelectedItem().toString();
        if ("Other".equalsIgnoreCase(category)) category = "Other"; // fallback
        String desc = etDescription.getText().toString().trim();

        Expense e = new Expense(amount, category, selectedDateMillis, desc);

        executor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            if (editingId != -1) {
                e.id = editingId;
                db.expenseDao().update(e);
            } else {
                db.expenseDao().insert(e);
            }
            runOnUiThread(() -> {
                Toast.makeText(this, "Expense saved", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            });
        });
    }
}
