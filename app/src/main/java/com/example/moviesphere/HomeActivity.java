package com.example.moviesphere;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    EditText searchEditText, yearEditText;
    Button profileButton, proButton, clearFiltersButton, top250Button;
    CheckBox actionCheckBox, comedyCheckBox, dramaCheckBox, horrorCheckBox, scifiCheckBox, thrillerCheckBox;
    ListView moviesListView;

    ArrayList<MovieItem> moviesList;
    ArrayList<MovieItem> allDefaultMovies; // Store all default movies
    MovieAdapter movieAdapter;

    SharedPreferences sharedPreferences;
    String username;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sharedPreferences = getSharedPreferences("MovieSpherePrefs", MODE_PRIVATE);
        username = sharedPreferences.getString("username", "User");
        userId = sharedPreferences.getInt("userId", -1);

        // Initialize views
        searchEditText = findViewById(R.id.searchEditText);
        yearEditText = findViewById(R.id.yearEditText);
        profileButton = findViewById(R.id.profileButton);
        proButton = findViewById(R.id.proButton);
        clearFiltersButton = findViewById(R.id.clearFiltersButton);
        top250Button = findViewById(R.id.top250Button);

        actionCheckBox = findViewById(R.id.actionCheckBox);
        comedyCheckBox = findViewById(R.id.comedyCheckBox);
        dramaCheckBox = findViewById(R.id.dramaCheckBox);
        horrorCheckBox = findViewById(R.id.horrorCheckBox);
        scifiCheckBox = findViewById(R.id.scifiCheckBox);
        thrillerCheckBox = findViewById(R.id.thrillerCheckBox);

        moviesListView = findViewById(R.id.moviesListView);

        moviesList = new ArrayList<>();
        allDefaultMovies = new ArrayList<>();
        movieAdapter = new MovieAdapter(this, moviesList);
        moviesListView.setAdapter(movieAdapter);

        // Load 20 default popular movies
        loadDefaultMovies();

        // Live search as you type
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (query.isEmpty()) {
                    // Show all default movies when search is empty
                    moviesList.clear();
                    moviesList.addAll(allDefaultMovies);
                    movieAdapter.notifyDataSetChanged();
                } else {
                    // Search from API
                    performSearch();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Profile button
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        // Pro button
        proButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeActivity.this, "⭐ Pro feature coming soon!", Toast.LENGTH_SHORT).show();
            }
        });

        // Top 250 button
        top250Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, Top250Activity.class);
                startActivity(intent);
            }
        });

        // Clear filters button
        clearFiltersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAllFilters();
            }
        });

        // Checkbox listeners
        CheckBox[] checkBoxes = {actionCheckBox, comedyCheckBox, dramaCheckBox, horrorCheckBox, scifiCheckBox, thrillerCheckBox};
        for (CheckBox checkBox : checkBoxes) {
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (searchEditText.getText().toString().trim().isEmpty()) {
                    // If no search query, just show message
                    Toast.makeText(HomeActivity.this, "Type a movie name to filter by genre", Toast.LENGTH_SHORT).show();
                } else {
                    performSearch();
                }
            });
        }

        // Movie item click
        moviesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MovieItem movie = moviesList.get(position);

                Intent intent = new Intent(HomeActivity.this, MovieDetailsActivity.class);
                intent.putExtra("movieTitle", movie.getTitle());
                intent.putExtra("imdbID", movie.getImdbID());
                startActivity(intent);
            }
        });
    }

    private void loadDefaultMovies() {
        // List of 20 popular movies to load by default
        String[] defaultMovieTitles = {
                "The Shawshank Redemption",
                "The Godfather",
                "The Dark Knight",
                "Pulp Fiction",
                "Forrest Gump",
                "Inception",
                "The Matrix",
                "Interstellar",
                "Fight Club",
                "Goodfellas",
                "The Silence of the Lambs",
                "Saving Private Ryan",
                "The Green Mile",
                "The Prestige",
                "The Departed",
                "Gladiator",
                "The Lion King",
                "Back to the Future",
                "The Avengers",
                "Spider-Man"
        };

        // Fetch each movie from API
        for (String title : defaultMovieTitles) {
            fetchMovieByTitle(title);
        }
    }

    private void fetchMovieByTitle(String title) {
        OMDbAPI.searchMovie(title, new OMDbAPI.MovieCallback() {
            @Override
            public void onSuccess(JSONObject movieData) {
                try {
                    if (movieData.getString("Response").equals("True")) {
                        String movieTitle = movieData.getString("Title");
                        String year = movieData.optString("Year", "N/A");
                        String type = movieData.optString("Type", "movie");
                        String imdbID = movieData.getString("imdbID");
                        String poster = movieData.optString("Poster", "N/A");

                        MovieItem movie = new MovieItem(movieTitle, year, type, imdbID, poster);
                        allDefaultMovies.add(movie);
                        moviesList.add(movie);
                        movieAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    // Silently fail for individual movies
                }
            }

            @Override
            public void onError(String error) {
                // Silently fail for individual movies
            }
        });
    }

    private void performSearch() {
        String query = searchEditText.getText().toString().trim();
        String year = yearEditText.getText().toString().trim();
        String genre = getSelectedGenre();

        if (query.isEmpty()) {
            return;
        }

        // Add genre to search query if selected
        if (!genre.isEmpty()) {
            query = query + " " + genre;
        }

        searchMovies(query, year.isEmpty() ? null : year, null);
    }

    private String getSelectedGenre() {
        if (actionCheckBox.isChecked()) return "action";
        if (comedyCheckBox.isChecked()) return "comedy";
        if (dramaCheckBox.isChecked()) return "drama";
        if (horrorCheckBox.isChecked()) return "horror";
        if (scifiCheckBox.isChecked()) return "sci-fi";
        if (thrillerCheckBox.isChecked()) return "thriller";
        return "";
    }

    private void clearAllFilters() {
        searchEditText.setText("");
        yearEditText.setText("");
        actionCheckBox.setChecked(false);
        comedyCheckBox.setChecked(false);
        dramaCheckBox.setChecked(false);
        horrorCheckBox.setChecked(false);
        scifiCheckBox.setChecked(false);
        thrillerCheckBox.setChecked(false);

        // Show all default movies again
        moviesList.clear();
        moviesList.addAll(allDefaultMovies);
        movieAdapter.notifyDataSetChanged();
    }

    private void searchMovies(String query, String year, String type) {
        OMDbAPI.searchWithFilters(query, year, type, new OMDbAPI.MovieCallback() {
            @Override
            public void onSuccess(JSONObject movieData) {
                try {
                    if (movieData.getString("Response").equals("True")) {
                        JSONArray searchResults = movieData.getJSONArray("Search");

                        moviesList.clear();

                        // Show ALL results from search
                        for (int i = 0; i < searchResults.length(); i++) {
                            JSONObject movie = searchResults.getJSONObject(i);

                            String title = movie.getString("Title");
                            String movieYear = movie.getString("Year");
                            String movieType = movie.getString("Type");
                            String imdbID = movie.getString("imdbID");
                            String poster = movie.optString("Poster", "N/A");

                            moviesList.add(new MovieItem(title, movieYear, movieType, imdbID, poster));
                        }

                        movieAdapter.notifyDataSetChanged();

                        if (moviesList.isEmpty()) {
                            Toast.makeText(HomeActivity.this, "No movies found", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        moviesList.clear();
                        movieAdapter.notifyDataSetChanged();
                        Toast.makeText(HomeActivity.this, "No results found", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(HomeActivity.this, "Error loading movies", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(HomeActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}