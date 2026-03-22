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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carsocialmedia.EditProfileActivity;
import com.example.carsocialmedia.LoginActivity;
import com.example.carsocialmedia.R;
import com.example.carsocialmedia.adapters.MyPostsAdapter;
import com.example.carsocialmedia.api.ApiClient;
import com.example.carsocialmedia.api.ApiService;
import com.example.carsocialmedia.api.SessionManager;
import com.example.carsocialmedia.models.Post;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private TextView tvProfileName, tvProfileBio, tvPostsCount;
    private Button btnEditProfile, btnBackToFeed, btnLogout;
    private ImageView imgProfile;

    private SharedPreferences sharedPreferences;

    private RecyclerView recyclerMyPosts;
    private MyPostsAdapter adapter;
    private List<Post> myPosts = new ArrayList<>();

    private ApiService apiService;
    private SessionManager sessionManager;

    private static final String PREF_NAME = "CarAppPrefs";
    private static final String KEY_PROFILE_BIO = "profile_bio";
    private static final String KEY_PROFILE_IMAGE_URI = "profile_image_uri";

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
        tvPostsCount = view.findViewById(R.id.tvPostsCount);
        imgProfile = view.findViewById(R.id.imgProfile);

        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnBackToFeed = view.findViewById(R.id.btnBackToFeed);
        btnLogout = view.findViewById(R.id.btnLogout);

        recyclerMyPosts = view.findViewById(R.id.my_posts_recycler_view);
        recyclerMyPosts.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerMyPosts.setNestedScrollingEnabled(false);

        adapter = new MyPostsAdapter(getContext(), myPosts);
        recyclerMyPosts.setAdapter(adapter);

        apiService = ApiClient.getApiService();
        sessionManager = new SessionManager(requireContext());

        sharedPreferences = requireActivity().getSharedPreferences(PREF_NAME, requireActivity().MODE_PRIVATE);

        loadProfileData();
        loadMyPosts();

        btnEditProfile.setOnClickListener(v -> {
            String username = sessionManager.getUsername();
            String bio = sharedPreferences.getString(KEY_PROFILE_BIO, "Car Enthusiast");
            String email = sessionManager.getEmail();

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
            sessionManager.clearSession();

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
        loadMyPosts();
    }

    private void loadProfileData() {
        if (sharedPreferences != null) {
            String username = sessionManager.getUsername();
            String bio = sharedPreferences.getString(KEY_PROFILE_BIO, "Car Enthusiast");
            String imageUriString = sharedPreferences.getString(KEY_PROFILE_IMAGE_URI, "");

            tvProfileName.setText(username != null ? username : "My Profile");
            tvProfileBio.setText(bio);

            if (!imageUriString.isEmpty()) {
                imgProfile.setImageURI(Uri.parse(imageUriString));
            } else {
                imgProfile.setImageResource(android.R.color.transparent);
            }
        }
    }

    private void loadMyPosts(){
        int currentUserId = sessionManager.getUserId();

        apiService.getPosts().enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {

                if (response.isSuccessful() && response.body() != null){
                    myPosts.clear();

                    for (Post p : response.body()){
                        if (p.getUserId() == currentUserId){
                            myPosts.add(p);
                        }
                    }

                    adapter.notifyDataSetChanged();

                    tvPostsCount.setText(String.valueOf(myPosts.size()));
                } else {
                    Toast.makeText(getContext(), "Response not successful", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}