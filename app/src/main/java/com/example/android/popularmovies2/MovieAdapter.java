package com.example.android.popularmovies2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies2.model.Movie;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();

    private ArrayList<Movie> mMovies;
    private Context mContext;

    // Create interface to handle selected movie
    public interface MovieAdapterListener {
        void onMovieSelected(Movie movie);
    }

    private final MovieAdapterListener mListener;

    public MovieAdapter(Context context, ArrayList<Movie> movies, MovieAdapterListener listener) {
        this.mListener = listener;
        this.mMovies = new ArrayList<>();
        this.mMovies = movies;
        this.mContext = context;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.gridview_item, parent,false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        Movie movie = mMovies.get(position);
        // Display picture using byte array from movie object
        if (movie != null) {
            byte[] imageByteArray = movie.getImage();
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
            holder.posterImageView.setImageBitmap(bitmap);
        }
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    /**
     * ViewHolder to hold data for a movie item.
     */
    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.movie_poster_iv) ImageView posterImageView;

        public MovieViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            mListener.onMovieSelected(mMovies.get(position));
        }
    }
}
