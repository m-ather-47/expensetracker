package com.example.expensetracker;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Redirect to the expense list activity which uses the shared BottomNavigation
        Intent i = new Intent(this, ExpenseListActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(i);
        finish();
    }

    @Override
    protected int getNavMenuItemId() {
        // This activity is only a redirect; no menu item to highlight here
        return 0;
    }
}
