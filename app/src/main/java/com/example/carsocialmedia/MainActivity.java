package com.example.carsocialmedia;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.carsocialmedia.fragments.HomeFragment;
import com.example.carsocialmedia.fragments.MarketplaceFragment;
import com.example.carsocialmedia.fragments.NewPostFragment;
import com.example.carsocialmedia.fragments.ProfileFragment;
import com.example.carsocialmedia.fragments.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }

        bottomNav.setOnItemSelectedListener(menuItem -> {
            Fragment selectedFragment = null;
            int id = menuItem.getItemId();

            if (id == R.id.home_nav) {
                selectedFragment = new HomeFragment();
            } else if (id == R.id.marketplace_nav) {
                selectedFragment = new MarketplaceFragment();
            } else if (id == R.id.add_post_nav) {
                selectedFragment = new NewPostFragment();
            } else if (id == R.id.search_nav) {
                selectedFragment = new SearchFragment();
            } else if (id == R.id.profile_nav) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }

            return true;
        });
    }
}