package com.example.android.popularmovies2;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies2.model.Review;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private Context mContext;
    private ArrayList<Review> mReviews;
    private ReviewClickHandlerListener mListener;

    // Create an interface to handle click on item
    public interface ReviewClickHandlerListener {
        void onReviewClicked(Review review);
    }

    public ReviewAdapter(Context context, ArrayList<Review> reviews, ReviewClickHandlerListener listener) {
        this.mContext = context;
        this.mReviews = new ArrayList<>();
        this.mReviews = reviews;
        this.mListener = listener;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.review_item, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        Review review = mReviews.get(position);
        holder.contentTextView.setText(review.getContent());
        holder.authorTextView.setText(review.getAuthor());
    }

    @Override
    public int getItemCount() {
        return mReviews.size();
    }

    /**
     * ViewHolder to hold data for a review item.
     */
    public class ReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.review_content_tv) TextView contentTextView;
        @BindView(R.id.review_author_tv) TextView authorTextView;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            mListener.onReviewClicked(mReviews.get(position));
        }
    }
}
