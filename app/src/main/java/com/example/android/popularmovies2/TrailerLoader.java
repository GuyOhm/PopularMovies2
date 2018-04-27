package com.example.android.popularmovies2;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;

import com.example.android.popularmovies2.model.Trailer;
import com.example.android.popularmovies2.utilities.NetworkUtils;

import java.util.ArrayList;

public class TrailerLoader extends AsyncTaskLoader<ArrayList<Trailer>> {

    // Url to query to get Trailers data
    private String mUrl;

    public TrailerLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public ArrayList<Trailer> loadInBackground() {
        // Ensure url is valid
        if (mUrl == null || TextUtils.isEmpty(mUrl)){
            return null;
        }

        // Perform the request on a background thread, parse response and extract a List of Trailers
        return NetworkUtils.fetchTrailerData(mUrl);
    }
}
