package com.example.flixster.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Movie {
    // values from API
    // tracks values coming back from the API
    private String title;
    private String overview;
    private String posterPath; // only the path
    private String backdropPath;

    // initialize from JSON data
    public Movie(JSONObject object) throws JSONException {
        // analogous to the constructor in other languages - initialises values based on the JSON object that was passed
        title = object.getString("title"); //allow the error to be thrown and then handled in the main activity
        // this was done by adding an exception for this line after clicking the red light bulb
        overview = object.getString("overview");
        posterPath = object.getString("poster_path");
        backdropPath = object.getString("backdrop_path");

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
}
