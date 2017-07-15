package eu.droidit.nanodegree.android.popularmovies.stage1.settings;

import eu.droidit.nanodegree.android.popularmovies.stage1.R;

public enum APIType {

    POPULAR("popular", "Popular"),
    TOP_RATED("top_rated", "Top Rated");

    private final String type;
    private final String description;

    APIType(String type, String description) {
        this.type = type;
        this.description = description;
    }

    public static APIType basedOnId(int lastSelectedItem) {
        if (lastSelectedItem == R.id.popular) {
            return POPULAR;
        } else if (lastSelectedItem == R.id.top_rated) {
            return TOP_RATED;
        }
        return TOP_RATED;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }
}
