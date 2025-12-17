package com.example.expensetracker;

import android.os.Bundle;
import androidx.annotation.Nullable;

public class OptionsActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityLayout(R.layout.activity_options);
    }

    @Override
    protected int getNavMenuItemId() {
        return R.id.nav_options;
    }
}
