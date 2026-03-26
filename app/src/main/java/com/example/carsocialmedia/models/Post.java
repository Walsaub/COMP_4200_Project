package com.example.carsocialmedia.models;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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

    @SerializedName("email")
    private String email;

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

    public String getCar(){return make + " " + model + " " + year;}
    public String getCreatedAt() {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());

            format.setTimeZone(TimeZone.getTimeZone("UTC"));

            Date past = format.parse(createdAt);
            long diff = System.currentTimeMillis() - past.getTime();

            long seconds = diff / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;

            if (seconds < 60) return "Just now";
            if (minutes < 60) return minutes + "min ago";
            if (hours < 24) return hours + "hrs ago";

            return new SimpleDateFormat("dd/MMM/yyyy", Locale.getDefault()).format(past);
        } catch (Exception e) {
            return createdAt;
        }
    }

    public String getUsername() { return username; }
    public String getEmail() { return email; }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setMileage(double mileage) {
        this.mileage = mileage;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
