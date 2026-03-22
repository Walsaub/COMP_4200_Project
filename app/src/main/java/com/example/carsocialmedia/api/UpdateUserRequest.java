package com.example.carsocialmedia.api;

import com.google.gson.annotations.SerializedName;

public class UpdateUserRequest {
    @SerializedName("user_id")
    private int userId;

    @SerializedName("username")
    private String username;

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    public UpdateUserRequest(int userId, String username, String email, String password) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.password = (password != null && !password.isEmpty()) ? password : null;
    }
}
