package com.example.android.popularmovies2;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;

import com.example.android.popularmovies2.data.MoviesContract;
import com.example.android.popularmovies2.model.Movie;
import com.example.android.popularmovies2.utilities.NetworkUtils;

import java.util.ArrayList;

public class MovieLoader extends AsyncTaskLoader<ArrayList<Movie>> {

    // Url to query to get Movies data
    private String mUrl;

    public MovieLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public ArrayList<Movie> loadInBackground() {
        // Ensure the url String is valid
        if (mUrl == null || TextUtils.isEmpty(mUrl)) {
            // If url is null Fetch favorite movie from local storage
            Cursor favoriteCursor = getContext().getContentResolver().query(MoviesContract.MovieEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null);
            // Create an arraylist of movie
            ArrayList<Movie> movies = new ArrayList<>();

            if (favoriteCursor != null) {
                while (favoriteCursor.moveToNext()) {
                    // Populate the arraylist from the cursor
                    movies.add(new Movie(
                            favoriteCursor.getString(favoriteCursor.getColumnIndex(MoviesContract.MovieEntry._ID)),
                            favoriteCursor.getString(favoriteCursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_TITLE)),
                            favoriteCursor.getString(favoriteCursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_SYNOPSIS)),
                            favoriteCursor.getString(favoriteCursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_RATING)),
                            favoriteCursor.getString(favoriteCursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE)),
                            favoriteCursor.getBlob(favoriteCursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_IMAGE))
                    ));
                }
                // Close the cursor
                favoriteCursor.close();
                return movies;
            } else {
                return null;
            }
        }

        // Perform the request, parse the response and extract a List of Movies
        return NetworkUtils.fetchMovieData(mUrl);
    }
}
