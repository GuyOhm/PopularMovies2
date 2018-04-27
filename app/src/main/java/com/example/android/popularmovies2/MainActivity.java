package com.example.android.popularmovies2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies2.model.Movie;
import com.example.android.popularmovies2.utilities.MoviesUtils;
import com.example.android.popularmovies2.utilities.NetworkUtils;
import com.facebook.stetho.Stetho;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterListener,
        LoaderManager.LoaderCallbacks<ArrayList<Movie>> {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final String MOVIES_QUERY_URL = "movies_query_url";

    private ArrayList<Movie> mMovies = new ArrayList<>();
    private MovieAdapter mMovieAdapter;
    public static final String MOVIE_INTENT_KEY = "movie_key";

    // Loader ID for AsyncTaskLoader
    private static final int LOADER_ID = 101;

    @BindView(R.id.loading_indicator_pb) ProgressBar mLoadingIndicator;
    @BindView(R.id.movies_rv) RecyclerView recyclerView;
    @BindView(R.id.no_network_tv) TextView mNoNetworkTextView;
    @BindView(R.id.error_loading_tv) TextView mErrorTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // Initialize Stetho debugging
        Stetho.initializeWithDefaults(this);

        // Create a layout manager
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,
                MoviesUtils.calculateNoOfColumns(this), GridLayoutManager.VERTICAL, false);

        // Create an adapter
        mMovieAdapter = new MovieAdapter(this, mMovies, this);

        // Set the layout manager and the adapter on to the recycler view
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(mMovieAdapter);

        // Check network connectivity
        if (NetworkUtils.isNetworkActive(this)) {
            // Create a bundle to pass to the loader
            Bundle queryBundle = new Bundle();
            queryBundle.putString(MOVIES_QUERY_URL, NetworkUtils.buildPopularUrl());

            showLoadingIndicator();

            // Initialize the loader
            getSupportLoaderManager().initLoader(LOADER_ID, queryBundle, this);

        } else {
            // Load movies from local database
            loadMovies(null);
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
                if (NetworkUtils.isNetworkActive(this)) {
                    // Load new data with popular url
                    loadMovies(NetworkUtils.buildPopularUrl());
                } else {
                    showNetworkError();
                }
                return true;
            case R.id.action_sort_by_highest_rate:
                if (NetworkUtils.isNetworkActive(this)) {
                    // Load new data with highest rated url
                    loadMovies(NetworkUtils.buildHighestRatedUrl());
                } else {
                    showNetworkError();
                }
                return true;
            case R.id.action_sort_by_favorite:
                loadMovies(null);
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadMovies(String url) {
        Bundle bundle = new Bundle();
        // Set the url as null so that the loader load favorite movies
        bundle.putString(MOVIES_QUERY_URL, url);
        getSupportLoaderManager().restartLoader(LOADER_ID, bundle, this);
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

    // ################################
    // [START - LOADER FOR MOVIES LIST]
    // ################################
    @Override
    public Loader<ArrayList<Movie>> onCreateLoader(int id, Bundle args) {
        return new MovieLoader(this, args.getString(MOVIES_QUERY_URL));
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> data) {
        showGridview();
        // Clear current list of movies and add new data
        mMovies.clear();
        mMovies.addAll(data);
        mMovieAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Movie>> loader) {

    }
    // ################################
    // [END - LOADER FOR MOVIES LIST]
    // ################################
}
