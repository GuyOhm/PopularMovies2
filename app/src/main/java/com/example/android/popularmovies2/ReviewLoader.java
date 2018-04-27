package com.example.android.popularmovies2;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;

import com.example.android.popularmovies2.model.Review;
import com.example.android.popularmovies2.utilities.NetworkUtils;

import java.util.ArrayList;

public class ReviewLoader extends AsyncTaskLoader<ArrayList<Review>> {

    // Url to query to get Reviews data
    private String mUrl;

    public ReviewLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public ArrayList<Review> loadInBackground() {
        // Ensure url is valid
        if (mUrl == null || TextUtils.isEmpty(mUrl)) {
            return null;
        }

        // Perform the request on a background thread, parse response and extract a list of Reviews
        return NetworkUtils.fetchReviewData(mUrl);
    }
}
