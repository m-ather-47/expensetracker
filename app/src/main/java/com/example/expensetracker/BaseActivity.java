package com.example.expensetracker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        // Wrap base context with selected locale
        String lang = Settings.getLanguage(newBase);
        Context ctx = LocaleHelper.wrap(newBase, lang);
        super.attachBaseContext(ctx);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // Apply saved theme before super.onCreate to ensure it's used during inflation
        String theme = Settings.getTheme(this);
        if (Settings.THEME_DARK.equals(theme)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else if (Settings.THEME_LIGHT.equals(theme)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        setupBottomNavigation();
    }

    protected void setActivityLayout(@LayoutRes int layoutResId) {
        FrameLayout base = findViewById(R.id.baseContent);
        LayoutInflater.from(this).inflate(layoutResId, base, true);
    }

    private void setupBottomNavigation() {
        BottomNavigationView nav = findViewById(R.id.bottomNavigation);
        if (nav == null) return;
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            // if this activity corresponds to the selected item, do nothing
            if (id == getNavMenuItemId()) return true;

            Intent intent = null;
            if (id == R.id.nav_expenses) {
                intent = new Intent(this, ExpenseListActivity.class);
            } else if (id == R.id.nav_summary) {
                intent = new Intent(this, SummaryActivity.class);
            } else if (id == R.id.nav_options) {
                intent = new Intent(this, OptionsActivity.class);
            }

            if (intent != null) {
                // Bring activity to front if already running
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                // prevent default selection handling here
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView nav = findViewById(R.id.bottomNavigation);
        if (nav != null) {
            int id = getNavMenuItemId();
            if (id != 0) nav.setSelectedItemId(id);
        }
    }

    /**
     * Child activities should return the menu item id that represents them.
     * Example: R.id.nav_expenses
     */
    protected abstract int getNavMenuItemId();
}
