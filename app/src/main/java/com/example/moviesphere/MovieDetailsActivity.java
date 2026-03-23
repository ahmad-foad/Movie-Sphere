package com.example.moviesphere;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;
import org.json.JSONObject;

public class MovieDetailsActivity extends AppCompatActivity {

    ImageView moviePosterImageView;
    TextView movieTitleTextView, movieYearTextView, movieRatingTextView, movieRuntimeTextView;
    TextView movieGenreTextView, moviePlotTextView;
    Button favouriteButton, shareButton, trailerButton;
    ImageButton backButton;

    DatabaseHelper databaseHelper;
    SharedPreferences sharedPreferences;

    String movieTitle;
    String imdbID;
    int userId;
    boolean isFavourite = false;

    String posterUrl = "";
    String rating = "";
    String year = "";
    String type = "movie";
    String plot = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        databaseHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("MovieSpherePrefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1);

        // Get data from intent
        movieTitle = getIntent().getStringExtra("movieTitle");
        imdbID = getIntent().getStringExtra("imdbID");

        // Initialize views
        moviePosterImageView = findViewById(R.id.moviePosterImageView);
        movieTitleTextView = findViewById(R.id.movieTitleTextView);
        movieYearTextView = findViewById(R.id.movieYearTextView);
        movieRatingTextView = findViewById(R.id.movieRatingTextView);
        movieRuntimeTextView = findViewById(R.id.movieRuntimeTextView);
        movieGenreTextView = findViewById(R.id.movieGenreTextView);
        moviePlotTextView = findViewById(R.id.moviePlotTextView);
        favouriteButton = findViewById(R.id.favouriteButton);
        shareButton = findViewById(R.id.shareButton);
        trailerButton = findViewById(R.id.trailerButton);
        backButton = findViewById(R.id.backButton);

        // Set title
        movieTitleTextView.setText(movieTitle);

        // Check if favourite
        checkFavouriteStatus();

        // Fetch movie details from API
        fetchMovieDetails();

        // Favourite button
        favouriteButton.setOnClickListener(v -> toggleFavourite());

        // Share button
        shareButton.setOnClickListener(v -> shareMovie());

        // Watch trailer button
        trailerButton.setOnClickListener(v -> watchTrailer());

        // Back button
        backButton.setOnClickListener(v -> finish());
    }

    private void fetchMovieDetails() {
        OMDbAPI.searchMovie(movieTitle, new OMDbAPI.MovieCallback() {
            @Override
            public void onSuccess(JSONObject movieData) {
                try {
                    if (movieData.getString("Response").equals("True")) {
                        // Extract data
                        year = movieData.optString("Year", "N/A");
                        rating = movieData.optString("imdbRating", "N/A");
                        String runtime = movieData.optString("Runtime", "N/A");
                        String genre = movieData.optString("Genre", "N/A");
                        plot = movieData.optString("Plot", "No plot available.");
                        posterUrl = movieData.optString("Poster", "N/A");
                        type = movieData.optString("Type", "movie");

                        // Update UI
                        movieYearTextView.setText(year);
                        movieRatingTextView.setText(rating);
                        movieRuntimeTextView.setText(runtime);
                        movieGenreTextView.setText(genre);
                        moviePlotTextView.setText(plot);

                        // Load poster
                        if (!posterUrl.equals("N/A") && !posterUrl.isEmpty()) {
                            Glide.with(MovieDetailsActivity.this)
                                    .load(posterUrl)
                                    .placeholder(android.R.color.darker_gray)
                                    .into(moviePosterImageView);
                        }

                        // Add to history AFTER details are fetched
                        addToHistory();

                    } else {
                        Toast.makeText(MovieDetailsActivity.this, "Movie details not found", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(MovieDetailsActivity.this, "Error loading details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(MovieDetailsActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addToHistory() {
        if (userId != -1) {
            databaseHelper.addToHistory(userId, movieTitle, imdbID, posterUrl, year, type);
        }
    }

    private void checkFavouriteStatus() {
        if (userId != -1) {
            isFavourite = databaseHelper.isFavourite(userId, movieTitle);
            updateFavouriteButton();
        }
    }

    private void toggleFavourite() {
        if (userId == -1) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isFavourite) {
            // Remove from favourites
            boolean success = databaseHelper.removeFromFavourites(userId, movieTitle);
            if (success) {
                isFavourite = false;
                Toast.makeText(this, "Removed from favourites", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Add to favourites
            boolean success = databaseHelper.addToFavourites(userId, movieTitle, imdbID, posterUrl, rating, year);
            if (success) {
                isFavourite = true;
                Toast.makeText(this, "Added to favourites ❤️", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Already in favourites", Toast.LENGTH_SHORT).show();
            }
        }

        updateFavouriteButton();
    }

    private void updateFavouriteButton() {
        if (isFavourite) {
            favouriteButton.setText("Favourited");
            favouriteButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorite, 0, 0, 0);
            favouriteButton.setBackgroundTintList(ContextCompat.getColorStateList(this, android.R.color.holo_red_dark));
        } else {
            favouriteButton.setText("Favourite");
            favouriteButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorite, 0, 0, 0);
            favouriteButton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.nav_item_color));
        }
    }

    private void shareMovie() {
        String shareText = "🎬 Check out this movie: " + movieTitle + "\n\n" +
                "⭐ Rating: " + rating + "/10\n" +
                "📅 Year: " + year + "\n\n" +
                "📖 " + plot + "\n\n" +
                "Watch it on MovieSphere! 🎥";

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "MovieSphere Recommendation");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);

        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    private void watchTrailer() {
        String trailerUrl = "https://www.youtube.com/results?search_query=" +
                movieTitle.replace(" ", "+") + "+official+trailer";
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(trailerUrl));
        startActivity(browserIntent);
    }
}
