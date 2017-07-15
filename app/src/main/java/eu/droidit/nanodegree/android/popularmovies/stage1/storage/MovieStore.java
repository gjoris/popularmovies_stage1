package eu.droidit.nanodegree.android.popularmovies.stage1.storage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import eu.droidit.nanodegree.android.popularmovies.stage1.storage.model.Movie;

/**
 * This class acts as the current storage of all movies, so we don't have to call the API again and again.
 * <p>
 * It shares its data between different activities.
 * <p>
 * This is actually for 'educational' use only, I agree that probably there are better strategies to employ.
 *
 * @author Geroen Joris
 */

public final class MovieStore {

    private static final Object MUTEX = new Object();
    private static MovieStore instance;
    //This is actually not really needed, since concurrency is probably not an issue for us, but considering we are writing to this list using
    //AsyncTasks, I still want to make really really sure ... Old (web) habits die hard ...
    private final List<Movie> list = Collections.synchronizedList(new ArrayList<Movie>());

    private MovieStore() {
    }

    private static MovieStore getInstance() {
        synchronized (MUTEX) {
            if (instance == null) {
                instance = new MovieStore();
            }
            return instance;
        }
    }

    public static void clear() {
        getInstance().list.clear();
    }

    public static void replace(List<Movie> newMovies) {
        clear();
        getInstance().list.addAll(newMovies);
    }

    public static Movie get(int index) {
        return getInstance().list.get(index);
    }

    public static Iterator<Movie> listIterator() {
        return getInstance().list.listIterator();
    }

    public static int size() {
        return getInstance().list.size();
    }
}
