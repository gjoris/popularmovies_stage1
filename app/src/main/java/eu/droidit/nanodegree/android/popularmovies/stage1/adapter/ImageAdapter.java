package eu.droidit.nanodegree.android.popularmovies.stage1.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import eu.droidit.nanodegree.android.popularmovies.stage1.settings.API;
import eu.droidit.nanodegree.android.popularmovies.stage1.settings.APIType;
import eu.droidit.nanodegree.android.popularmovies.stage1.settings.ImageType;
import eu.droidit.nanodegree.android.popularmovies.stage1.storage.MovieStore;
import eu.droidit.nanodegree.android.popularmovies.stage1.storage.model.Movie;
import eu.droidit.nanodegree.android.popularmovies.stage1.utils.NetworkUtils;

import static java.util.Locale.ENGLISH;

public class ImageAdapter extends BaseAdapter {

    private final Context mContext;
    private Handler.Callback callbackBefore;
    private Handler.Callback callbackAfter;

    public ImageAdapter(Context mContext, APIType apiType) {
        this.mContext = mContext;
        reload(apiType);
    }

    public ImageAdapter(Context mContext, APIType apiType, Handler.Callback callbackBefore, Handler.Callback callbackAfter) {
        if (callbackBefore != null && callbackAfter == null)
            throw new IllegalStateException("Cannot have a callback before, and not a callback after");
        this.mContext = mContext;
        this.callbackBefore = callbackBefore;
        this.callbackAfter = callbackAfter;
        reload(apiType);
    }

    public void reload(APIType apiType) {
        if (callbackBefore != null) callbackBefore.handleMessage(null);
        clearCurrentCache();
        new MovieQueryTask().execute(API.buildMovieDbURL(apiType));
    }

    private void clearCurrentCache() {
        MovieStore.clear();
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return MovieStore.size();
    }

    @Override
    public Object getItem(int i) {
        return MovieStore.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ImageView imageView;
        if (view == null) {
            imageView = new ImageView(mContext);
        } else {
            imageView = (ImageView) view;
        }
        try {
            new GetCreatorForBitmapTask().execute(new BitmapParams(MovieStore.get(i).getMoviePosterLocation(), mContext, ImageType.THUMBNAIL)).get().into(imageView);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        if (i == MovieStore.size() - 1 && callbackAfter != null) callbackAfter.handleMessage(null);
        return imageView;
    }

    private void parseJson(String s) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(s);
            JSONArray results = jsonObject.getJSONArray("results");
            List<Movie> movies = new ArrayList<>();
            for (int i = 0; i < results.length() - 1; i++) {
                JSONObject result = results.getJSONObject(i);
                String posterPath = result.getString("poster_path");
                double voteAverage = result.getDouble("vote_average");
                Date releaseDate = new SimpleDateFormat("yyyy-MM-dd", ENGLISH).parse(result.getString("release_date"));
                String plotSynopsis = result.getString("overview");
                String title = result.getString("title");
                movies.add(new Movie(title, releaseDate, posterPath, voteAverage, plotSynopsis));
            }
            MovieStore.replace(movies);
        } catch (JSONException | ParseException e) {
            Log.e("ImageAdapter", "Error occurred while parsing the JSON", e);
            //Not sure if we should throw a RuntimeException here. In a production app, probably not.
            throw new RuntimeException(e);
        }
    }

    public static class GetCreatorForBitmapTask extends AsyncTask<BitmapParams, Void, RequestCreator> {
        BitmapParams params;

        @Override
        protected RequestCreator doInBackground(BitmapParams... bitmapParamses) {
            params = bitmapParamses[0];
            return Picasso.with(params.contextOfTheView).load(API.buildImageUrlAsString(params.pathToGetTheImageFrom, params.imageType));
        }

        @Override
        protected void onPostExecute(RequestCreator requestCreator) {
            if (params.callback != null) {
                requestCreator.fetch(new Callback() {
                    @Override
                    public void onSuccess() {
                        params.callback.handleMessage(null);
                    }

                    @Override
                    public void onError() {
                    }
                });
            }
            super.onPostExecute(requestCreator);
        }
    }

    public static class BitmapParams {
        private final String pathToGetTheImageFrom;
        private final Context contextOfTheView;
        private final ImageType imageType;
        private Handler.Callback callback;

        public BitmapParams(String pathToGetTheImageFrom, Context contextOfTheView, ImageType imageType) {
            this.pathToGetTheImageFrom = pathToGetTheImageFrom;
            this.contextOfTheView = contextOfTheView;
            this.imageType = imageType;
        }

        public BitmapParams(String pathToGetTheImageFrom, Context contextOfTheView, ImageType imageType, Handler.Callback callback) {
            this(pathToGetTheImageFrom, contextOfTheView, imageType);
            this.callback = callback;
        }

    }

    private class MovieQueryTask extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {
            URL url = urls[0];
            String responseFromHttpUrl = null;
            try {
                responseFromHttpUrl = NetworkUtils.getResponseFromHttpUrl(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responseFromHttpUrl;
        }

        @Override
        protected void onPostExecute(String s) {
            parseJson(s);
            notifyDataSetChanged();
        }
    }
}
