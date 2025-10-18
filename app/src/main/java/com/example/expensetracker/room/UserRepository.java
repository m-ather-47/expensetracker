package com.example.expensetracker.room;

import android.content.Context;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserRepository {
    private final AppDatabase db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public interface LoginCallback {
        void onResult(User user);
    }

    public interface InsertCallback {
        void onInserted(long id);
    }

    public interface FindCallback {
        void onResult(User user);
    }

    public UserRepository(Context context) {
        db = AppDatabase.getInstance(context);
    }

    public void insertUser(User user, InsertCallback callback) {
        executor.execute(() -> {
            long id = db.userDao().insert(user);
            if (callback != null) callback.onInserted(id);
        });
    }

    public void loginAsync(String email, String password, LoginCallback callback) {
        executor.execute(() -> {
            User user = db.userDao().getUserByEmailAndPassword(email, password);
            if (callback != null) callback.onResult(user);
        });
    }

    public void findByEmailAsync(String email, FindCallback callback) {
        executor.execute(() -> {
            User user = db.userDao().findByEmail(email);
            if (callback != null) callback.onResult(user);
        });
    }
}
