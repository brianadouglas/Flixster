package com.example.flixster.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel //annotation indicates class is Parcelable
public class Movie {
    // values from API
    // tracks values coming back from the API
    String title;
    String overview;
    String posterPath; // only the path
    String backdropPath;
    Double voteAverage;

    //no-arg, empty constructor required for Parceler
    public Movie() {}

    // initialize from JSON data
    public Movie(JSONObject object) throws JSONException {
        // analogous to the constructor in other languages - initialises values based on the JSON object that was passed
        title = object.getString("title"); //allow the error to be thrown and then handled in the main activity
        // this was done by adding an exception for this line after clicking the red light bulb
        overview = object.getString("overview");
        posterPath = object.getString("poster_path");
        backdropPath = object.getString("backdrop_path");
        voteAverage = object.getDouble("vote_average");

    }

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }
}
