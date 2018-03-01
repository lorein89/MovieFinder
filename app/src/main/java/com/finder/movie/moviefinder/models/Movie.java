package com.finder.movie.moviefinder.models;

import java.io.Serializable;

/**
 * Created by Salaheddin on 2/27/2018.
 */

public class Movie implements Serializable{
    private String posterUrl;
    private String movieName;
    private double voteAverage;
    private int voteCount;
    private String overview;

    public Movie(){

    }

    public Movie(String posterUrl, String movieName, double voteAverage, int voteCount, String overview) {
        this.posterUrl = posterUrl;
        this.movieName = movieName;
        this.voteAverage = voteAverage;
        this.voteCount = voteCount;
        this.overview = overview;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public String getMovieName() {
        return movieName;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public String getOverview() {
        return overview;
    }
}
