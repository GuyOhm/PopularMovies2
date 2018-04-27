package com.example.android.popularmovies2.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.android.popularmovies2.data.MoviesContract.MovieEntry.COLUMN_IMAGE;
import static com.example.android.popularmovies2.data.MoviesContract.MovieEntry.COLUMN_RATING;
import static com.example.android.popularmovies2.data.MoviesContract.MovieEntry.COLUMN_RELEASE_DATE;
import static com.example.android.popularmovies2.data.MoviesContract.MovieEntry.COLUMN_SYNOPSIS;
import static com.example.android.popularmovies2.data.MoviesContract.MovieEntry.COLUMN_TITLE;
import static com.example.android.popularmovies2.data.MoviesContract.MovieEntry.TABLE_MOVIE;
import static com.example.android.popularmovies2.data.MoviesContract.MovieEntry._ID;

public class MoviesDBHelper extends SQLiteOpenHelper {

    // Declare database name and version
    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 1;

    public MoviesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create the SQL statement to create the table
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " +
                TABLE_MOVIE + "(" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT NOT NULL, " +
                COLUMN_IMAGE + " BLOB, " +
                COLUMN_SYNOPSIS + " TEXT, " +
                COLUMN_RATING + " TEXT, " +
                COLUMN_RELEASE_DATE + " TEXT);";

        // Execute the SQL statement
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // Drop the existing table
        final String SQL_DROP_TABLE_IF_EXISTS = "DROP TABLE IF EXISTS " + TABLE_MOVIE;
        sqLiteDatabase.execSQL(SQL_DROP_TABLE_IF_EXISTS);

        // Re-create the database
        onCreate(sqLiteDatabase);
    }
}
