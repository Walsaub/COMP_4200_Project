package com.example.carsocialmedia.api;

import com.example.carsocialmedia.models.Post;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    @POST("api/auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    @POST("api/auth/register")
    Call<AuthResponse> register(@Body RegisterRequest request);

    @PUT("api/auth/users/{id}")
    Call<AuthResponse> updateUser(@Path("id") int id, @Body UpdateUserRequest request);

    @GET("api/posts")
    Call<List<Post>> getPosts();

    @POST("api/posts")
    Call<Post> createPost(@Body Post post);
}

