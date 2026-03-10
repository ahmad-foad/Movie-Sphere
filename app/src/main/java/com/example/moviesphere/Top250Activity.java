package com.example.moviesphere;

import android.content.Intent;
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

public class Top250Activity extends AppCompatActivity {

    EditText searchEditText, yearEditText;
    Button backButton, clearFiltersButton;
    CheckBox actionCheckBox, comedyCheckBox, dramaCheckBox, horrorCheckBox, scifiCheckBox, thrillerCheckBox;
    ListView moviesListView;

    ArrayList<MovieItem> moviesList;
    MovieAdapter movieAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top250);

        // Initialize views
        searchEditText = findViewById(R.id.searchEditText);
        yearEditText = findViewById(R.id.yearEditText);
        backButton = findViewById(R.id.backButton);
        clearFiltersButton = findViewById(R.id.clearFiltersButton);

        actionCheckBox = findViewById(R.id.actionCheckBox);
        comedyCheckBox = findViewById(R.id.comedyCheckBox);
        dramaCheckBox = findViewById(R.id.dramaCheckBox);
        horrorCheckBox = findViewById(R.id.horrorCheckBox);
        scifiCheckBox = findViewById(R.id.scifiCheckBox);
        thrillerCheckBox = findViewById(R.id.thrillerCheckBox);

        moviesListView = findViewById(R.id.moviesListView);

        moviesList = new ArrayList<>();
        movieAdapter = new MovieAdapter(this, moviesList);
        moviesListView.setAdapter(movieAdapter);

        // Load top movies (search for highly rated classics)
        loadTopMovies();

        // Live search
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                performSearch();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Clear filters
        clearFiltersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAllFilters();
            }
        });

        // Checkbox listeners
        CheckBox[] checkBoxes = {actionCheckBox, comedyCheckBox, dramaCheckBox, horrorCheckBox, scifiCheckBox, thrillerCheckBox};
        for (CheckBox checkBox : checkBoxes) {
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> performSearch());
        }

        // Movie click
        moviesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MovieItem movie = moviesList.get(position);

                Intent intent = new Intent(Top250Activity.this, MovieDetailsActivity.class);
                intent.putExtra("movieTitle", movie.getTitle());
                intent.putExtra("imdbID", movie.getImdbID());
                startActivity(intent);
            }
        });
    }

    private void loadTopMovies() {
        // Search for highly rated movies
        String[] topSearches = {"godfather", "shawshank", "dark knight", "pulp fiction", "schindler",
                "lord rings", "fight club", "inception", "matrix", "goodfellas"};

        // For demo, search one of these classics
        searchMovies(topSearches[0], null, null);
    }

    private void performSearch() {
        String query = searchEditText.getText().toString().trim();
        String year = yearEditText.getText().toString().trim();
        String genre = getSelectedGenre();

        if (query.isEmpty() && genre.isEmpty()) {
            loadTopMovies();
            return;
        }

        if (query.isEmpty()) {
            query = genre.isEmpty() ? "best" : genre;
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

        loadTopMovies();
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

                    } else {
                        moviesList.clear();
                        movieAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    Toast.makeText(Top250Activity.this, "Error loading movies", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(Top250Activity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}