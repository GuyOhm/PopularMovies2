package com.example.android.popularmovies2.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class MoviesContract {

    // The authority
    public static final String CONTENT_AUTHORITY = "com.example.android.popularmovies2";

    // The base content URI
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Paths for accessing data in this contract
    public static final String PATH_MOVIES = "movies";

    public static final class MovieEntry implements BaseColumns {
        // MovieEntry content URI
        public static final Uri CONTENT_URI = BASE_CONTENT_URI
                .buildUpon()
                .appendPath(PATH_MOVIES)
                .build();

        // Table name
        public static final String TABLE_MOVIE = "movies";

        // Columns
        public static final String _ID = "_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_SYNOPSIS = "synopsis";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_RELEASE_DATE = "release_date";
    }
}
