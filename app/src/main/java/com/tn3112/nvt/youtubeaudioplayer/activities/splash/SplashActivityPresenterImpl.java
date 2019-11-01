package com.tn3112.nvt.youtubeaudioplayer.activities.splash;

import android.content.DialogInterface;

import com.tn3112.nvt.youtubeaudioplayer.R;
import com.tn3112.nvt.youtubeaudioplayer.custom.exceptions.UserFriendly;
import com.tn3112.nvt.youtubeaudioplayer.db.dto.YoutubeSongDto;
import com.tn3112.nvt.youtubeaudioplayer.tasks.LoadRecentsAsyncTask;
import com.tn3112.nvt.youtubeaudioplayer.utilities.YoutubeRecommendationsFetchUtils;
import com.tn3112.nvt.youtubeaudioplayer.utilities.common.CommonUtils;

import java.util.LinkedList;
import java.util.Map;

public class SplashActivityPresenterImpl implements SplashActivityContract.Presenter {

    private CommonUtils utils;
    private SplashActivityContract.View view;

    public SplashActivityPresenterImpl(SplashActivityContract.View view, CommonUtils commonUtils) {
        view.setPresenter(this);
        this.view = view;
        utils = commonUtils;
    }

    @Override
    public <T extends UserFriendly> void handleException(T exception) {
        DialogInterface.OnClickListener listener = (dialog, which) -> dialog.dismiss();
        utils.createAlertDialog(R.string.error, exception.getUserErrorMessage(), true,
                R.string.button_ok, listener, 0, null);
    }

    //TODO: Implement it
    @Override
    public void handleException(Exception exception) {

    }

    @Override
    public void loadYoutubeRecommendations() {
        if (utils.isNetworkAvailable()) {
            new YoutubeRecommendationsFetchUtils(utils, this).fetchYoutubeRecommendations();
        } else {
            view.invokeNoConnectionDialog();
        }
    }

    @Override
    public void loadRecents(Map<String, LinkedList<YoutubeSongDto>> recommendationsMap) {
        new LoadRecentsAsyncTask(view).execute(recommendationsMap);
    }
}
