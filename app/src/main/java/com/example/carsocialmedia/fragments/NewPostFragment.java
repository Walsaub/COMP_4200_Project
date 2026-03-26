package com.example.carsocialmedia.fragments;

import static android.content.Context.MODE_PRIVATE;
import static android.widget.Toast.LENGTH_SHORT;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
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

    private CheckBox toggleSaleInfo;
    private LinearLayout saleInfoSection;
    private ImageView previewImage;
    private ApiService apiService;
    private SessionManager sessionManager;


    private EditText etTitle, etMake, etModel, etPrice, etMileage, etCarHistory, etImageUrl, etYear;
    private Button btnSave;
    private boolean isImageValid = false;



    public NewPostFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_post, container, false);

        // Set up the View
        previewImage = view.findViewById(R.id.selectedImg);
        etImageUrl = view.findViewById(R.id.carImage);

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


        etTitle = view.findViewById(R.id.carTitle);
        etMake = view.findViewById(R.id.carMake);
        etModel = view.findViewById(R.id.carModel);
        etYear = view.findViewById(R.id.carYear);
        etPrice = view.findViewById(R.id.carPrice);
        etMileage = view.findViewById(R.id.carMileage);
        etCarHistory = view.findViewById(R.id.carHistory);
        btnSave = view.findViewById(R.id.btnSubmit);

        etImageUrl.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus){
                String url = etImageUrl.getText().toString().trim();

                if (url.isEmpty()) return;

                Glide.with(this)
                        .load(url)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, @Nullable Object model, @NonNull Target<Drawable> target, boolean isFirstResource) {
                                isImageValid = false;
                                Toast.makeText(getContext(), "Invalid image URL", Toast.LENGTH_SHORT).show();
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(@NonNull Drawable resource, @NonNull Object model, Target<Drawable> target, @NonNull DataSource dataSource, boolean isFirstResource) {
                                isImageValid = true;
                                return false;
                            }
                        })
                        .into(previewImage);
            }

        });


        btnSave.setOnClickListener(v -> {
            int userId = sessionManager.getUserId();
            Post newPost = new Post();
            newPost.setUserId(userId);
            newPost.setUsername(sessionManager.getUsername());
            newPost.setEmail(sessionManager.getEmail());

            String title = etTitle.getText().toString().trim();
            String imageUrl = etImageUrl.getText().toString().trim();

            if (imageUrl.isEmpty() || !isImageValid){
                etImageUrl.setError("Image URL is Invalid");
                etImageUrl.requestFocus();
                return;
            }

            if (title.isEmpty()){
                etTitle.setError("Title is required");
                etTitle.requestFocus();
                return;
            }



            newPost.setTitle(title);
            newPost.setImageUrl(imageUrl);

            String make = etMake.getText().toString().trim();
            String model = etModel.getText().toString().trim();
            String year = etYear.getText().toString().trim();

            if (make.isEmpty()){
                etMake.setError("Make is required");
                etMake.requestFocus();
                return;
            }

            if (model.isEmpty()){
                etModel.setError("Model is required");
                etModel.requestFocus();
                return;
            }

            if (year.isEmpty()){
                etYear.setError("Year is required");
                etYear.requestFocus();
                return;
            }
            newPost.setMake(make);
            newPost.setModel(model);
            try {
                newPost.setYear(Integer.parseInt(year));
            } catch (Exception e){
                etYear.setError("Invalid year");
                etYear.requestFocus();
                return;
            }

            if(toggleSaleInfo.isChecked()) {
                String price = etPrice.getText().toString().trim();
                String mileage = etMileage.getText().toString().trim();
                String description = etCarHistory.getText().toString().trim();
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

                try {
                    newPost.setPrice(Integer.parseInt(price));
                } catch (Exception e){
                    etPrice.setError("Invalid price");
                    etPrice.requestFocus();
                    return;
                }
                try {
                    newPost.setMileage(Integer.parseInt(mileage));
                } catch (Exception e){
                    etMileage.setError("Invalid mileage");
                    etMileage.requestFocus();
                    return;
                }
                newPost.setDescription(description);
            } else {
                newPost.setPrice(1);
                newPost.setMileage(1);
                newPost.setDescription("Car History");
            }


            apiService.createPost(newPost).enqueue(new Callback<Post>() {
                @Override
                public void onResponse(Call<Post> call, Response<Post> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(NewPostFragment.this.getContext(), "Post created successful", LENGTH_SHORT).show();
                        BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottom_nav);
                        if (bottomNav != null) {
                            bottomNav.setSelectedItemId(R.id.home_nav);
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


}