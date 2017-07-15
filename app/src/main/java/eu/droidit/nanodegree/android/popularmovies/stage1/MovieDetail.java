package eu.droidit.nanodegree.android.popularmovies.stage1;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import eu.droidit.nanodegree.android.popularmovies.stage1.adapter.ImageAdapter;
import eu.droidit.nanodegree.android.popularmovies.stage1.settings.ImageType;
import eu.droidit.nanodegree.android.popularmovies.stage1.storage.MovieStore;
import eu.droidit.nanodegree.android.popularmovies.stage1.storage.model.Movie;

public class MovieDetail extends AppCompatActivity {

    ImageView picture;
    TextView plotSynopsis;
    TextView title;
    TextView voteAverage;

    private ProgressBar progressBar;
    private TextView releaseDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        picture = (ImageView) findViewById(R.id.picture);
        plotSynopsis = (TextView) findViewById(R.id.synopsis);
        title = (TextView) findViewById(R.id.title);
        voteAverage = (TextView) findViewById(R.id.voteAverage);
        releaseDate = (TextView) findViewById(R.id.releaseDate);
        progressBar = (ProgressBar) findViewById(R.id.detail_loading_indicator);

        final Movie movie = MovieStore.get(getIntent().getIntExtra(Intent.EXTRA_TEXT, 0));

        //We only want to set the title, the rest has to be set when we are done loading (offers better user experience imho)
        setTitle(movie.getTitle());

        progressBar.setVisibility(View.VISIBLE);

        Handler.Callback callback = new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                progressBar.setVisibility(View.INVISIBLE);
                title.setText(movie.getTitle());
                plotSynopsis.setText(movie.getPlotSynopsis());
                voteAverage.setText("Vote Average: ".concat(String.valueOf(movie.getVoteAverage())));
                releaseDate.setText("Release Date: ".concat(new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(movie.getReleaseDate())));
                return true;
            }
        };

        try {
            new ImageAdapter.GetCreatorForBitmapTask().execute(new ImageAdapter.BitmapParams(movie.getMoviePosterLocation(), this, ImageType.EXTRA_LARGE, callback)).get().into(picture);
        } catch (InterruptedException | ExecutionException e) {
            Log.e("MovieDetail", "Error setting the image to the picture imageview", e);
            throw new RuntimeException(e);
        }
    }
}
