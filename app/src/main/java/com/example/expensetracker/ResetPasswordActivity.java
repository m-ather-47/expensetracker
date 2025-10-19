package com.example.expensetracker;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.example.expensetracker.room.UserRepository;

public class ResetPasswordActivity extends AppCompatActivity  {

    private static final String PREFS_NAME = "reset_prefs";
    private static final String KEY_OTP = "reset_otp";
    private static final String KEY_EXPIRY = "reset_expiry";
    private static final String KEY_EMAIL = "reset_email";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reset_password);

        // Input Fields
        EditText otpInput = findViewById(R.id.otpInput);
        Button verifyOtpBtn = findViewById(R.id.verifyOtpBtn);
        LinearLayout resetFormContainer = findViewById(R.id.resetFormContainer);
        EditText newPasswordInput = findViewById(R.id.newPasswordInput);
        Button resetBtn = findViewById(R.id.resetBtn);

        ////Initialize Awesome Validation
        AwesomeValidation validator = new AwesomeValidation(BASIC);
        String regexPassword = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_\\-+=\\[\\]{};:'\",.<>/?\\\\|`~]).{8,}$";
        validator.addValidation(this, R.id.newPasswordInput, regexPassword, R.string.err_password);
        validator.addValidation(this, R.id.confirmNewPasswordInput, R.id.newPasswordInput, R.string.err_password_confirmation);

        // Repository for DB operations
        UserRepository userRepository = new UserRepository(this);

        // Verify OTP Button Click Handler
        verifyOtpBtn.setOnClickListener(v ->
        {
            String enteredOtp = otpInput.getText().toString().trim();
            if (enteredOtp.isEmpty()) {
                Toast.makeText(ResetPasswordActivity.this, "Please enter OTP", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            String storedOtp = prefs.getString(KEY_OTP, null);
            long expiry = prefs.getLong(KEY_EXPIRY, 0L);
            String storedEmail = prefs.getString(KEY_EMAIL, null);

            long now = System.currentTimeMillis();
            if (storedOtp == null || storedEmail == null || expiry == 0L) {
                Toast.makeText(ResetPasswordActivity.this, "OTP Not Found.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (now > expiry) {
                Toast.makeText(ResetPasswordActivity.this, "OTP Expired.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!enteredOtp.equals(storedOtp)) {
                Toast.makeText(ResetPasswordActivity.this, "Incorrect OTP", Toast.LENGTH_SHORT).show();
                return;
            }

            // OTP verified: reveal reset form
            Toast.makeText(ResetPasswordActivity.this, "OTP Verified", Toast.LENGTH_SHORT).show();
            otpInput.setVisibility(View.GONE);
            verifyOtpBtn.setVisibility(View.GONE);
            resetFormContainer.setVisibility(View.VISIBLE);
        });

        // Reset Button Click Handler
        resetBtn.setOnClickListener(v ->
        {
            // Form Validation
            if(!validator.validate())
            {
                return;
            }

            String newPass = newPasswordInput.getText().toString();
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            String email = prefs.getString(KEY_EMAIL, null);

            if (email == null) {
                Toast.makeText(ResetPasswordActivity.this, "Email Not Available", Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread(() -> {
                userRepository.updatePasswordAsync(email, newPass);
                // Clear stored OTP after successful reset
                prefs.edit().remove(KEY_OTP).remove(KEY_EXPIRY).remove(KEY_EMAIL).apply();
                runOnUiThread(() -> {
                    Toast.makeText(ResetPasswordActivity.this, "Password Updated Successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                });
            }).start();
        });
    }
}
