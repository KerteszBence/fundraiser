package org.fundraiser.config;

public enum CampaignCategory {
    ARTIST("Artist"),
    COSPLAYERS("Cosplayers"),
    MUSIC("Music"),
    WRITERS("Writers"),
    PODCASTERS("Podcasters"),
    DEVELOPERS("Developers"),
    STREAMERS("Streamers"),
    EDUCATION("Education"),
    LOCAL_BUSINESSES("Local Businesses"),
    VIDEO_CREATORS("Video Creator");
    private final String displayName;

    CampaignCategory(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
