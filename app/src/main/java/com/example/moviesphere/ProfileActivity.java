package com.example.moviesphere;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    TextView usernameTextView;
    CardView historyLayout, favouritesLayout;
    Button logoutButton;
    SharedPreferences sharedPreferences;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getWindow().setNavigationBarColor(Color.parseColor("#1B263B"));

        sharedPreferences = getSharedPreferences("MovieSpherePrefs", MODE_PRIVATE);
        username = sharedPreferences.getString("username", "User");

        usernameTextView = findViewById(R.id.usernameTextView);
        historyLayout = findViewById(R.id.historyLayout);
        favouritesLayout = findViewById(R.id.favouritesLayout);
        logoutButton = findViewById(R.id.logoutButton);

        usernameTextView.setText(username);

        // History card button
        historyLayout.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, HistoryActivity.class)));

        // Favourites card button
        favouritesLayout.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, MyFavouritesActivity.class)));

        // Logout button
        logoutButton.setOnClickListener(v -> logout());

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                Intent intent = new Intent(this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_favourites) {
                startActivity(new Intent(this, MyFavouritesActivity.class));
                return true;
            } else if (itemId == R.id.nav_history) {
                startActivity(new Intent(this, HistoryActivity.class));
                return true;
            } else if (itemId == R.id.nav_profile) {
                return true;
            }
            return false;
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
