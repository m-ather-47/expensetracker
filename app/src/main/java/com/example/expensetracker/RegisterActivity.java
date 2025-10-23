package com.example.expensetracker;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.example.expensetracker.room.User;
import com.example.expensetracker.room.UserRepository;

public class RegisterActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        //Initialize Awesome Validation
        AwesomeValidation validator = new AwesomeValidation(BASIC);
        validator.addValidation(this, R.id.registerEmailInput, android.util.Patterns.EMAIL_ADDRESS, R.string.err_email);
        String regexPassword = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_\\-+=\\[\\]{};:'\",.<>/?\\\\|`~]).{8,}$";
        validator.addValidation(this, R.id.registerPasswordInput, regexPassword, R.string.err_password);
        validator.addValidation(this, R.id.registerConfirmPasswordInput, R.id.registerPasswordInput, R.string.err_password_confirmation);

        // Input fields
        EditText emailInput = findViewById(R.id.registerEmailInput);
        EditText passwordInput = findViewById(R.id.registerPasswordInput);

        // Repository for DB operations
        UserRepository userRepository = new UserRepository(this);

        // Register button click handler
        Button registerBtn = findViewById(R.id.registerBtn);
        registerBtn.setOnClickListener(v ->
        {
            // Form Validation
            if(!validator.validate())
            {
                return;
            }

            // Input Fields
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString();

            // Check if email already exists
            userRepository.findByEmailAsync(email, existingUser ->
                    runOnUiThread(() ->
                    {
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
                    }));
        });

        // Login link
        TextView registerTextView = findViewById(R.id.loginLink);
        registerTextView.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
