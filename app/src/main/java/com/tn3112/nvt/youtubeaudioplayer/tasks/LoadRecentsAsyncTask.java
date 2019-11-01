package com.tn3112.nvt.youtubeaudioplayer.tasks;

import android.os.AsyncTask;

import com.tn3112.nvt.youtubeaudioplayer.App;
import com.tn3112.nvt.youtubeaudioplayer.activities.splash.SplashActivityContract;
import com.tn3112.nvt.youtubeaudioplayer.db.dao.YoutubeSongDao;
import com.tn3112.nvt.youtubeaudioplayer.db.dto.YoutubeSongDto;
import com.tn3112.nvt.youtubeaudioplayer.utilities.common.Constants;

import java.util.LinkedList;
import java.util.Map;

public class LoadRecentsAsyncTask extends AsyncTask<Map<String, LinkedList<YoutubeSongDto>>, Void, Map<String, LinkedList<YoutubeSongDto>>> {

    private SplashActivityContract.View view;

    public LoadRecentsAsyncTask(SplashActivityContract.View view) {
        this.view = view;
    }

    @SafeVarargs
    @Override
    protected final Map<String, LinkedList<YoutubeSongDto>> doInBackground(Map<String, LinkedList<YoutubeSongDto>>... maps) {
        Map<String, LinkedList<YoutubeSongDto>> recommendationsMap = maps[0];
        YoutubeSongDao youtubeSongDao = App.getInstance().getDatabase().youtubeSongDao();
        LinkedList<YoutubeSongDto> youtubeSongsFromDb =
                new LinkedList<>(youtubeSongDao.getLastPlayed(20));
        if (youtubeSongsFromDb.size() > 0) {
            recommendationsMap.put(Constants.RECOMMENDATIONS_RECENT, youtubeSongsFromDb);
        }
        return recommendationsMap;
    }

    @Override
    protected void onPostExecute(Map<String, LinkedList<YoutubeSongDto>> recommendationsMap) {
        view.onRecommendationsReady(recommendationsMap);
        super.onPostExecute(recommendationsMap);
    }
}
