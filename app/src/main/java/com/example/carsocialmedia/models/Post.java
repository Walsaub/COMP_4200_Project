package com.example.carsocialmedia.models;

import com.google.gson.annotations.SerializedName;

public class Post {
    @SerializedName("id")
    private int id;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("make")
    private String make;

    @SerializedName("model")
    private String model;

    @SerializedName("year")
    private int year;

    @SerializedName("price")
    private double price;

    @SerializedName("mileage")
    private double mileage;

    @SerializedName("image_url")
    private String imageUrl;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("username")
    private String username;

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getMake() { return make; }
    public String getModel() { return model; }
    public int getYear() { return year; }
    public double getPrice() { return price; }
    public double getMileage() { return mileage; }
    public String getImageUrl() { return imageUrl; }
    public String getCreatedAt() { return createdAt; }
    public String getUsername() { return username; }
}
