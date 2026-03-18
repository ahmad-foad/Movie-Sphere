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

public class HistoryActivity extends AppCompatActivity {

    ListView historyListView;
    TextView emptyHistoryTextView;

    DatabaseHelper databaseHelper;
    SharedPreferences sharedPreferences;

    ArrayList<MovieItem> historyList;
    MovieAdapter movieAdapter;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        getWindow().setNavigationBarColor(Color.parseColor("#1B263B"));

        databaseHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("MovieSpherePrefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1);

        historyListView = findViewById(R.id.historyListView);
        emptyHistoryTextView = findViewById(R.id.emptyHistoryTextView);

        historyList = new ArrayList<>();
        movieAdapter = new MovieAdapter(this, historyList);
        historyListView.setAdapter(movieAdapter);

        loadHistory();

        // Item click
        historyListView.setOnItemClickListener((parent, view, position, id) -> {
            MovieItem movie = historyList.get(position);
            Intent intent = new Intent(HistoryActivity.this, MovieDetailsActivity.class);
            intent.putExtra("movieTitle", movie.getTitle());
            intent.putExtra("imdbID", movie.getImdbID());
            startActivity(intent);
        });

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_history);

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
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            return false;
        });
    }

    private void loadHistory() {
        if (userId != -1) {
            historyList.clear(); // Clear existing list before reloading
            Cursor cursor = databaseHelper.getUserHistory(userId);
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    String title = cursor.getString(cursor.getColumnIndexOrThrow("movie_title"));
                    String movieId = cursor.getString(cursor.getColumnIndexOrThrow("movie_id"));
                    String poster = cursor.getString(cursor.getColumnIndexOrThrow("poster_url"));
                    String year = cursor.getString(cursor.getColumnIndexOrThrow("year"));
                    String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
                    historyList.add(new MovieItem(title, year, type != null ? type : "Movie", movieId, poster));
                }
                movieAdapter.notifyDataSetChanged();
                historyListView.setVisibility(View.VISIBLE);
                emptyHistoryTextView.setVisibility(View.GONE);
            } else {
                historyListView.setVisibility(View.GONE);
                emptyHistoryTextView.setVisibility(View.VISIBLE);
            }
            if (cursor != null) cursor.close();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadHistory(); // Refresh history whenever the activity comes to foreground
    }
}
