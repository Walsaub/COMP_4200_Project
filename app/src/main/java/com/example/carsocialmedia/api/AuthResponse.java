package com.example.carsocialmedia.api;

import com.example.carsocialmedia.models.User;
import com.google.gson.annotations.SerializedName;

public class AuthResponse {
    @SerializedName("user")
    private User user;

    public User getUser() { return user; }
}
