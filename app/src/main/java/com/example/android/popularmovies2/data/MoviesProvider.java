package com.example.android.popularmovies2.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class MoviesProvider extends ContentProvider {

    // Constant for all movies
    public static final int MOVIES = 100;

    // Constant for a single movie
    public static final int MOVIE_WITH_ID = 101;

    public static final UriMatcher sUriMatcher = buildUriMatcher();

    // Helper method to build a UriMatcher
    public static UriMatcher buildUriMatcher() {
        // Construct an empty matcher
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        // Add matches with URI
        uriMatcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH_MOVIES, MOVIES );
        uriMatcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH_MOVIES + "/#", MOVIE_WITH_ID );
        return uriMatcher;
    }

    MoviesDBHelper mMoviesDBHelper;

    @Override
    public boolean onCreate() {
        mMoviesDBHelper = new MoviesDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        // Get access to the Movies database
        final SQLiteDatabase db = mMoviesDBHelper.getReadableDatabase();

        // Identify the match
        int match = sUriMatcher.match(uri);

        Cursor returnCursor;

        switch (match) {
            case MOVIES:
                returnCursor = db.query(MoviesContract.MovieEntry.TABLE_MOVIE,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                        );
                break;
            case MOVIE_WITH_ID:
                // Get the id from the uri
                String id = uri.getPathSegments().get(1);

                // Create the selection and the selection args
                String mSelection = "_id=?";
                String[] mSelectionArgs = new String[] {id};

                returnCursor = db.query(MoviesContract.MovieEntry.TABLE_MOVIE,
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return returnCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        // Get access to the Movies database
        final SQLiteDatabase db = mMoviesDBHelper.getWritableDatabase();

        // Identify the match
        int match = sUriMatcher.match(uri);

        Uri returnUri;

        switch (match){
            case MOVIES:
                // Insert values into the movies table
                long id = db.insert(MoviesContract.MovieEntry.TABLE_MOVIE, null, contentValues);
                if (id > 0) {
                    // Success
                    returnUri = ContentUris.withAppendedId(MoviesContract.MovieEntry.CONTENT_URI, id);
                } else {
                    // Failure
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("UnknoWn uri: " + uri);
        }

        // Notify the content resolver
        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        // Get access to the Movies database
        final SQLiteDatabase db = mMoviesDBHelper.getWritableDatabase();

        // Identify the match
        int match = sUriMatcher.match(uri);

        int nbOfRows;

        switch(match){
            case MOVIES:
                nbOfRows = db.delete(MoviesContract.MovieEntry.TABLE_MOVIE,
                        null,
                        null);
                break;
            case MOVIE_WITH_ID:
                // Get the id from the uri
                String id = uri.getPathSegments().get(1);

                // Create selection and selectionArgs
                String mSelection = "_id=?";
                String[] mSelectionArgs = new String[] {id};

                nbOfRows = db.delete(MoviesContract.MovieEntry.TABLE_MOVIE,
                        mSelection,
                        mSelectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (nbOfRows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return nbOfRows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection,
                      @Nullable String[] selectionArgs) {

        throw new UnsupportedOperationException("Not yet implemented");
    }
}
