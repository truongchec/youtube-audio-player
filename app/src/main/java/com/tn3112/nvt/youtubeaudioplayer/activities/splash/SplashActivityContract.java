package com.tn3112.nvt.youtubeaudioplayer.activities.splash;

import com.tn3112.nvt.youtubeaudioplayer.activities.BasePresenter;
import com.tn3112.nvt.youtubeaudioplayer.activities.BaseView;
import com.tn3112.nvt.youtubeaudioplayer.db.dto.YoutubeSongDto;

import java.util.LinkedList;
import java.util.Map;

public interface SplashActivityContract {

    interface View extends BaseView<SplashActivityContract.Presenter> {

        void onRecommendationsReady(Map<String, LinkedList<YoutubeSongDto>> recommendations);

        void invokeNoConnectionDialog();
    }

    interface Presenter extends BasePresenter {

        void loadYoutubeRecommendations();

        void loadRecents(Map<String, LinkedList<YoutubeSongDto>> recommendationsMap);
    }
}
