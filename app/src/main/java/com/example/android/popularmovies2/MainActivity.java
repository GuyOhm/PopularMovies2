package com.example.android.popularmovies2;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies2.model.Movie;
import com.example.android.popularmovies2.utilities.MoviesUtils;
import com.example.android.popularmovies2.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterListener,
        LoaderManager.LoaderCallbacks<String>{

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    // Constants for JSON keys
    private static final String JSON_TITLE_KEY = "title";
    private static final String JSON_POSTER_KEY = "poster_path";
    private static final String JSON_OVERVIEW_KEY = "overview";
    private static final String JSON_VOTE_AVERAGE_KEY = "vote_average";
    private static final String JSON_RELEASE_DATE_KEY = "release_date";

    private static final String MOVIES_QUERY_URL_EXTRA = "movies_query";

    private final ArrayList<Movie> mMovies = new ArrayList<>();
    private MovieAdapter mMovieAdapter;
    public static final String MOVIE_INTENT_KEY = "movie_key";

    // Loader ID for AsyncTaskLoader
    private static final int LOADER_ID = 101;

    @BindView(R.id.loading_indicator_pb) ProgressBar mLoadingIndicator;
    @BindView(R.id.movies_rv) RecyclerView recyclerView;
    @BindView(R.id.no_network_tv) TextView mNoNetworkTextView;
    @BindView(R.id.error_laoding_tv) TextView mErrorTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // Create and set the adapter to our gridview
        // mMovieAdapter = new MoviesAdapter(MainActivity.this, mMovies, this);
        // gridView.setAdapter(mMovieAdapter);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,
                MoviesUtils.calculateNoOfColumns(this), GridLayoutManager.VERTICAL, false);

        mMovieAdapter = new MovieAdapter(this, mMovies, this);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(mMovieAdapter);

        // Check network connectivity
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {

            // Create a bundle to pass to the loader
            Bundle queryBundle = new Bundle();
            queryBundle.putString(MOVIES_QUERY_URL_EXTRA, NetworkUtils.buildPopularUrl().toString());

            // Initialize the loader
            getSupportLoaderManager().initLoader(LOADER_ID, queryBundle, this);

        } else {
            showNetworkError();
        }
    }

    /**
     * Callback to handle selected movie and display its details
     *
     * @param movie object
     */
    @Override
    public void onMovieSelected(Movie movie) {
        Intent movieIntent = new Intent(MainActivity.this, DetailActivity.class);
        movieIntent.putExtra(MOVIE_INTENT_KEY, movie);
        startActivity(movieIntent);
    }

    @Override
    public Loader<String> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<String>(this) {
            @Override
            protected void onStartLoading() {
                if (args == null){
                    return;
                }
                showLoadingIndicator();
                forceLoad();
            }

            @Override
            public String loadInBackground() {
                // Ensure the String is valid
                String searchQueryUrlString = args.getString(MOVIES_QUERY_URL_EXTRA);
                if (searchQueryUrlString == null || TextUtils.isEmpty(searchQueryUrlString)) {
                    return null;
                }

                try {
                    // Build the url from the string
                    URL searchQueryUrl = new URL(args.getString(MOVIES_QUERY_URL_EXTRA));
                    // Return the http response to the url request
                    return NetworkUtils.getResponseFromHttpUrl(searchQueryUrl);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        if (data != null && !TextUtils.isEmpty(data)) {
            try {
                // Convert JSON data to movie objects
                convertJsonToObject(data);
                showGridview();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

    /**
     * Convert the response from TMDB server to Movie objects and add them to an ArrayList
     *
     * @param s String which is the response from TMDB server
     * @throws JSONException
     */
    private void convertJsonToObject(String s) throws JSONException {
        JSONObject jsonObject = new JSONObject(s);
        JSONArray results = jsonObject.optJSONArray("results");
        for (int i = 0; i < results.length(); i++) {
            JSONObject result = results.optJSONObject(i);
            mMovies.add(new Movie(
                    result.optString(JSON_TITLE_KEY),
                    result.optString(JSON_POSTER_KEY),
                    result.optString(JSON_OVERVIEW_KEY),
                    result.optString(JSON_VOTE_AVERAGE_KEY),
                    result.optString(JSON_RELEASE_DATE_KEY)
            ));
        }
        mMovieAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate main menu options
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_sort_by_popular:
                // Clear current list of movies
                mMovies.clear();
                // Load new data with popular url
                Bundle popularBundle = new Bundle();
                popularBundle.putString(MOVIES_QUERY_URL_EXTRA, NetworkUtils.buildPopularUrl().toString());
                getSupportLoaderManager().restartLoader(LOADER_ID, popularBundle, this);
                return true;
            case R.id.action_sort_by_highest_rate:
                // Clear current list of movies
                mMovies.clear();
                // Load new data with highest rated url
                Bundle HighestRatedbundle = new Bundle();
                HighestRatedbundle.putString(MOVIES_QUERY_URL_EXTRA, NetworkUtils.buildHighestRatedUrl().toString());
                getSupportLoaderManager().restartLoader(LOADER_ID, HighestRatedbundle, this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showLoadingIndicator() {
        mLoadingIndicator.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
        mNoNetworkTextView.setVisibility(View.INVISIBLE);
        mErrorTextView.setVisibility(View.INVISIBLE);
    }

    private void showGridview() {
        recyclerView.setVisibility(View.VISIBLE);
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mNoNetworkTextView.setVisibility(View.INVISIBLE);
        mErrorTextView.setVisibility(View.INVISIBLE);
    }

    private void showNetworkError() {
        mNoNetworkTextView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mErrorTextView.setVisibility(View.INVISIBLE);
    }
}
