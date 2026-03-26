package com.example.carsocialmedia.fragments;

import static android.content.Context.MODE_PRIVATE;
import static android.widget.Toast.LENGTH_SHORT;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.carsocialmedia.EditProfileActivity;
import com.example.carsocialmedia.R;
import com.example.carsocialmedia.api.ApiClient;
import com.example.carsocialmedia.api.ApiService;
import com.example.carsocialmedia.api.SessionManager;
import com.example.carsocialmedia.models.Post;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NewPostFragment extends Fragment {

    private CheckBox toggleSaleInfo, chkBoxRust, chkBoxBodyCond;
    private LinearLayout saleInfoSection;
    private TextView uploadImageBtn;
    private ImageView previewImage;
    private Uri selectedImageUri;
    private ApiService apiService;
    private SessionManager sessionManager;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "CarAppPrefs";


    private EditText etTitle, etMake, etModel, etPrice, etMileage, etCarHistory;
    private Button btnSave;
    private static final String KEY_PROFILE_IMAGE_URI = "profile_image_uri";



    public NewPostFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_post, container, false);

        // Set up the View
        uploadImageBtn = view.findViewById(R.id.uploadBtn);
        previewImage = view.findViewById(R.id.selectedImg);
        uploadImageBtn.setOnClickListener(v -> {
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });
        toggleSaleInfo = view.findViewById(R.id.chkbxSaleInfo);
        saleInfoSection = view.findViewById(R.id.saleInfoSection);
        toggleSaleInfo.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                saleInfoSection.setVisibility(View.VISIBLE);
            } else {
                saleInfoSection.setVisibility(View.GONE);
            }
        });

        // Save to the DB
        apiService = ApiClient.getApiService();
        sessionManager = new SessionManager(this.getContext());
        sharedPreferences = getContext().getSharedPreferences(PREF_NAME, MODE_PRIVATE);


        etTitle = view.findViewById(R.id.carTitle);
        etMake = view.findViewById(R.id.carMake);
        etModel = view.findViewById(R.id.carModel);
        etPrice = view.findViewById(R.id.carPrice);
        etMileage = view.findViewById(R.id.carMileage);
        etCarHistory = view.findViewById(R.id.carHistory);
        chkBoxRust = view.findViewById(R.id.carRust);
        chkBoxBodyCond = view.findViewById(R.id.carBody);
        btnSave = view.findViewById(R.id.btnSubmit);


        btnSave.setOnClickListener(v -> {
            int userId = sessionManager.getUserId();
            Post newPost = new Post();
            newPost.setUserId(userId);
            newPost.setUsername(sessionManager.getUsername());
            newPost.setEmail(sessionManager.getEmail());

            String title = etTitle.getText().toString();
            newPost.setTitle(title);
            if (!selectedImageUri.toString().isEmpty()) {
                newPost.setImageUrl(selectedImageUri.toString());
            }
            //newPost.setImageUrl("imageuri");
            newPost.setMake(etMake.getText().toString());
            newPost.setModel(etModel.getText().toString());
            newPost.setYear(new Date().getYear());

            if(toggleSaleInfo.isChecked()) {
                String price = etPrice.getText().toString();
                String mileage = etMileage.getText().toString();
                String description = etCarHistory.getText().toString();
                if (price.isEmpty()) {
                    etPrice.setError("Price is required");
                    etPrice.requestFocus();
                    return;
                }
                if (mileage.isEmpty()) {
                    etMileage.setError("Mileage is required");
                    etMileage.requestFocus();
                    return;
                }
                if (description.isEmpty()) {
                    etCarHistory.setError("History is required");
                    etCarHistory.requestFocus();
                    return;
                }
                newPost.setPrice(Integer.parseInt(price));
                newPost.setMileage(Integer.parseInt(mileage));
                newPost.setDescription(description);
            } else {
                newPost.setPrice(1);
                newPost.setMileage(1);
                newPost.setDescription("Car History");
            }

            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (selectedImageUri != null) {
                editor.putString(KEY_PROFILE_IMAGE_URI, selectedImageUri.toString());
            }
            editor.apply();

            apiService.createPost(newPost).enqueue(new Callback<Post>() {
                @Override
                public void onResponse(Call<Post> call, Response<Post> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(NewPostFragment.this.getContext(), "Post created successful", LENGTH_SHORT).show();
                        BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottom_nav);
                        if (bottomNav != null) {
                            bottomNav.setSelectedItemId(R.id.marketplace_nav);
                        }
                    } else {
                        Toast.makeText(NewPostFragment.this.getContext(), "Post creation failed" + userId, LENGTH_SHORT).show();
                        System.out.println(response.errorBody());
                    }
                }

                @Override
                public void onFailure(Call<Post> call, Throwable t) {
                    Toast.makeText(NewPostFragment.this.getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        });
        return view;
    }

    private final ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    previewImage.setImageURI(uri);
                } else {
                    Toast.makeText(NewPostFragment.this.getContext(),"No photo Selected", LENGTH_SHORT).show();
                }
            });
}