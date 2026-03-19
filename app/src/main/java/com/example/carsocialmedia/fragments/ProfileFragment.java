package com.example.carsocialmedia.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.carsocialmedia.EditProfileActivity;
import com.example.carsocialmedia.LoginActivity;
import com.example.carsocialmedia.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileFragment extends Fragment {

    private TextView tvProfileName, tvProfileBio;
    private Button btnEditProfile, btnBackToFeed, btnLogout;
    private ImageView imgProfile;

    private SharedPreferences sharedPreferences;

    private static final String PREF_NAME = "CarAppPrefs";
    private static final String KEY_REGISTERED_USERNAME = "registered_username";
    private static final String KEY_REGISTERED_EMAIL = "registered_email";
    private static final String KEY_PROFILE_BIO = "profile_bio";
    private static final String KEY_PROFILE_IMAGE_URI = "profile_image_uri";
    private static final String KEY_REMEMBER = "remember_user";
    private static final String KEY_SAVED_LOGIN_EMAIL = "saved_login_email";
    private static final String KEY_SAVED_LOGIN_PASSWORD = "saved_login_password";

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvProfileName = view.findViewById(R.id.tvProfileName);
        tvProfileBio = view.findViewById(R.id.tvProfileBio);
        imgProfile = view.findViewById(R.id.imgProfile);

        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnBackToFeed = view.findViewById(R.id.btnBackToFeed);
        btnLogout = view.findViewById(R.id.btnLogout);

        sharedPreferences = requireActivity().getSharedPreferences(PREF_NAME, requireActivity().MODE_PRIVATE);

        loadProfileData();

        btnEditProfile.setOnClickListener(v -> {
            String username = sharedPreferences.getString(KEY_REGISTERED_USERNAME, "My Profile");
            String bio = sharedPreferences.getString(KEY_PROFILE_BIO, "Car Enthusiast");
            String email = sharedPreferences.getString(KEY_REGISTERED_EMAIL, "user@example.com");

            Intent intent = new Intent(requireActivity(), EditProfileActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("bio", bio);
            intent.putExtra("email", email);
            startActivity(intent);
        });

        btnBackToFeed.setOnClickListener(v -> {
            BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottom_nav);
            if (bottomNav != null) {
                bottomNav.setSelectedItemId(R.id.home_nav);
            }
        });

        btnLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(KEY_REMEMBER, false);
            editor.remove(KEY_SAVED_LOGIN_EMAIL);
            editor.remove(KEY_SAVED_LOGIN_PASSWORD);
            editor.apply();

            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadProfileData();
    }

    private void loadProfileData() {
        if (sharedPreferences != null) {
            String username = sharedPreferences.getString(KEY_REGISTERED_USERNAME, "My Profile");
            String bio = sharedPreferences.getString(KEY_PROFILE_BIO, "Car Enthusiast");
            String imageUriString = sharedPreferences.getString(KEY_PROFILE_IMAGE_URI, "");

            tvProfileName.setText(username);
            tvProfileBio.setText(bio);

            if (!imageUriString.isEmpty()) {
                imgProfile.setImageURI(Uri.parse(imageUriString));
            } else {
                imgProfile.setImageResource(android.R.color.transparent);
            }
        }
    }
}