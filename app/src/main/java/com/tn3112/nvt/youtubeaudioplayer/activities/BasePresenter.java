package com.tn3112.nvt.youtubeaudioplayer.activities;

import com.tn3112.nvt.youtubeaudioplayer.custom.exceptions.UserFriendly;

public interface BasePresenter {

    <T extends UserFriendly> void handleException(T exception);

    void handleException(Exception exception);
}
