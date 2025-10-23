package com.example.expensetracker;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.example.expensetracker.room.UserRepository;

import java.util.Locale;

public class ResetPasswordActivity extends AppCompatActivity
{

    private static final String PREFS_NAME = "reset_prefs";
    private static final String KEY_OTP = "reset_otp";
    private static final String KEY_EXPIRY = "reset_expiry";
    private static final String KEY_EMAIL = "reset_email";

    // OTP countdown (60 seconds)
    private CountDownTimer otpTimer;
    private TextView otpTimerText;
    // OTP input boxes and verify button as fields to avoid resource reflection
    private EditText[] otpBoxes;
    private Button verifyBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reset_password);

        // Input Fields
        TextView otpLabel = findViewById(R.id.otpLabel);
        EditText otpBox1 = findViewById(R.id.otpBox1);
        EditText otpBox2 = findViewById(R.id.otpBox2);
        EditText otpBox3 = findViewById(R.id.otpBox3);
        EditText otpBox4 = findViewById(R.id.otpBox4);
        EditText otpBox5 = findViewById(R.id.otpBox5);
        EditText otpBox6 = findViewById(R.id.otpBox6);
        LinearLayout otpBoxesContainer = findViewById(R.id.otpBoxesContainer);
        otpTimerText = findViewById(R.id.otpTimerText);
        otpBoxes = new EditText[]{otpBox1, otpBox2, otpBox3, otpBox4, otpBox5, otpBox6};
        verifyBtn = findViewById(R.id.verifyOtpBtn);
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

        // Attach behavior: auto-advance on input, move back on delete
        setupOtpInputs(otpBoxes);

        // Start the 60-second countdown
        startOtpCountdown();

        // Verify OTP Button Click Handler
        verifyBtn.setOnClickListener(v ->
        {
            String enteredOtp = getOtpFromBoxes(otpBoxes);
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
            otpLabel.setVisibility(View.GONE);
            otpBoxesContainer.setVisibility(View.GONE);
            verifyBtn.setVisibility(View.GONE);
            resetFormContainer.setVisibility(View.VISIBLE);

            // cancel timer when OTP verified
            if (otpTimer != null) {
                otpTimer.cancel();
                otpTimer = null;
            }
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

    // Start a 60-second countdown to show under OTP boxes
    private void startOtpCountdown() {
        // Cancel any existing timer
        if (otpTimer != null) {
            otpTimer.cancel();
        }

        otpTimer = new CountDownTimer(60_000, 1_000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);
                int minutesPart = seconds / 60;
                int secondsPart = seconds % 60;
                if (otpTimerText != null) {
                    otpTimerText.setText(String.format(Locale.getDefault(), "%02d:%02d", minutesPart, secondsPart));
                }
            }

            @Override
            public void onFinish() {
                if (otpTimerText != null) otpTimerText.setText(getString(R.string.otp_timer_finished));
                // disable verify button and move back to forgot password activity
                if (verifyBtn != null) verifyBtn.setEnabled(false);
                Intent intent = new Intent(ResetPasswordActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
                finish();
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (otpTimer != null) {
            otpTimer.cancel();
            otpTimer = null;
        }
    }

    // Helper to collect digits from the 6 boxes
    private String getOtpFromBoxes(EditText[] boxes) {
        StringBuilder sb = new StringBuilder();
        for (EditText e : boxes) {
            String s = e.getText().toString().trim();
            if (s.isEmpty()) return ""; // require all boxes filled
            sb.append(s);
        }
        return sb.toString();
    }

    // Set up listeners to auto-advance and handle backspace
    private void setupOtpInputs(final EditText[] boxes) {
        for (int i = 0; i < boxes.length; i++) {
            final int index = i;
            final EditText current = boxes[i];

            // TextWatcher for auto-advance
            current.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s != null && s.length() == 1) {
                        // move to next box if exists
                        if (index + 1 < boxes.length) {
                            boxes[index + 1].requestFocus();
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });

            // Handle backspace to move focus back
            current.setOnKeyListener((v, keyCode, event) -> {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL) {
                    if (current.getText().toString().isEmpty()) {
                        // move to previous and clear
                        if (index - 1 >= 0) {
                            boxes[index - 1].requestFocus();
                            boxes[index - 1].setText("");
                        }
                        return true;
                    }
                }
                return false;
            });
        }
        // focus first box initially
        boxes[0].requestFocus();
    }
}
