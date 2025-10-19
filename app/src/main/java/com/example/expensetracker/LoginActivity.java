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
import com.example.expensetracker.room.UserRepository;

public class LoginActivity extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        //Initialize Awesome Validation
        AwesomeValidation validator = new AwesomeValidation(BASIC);
        validator.addValidation(this, R.id.emailInput, android.util.Patterns.EMAIL_ADDRESS, R.string.err_email);

        // Repository for DB operations
        UserRepository userRepository = new UserRepository(this);

        // Input fields
        EditText emailInput = findViewById(R.id.emailInput);
        EditText passwordInput = findViewById(R.id.passwordInput);

        // Login button
        Button loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(v ->
        {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            // Form Validation
            boolean result = validator.validate();
            if(!result){
                return;
            }

            // Perform login on background thread via repository
            userRepository.loginAsync(email, password, user ->
                    runOnUiThread(() ->
                    {
                        if (user != null) {
                            Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                            // Navigate to main activity (assumes MainActivity exists)
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                        }
                    }));

        });

        // Forgot password link
        TextView forgetPasswordTextView = findViewById(R.id.forgotPasswordLink);
        forgetPasswordTextView.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        // Register link
        TextView registerTextView = findViewById(R.id.registerLink);
        registerTextView.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}
