package com.example.carsocialmedia;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carsocialmedia.api.ApiClient;
import com.example.carsocialmedia.api.ApiService;
import com.example.carsocialmedia.api.AuthResponse;
import com.example.carsocialmedia.api.LoginRequest;
import com.example.carsocialmedia.api.SessionManager;
import com.example.carsocialmedia.models.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Switch switchRememberUser;
    private Button btnLogin, btnRegister;

    private SharedPreferences sharedPreferences;

    private static final String PREF_NAME = "CarAppPrefs";

    private static final String KEY_REMEMBER = "remember_user";
    private static final String KEY_SAVED_LOGIN_EMAIL = "saved_login_email";
    private static final String KEY_SAVED_LOGIN_PASSWORD = "saved_login_password";


    private ApiService apiService;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        apiService = ApiClient.getApiService();
        sessionManager = new SessionManager(this);

        if (sessionManager.isLoggedIn()){
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        switchRememberUser = findViewById(R.id.switchRememberUser);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        loadRememberedUser();

        btnLogin.setOnClickListener(v -> {
            String enteredEmail = etEmail.getText().toString().trim();
            String enteredPassword = etPassword.getText().toString().trim();

            if (TextUtils.isEmpty(enteredEmail)) {
                etEmail.setError("Email is required");
                etEmail.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(enteredPassword)) {
                etPassword.setError("Password is required");
                etPassword.requestFocus();
                return;
            }

            LoginRequest request = new LoginRequest(enteredEmail, enteredPassword);

            apiService.login(request).enqueue(new Callback<AuthResponse>() {
                @Override
                public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                    if(response.isSuccessful() && response.body() != null){
                        User user = response.body().getUser();

                        sessionManager.saveSession(user.getId(), user.getUsername(), user.getEmail());

                        if (switchRememberUser.isChecked()){
                            saveRememberedUser(enteredEmail, enteredPassword);
                        } else {
                            clearRememberedUser();
                        }
                        Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<AuthResponse> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });


        });

        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void loadRememberedUser() {
        boolean remember = sharedPreferences.getBoolean(KEY_REMEMBER, false);
        String savedEmail = sharedPreferences.getString(KEY_SAVED_LOGIN_EMAIL, "");
        String savedPassword = sharedPreferences.getString(KEY_SAVED_LOGIN_PASSWORD, "");

        switchRememberUser.setChecked(remember);

        if (remember) {
            etEmail.setText(savedEmail);
            etPassword.setText(savedPassword);
        }
    }

    private void saveRememberedUser(String email, String password) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_REMEMBER, true);
        editor.putString(KEY_SAVED_LOGIN_EMAIL, email);
        editor.putString(KEY_SAVED_LOGIN_PASSWORD, password);
        editor.apply();
    }

    private void clearRememberedUser() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_SAVED_LOGIN_EMAIL);
        editor.remove(KEY_SAVED_LOGIN_PASSWORD);
        editor.putBoolean(KEY_REMEMBER, false);
        editor.apply();
    }
}
