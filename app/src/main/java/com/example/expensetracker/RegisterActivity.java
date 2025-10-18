package com.example.expensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.expensetracker.room.User;
import com.example.expensetracker.room.UserRepository;

public class RegisterActivity extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        EditText emailInput = findViewById(R.id.registerEmailInput);
        EditText passwordInput = findViewById(R.id.registerPasswordInput);
        EditText confirmPasswordInput = findViewById(R.id.registerConfirmPasswordInput);
        Button registerBtn = findViewById(R.id.registerBtn);

        UserRepository userRepository = new UserRepository(this);

        registerBtn.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString();
            String confirm = confirmPasswordInput.getText().toString();


            // Form Validation
            if (email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirm)) {
                Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if email already exists
            userRepository.findByEmailAsync(email, existingUser -> {
                // Callback runs on background thread; switch to UI thread for UI ops
                runOnUiThread(() -> {
                    if (existingUser != null) {
                        Toast.makeText(RegisterActivity.this, "Email is already registered", Toast.LENGTH_SHORT).show();
                    } else {
                        // Insert new user
                        User newUser = new User(email, password);
                        userRepository.insertUser(newUser, id -> runOnUiThread(() -> {
                            Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                            // Go to LoginActivity
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }));
                    }
                });
            });
        });
    }
}
