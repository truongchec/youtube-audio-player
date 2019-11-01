package com.tn3112.nvt.youtubeaudioplayer.data.dataSource;

import com.tn3112.nvt.youtubeaudioplayer.App;
import com.tn3112.nvt.youtubeaudioplayer.db.AppDatabase;
import com.tn3112.nvt.youtubeaudioplayer.db.dao.PlaylistDao;
import com.tn3112.nvt.youtubeaudioplayer.db.dao.PlaylistSongsDao;
import com.tn3112.nvt.youtubeaudioplayer.db.dao.YoutubeSongDao;

public class LocalDataSource {

    private LocalDataSource localDataSource;
    private PlaylistDao playlistDao;
    private PlaylistSongsDao playlistSongsDao;
    private YoutubeSongDao youtubeSongDao;

    private LocalDataSource() {
        AppDatabase appDatabase = App.getInstance().getDatabase();
        playlistDao = appDatabase.playlistDao();
        playlistSongsDao = appDatabase.playlistSongsDao();
        youtubeSongDao = appDatabase.youtubeSongDao();
    }


}
