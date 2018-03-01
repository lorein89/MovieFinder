package com.finder.movie.moviefinder;

import com.finder.movie.moviefinder.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JsonParser {

    public static ArrayList<Movie> jsonToMovieList(JSONArray array) {
        ArrayList<Movie> movies = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            Movie movie = new Movie();
            try {
                movie = jsonToMovie(array.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            movies.add(movie);
        }
        return movies;
    }

    public static Movie jsonToMovie(JSONObject object) {
        String posterUrl = "";
        String movieName = "";
        double voteAverage = 0;
        int voteCount = 0;
        String overview = "";

        try {
            posterUrl = object.getString("poster_path");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            movieName = object.getString("original_title");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            voteAverage = object.getDouble("vote_average");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            voteCount = object.getInt("vote_count");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            overview = object.getString("overview");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new Movie(posterUrl, movieName, voteAverage, voteCount, overview);
    }
}
