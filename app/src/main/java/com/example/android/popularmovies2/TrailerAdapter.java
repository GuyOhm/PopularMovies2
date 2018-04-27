package com.example.android.popularmovies2;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies2.model.Trailer;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    private Context mContext;
    private ArrayList<Trailer> mTrailers;

    // Create an interface to handle play button
    public interface PlayTrailerListener {
        void onPlayTrailerClicked(Trailer trailer);
    }

    private final PlayTrailerListener mListener;

    public TrailerAdapter(Context context, ArrayList<Trailer> trailers, PlayTrailerListener listener) {
        this.mContext = context;
        this.mTrailers = new ArrayList<>();
        this.mTrailers = trailers;
        this.mListener = listener;
    }

    @Override
    public TrailerAdapter.TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.trailer_item, parent, false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerAdapter.TrailerViewHolder holder, int position) {
        final Trailer trailer = mTrailers.get(position);
        // Display title of the trailer
        holder.titleTextView.setText(trailer.getName());

        // Set a click handler to play image passing data to listener
        holder.playImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onPlayTrailerClicked(trailer);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTrailers.size();
    }

    /**
     * ViewHolder to hold data for a trailer item.
     */
    public class TrailerViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.play_iv) ImageView playImageView;
        @BindView(R.id.teaser_title_tv) TextView titleTextView;

        public TrailerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
