package eu.droidit.nanodegree.android.popularmovies.stage1.settings;

public enum ImageType {
    LARGE("w342"),
    EXTRA_LARGE("w500"),
    THUMBNAIL("w185");

    private final String definition;

    ImageType(String definition) {
        this.definition = definition;
    }

    public String getDefinition() {
        return definition;
    }
}
