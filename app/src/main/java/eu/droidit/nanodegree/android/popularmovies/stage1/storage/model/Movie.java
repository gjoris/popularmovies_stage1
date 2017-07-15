package eu.droidit.nanodegree.android.popularmovies.stage1.storage.model;

import java.util.Date;

/**
 * Created by geroen on 13/07/2017.
 */

public class Movie {

    private final String title;
    //Date ... you never know when you have to compare
    private final Date releaseDate;
    private final String moviePosterLocation;
    private final double voteAverage;
    private final String plotSynopsis;

    public Movie(String title, Date releaseDate, String moviePosterLocation, double voteAverage, String plotSynopsis) {
        this.title = title;
        this.releaseDate = releaseDate;
        this.moviePosterLocation = moviePosterLocation;
        this.voteAverage = voteAverage;
        this.plotSynopsis = plotSynopsis;
    }

    public String getTitle() {
        return title;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public String getMoviePosterLocation() {
        return moviePosterLocation;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public String getPlotSynopsis() {
        return plotSynopsis;
    }
}
