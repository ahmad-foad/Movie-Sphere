package com.example.moviesphere;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;

public class MovieAdapter extends ArrayAdapter<MovieItem> {

    private Context context;
    private ArrayList<MovieItem> movies;

    public MovieAdapter(Context context, ArrayList<MovieItem> movies) {
        super(context, 0, movies);
        this.context = context;
        this.movies = movies;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.movie_item_card, parent, false);
        }

        MovieItem movie = movies.get(position);

        ImageView posterImageView = convertView.findViewById(R.id.moviePosterImageView);
        TextView titleTextView = convertView.findViewById(R.id.movieTitleTextView);
        TextView yearTextView = convertView.findViewById(R.id.movieYearTextView);
        TextView typeTextView = convertView.findViewById(R.id.movieTypeTextView);

        titleTextView.setText(movie.getTitle());
        yearTextView.setText(movie.getYear());
        typeTextView.setText(movie.getType());

        // Load poster with Glide
        if (movie.getPoster() != null && !movie.getPoster().equals("N/A")) {
            Glide.with(context)
                    .load(movie.getPoster())
                    .placeholder(android.R.color.darker_gray)
                    .into(posterImageView);
        } else {
            posterImageView.setImageResource(android.R.color.darker_gray);
        }

        return convertView;
    }
}