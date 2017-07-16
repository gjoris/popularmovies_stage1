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

import butterknife.BindView;
import eu.droidit.nanodegree.android.popularmovies.stage1.adapter.ImageAdapter;
import eu.droidit.nanodegree.android.popularmovies.stage1.settings.ImageType;
import eu.droidit.nanodegree.android.popularmovies.stage1.storage.MovieStore;
import eu.droidit.nanodegree.android.popularmovies.stage1.storage.model.Movie;

public class MovieDetail extends AppCompatActivity {

    @BindView(R.id.picture)
    private ImageView picture;
    @BindView(R.id.synopsis)
    private TextView plotSynopsis;
    @BindView(R.id.title)
    private TextView title;
    @BindView(R.id.voteAverage)
    private TextView voteAverage;

    @BindView(R.id.detail_loading_indicator)
    private ProgressBar progressBar;
    @BindView(R.id.releaseDate)
    private TextView releaseDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

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
