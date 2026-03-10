package com.example.moviesphere;

public class MovieItem {
    private String title;
    private String year;
    private String type;
    private String imdbID;
    private String poster;

    public MovieItem(String title, String year, String type, String imdbID, String poster) {
        this.title = title;
        this.year = year;
        this.type = type;
        this.imdbID = imdbID;
        this.poster = poster;
    }

    public String getTitle() {
        return title;
    }

    public String getYear() {
        return year;
    }

    public String getType() {
        return type;
    }

    public String getImdbID() {
        return imdbID;
    }

    public String getPoster() {
        return poster;
    }
}