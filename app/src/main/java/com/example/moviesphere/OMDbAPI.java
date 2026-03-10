package com.example.moviesphere;

import android.os.Handler;
import android.os.Looper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;
import java.io.IOException;

public class OMDbAPI {

    private static final String API_KEY = "41bcba27"; // PUT YOUR API KEY HERE
    private static final String BASE_URL = "https://www.omdbapi.com/";
    private static final OkHttpClient client = new OkHttpClient();

    public interface MovieCallback {
        void onSuccess(JSONObject movieData);
        void onError(String error);
    }

    // Search movie by title
    public static void searchMovie(String title, final MovieCallback callback) {
        String url = BASE_URL + "?apikey=" + API_KEY + "&t=" + title.replace(" ", "+");
        makeRequest(url, callback);
    }

    // Search movies by query (returns multiple results)
    public static void searchMovies(String query, final MovieCallback callback) {
        String url = BASE_URL + "?apikey=" + API_KEY + "&s=" + query.replace(" ", "+");
        makeRequest(url, callback);
    }

    // Search with filters (genre, year, etc.)
    public static void searchWithFilters(String query, String year, String type, final MovieCallback callback) {
        StringBuilder urlBuilder = new StringBuilder(BASE_URL);
        urlBuilder.append("?apikey=").append(API_KEY);
        urlBuilder.append("&s=").append(query.replace(" ", "+"));

        if (year != null && !year.isEmpty()) {
            urlBuilder.append("&y=").append(year);
        }

        if (type != null && !type.isEmpty()) {
            urlBuilder.append("&type=").append(type);
        }

        makeRequest(urlBuilder.toString(), callback);
    }

    private static void makeRequest(String url, final MovieCallback callback) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    callback.onError(e.getMessage());
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseData = response.body().string();
                    new Handler(Looper.getMainLooper()).post(() -> {
                        try {
                            JSONObject json = new JSONObject(responseData);
                            callback.onSuccess(json);
                        } catch (Exception e) {
                            callback.onError(e.getMessage());
                        }
                    });
                } else {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        callback.onError("Failed to fetch data");
                    });
                }
            }
        });
    }
}