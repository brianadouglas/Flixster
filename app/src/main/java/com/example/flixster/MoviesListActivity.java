package com.example.flixster;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flixster.models.Config;
import com.example.flixster.models.Movie;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MoviesListActivity extends AppCompatActivity {

    // constants
    // the base URL for the API call - the first part of the API call that will always be the same
    public final static String API_BASE_URL = "https://api.themoviedb.org/3";
    // the parameter for the API key
    public final static String API_KEY_PARAM = "api_key";
    // tag for logging from this activity
    public final static String TAG = "MoviesListActivity";

    // instance fields - only have values associated with a specific instance this activity
    AsyncHttpClient client; //used throughout this instance of the Activity to make API calls

    // to track the 2 values for viewing images as instance fields
    // the base URL for loading images
    String imageBaseUrl;
    // the poster size to use when fetching images, part of the URL
    String posterSize;

    // the list of currently playing movies
    ArrayList<Movie> movies;

    // final wiring in movie list activity
    // to track the adapter and recycler view

    RecyclerView rvMovies;
    // the adapter wired to the recycler view
    MovieAdapter adapter;

    // image config
    Config config;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // initialise the client
        client = new AsyncHttpClient();
        // initialize the list of movies strategically before the network call
        movies = new ArrayList<>();

        // initialise the adapter after the movies array list since this array list needs to be passed to the adapter
        // beyond this point, the movies arraylist cannot be re-initialised
        adapter = new MovieAdapter(movies);

        // resolve the reference to the recycler view from the layout, wire it to the adapter, and connect the adapter
        rvMovies = (RecyclerView) findViewById(R.id.rvMovies);
        rvMovies.setLayoutManager(new LinearLayoutManager(this));
        rvMovies.setAdapter(adapter);

        // get the configuration on app creation
        getConfiguration();
        // if getNowPlaying was here, there would be no guarantee in the order of these method calls returning since
        // they are both asynchronous and won't wait for a method to return before moving to the next one
    }

    //get the list of currently playing movies from API
    private void getNowPlaying() {
        //create the URL that will be accessed
        String url = API_BASE_URL + "/movie/now_playing";
        // set the request parameters - the parameter values that get appended to the URL
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.api_key));
        // GET request
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // load the results into the movies list
                // start parsing the results value as a JSON
                try {
                    JSONArray results = response.getJSONArray("results");
                    // iterate through result set and create Movie objects
                    for (int i = 0; i < results.length(); i++) {
                        Movie movie = new Movie(results.getJSONObject(i));
                        movies.add(movie);
                        // notify the adapter that the dataset has changed when we add a movie to the list
                        // a row was added - specifically tell the adapter
                        adapter.notifyItemInserted(movies.size()-1);
                    }
                    Log.i(TAG, String.format("Loaded %s movies", results.length()));
                } catch (JSONException e) {
                    logError("Failed to parse the now playing movies", e, true);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                logError("Failed to get data from now playing endpoint", throwable, true);
            }
        });
    }


    //access the configuration endpoint - gets the configuration from the API
    // to make a connection and calls to the API and send get requests to the API for information to be sent back to the application
    public void getConfiguration() {
        //create the URL that will be accessed
        String url = API_BASE_URL + "/configuration";
        // set the request parameters - the parameter values that get appended to the URL
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.api_key));
        // execute a GET request using the client, expecting a JSON object response
        client.get(url, params, new JsonHttpResponseHandler() {
            // need to override methods in this JSON handler - customizing the builtin methods

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // getting the values from the JSON object response (image base URL)
                try {
                    config = new Config(response);
                    Log.i(TAG, String.format("Loaded configuration with imageBaseUrl %s and posterSize %s", config.getImageBaseUrl(), config.getPosterSize()));
                    // pass config to the adapter
                    adapter.setConfig(config);
                    // get the now playing movie list
                    getNowPlaying();
                } catch (JSONException e) {
                    logError("Failed parsing configuration", e, true);
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                // changed the original failure handler to the log defined below
                logError("Failed getting configuration", throwable, true);
            }
        });

    }

    // handle errors, log and alert the user to avoid silent errors (where the users are not notified of errors)
    private void logError(String message, Throwable error, boolean alertUser) {
        // always log the error
        Log.e(TAG, message, error);
        // alert the user to avoid silent errors
        if (alertUser) {
            // show a long toast with the error message
            // a toast is a low-impact way of displaying a message to the user within the app
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }
}
