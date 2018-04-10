package com.example.android.popularmovies2.utilities;

import android.net.Uri;

import com.example.android.popularmovies2.BuildConfig;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Utilities class to be used to communicate with the moviedb API
 *
 * Credit: inspired from Sunshine NetworkUtils class
 */
public class NetworkUtils {

    private static final String BASE_URL = "https://api.themoviedb.org/3";
    private static final String IMAGE_URL = "https://image.tmdb.org/t/p";
    private static final String MOVIE_PATH = "movie";
    private static final String IMAGE_SIZE_PATH = "w185";

    // Please enter your own TheMovieDB API key
    private static final String API_KEY = BuildConfig.API_KEY;

    // Queries name
    private static final String API_KEY_QUERY = "api_key";
    private static final String POPULAR_QUERY = "popular";
    private static final String TOP_RATED_QUERY = "top_rated";

    /**
     * Build URL for popular movies to TLDB server
     *
     * @return URL
     */
    public static URL buildPopularUrl(){
        Uri builtUri = Uri
                .parse(BASE_URL)
                .buildUpon()
                .appendPath(MOVIE_PATH)
                .appendPath(POPULAR_QUERY)
                .appendQueryParameter(API_KEY_QUERY, API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * Build URL for highest rated movies to TLDB server
     *
     * @return URL
     */
    public static URL buildHighestRatedUrl(){
        Uri builtUri = Uri
                .parse(BASE_URL)
                .buildUpon()
                .appendPath(MOVIE_PATH)
                .appendPath(TOP_RATED_QUERY)
                .appendQueryParameter(API_KEY_QUERY, API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * Build URL to the movie poster
     *
     * @param string picture path
     * @return String which is the URL to the poster
     */
    public static String buildPosterUrl(String string) {
        Uri builtUri = Uri
                .parse(IMAGE_URL)
                .buildUpon()
                .appendPath(IMAGE_SIZE_PATH)
                .build();

        return builtUri.toString() + string;
    }


    /**
     * Handle HTTP request and get response from it using OkHttp library
     * http://square.github.io/okhttp/
     *
     * @param url to request
     * @return String as a response
     * @throws IOException if an error occurred
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();

        return (response.body() != null) ? response.body().string() : null;
    }
}
