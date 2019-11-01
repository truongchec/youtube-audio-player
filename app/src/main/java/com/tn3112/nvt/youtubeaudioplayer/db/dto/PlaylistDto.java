package com.tn3112.nvt.youtubeaudioplayer.db.dto;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "playlist_data")
public class PlaylistDto implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true)
    private long playlistId;
    private String playlistName;

    public PlaylistDto(long playlistId) {
        this.playlistId = playlistId;
    }

    @Ignore
    public PlaylistDto(String playlistName) {
        this.playlistName = playlistName;
    }

    public long getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(long playlistId) {
        this.playlistId = playlistId;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }
}
