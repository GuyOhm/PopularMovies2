package com.example.android.popularmovies2.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import com.example.android.popularmovies2.BuildConfig;
import com.example.android.popularmovies2.model.Movie;
import com.example.android.popularmovies2.model.Review;
import com.example.android.popularmovies2.model.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Utilities class to be used to communicate with the moviedb API
 *
 * Credit: inspired from Sunshine NetworkUtils class
 */
public class NetworkUtils {

    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();

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
    private static final String TRAILERS_QUERY = "videos";
    private static final String REVIEWS_QUERY = "reviews";

    // Constants for JSON keys
    private static final String JSON_ID_KEY = "id";
    private static final String JSON_RESULTS_KEY = "results";
    private static final String JSON_TITLE_KEY = "title";
    private static final String JSON_POSTER_KEY = "poster_path";
    private static final String JSON_OVERVIEW_KEY = "overview";
    private static final String JSON_VOTE_AVERAGE_KEY = "vote_average";
    private static final String JSON_RELEASE_DATE_KEY = "release_date";
    private static final String JSON_TRAILER_NAME_KEY = "name";
    private static final String JSON_TRAILER_KEY_KEY = "key";
    private static final String JSON_REVIEW_AUTHOR_KEY = "author";
    private static final String JSON_REVIEW_CONTENT_KEY = "content";
    private static final String JSON_REVIEW_URL_KEY = "url";

    // Timeouts for URLConnection in millisec
    private static final int CONNECT_TIMEOUT = 5000;
    private static final int READ_TIMEOUT = 10000;

    /**
     * Build URL for popular movies to TMDB server
     *
     * @return String which is the url
     */
    public static String buildPopularUrl(){
        Uri builtUri = Uri
                .parse(BASE_URL)
                .buildUpon()
                .appendPath(MOVIE_PATH)
                .appendPath(POPULAR_QUERY)
                .appendQueryParameter(API_KEY_QUERY, API_KEY)
                .build();

        return builtUri.toString();
    }

    /**
     * Build URL for highest rated movies to TMDB server
     *
     * @return String which is the url
     */
    public static String buildHighestRatedUrl(){
        Uri builtUri = Uri
                .parse(BASE_URL)
                .buildUpon()
                .appendPath(MOVIE_PATH)
                .appendPath(TOP_RATED_QUERY)
                .appendQueryParameter(API_KEY_QUERY, API_KEY)
                .build();

        return builtUri.toString();
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
     * Build URL to the trailers data
     *
     * @param movieId is the TMDB id for the movie
     * @return a String which is the url
     */
    public static String buildTrailersUrl(String movieId) {
        Uri builtUri = Uri
                .parse(BASE_URL)
                .buildUpon()
                .appendPath(MOVIE_PATH)
                .appendPath(movieId)
                .appendPath(TRAILERS_QUERY)
                .appendQueryParameter(API_KEY_QUERY, API_KEY)
                .build();

        return builtUri.toString();
    }

    /**
     *Build URL to the reviews data
     *
     * @param movieId is the TMDB id for the movie
     * @return a String which is the URL
     */
    public static String buildReviewsUrl(String movieId) {
        Uri builtUri = Uri
                .parse(BASE_URL)
                .buildUpon()
                .appendPath(MOVIE_PATH)
                .appendPath(movieId)
                .appendPath(REVIEWS_QUERY)
                .appendQueryParameter(API_KEY_QUERY, API_KEY)
                .build();

        return builtUri.toString();
    }

    /**
     * This method fetch data from the server, parse the JSON response and convert it to
     * an ArrayList of Movie objects
     *
     * @param requestUrl is the url to request data to API
     *
     * @return an ArrayList of Movie
     */
    public static ArrayList<Movie> fetchMovieData(String requestUrl) {
        // Get the url to make the server request
        URL url;
        try {
            url = new URL(requestUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, e.toString());
            return null;
        }

        // Get the JSON data from the server
        String jsonResponse;
        try {
            jsonResponse = getResponseFromHttpUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, e.toString());
            return null;
        }

        // Convert JSON data to a list of Movies
        ArrayList<Movie> movies = null;
        try {
            movies = convertJsonToMovieObject(jsonResponse);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, e.toString());
        }

        return movies;
    }

    /**
     * This method fetch data from the server, parse the JSON response and convert it to
     * an ArrayList of Trailer objects
     *
     * @param requestUrl is the url to request data to API
     *
     * @return an ArrayList of Trailer
     */
    public static ArrayList<Trailer> fetchTrailerData(String requestUrl) {
        // Get the url to make the server request
        URL url;
        try {
            url = new URL(requestUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, e.toString());
            return null;
        }

        // Get the JSON data from the server
        String jsonResponse;
        try {
            jsonResponse = getResponseFromHttpUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, e.toString());
            return null;
        }

        // Convert JSON data to a list of Trailer
        ArrayList<Trailer> trailers = null;
        try {
            trailers = convertJsonToTrailerObject(jsonResponse);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, e.toString());
        }

        return trailers;
    }

    /**
     This method fetch data from the server, parse the JSON response and convert it to
     * an ArrayList of Review objects
     *
     * @param requestUrl is the url to request data to API
     * @return an ArrayList of Review
     */
    public static ArrayList<Review> fetchReviewData(String requestUrl) {
        // Get the url to make the server request
        URL url;
        try {
            url = new URL(requestUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, e.toString());
            return null;
        }

        // Get the JSON data from the server
        String jsonResponse;
        try {
            jsonResponse = getResponseFromHttpUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, e.toString());
            return null;
        }

        // Convert JSON data to a list of Review
        ArrayList<Review> reviews = null;
        try {
            reviews = convertJsonToReviewObject(jsonResponse);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, e.toString());
        }

        return reviews;
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

    /**
     * Parse the JSON response from TMDB server and convert it to an ArrayList of Movie
     *
     * @param s JSON response from the server
     * @return an ArrayList of Movies
     * @throws JSONException
     */
    public static ArrayList<Movie> convertJsonToMovieObject(String s) throws JSONException {
        JSONObject jsonObject = new JSONObject(s);
        JSONArray results = jsonObject.optJSONArray(JSON_RESULTS_KEY);
        ArrayList<Movie> movies = new ArrayList<>();
        for (int i = 0; i < results.length(); i++) {
            JSONObject result = results.optJSONObject(i);
            movies.add(new Movie(
                    result.optString(JSON_ID_KEY),
                    result.optString(JSON_TITLE_KEY),
                    result.optString(JSON_OVERVIEW_KEY),
                    result.optString(JSON_VOTE_AVERAGE_KEY),
                    result.optString(JSON_RELEASE_DATE_KEY),
                    getImageFromUrl(buildPosterUrl(result.optString(JSON_POSTER_KEY)))
            ));
        }
        return movies;
    }

    /**
     * Get an image from a url and convert it to a byte array so that we can store to SQLite
     * database if needed i.e. the movie is marked as favorite
     *
     * @param url to the poster image
     * @return an array of byte
     */
    private static byte[] getImageFromUrl(String url) {
        try {
            // Create the url
            URL imageUrl = new URL(url);

            // Open url connection
            URLConnection connection = imageUrl.openConnection();

            // Set connecting and reading timeouts as per:
            // https://eventuallyconsistent.net/2011/08/02/working-with-urlconnection-and-timeouts/
            connection.setConnectTimeout(CONNECT_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);

            // Get the input stream from the connection
            InputStream inputStream = connection.getInputStream();

            // Create a bitmap from the input stream
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

            return byteArrayOutputStream.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Parse the JSON response from TMDB server and convert it to an ArrayList of Trailer
     *
     * @param jsonResponse JSON response from the server
     * @return an ArrayList of Trailers
     * @throws JSONException
     */
    private static ArrayList<Trailer> convertJsonToTrailerObject(String jsonResponse) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonResponse);
        JSONArray results = jsonObject.optJSONArray(JSON_RESULTS_KEY);
        ArrayList<Trailer> trailers = new ArrayList<>();
        for (int i = 0; i < results.length(); i++) {
            JSONObject result = results.optJSONObject(i);
            trailers.add(new Trailer(
                    result.optString(JSON_TRAILER_NAME_KEY),
                    result.optString(JSON_TRAILER_KEY_KEY)
            ));
        }
        return trailers;
    }


    /**
     * Parse the JSON response from TMDB server and convert it to an ArrayList of Review
     *
     * @param jsonResponse JSON response from the server
     * @return an ArrayList of Reviews
     * @throws JSONException
     */
    private static ArrayList<Review> convertJsonToReviewObject(String jsonResponse) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonResponse);
        JSONArray results = jsonObject.optJSONArray(JSON_RESULTS_KEY);
        ArrayList<Review> reviews = new ArrayList<>();
        for (int i = 0; i < results.length(); i++) {
            JSONObject result = results.optJSONObject(i);
            reviews.add(new Review(
                    result.optString(JSON_REVIEW_AUTHOR_KEY),
                    result.optString(JSON_REVIEW_CONTENT_KEY),
                    result.optString(JSON_REVIEW_URL_KEY)
            ));
        }
        return reviews;
    }

    /**
     * This method checks network connectivity
     *
     * @return boolean whether or not the device is connected
     */
    public static boolean isNetworkActive(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
