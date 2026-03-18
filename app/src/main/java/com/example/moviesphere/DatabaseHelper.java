package com.example.moviesphere;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "MovieSphere.db";
    private static final int DATABASE_VERSION = 5; 

    // Users Table
    private static final String TABLE_USERS = "users";
    private static final String COL_USER_ID = "id";
    private static final String COL_USERNAME = "username";
    private static final String COL_EMAIL = "email";
    private static final String COL_PASSWORD = "password";

    // History Table
    private static final String TABLE_HISTORY = "history";
    private static final String COL_HISTORY_ID = "id";
    private static final String COL_HISTORY_USER_ID = "user_id";
    private static final String COL_HISTORY_MOVIE_TITLE = "movie_title";
    private static final String COL_HISTORY_MOVIE_ID = "movie_id";
    private static final String COL_HISTORY_POSTER = "poster_url";
    private static final String COL_HISTORY_YEAR = "year";
    private static final String COL_HISTORY_TYPE = "type";
    private static final String COL_HISTORY_DATE = "date_added";

    // Favourites Table
    private static final String TABLE_FAVOURITES = "favourites";
    private static final String COL_FAV_ID = "id";
    private static final String COL_FAV_USER_ID = "user_id";
    private static final String COL_FAV_MOVIE_TITLE = "movie_title";
    private static final String COL_FAV_MOVIE_ID = "movie_id";
    private static final String COL_FAV_POSTER = "poster_url";
    private static final String COL_FAV_RATING = "rating";
    private static final String COL_FAV_YEAR = "year";
    private static final String COL_FAV_DATE = "date_added";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Users Table
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USERNAME + " TEXT UNIQUE NOT NULL, " +
                COL_EMAIL + " TEXT UNIQUE NOT NULL, " +
                COL_PASSWORD + " TEXT NOT NULL)");

        // Create History Table
        db.execSQL("CREATE TABLE " + TABLE_HISTORY + " (" +
                COL_HISTORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_HISTORY_USER_ID + " INTEGER NOT NULL, " +
                COL_HISTORY_MOVIE_TITLE + " TEXT NOT NULL, " +
                COL_HISTORY_MOVIE_ID + " TEXT, " +
                COL_HISTORY_POSTER + " TEXT, " +
                COL_HISTORY_YEAR + " TEXT, " +
                COL_HISTORY_TYPE + " TEXT, " +
                COL_HISTORY_DATE + " INTEGER, " +
                "FOREIGN KEY(" + COL_HISTORY_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + "))");

        // Create Favourites Table
        db.execSQL("CREATE TABLE " + TABLE_FAVOURITES + " (" +
                COL_FAV_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_FAV_USER_ID + " INTEGER NOT NULL, " +
                COL_FAV_MOVIE_TITLE + " TEXT NOT NULL, " +
                COL_FAV_MOVIE_ID + " TEXT, " +
                COL_FAV_POSTER + " TEXT, " +
                COL_FAV_RATING + " TEXT, " +
                COL_FAV_YEAR + " TEXT, " +
                COL_FAV_DATE + " INTEGER, " +
                "UNIQUE(" + COL_FAV_USER_ID + ", " + COL_FAV_MOVIE_TITLE + "), " +
                "FOREIGN KEY(" + COL_FAV_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + "))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVOURITES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // ==================== USER METHODS ====================

    public boolean registerUser(String username, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, username);
        values.put(COL_EMAIL, email);
        values.put(COL_PASSWORD, password);
        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COL_USERNAME + "=? AND " + COL_PASSWORD + "=?", new String[]{username, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    public boolean isUsernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COL_USERNAME + "=?", new String[]{username});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    public boolean isEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COL_EMAIL + "=?", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    public int getUserId(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COL_USER_ID + " FROM " + TABLE_USERS + " WHERE " + COL_USERNAME + "=?", new String[]{username});
        int userId = -1;
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return userId;
    }

    // ==================== HISTORY METHODS ====================

    public boolean addToHistory(int userId, String movieTitle, String movieId, String posterUrl, String year, String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE_HISTORY, COL_HISTORY_USER_ID + "=? AND " + COL_HISTORY_MOVIE_TITLE + "=?", 
                    new String[]{String.valueOf(userId), movieTitle});

            ContentValues values = new ContentValues();
            values.put(COL_HISTORY_USER_ID, userId);
            values.put(COL_HISTORY_MOVIE_TITLE, movieTitle);
            values.put(COL_HISTORY_MOVIE_ID, movieId);
            values.put(COL_HISTORY_POSTER, posterUrl);
            values.put(COL_HISTORY_YEAR, year);
            values.put(COL_HISTORY_TYPE, type);
            values.put(COL_HISTORY_DATE, System.currentTimeMillis());

            db.insert(TABLE_HISTORY, null, values);
            db.setTransactionSuccessful();
            return true;
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public Cursor getUserHistory(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_HISTORY + " WHERE " + COL_HISTORY_USER_ID + "=? ORDER BY " + COL_HISTORY_DATE + " DESC", 
                new String[]{String.valueOf(userId)});
    }

    // ==================== FAVOURITES METHODS ====================

    public boolean addToFavourites(int userId, String movieTitle, String movieId, String posterUrl, String rating, String year) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE_FAVOURITES, COL_FAV_USER_ID + "=? AND " + COL_FAV_MOVIE_TITLE + "=?", 
                    new String[]{String.valueOf(userId), movieTitle});

            ContentValues values = new ContentValues();
            values.put(COL_FAV_USER_ID, userId);
            values.put(COL_FAV_MOVIE_TITLE, movieTitle);
            values.put(COL_FAV_MOVIE_ID, movieId);
            values.put(COL_FAV_POSTER, posterUrl);
            values.put(COL_FAV_RATING, rating);
            values.put(COL_FAV_YEAR, year);
            values.put(COL_FAV_DATE, System.currentTimeMillis());

            db.insert(TABLE_FAVOURITES, null, values);
            db.setTransactionSuccessful();
            return true;
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public boolean removeFromFavourites(int userId, String movieTitle) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_FAVOURITES, COL_FAV_USER_ID + "=? AND " + COL_FAV_MOVIE_TITLE + "=?", new String[]{String.valueOf(userId), movieTitle});
        db.close();
        return result > 0;
    }

    public boolean isFavourite(int userId, String movieTitle) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_FAVOURITES + " WHERE " + COL_FAV_USER_ID + "=? AND " + COL_FAV_MOVIE_TITLE + "=?", new String[]{String.valueOf(userId), movieTitle});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    public Cursor getUserFavourites(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_FAVOURITES + " WHERE " + COL_FAV_USER_ID + "=? ORDER BY " + COL_FAV_DATE + " DESC", 
                new String[]{String.valueOf(userId)});
    }
}
