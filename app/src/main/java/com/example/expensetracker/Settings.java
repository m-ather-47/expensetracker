// ...new file...
package com.example.expensetracker;

import android.content.Context;
import android.content.SharedPreferences;

public class Settings {
    private static final String PREFS_NAME = "app_prefs";
    private static final String KEY_LANGUAGE = "pref_language";
    private static final String KEY_THEME = "pref_theme";

    public static final String THEME_SYSTEM = "system";
    public static final String THEME_LIGHT = "light";
    public static final String THEME_DARK = "dark";

    public static final String LANG_EN = "en";
    public static final String LANG_ES = "es";

    private static SharedPreferences prefs(Context ctx) {
        return ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static String getLanguage(Context ctx) {
        return prefs(ctx).getString(KEY_LANGUAGE, LANG_EN);
    }

    public static void setLanguage(Context ctx, String language) {
        prefs(ctx).edit().putString(KEY_LANGUAGE, language).apply();
    }

    public static String getTheme(Context ctx) {
        return prefs(ctx).getString(KEY_THEME, THEME_SYSTEM);
    }

    public static void setTheme(Context ctx, String theme) {
        prefs(ctx).edit().putString(KEY_THEME, theme).apply();
    }
}

