package com.example.moviesphere;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class ProfileActivity extends AppCompatActivity {

    TextView usernameTextView;
    CardView historyLayout, favouritesLayout;
    Button backButton, logoutButton;
    SharedPreferences sharedPreferences;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Force navigation bar color to match the app's bottom nav
        getWindow().setNavigationBarColor(Color.parseColor("#1B263B"));

        sharedPreferences = getSharedPreferences("MovieSpherePrefs", MODE_PRIVATE);
        username = sharedPreferences.getString("username", "User");

        usernameTextView = findViewById(R.id.usernameTextView);
        historyLayout = findViewById(R.id.historyLayout);
        favouritesLayout = findViewById(R.id.favouritesLayout);
        backButton = findViewById(R.id.backButton);
        logoutButton = findViewById(R.id.logoutButton);

        usernameTextView.setText(username);

        // Back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // History button
        historyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, HistoryActivity.class);
                startActivity(intent);
            }
        });

        // Favourites button
        favouritesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, MyFavouritesActivity.class);
                startActivity(intent);
            }
        });

        // Logout button
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        Button navHomeButton = findViewById(R.id.navHomeButton);
        Button navFavouritesButton = findViewById(R.id.navFavouritesButton);
        Button navHistoryButton = findViewById(R.id.navHistoryButton);
        Button navProfileButton = findViewById(R.id.navProfileButton);

        navHomeButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });

        navFavouritesButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, MyFavouritesActivity.class);
            startActivity(intent);
        });

        navHistoryButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, HistoryActivity.class);
            startActivity(intent);
        });

        navProfileButton.setOnClickListener(v -> {
            // Already here
        });
    }

    private void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.remove("username");
        editor.remove("userId");
        editor.apply();

        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
