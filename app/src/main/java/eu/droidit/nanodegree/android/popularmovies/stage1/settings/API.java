package eu.droidit.nanodegree.android.popularmovies.stage1.settings;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import eu.droidit.nanodegree.android.popularmovies.stage1.R;

public class API {

    private static String KEY;

    public static void readKey(Context context) {
        try {
            KEY = IOUtils.toString(context.getResources().openRawResource(R.raw.moviedb), "UTF-8").trim();
        } catch (IOException e) {
            Log.e("API", "Could not set MovieDB key!");
            throw new RuntimeException(e);
        }
    }

    public static URL buildMovieDbURL(APIType type) {
        Uri uri = new Uri.Builder()
                .scheme("http")
                .authority("api.themoviedb.org")
                .path("3")
                .appendPath("movie")
                .appendPath(type.getType())
                .appendQueryParameter("api_key", KEY)
                .build();
        try {
            return new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static URL buildImageUrl(String imageKey, ImageType imageType) {
        if (nullOrEmpty(imageKey)) return null;
        String parsedImageKey = imageKey.startsWith("/") ? imageKey : "/".concat(imageKey);
        Uri uri = new Uri.Builder()
                .scheme("http")
                .authority("image.tmdb.org")
                .path("t")
                .appendPath("p")
                .appendPath(imageType.getDefinition())
                .build();
        String spec = uri.toString().concat("/").concat(parsedImageKey);
        try {
            return new URL(spec);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String buildImageUrlAsString(String imageKey, ImageType imageType) {
        URL url = buildImageUrl(imageKey, imageType);
        return url == null ? null : url.toString();
    }

    private static boolean nullOrEmpty(String imageKey) {
        return imageKey == null || imageKey.isEmpty();
    }

}
