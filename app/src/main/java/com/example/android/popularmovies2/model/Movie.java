package com.example.android.popularmovies2.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * POJO representing a Movie Object
 */

public class Movie implements Parcelable {

    private String id;
    private String title;
    private String synopsis;
    private String rating;
    private String releaseDate;
    // This member variable will allow to store directly the poster image
    private byte[] image;

    public Movie(String id, String title, String synopsis, String rating,
                 String releaseDate, byte[] image) {
        this.id = id;
        this.title = title;
        this.synopsis = synopsis;
        this.rating = rating;
        this.releaseDate = releaseDate;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "id: " + this.getId() + "\n" +
                "Title: " + this.getTitle() + "\n" +
                "Synopsis: " + this.getSynopsis() + "\n" +
                "Rating: " + this.getRating() + "\n" +
                "Release date: " + this.getReleaseDate() + "\n";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.title);
        dest.writeString(this.synopsis);
        dest.writeString(this.rating);
        dest.writeString(this.releaseDate);
        dest.writeByteArray(this.image);
    }

    protected Movie(Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        this.synopsis = in.readString();
        this.rating = in.readString();
        this.releaseDate = in.readString();
        this.image = in.createByteArray();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
