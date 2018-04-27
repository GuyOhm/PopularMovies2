package com.example.android.popularmovies2;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies2.data.MoviesContract;
import com.example.android.popularmovies2.model.Movie;
import com.example.android.popularmovies2.model.Review;
import com.example.android.popularmovies2.model.Trailer;
import com.example.android.popularmovies2.utilities.NetworkUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.android.popularmovies2.MainActivity.MOVIE_INTENT_KEY;

public class DetailActivity extends AppCompatActivity
        implements TrailerAdapter.PlayTrailerListener, ReviewAdapter.ReviewClickHandlerListener{

    private static final String LOG_TAG = DetailActivity.class.getSimpleName();
    private static final String TRAILERS_QUERY_URL = "trailers_query_url";
    private static final String REVIEWS_QUERY_URL = "reviews_query_url";
    private static final int TRAILER_LOADER_ID = 102;
    private static final int REVIEW_LOADER_ID = 103;
    private ArrayList<Trailer> mTrailers = new ArrayList<>();
    private TrailerAdapter mTrailerAdapter;
    private ArrayList<Review> mReviews = new ArrayList<>();
    private ReviewAdapter mReviewAdapter;

    // Flag for favorite state
    private boolean isSetAsFavorite = false;

    // Membre variable for movie uri
    private Uri mUri;

    @BindView(R.id.detail_poster_iv) ImageView posterImageView;
    @BindView(R.id.synopsis_tv) TextView synopsisTextView;
    @BindView(R.id.title_tv) TextView titleTextView;
    @BindView(R.id.release_tv) TextView releaseTextView;
    @BindView(R.id.rating_tv) TextView ratingTextView;
    @BindView(R.id.trailers_rv) RecyclerView trailersRecyclerView;
    @BindView(R.id.reviews_rv) RecyclerView reviewsRecyclerView;
    @BindView(R.id.favorite_ib) ImageButton favoriteImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        // Create layout managers
        LinearLayoutManager trailerLayoutManager = new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, false);

        LinearLayoutManager reviewLayoutManager = new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, false);

        // Create adapters
        mTrailerAdapter = new TrailerAdapter(this, mTrailers, this);
        mReviewAdapter = new ReviewAdapter(this, mReviews, this);

        // Set up recycler views
        trailersRecyclerView.setHasFixedSize(true);
        trailersRecyclerView.setLayoutManager(trailerLayoutManager);
        trailersRecyclerView.setAdapter(mTrailerAdapter);

        reviewsRecyclerView.setHasFixedSize(true);
        reviewsRecyclerView.setLayoutManager(reviewLayoutManager);
        reviewsRecyclerView.setAdapter(mReviewAdapter);

        // Get intent that has started the activity
        Intent intent = getIntent();
        Movie selectedMovie = null;
        if (intent.hasExtra(MOVIE_INTENT_KEY)) {
            selectedMovie = intent.getParcelableExtra(MOVIE_INTENT_KEY);
            Log.d(LOG_TAG, selectedMovie.toString());
        }

        // Populate UI with intent data
        if (selectedMovie != null) {
            populateUI(selectedMovie);
        } else  {
            Toast.makeText(DetailActivity.this, R.string.no_movie_selected, Toast.LENGTH_SHORT)
                    .show();
            finish();
        }


        // Set up favorite button
        favoriteImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isSetAsFavorite) {
                    setAsNotFavorite();
                    deleteMovieFromDatabase();
                } else {
                    setAsFavorite();
                    insertMovieToDatabase();
                }
            }
        });
    }

    private void setAsNotFavorite() {
        favoriteImageButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_border_grey_36dp));
        isSetAsFavorite = false;
    }

    private void setAsFavorite() {
        favoriteImageButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_grey_36dp));
        isSetAsFavorite = true;
    }

    private void insertMovieToDatabase() {
        Intent intent = getIntent();
        Movie selectedMovie = intent.getParcelableExtra(MOVIE_INTENT_KEY);

        // Create a content values object
        ContentValues cv = new ContentValues();

        // Add movie data to the CV
        cv.put(MoviesContract.MovieEntry.COLUMN_TITLE, selectedMovie.getTitle());
        cv.put(MoviesContract.MovieEntry.COLUMN_IMAGE, selectedMovie.getImage());
        cv.put(MoviesContract.MovieEntry.COLUMN_SYNOPSIS, selectedMovie.getSynopsis());
        cv.put(MoviesContract.MovieEntry.COLUMN_RATING, selectedMovie.getRating());
        cv.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, selectedMovie.getReleaseDate());

        // Insert new movie data via content resolver
        mUri = getContentResolver().insert(MoviesContract.MovieEntry.CONTENT_URI, cv);
    }

    private void deleteMovieFromDatabase() {
        getContentResolver().delete(mUri, null, null);
    }

    /**
     * Display selected movie data to the UI
     *
     * @param selectedMovie that has been selected from the list
     */
    private void populateUI(Movie selectedMovie) {
        // Display text movie details
        titleTextView.setText(selectedMovie.getTitle());
        releaseTextView.setText(selectedMovie.getReleaseDate());
        ratingTextView.setText(selectedMovie.getRating());
        synopsisTextView.setText(selectedMovie.getSynopsis());

        // Display poster image from byte array
        byte[] imageByteArray = selectedMovie.getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
        posterImageView.setImageBitmap(bitmap);

        // Display whether or not the selected movie is favorite
        initializeFavorite(selectedMovie);

        if (NetworkUtils.isNetworkActive(this)) {
            // Display trailers list
            initializeTrailersList(selectedMovie);
            // Display reviews list
            initializeReviewsList(selectedMovie);
        }
    }

    private void initializeFavorite(Movie selectedMovie) {
        String selection = "title=?";
        String title = selectedMovie.getTitle();
        String[] selectionArgs = new String[] {title};

        Cursor cursor = getContentResolver().query(MoviesContract.MovieEntry.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                setAsFavorite();
                // Initialize the uri
                String id = cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry._ID));
                mUri = MoviesContract.MovieEntry.CONTENT_URI
                        .buildUpon()
                        .appendPath(id)
                        .build();
            } else {
                setAsNotFavorite();
            }
            cursor.close();
        }

    }

    /**
     * Display the selected movie's trailers using a loader
     *
     * @param selectedMovie that has been selected from the list
     */
    private void initializeTrailersList(Movie selectedMovie) {
        String url = NetworkUtils.buildTrailersUrl(selectedMovie.getId());
        Bundle bundle = new Bundle();
        bundle.putString(TRAILERS_QUERY_URL, url);
        getSupportLoaderManager().initLoader(TRAILER_LOADER_ID, bundle, dataTrailersLoaderListener);
    }

    /**
     * Display the selected movie's reviews using a loader
     *
     * @param selectedMovie that has been selected from the list
     */
    private void initializeReviewsList(Movie selectedMovie) {
        String url = NetworkUtils.buildReviewsUrl(selectedMovie.getId());
        Bundle bundle = new Bundle();
        bundle.putString(REVIEWS_QUERY_URL, url);
        getSupportLoaderManager().initLoader(REVIEW_LOADER_ID, bundle, dataReviewsLoaderListener);
    }

    // ##################################
    // [START - LOADER FOR TRAILERS LIST]
    // ##################################
    LoaderManager.LoaderCallbacks<ArrayList<Trailer>> dataTrailersLoaderListener =
            new LoaderManager.LoaderCallbacks<ArrayList<Trailer>>() {
        @Override
        public Loader<ArrayList<Trailer>> onCreateLoader(int id, Bundle args) {
            return new TrailerLoader(DetailActivity.this, args.getString(TRAILERS_QUERY_URL));
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<Trailer>> loader, ArrayList<Trailer> data) {
            mTrailers.clear();
            mTrailers.addAll(data);
            mTrailerAdapter.notifyDataSetChanged();
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<Trailer>> loader) {
        }
    };
    // ##################################
    // [END - LOADER FOR TRAILERS LIST]
    // ##################################

    // ##################################
    // [START - LOADER FOR REVIEWS LIST]
    // ##################################
    private LoaderManager.LoaderCallbacks<ArrayList<Review>> dataReviewsLoaderListener =
            new LoaderManager.LoaderCallbacks<ArrayList<Review>>() {

        @Override
        public Loader<ArrayList<Review>> onCreateLoader(int id, Bundle args) {
            return new ReviewLoader(DetailActivity.this, args.getString(REVIEWS_QUERY_URL));
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<Review>> loader, ArrayList<Review> data) {
            mReviews.clear();
            mReviews.addAll(data);
            mReviewAdapter.notifyDataSetChanged();
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<Review>> loader) {
        }
    };
    // ##################################
    // [END - LOADER FOR REVIEWS LIST]
    // ##################################

    /**
     * Handle click to play the trailer
     *
     * Inspired from :
     * https://stackoverflow.com/questions/574195/android-youtube-app-play-video-intent
     *
     * @param trailer object
     */
    @Override
    public void onPlayTrailerClicked(Trailer trailer) {
        // Create an intent for youtube app
        Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + trailer.getKey()));
        // Create an intent for browser
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(
                "https://www.youtube.com/watch?v=" + trailer.getKey()));

        try {
            // Play the trailer video with youtube app
            startActivity(youtubeIntent);
        } catch (ActivityNotFoundException e) {
            // Youtube app not installed, play video in browser
            startActivity(browserIntent);
        }
    }

    /**
     * Handle click on review item and open the review in the browser
     *
     * @param review object that has been clicked
     */
    @Override
    public void onReviewClicked(Review review) {
        // Create an intent for browser app
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(review.getUrl()));
        startActivity(browserIntent);
    }
}
