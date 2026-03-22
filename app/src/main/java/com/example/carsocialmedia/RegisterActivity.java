package com.example.carsocialmedia;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carsocialmedia.api.ApiClient;
import com.example.carsocialmedia.api.ApiService;
import com.example.carsocialmedia.api.AuthResponse;
import com.example.carsocialmedia.api.RegisterRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername, etEmail, etPassword, etConfirmPassword;
    private Button btnRegister, btnBackToLogin;

    private TextView ruleLength, ruleUpper, ruleLower, ruleNumber, ruleSpecial;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        apiService = ApiClient.getApiService();

        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etRegisterEmail);
        etPassword = findViewById(R.id.etRegisterPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        btnRegister = findViewById(R.id.btnRegisterAccount);
        btnBackToLogin = findViewById(R.id.btnBackToLogin);

        ruleLength = findViewById(R.id.ruleLength);
        ruleUpper = findViewById(R.id.ruleUpper);
        ruleLower = findViewById(R.id.ruleLower);
        ruleNumber = findViewById(R.id.ruleNumber);
        ruleSpecial = findViewById(R.id.ruleSpecial);


        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = s.toString();

                setRule(ruleLength, password.length() >= 8);
                setRule(ruleUpper, password.matches(".*[A-Z].*"));
                setRule(ruleLower, password.matches(".*[a-z].*"));
                setRule(ruleNumber, password.matches(".*[0-9].*"));
                setRule(ruleSpecial, password.matches(".*[@#$%&!].*"));
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        btnRegister.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if (TextUtils.isEmpty(username)) {
                etUsername.setError("Username is required");
                etUsername.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(email)) {
                etEmail.setError("Email is required");
                etEmail.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                etPassword.setError("Password is required");
                etPassword.requestFocus();
                return;
            }

            if (!isValidPassword(password)) {
                Toast.makeText(RegisterActivity.this,
                        "Password does not meet the requirements",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(RegisterActivity.this,
                        "Passwords do not match!",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            RegisterRequest request = new RegisterRequest(username, email, password);

            apiService.register(request).enqueue(new Callback<AuthResponse>() {
                @Override
                public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                    if (response.isSuccessful() && response.body() != null){
                        Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Registration failed (email may already exist)", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<AuthResponse> call, Throwable t) {
                    Toast.makeText(RegisterActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        btnBackToLogin.setOnClickListener(v -> finish());
    }

    private void setRule(TextView rule, boolean satisfied) {
        if (satisfied) {
            rule.setTextColor(Color.parseColor("#4CAF50"));
        } else {
            rule.setTextColor(Color.parseColor("#888888"));
        }
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 8 &&
                password.matches(".*[A-Z].*") &&
                password.matches(".*[a-z].*") &&
                password.matches(".*[0-9].*") &&
                password.matches(".*[@#$%&!].*");
    }
}