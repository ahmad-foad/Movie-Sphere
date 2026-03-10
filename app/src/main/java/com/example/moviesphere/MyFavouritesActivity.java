package com.example.moviesphere;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class MyFavouritesActivity extends AppCompatActivity {

    ListView favouritesListView;
    TextView emptyFavouritesTextView;
    Button backButton;

    DatabaseHelper databaseHelper;
    SharedPreferences sharedPreferences;

    ArrayList<MovieItem> favouritesList;
    MovieAdapter movieAdapter;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_favourites);

        databaseHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("MovieSpherePrefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1);

        favouritesListView = findViewById(R.id.favouritesListView);
        emptyFavouritesTextView = findViewById(R.id.emptyFavouritesTextView);
        backButton = findViewById(R.id.backButton);

        favouritesList = new ArrayList<>();
        movieAdapter = new MovieAdapter(this, favouritesList);
        favouritesListView.setAdapter(movieAdapter);

        loadFavourites();

        // Back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Item click
        favouritesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MovieItem movie = favouritesList.get(position);

                Intent intent = new Intent(MyFavouritesActivity.this, MovieDetailsActivity.class);
                intent.putExtra("movieTitle", movie.getTitle());
                intent.putExtra("imdbID", movie.getImdbID());
                startActivity(intent);
            }
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

            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh favourites when returning
        favouritesList.clear();
        loadFavourites();
    }
}