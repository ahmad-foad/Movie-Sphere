package com.example.moviesphere;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;

public class HomeActivity extends AppCompatActivity {

    EditText searchEditText;
    Button filterButton;
    ListView moviesListView;

    ArrayList<MovieItem> moviesList;
    ArrayList<MovieItem> allDefaultMovies;
    MovieAdapter movieAdapter;
    HashMap<String, ArrayList<String>> movieGenres;

    SharedPreferences sharedPreferences;
    String username;
    int userId;
    
    BottomNavigationView bottomNavigationView;

    // Debouncing for search
    private Handler mHandler;
    private Runnable mSearchRunnable;
    private static final long SEARCH_DELAY = 500; // 500ms delay

    // Filter states
    boolean isActionSelected = false;
    boolean isComedySelected = false;
    boolean isDramaSelected = false;
    boolean isHorrorSelected = false;
    boolean isScifiSelected = false;
    boolean isThrillerSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sharedPreferences = getSharedPreferences("MovieSpherePrefs", MODE_PRIVATE);
        username = sharedPreferences.getString("username", "User");
        userId = sharedPreferences.getInt("userId", -1);

        searchEditText = findViewById(R.id.searchEditText);
        filterButton = findViewById(R.id.filterButton);
        moviesListView = findViewById(R.id.moviesListView);

        moviesList = new ArrayList<>();
        allDefaultMovies = new ArrayList<>();
        movieGenres = new HashMap<>();
        movieAdapter = new MovieAdapter(this, moviesList);
        moviesListView.setAdapter(movieAdapter);

        // Initialize Handler for debouncing
        mHandler = new Handler(Looper.getMainLooper());

        initializeGenreMappings();
        loadDefaultMovies();

        // Debounced search listener
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Remove previous callback to cancel pending searches
                if (mSearchRunnable != null) {
                    mHandler.removeCallbacks(mSearchRunnable);
                }

                String query = s.toString().trim();
                if (query.isEmpty()) {
                    applyGenreFiltersToDefaults();
                } else {
                    // Create new search task with delay
                    mSearchRunnable = () -> performSearch();
                    mHandler.postDelayed(mSearchRunnable, SEARCH_DELAY);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        filterButton.setOnClickListener(v -> showFilterBottomSheet());

        setupBottomNavigation();

        moviesListView.setOnItemClickListener((parent, view, position, id) -> {
            MovieItem movie = moviesList.get(position);
            Intent intent = new Intent(HomeActivity.this, MovieDetailsActivity.class);
            intent.putExtra("movieTitle", movie.getTitle());
            intent.putExtra("imdbID", movie.getImdbID());
            startActivity(intent);
        });
    }

    private void showFilterBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.layout_filter_bottom_sheet, null);
        bottomSheetDialog.setContentView(view);

        CheckBox actionCb = view.findViewById(R.id.actionCheckBoxSheet);
        CheckBox comedyCb = view.findViewById(R.id.comedyCheckBoxSheet);
        CheckBox dramaCb = view.findViewById(R.id.dramaCheckBoxSheet);
        CheckBox horrorCb = view.findViewById(R.id.horrorCheckBoxSheet);
        CheckBox scifiCb = view.findViewById(R.id.scifiCheckBoxSheet);
        CheckBox thrillerCb = view.findViewById(R.id.thrillerCheckBoxSheet);
        Button resetBtn = view.findViewById(R.id.resetButton);
        Button applyBtn = view.findViewById(R.id.applyFilterButton);

        // Set current states
        actionCb.setChecked(isActionSelected);
        comedyCb.setChecked(isComedySelected);
        dramaCb.setChecked(isDramaSelected);
        horrorCb.setChecked(isHorrorSelected);
        scifiCb.setChecked(isScifiSelected);
        thrillerCb.setChecked(isThrillerSelected);

        resetBtn.setOnClickListener(v -> {
            isActionSelected = isComedySelected = isDramaSelected = isHorrorSelected = isScifiSelected = isThrillerSelected = false;
            applyGenreFiltersToDefaults();
            bottomSheetDialog.dismiss();
            Toast.makeText(this, "Filters reset", Toast.LENGTH_SHORT).show();
        });

        applyBtn.setOnClickListener(v -> {
            isActionSelected = actionCb.isChecked();
            isComedySelected = comedyCb.isChecked();
            isDramaSelected = dramaCb.isChecked();
            isHorrorSelected = horrorCb.isChecked();
            isScifiSelected = scifiCb.isChecked();
            isThrillerSelected = thrillerCb.isChecked();

            if (searchEditText.getText().toString().trim().isEmpty()) {
                applyGenreFiltersToDefaults();
            } else {
                performSearch();
            }
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }
    }

    private void setupBottomNavigation() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_favourites) {
                startActivity(new Intent(this, MyFavouritesActivity.class));
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

    private void loadDefaultMovies() {
        String[] defaultMovieTitles = {
                "The Shawshank Redemption", "The Godfather", "The Dark Knight", "Pulp Fiction",
                "Forrest Gump", "Inception", "The Matrix", "Interstellar", "Fight Club", "Goodfellas",
                "The Silence of the Lambs", "Saving Private Ryan", "The Green Mile", "The Prestige",
                "The Departed", "Gladiator", "The Lion King", "Back to the Future", "The Avengers", "Spider-Man",
                "Titanic", "Avatar", "The Wizard of Oz", "Citizen Kane", "Casablanca",
                "Singin' in the Rain", "It's a Wonderful Life", "Vertigo", "Psycho", "Jaws",
                "E.T. the Extra-Terrestrial", "Raiders of the Lost Ark", "Return of the Jedi",
                "The Empire Strikes Back", "Jurassic Park", "The Sixth Sense", "The Usual Suspects",
                "Se7en", "Heat", "The Social Network"
        };

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
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {
                // Error loading default movie - will try next one
            }
        });
    }

    private void performSearch() {
        String query = searchEditText.getText().toString().trim();
        if (query.isEmpty()) {
            return;
        }
        searchMovies(query, null, null);
    }

    private void searchMovies(String query, String year, String type) {
        OMDbAPI.searchWithFilters(query, year, type, new OMDbAPI.MovieCallback() {
            @Override
            public void onSuccess(JSONObject movieData) {
                try {
                    if (movieData.getString("Response").equals("True")) {
                        JSONArray searchResults = movieData.getJSONArray("Search");
                        moviesList.clear();
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

    private void initializeGenreMappings() {
        movieGenres.put("The Shawshank Redemption", createGenreList("drama"));
        movieGenres.put("The Godfather", createGenreList("drama", "crime"));
        movieGenres.put("The Dark Knight", createGenreList("action", "crime", "drama"));
        movieGenres.put("Pulp Fiction", createGenreList("crime", "drama"));
        movieGenres.put("Forrest Gump", createGenreList("drama", "romance"));
        movieGenres.put("Inception", createGenreList("action", "sci-fi", "thriller"));
        movieGenres.put("The Matrix", createGenreList("action", "sci-fi"));
        movieGenres.put("Interstellar", createGenreList("adventure", "drama", "sci-fi"));
        movieGenres.put("Fight Club", createGenreList("drama", "thriller"));
        movieGenres.put("Goodfellas", createGenreList("crime", "drama"));
        movieGenres.put("The Silence of the Lambs", createGenreList("crime", "drama", "thriller"));
        movieGenres.put("Saving Private Ryan", createGenreList("drama", "war"));
        movieGenres.put("The Green Mile", createGenreList("crime", "drama"));
        movieGenres.put("The Prestige", createGenreList("drama", "mystery", "sci-fi"));
        movieGenres.put("The Departed", createGenreList("crime", "drama", "thriller"));
        movieGenres.put("Gladiator", createGenreList("action", "adventure", "drama"));
        movieGenres.put("The Lion King", createGenreList("animation", "adventure", "comedy"));
        movieGenres.put("Back to the Future", createGenreList("adventure", "comedy", "sci-fi"));
        movieGenres.put("The Avengers", createGenreList("action", "adventure", "sci-fi"));
        movieGenres.put("Spider-Man", createGenreList("action", "adventure", "sci-fi"));
        movieGenres.put("Titanic", createGenreList("drama", "romance"));
        movieGenres.put("Avatar", createGenreList("action", "adventure", "sci-fi"));
        movieGenres.put("The Wizard of Oz", createGenreList("adventure", "family", "fantasy"));
        movieGenres.put("Citizen Kane", createGenreList("drama", "mystery"));
        movieGenres.put("Casablanca", createGenreList("drama", "romance", "war"));
        movieGenres.put("Singin' in the Rain", createGenreList("comedy", "musical", "romance"));
        movieGenres.put("It's a Wonderful Life", createGenreList("comedy", "drama", "family"));
        movieGenres.put("Vertigo", createGenreList("mystery", "thriller"));
        movieGenres.put("Psycho", createGenreList("horror", "thriller"));
        movieGenres.put("Jaws", createGenreList("adventure", "horror", "thriller"));
        movieGenres.put("E.T. the Extra-Terrestrial", createGenreList("adventure", "family", "sci-fi"));
        movieGenres.put("Raiders of the Lost Ark", createGenreList("action", "adventure"));
        movieGenres.put("Return of the Jedi", createGenreList("action", "adventure", "sci-fi"));
        movieGenres.put("The Empire Strikes Back", createGenreList("action", "adventure", "sci-fi"));
        movieGenres.put("Jurassic Park", createGenreList("action", "adventure", "sci-fi"));
        movieGenres.put("The Sixth Sense", createGenreList("drama", "mystery", "thriller"));
        movieGenres.put("The Usual Suspects", createGenreList("crime", "drama", "mystery"));
        movieGenres.put("Se7en", createGenreList("crime", "drama", "mystery", "thriller"));
        movieGenres.put("Heat", createGenreList("action", "crime", "drama"));
        movieGenres.put("The Social Network", createGenreList("biography", "drama"));
    }

    private ArrayList<String> createGenreList(String... genres) {
        ArrayList<String> list = new ArrayList<>();
        for (String genre : genres) {
            list.add(genre.toLowerCase());
        }
        return list;
    }

    private void applyGenreFiltersToDefaults() {
        ArrayList<MovieItem> filteredMovies = new ArrayList<>();
        ArrayList<String> selectedGenres = new ArrayList<>();
        if (isActionSelected) selectedGenres.add("action");
        if (isComedySelected) selectedGenres.add("comedy");
        if (isDramaSelected) selectedGenres.add("drama");
        if (isHorrorSelected) selectedGenres.add("horror");
        if (isScifiSelected) selectedGenres.add("sci-fi");
        if (isThrillerSelected) selectedGenres.add("thriller");

        if (selectedGenres.isEmpty()) {
            moviesList.clear();
            moviesList.addAll(allDefaultMovies);
        } else {
            for (MovieItem movie : allDefaultMovies) {
                ArrayList<String> movieGenresList = movieGenres.get(movie.getTitle());
                if (movieGenresList != null) {
                    boolean matchesGenre = false;
                    for (String selectedGenre : selectedGenres) {
                        if (movieGenresList.contains(selectedGenre)) {
                            matchesGenre = true;
                            break;
                        }
                    }
                    if (matchesGenre) {
                        filteredMovies.add(movie);
                    }
                }
            }
            moviesList.clear();
            moviesList.addAll(filteredMovies);
        }
        movieAdapter.notifyDataSetChanged();
    }
}
