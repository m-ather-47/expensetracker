package com.example.expensetracker;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.example.expensetracker.libs.EmailSender;
import com.example.expensetracker.room.UserRepository;

import java.util.Locale;

public class ForgotPasswordActivity extends AppCompatActivity
{

    private static final String PREFS_NAME = "reset_prefs";
    private static final String KEY_OTP = "reset_otp";
    private static final String KEY_EXPIRY = "reset_expiry";
    private static final String KEY_EMAIL = "reset_email";
    private static final long OTP_TTL_MS = 60 * 1000L; // 1 minutes

    private static final String TAG = "ForgotPassword";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forget_password);

        // Initialize Awesome Validation
        AwesomeValidation validator = new AwesomeValidation(BASIC);
        validator.addValidation(this, R.id.forgotEmailInput, android.util.Patterns.EMAIL_ADDRESS, R.string.err_email);

        // Repository for DB operations
        UserRepository userRepository = new UserRepository(this);

        // Sender credentials from strings (replace with secure retrieval in production)
        final String senderEmail = getString(R.string.sender_email);
        final String senderPassword = getString(R.string.sender_password);

        // Handle Reset Link Button Click
        Button resetLinkBtn = findViewById(R.id.resetLinkBtn);
        EditText forgotEmailInput = findViewById(R.id.forgotEmailInput);
        resetLinkBtn.setOnClickListener(v ->
        {
            // Form Validation
            if(!validator.validate()){
                return;
            }

            String email = forgotEmailInput.getText().toString().trim();
            userRepository.findByEmailAsync(email, existingUser ->
                    runOnUiThread(() ->
                    {
                        if (existingUser != null)
                        {
                            // Generate 6-digit OTP
                            int otpInt = (int)(Math.random() * 900000) + 100000;
                            String otp = String.format(Locale.US, "%06d", otpInt);

                            // Save OTP, expiry and email in SharedPreferences
                            long expiry = System.currentTimeMillis() + OTP_TTL_MS;
                            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                            prefs.edit()
                                    .putString(KEY_OTP, otp)
                                    .putLong(KEY_EXPIRY, expiry)
                                    .putString(KEY_EMAIL, email)
                                    .apply();

                            // Prepare email content
                            String subject = "Password Reset OTP";
                            String body = "Your OTP is: " + otp + "\n\n" +
                                    "This OTP will expire in 1 minute.";

                            // Disable the button and show sending state
                            resetLinkBtn.setEnabled(false);
                            resetLinkBtn.setText("Sending...");

                            // Send email on background thread using EmailSender
                            new Thread(() -> {
                                try {
                                    EmailSender.sendEmail(senderEmail, senderPassword, email, subject, body);
                                    // On success, navigate to ResetPasswordActivity
                                    runOnUiThread(() -> {
                                        Toast.makeText(ForgotPasswordActivity.this, "OTP sent successfully", Toast.LENGTH_SHORT).show();
                                        // restore button state
                                        resetLinkBtn.setEnabled(true);
                                        resetLinkBtn.setText("Send Reset Link");
                                        Intent reset = new Intent(ForgotPasswordActivity.this, ResetPasswordActivity.class);
                                        startActivity(reset);
                                    });
                                } catch (Exception e) {
                                    runOnUiThread(() -> {
                                        Toast.makeText(ForgotPasswordActivity.this, "Failed to send OTP", Toast.LENGTH_LONG).show();
                                        // restore button state
                                        resetLinkBtn.setEnabled(true);
                                        resetLinkBtn.setText("Send Reset Link");
                                    });
                                }
                            }).start();

                        } else {
                            Toast.makeText(ForgotPasswordActivity.this, "Email not found", Toast.LENGTH_SHORT).show();
                        }
                    }));
        });
    }
}
