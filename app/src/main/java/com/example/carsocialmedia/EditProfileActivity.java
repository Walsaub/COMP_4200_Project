package com.example.carsocialmedia;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.carsocialmedia.api.ApiClient;
import com.example.carsocialmedia.api.ApiService;
import com.example.carsocialmedia.api.AuthResponse;
import com.example.carsocialmedia.api.SessionManager;
import com.example.carsocialmedia.api.UpdateUserRequest;
import com.example.carsocialmedia.models.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    private TextView tvBack, tvChangePhoto;
    private EditText etUsername, etBio, etEmail;
    private Button btnSaveChanges;
    private ImageView imgEditProfile;

    private SharedPreferences sharedPreferences;
    private ApiService apiService;
    private SessionManager sessionManager;

    private static final String PREF_NAME = "CarAppPrefs";

    private static final String KEY_PROFILE_BIO = "profile_bio";
    private static final String KEY_PROFILE_IMAGE_URI = "profile_image_uri";

    private Uri selectedImageUri;

    private final ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    Glide.with(this)
                            .load(uri)
                            .into(imgEditProfile);
                } else {
                    Toast.makeText(EditProfileActivity.this, "No photo selected", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        apiService = ApiClient.getApiService();
        sessionManager = new SessionManager(this);

        tvBack = findViewById(R.id.tvBack);
        tvChangePhoto = findViewById(R.id.tvChangePhoto);
        etUsername = findViewById(R.id.etEditUsername);
        etBio = findViewById(R.id.etEditBio);
        etEmail = findViewById(R.id.etEditEmail);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        imgEditProfile = findViewById(R.id.imgEditProfile);

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        String username = getIntent().getStringExtra("username");
        String bio = getIntent().getStringExtra("bio");
        String email = getIntent().getStringExtra("email");

        if (bio == null || bio.isEmpty()) {
            bio = sharedPreferences.getString(KEY_PROFILE_BIO, "Car Enthusiast");
        }

        etUsername.setText(username);
        etBio.setText(bio);
        etEmail.setText(email);

        String savedImageUri = sharedPreferences.getString(KEY_PROFILE_IMAGE_URI, "");
        if (!savedImageUri.isEmpty()) {
            Uri uri = Uri.parse(savedImageUri);
            Glide.with(this)
                    .load(uri)
                    .into(imgEditProfile);
        }

        tvBack.setOnClickListener(v -> finish());

        tvChangePhoto.setOnClickListener(v -> {
            pickMedia.launch(
                    new PickVisualMediaRequest.Builder()
                            .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                            .build()
            );
        });

        btnSaveChanges.setOnClickListener(v -> {
            String updatedUsername = etUsername.getText().toString().trim();
            String updatedBio = etBio.getText().toString().trim();
            String updatedEmail = etEmail.getText().toString().trim();

            if (updatedUsername.isEmpty()) {
                etUsername.setError("Username is required");
                etUsername.requestFocus();
                return;
            }

            if (updatedBio.isEmpty()) {
                etBio.setError("Bio is required");
                etBio.requestFocus();
                return;
            }

            if (updatedEmail.isEmpty()) {
                etEmail.setError("Email is required");
                etEmail.requestFocus();
                return;
            }

            int userId = sessionManager.getUserId();

            UpdateUserRequest request = new UpdateUserRequest(
                    userId, updatedUsername, updatedEmail, null
            );

            apiService.updateUser(userId, request).enqueue(new Callback<AuthResponse>() {
                @Override
                public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                    if (response.isSuccessful() && response.body() != null){
                        User updatedUser = response.body().getUser();

                        sessionManager.saveSession(
                                updatedUser.getId(), updatedUser.getUsername(), updatedUser.getEmail()
                        );

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(KEY_PROFILE_BIO, updatedBio);

                        if (selectedImageUri != null) {
                            editor.putString(KEY_PROFILE_IMAGE_URI, selectedImageUri.toString());
                        }

                        editor.apply();

                        Toast.makeText(EditProfileActivity.this, "Profile updated!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(EditProfileActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<AuthResponse> call, Throwable t) {
                    Toast.makeText(EditProfileActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}