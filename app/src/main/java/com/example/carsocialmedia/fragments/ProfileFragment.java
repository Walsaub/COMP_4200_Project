package com.example.carsocialmedia.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carsocialmedia.R;
import com.example.carsocialmedia.adapters.PostAdapter;
import com.example.carsocialmedia.api.ApiClient;
import com.example.carsocialmedia.api.ApiService;
import com.example.carsocialmedia.api.AuthResponse;
import com.example.carsocialmedia.api.LoginRequest;
import com.example.carsocialmedia.api.RegisterRequest;
import com.example.carsocialmedia.api.SessionManager;
import com.example.carsocialmedia.api.UpdateUserRequest;
import com.example.carsocialmedia.models.Post;
import com.example.carsocialmedia.models.User;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private SessionManager sessionManager;
    private ApiService apiService;
    private PostAdapter postAdapter;

    // Auth views
    private View layoutAuth;
    private View formLogin;
    private View formRegister;
    private TextView tabLogin;
    private TextView tabRegister;
    private TextInputEditText editLoginEmail;
    private TextInputEditText editLoginPassword;
    private TextInputEditText editRegisterUsername;
    private TextInputEditText editRegisterEmail;
    private TextInputEditText editRegisterPassword;
    private Button btnLogin;
    private Button btnRegister;
    private TextView loginError;
    private TextView registerError;

    // Profile views
    private View layoutProfile;
    private TextView profileUsername;
    private TextView profileEmail;
    private Button btnLogout;
    private RecyclerView recyclerPosts;
    private TextView emptyPosts;

    // Edit profile views
    private View editProfileToggle;
    private View editProfileForm;
    private TextView editProfileChevron;
    private TextInputEditText editUsername;
    private TextInputEditText editEmail;
    private TextInputEditText editPassword;
    private Button btnSaveProfile;
    private TextView editProfileError;
    private TextView editProfileSuccess;

    public ProfileFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        sessionManager = new SessionManager(requireContext());
        apiService = ApiClient.getApiService();

        bindViews(view);
        setupTabs();
        setupButtons();

        if (sessionManager.isLoggedIn()) {
            showProfile();
        } else {
            showAuth();
        }

        return view;
    }

    private void bindViews(View view) {
        layoutAuth = view.findViewById(R.id.layout_auth);
        layoutProfile = view.findViewById(R.id.layout_profile);

        tabLogin = view.findViewById(R.id.tab_login);
        tabRegister = view.findViewById(R.id.tab_register);

        formLogin = view.findViewById(R.id.form_login);
        formRegister = view.findViewById(R.id.form_register);

        editLoginEmail = view.findViewById(R.id.login_email);
        editLoginPassword = view.findViewById(R.id.login_password);

        editRegisterUsername = view.findViewById(R.id.register_username);
        editRegisterEmail = view.findViewById(R.id.register_email);
        editRegisterPassword = view.findViewById(R.id.register_password);

        btnLogin = view.findViewById(R.id.btn_login);
        btnRegister = view.findViewById(R.id.btn_register);

        loginError = view.findViewById(R.id.login_error);
        registerError = view.findViewById(R.id.register_error);

        profileUsername = view.findViewById(R.id.profile_username);
        profileEmail = view.findViewById(R.id.profile_email);
        btnLogout = view.findViewById(R.id.btn_logout);

        recyclerPosts = view.findViewById(R.id.recycler_posts);
        emptyPosts = view.findViewById(R.id.empty_posts);

        editProfileToggle = view.findViewById(R.id.edit_profile_toggle);
        editProfileForm = view.findViewById(R.id.edit_profile_form);
        editProfileChevron = view.findViewById(R.id.edit_profile_chevron);
        editUsername = view.findViewById(R.id.edit_username);
        editEmail = view.findViewById(R.id.edit_email);
        editPassword = view.findViewById(R.id.edit_password);
        btnSaveProfile = view.findViewById(R.id.btn_save_profile);
        editProfileError = view.findViewById(R.id.edit_profile_error);
        editProfileSuccess = view.findViewById(R.id.edit_profile_success);
    }

    private void setupTabs() {
        tabLogin.setOnClickListener(v -> switchToLogin());
        tabRegister.setOnClickListener(v -> switchToRegister());
        switchToLogin();
    }

    private void switchToLogin() {
        formLogin.setVisibility(View.VISIBLE);
        formRegister.setVisibility(View.GONE);
        tabLogin.setAlpha(1.0f);
        tabRegister.setAlpha(0.45f);
    }

    private void switchToRegister() {
        formLogin.setVisibility(View.GONE);
        formRegister.setVisibility(View.VISIBLE);
        tabLogin.setAlpha(0.45f);
        tabRegister.setAlpha(1.0f);
    }

    private void setupButtons() {
        btnLogin.setOnClickListener(v -> handleLogin());
        btnRegister.setOnClickListener(v -> handleRegister());
        btnLogout.setOnClickListener(v -> handleLogout());
        editProfileToggle.setOnClickListener(v -> toggleEditForm());
        btnSaveProfile.setOnClickListener(v -> handleSaveProfile());
    }

    private void toggleEditForm() {
        if (editProfileForm.getVisibility() == View.VISIBLE) {
            editProfileForm.setVisibility(View.GONE);
            editProfileChevron.setText("▼");
        } else {
            editProfileForm.setVisibility(View.VISIBLE);
            editProfileChevron.setText("▲");
            // Pre-fill fields with current values
            editUsername.setText(sessionManager.getUsername());
            editEmail.setText(sessionManager.getEmail());
            editPassword.setText("");
            editProfileError.setVisibility(View.GONE);
            editProfileSuccess.setVisibility(View.GONE);
        }
    }

    private void handleLogin() {
        String email = editLoginEmail.getText() != null
                ? editLoginEmail.getText().toString().trim() : "";
        String password = editLoginPassword.getText() != null
                ? editLoginPassword.getText().toString() : "";

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            showLoginError("Please fill in all fields.");
            return;
        }

        loginError.setVisibility(View.GONE);
        btnLogin.setEnabled(false);

        apiService.login(new LoginRequest(email, password)).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (!isAdded()) return;
                btnLogin.setEnabled(true);
                if (response.isSuccessful() && response.body() != null && response.body().getUser() != null) {
                    User user = response.body().getUser();
                    sessionManager.saveSession(user.getId(), user.getUsername(), user.getEmail());
                    showProfile();
                } else {
                    showLoginError("Invalid email or password.");
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                if (!isAdded()) return;
                btnLogin.setEnabled(true);
                showLoginError("Network error. Please try again.");
            }
        });
    }

    private void handleRegister() {
        String username = editRegisterUsername.getText() != null
                ? editRegisterUsername.getText().toString().trim() : "";
        String email = editRegisterEmail.getText() != null
                ? editRegisterEmail.getText().toString().trim() : "";
        String password = editRegisterPassword.getText() != null
                ? editRegisterPassword.getText().toString() : "";

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            showRegisterError("Please fill in all fields.");
            return;
        }

        registerError.setVisibility(View.GONE);
        btnRegister.setEnabled(false);

        apiService.register(new RegisterRequest(username, email, password)).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (!isAdded()) return;
                btnRegister.setEnabled(true);
                if (response.isSuccessful() && response.body() != null && response.body().getUser() != null) {
                    User user = response.body().getUser();
                    sessionManager.saveSession(user.getId(), user.getUsername(), user.getEmail());
                    showProfile();
                } else if (response.code() == 409) {
                    showRegisterError("Username or email already taken.");
                } else {
                    showRegisterError("Registration failed. Please try again.");
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                if (!isAdded()) return;
                btnRegister.setEnabled(true);
                showRegisterError("Network error. Please try again.");
            }
        });
    }

    private void handleSaveProfile() {
        String username = editUsername.getText() != null
                ? editUsername.getText().toString().trim() : "";
        String email = editEmail.getText() != null
                ? editEmail.getText().toString().trim() : "";
        String password = editPassword.getText() != null
                ? editPassword.getText().toString() : "";

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email)) {
            showEditError("Username and email cannot be empty.");
            return;
        }

        editProfileError.setVisibility(View.GONE);
        editProfileSuccess.setVisibility(View.GONE);
        btnSaveProfile.setEnabled(false);

        int userId = sessionManager.getUserId();
        String newPassword = TextUtils.isEmpty(password) ? null : password;

        apiService.updateUser(userId, new UpdateUserRequest(userId, username, email, newPassword))
                .enqueue(new Callback<AuthResponse>() {
                    @Override
                    public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                        if (!isAdded()) return;
                        btnSaveProfile.setEnabled(true);
                        if (response.isSuccessful() && response.body() != null && response.body().getUser() != null) {
                            User user = response.body().getUser();
                            sessionManager.saveSession(user.getId(), user.getUsername(), user.getEmail());
                            profileUsername.setText(user.getUsername());
                            profileEmail.setText(user.getEmail());
                            editProfileSuccess.setText("Profile updated successfully.");
                            editProfileSuccess.setVisibility(View.VISIBLE);
                            editPassword.setText("");
                        } else if (response.code() == 409) {
                            showEditError("Username or email already taken.");
                        } else {
                            showEditError("Update failed. Please try again.");
                        }
                    }

                    @Override
                    public void onFailure(Call<AuthResponse> call, Throwable t) {
                        if (!isAdded()) return;
                        btnSaveProfile.setEnabled(true);
                        showEditError("Network error. Please try again.");
                    }
                });
    }

    private void handleLogout() {
        sessionManager.clearSession();
        showAuth();
    }

    private void showLoginError(String message) {
        loginError.setText(message);
        loginError.setVisibility(View.VISIBLE);
    }

    private void showRegisterError(String message) {
        registerError.setText(message);
        registerError.setVisibility(View.VISIBLE);
    }

    private void showEditError(String message) {
        editProfileError.setText(message);
        editProfileError.setVisibility(View.VISIBLE);
    }

    private void showAuth() {
        layoutAuth.setVisibility(View.VISIBLE);
        layoutProfile.setVisibility(View.GONE);
        switchToLogin();
        editLoginEmail.setText("");
        editLoginPassword.setText("");
        loginError.setVisibility(View.GONE);
    }

    private void showProfile() {
        String username = sessionManager.getUsername();
        // Guard: stale/corrupt session — force re-login
        if (username == null || username.isEmpty()) {
            sessionManager.clearSession();
            showAuth();
            return;
        }

        layoutAuth.setVisibility(View.GONE);
        layoutProfile.setVisibility(View.VISIBLE);

        profileUsername.setText(username);
        profileEmail.setText(sessionManager.getEmail());

        // Ensure edit form starts collapsed
        editProfileForm.setVisibility(View.GONE);
        editProfileChevron.setText("▼");

        postAdapter = new PostAdapter(new ArrayList<>());
        recyclerPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerPosts.setAdapter(postAdapter);
        recyclerPosts.setNestedScrollingEnabled(false);

        loadUserPosts();
    }

    private void loadUserPosts() {
        int userId = sessionManager.getUserId();

        apiService.getPosts().enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    List<Post> userPosts = new ArrayList<>();
                    for (Post post : response.body()) {
                        if (post.getUserId() == userId) {
                            userPosts.add(post);
                        }
                    }
                    postAdapter.updatePosts(userPosts);
                    emptyPosts.setVisibility(userPosts.isEmpty() ? View.VISIBLE : View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                // Posts silently fail to load; user can still see the rest of their profile
            }
        });
    }
}