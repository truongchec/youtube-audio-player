package com.tn3112.nvt.youtubeaudioplayer.data.models.youtubeSearch;

import com.google.gson.annotations.SerializedName;

public class Localized {

    @SerializedName("description")
    private String description;

    @SerializedName("title")
    private String title;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return
                "Localized{" +
                        "description = '" + description + '\'' +
                        ",title = '" + title + '\'' +
                        "}";
    }
}