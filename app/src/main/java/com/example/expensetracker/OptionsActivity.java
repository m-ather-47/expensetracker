package com.example.expensetracker;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatDelegate;

public class OptionsActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityLayout(R.layout.activity_options);

        setupControls();
    }

    private void setupControls() {
        RadioGroup rgLang = findViewById(R.id.rg_language);
        RadioGroup rgTheme = findViewById(R.id.rg_theme);

        RadioButton rbEn = findViewById(R.id.rb_lang_en);
        RadioButton rbEs = findViewById(R.id.rb_lang_es);

        RadioButton rbSystem = findViewById(R.id.rb_theme_system);
        RadioButton rbLight = findViewById(R.id.rb_theme_light);
        RadioButton rbDark = findViewById(R.id.rb_theme_dark);

        // Language
        String lang = Settings.getLanguage(this);
        if (Settings.LANG_ES.equals(lang)) {
            rbEs.setChecked(true);
        } else {
            rbEn.setChecked(true);
        }

        rgLang.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_lang_en) {
                Settings.setLanguage(OptionsActivity.this, Settings.LANG_EN);
            } else if (checkedId == R.id.rb_lang_es) {
                Settings.setLanguage(OptionsActivity.this, Settings.LANG_ES);
            }
            // recreate activity to apply locale (attachBaseContext will wrap)
            recreate();
        });

        // Theme
        String theme = Settings.getTheme(this);
        if (Settings.THEME_LIGHT.equals(theme)) {
            rbLight.setChecked(true);
        } else if (Settings.THEME_DARK.equals(theme)) {
            rbDark.setChecked(true);
        } else {
            rbSystem.setChecked(true);
        }

        rgTheme.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_theme_light) {
                Settings.setTheme(OptionsActivity.this, Settings.THEME_LIGHT);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else if (checkedId == R.id.rb_theme_dark) {
                Settings.setTheme(OptionsActivity.this, Settings.THEME_DARK);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                Settings.setTheme(OptionsActivity.this, Settings.THEME_SYSTEM);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            }
            // recreate to apply theme immediately
            recreate();
        });
    }

    @Override
    protected int getNavMenuItemId() {
        return R.id.nav_options;
    }
}
