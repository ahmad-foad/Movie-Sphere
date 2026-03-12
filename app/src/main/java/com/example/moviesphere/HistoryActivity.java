package com.example.moviesphere;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    ListView historyListView;
    TextView emptyHistoryTextView;
    Button backButton;

    DatabaseHelper databaseHelper;
    SharedPreferences sharedPreferences;

    ArrayList<MovieItem> historyList;
    MovieAdapter movieAdapter;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // Force navigation bar color to match the app's bottom nav
        getWindow().setNavigationBarColor(Color.parseColor("#1B263B"));

        databaseHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("MovieSpherePrefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1);

        historyListView = findViewById(R.id.historyListView);
        emptyHistoryTextView = findViewById(R.id.emptyHistoryTextView);
        backButton = findViewById(R.id.backButton);

        historyList = new ArrayList<>();
        movieAdapter = new MovieAdapter(this, historyList);
        historyListView.setAdapter(movieAdapter);

        loadHistory();

        // Back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Item click
        historyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MovieItem movie = historyList.get(position);

                Intent intent = new Intent(HistoryActivity.this, MovieDetailsActivity.class);
                intent.putExtra("movieTitle", movie.getTitle());
                intent.putExtra("imdbID", movie.getImdbID());
                startActivity(intent);
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
            Intent intent = new Intent(HistoryActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });

        navFavouritesButton.setOnClickListener(v -> {
            Intent intent = new Intent(HistoryActivity.this, MyFavouritesActivity.class);
            startActivity(intent);
        });

        navHistoryButton.setOnClickListener(v -> {
            // Already here
        });

        navProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(HistoryActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }

    private void loadHistory() {
        if (userId != -1) {
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

            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
