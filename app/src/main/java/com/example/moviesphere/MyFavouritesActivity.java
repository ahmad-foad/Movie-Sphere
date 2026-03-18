package com.example.moviesphere;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;

public class MyFavouritesActivity extends AppCompatActivity {

    ListView favouritesListView;
    TextView emptyFavouritesTextView;

    DatabaseHelper databaseHelper;
    SharedPreferences sharedPreferences;

    ArrayList<MovieItem> favouritesList;
    MovieAdapter movieAdapter;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_favourites);

        getWindow().setNavigationBarColor(Color.parseColor("#1B263B"));

        databaseHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("MovieSpherePrefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1);

        favouritesListView = findViewById(R.id.favouritesListView);
        emptyFavouritesTextView = findViewById(R.id.emptyFavouritesTextView);

        favouritesList = new ArrayList<>();
        movieAdapter = new MovieAdapter(this, favouritesList);
        favouritesListView.setAdapter(movieAdapter);

        loadFavourites();

        // Item click
        favouritesListView.setOnItemClickListener((parent, view, position, id) -> {
            MovieItem movie = favouritesList.get(position);
            Intent intent = new Intent(MyFavouritesActivity.this, MovieDetailsActivity.class);
            intent.putExtra("movieTitle", movie.getTitle());
            intent.putExtra("imdbID", movie.getImdbID());
            startActivity(intent);
        });

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_favourites);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                Intent intent = new Intent(this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_favourites) {
                return true;
            } else if (itemId == R.id.nav_history) {
                startActivity(new Intent(this, HistoryActivity.class));
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            return false;
        });
    }

    private void loadFavourites() {
        if (userId != -1) {
            Cursor cursor = databaseHelper.getUserFavourites(userId);
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    String title = cursor.getString(cursor.getColumnIndexOrThrow("movie_title"));
                    String movieId = cursor.getString(cursor.getColumnIndexOrThrow("movie_id"));
                    String poster = cursor.getString(cursor.getColumnIndexOrThrow("poster_url"));
                    String year = cursor.getString(cursor.getColumnIndexOrThrow("year"));
                    favouritesList.add(new MovieItem(title, year, "Movie", movieId, poster));
                }
                movieAdapter.notifyDataSetChanged();
                favouritesListView.setVisibility(View.VISIBLE);
                emptyFavouritesTextView.setVisibility(View.GONE);
            } else {
                favouritesListView.setVisibility(View.GONE);
                emptyFavouritesTextView.setVisibility(View.VISIBLE);
            }
            if (cursor != null) cursor.close();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        favouritesList.clear();
        loadFavourites();
    }
}
