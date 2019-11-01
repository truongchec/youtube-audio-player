package com.tn3112.nvt.youtubeaudioplayer.activities.splash;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.tn3112.nvt.youtubeaudioplayer.R;
import com.tn3112.nvt.youtubeaudioplayer.activities.main.MainActivity;
import com.tn3112.nvt.youtubeaudioplayer.data.liveData.RecommendationsViewModel;
import com.tn3112.nvt.youtubeaudioplayer.db.dto.YoutubeSongDto;
import com.tn3112.nvt.youtubeaudioplayer.utilities.YoutubeRecommendationsFetchUtils;
import com.tn3112.nvt.youtubeaudioplayer.utilities.common.CommonUtils;
import com.tn3112.nvt.youtubeaudioplayer.utilities.common.Constants;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static com.tn3112.nvt.youtubeaudioplayer.utilities.common.Constants.APP_PREFERENCES;

public class SplashActivity extends AppCompatActivity implements SplashActivityContract.View {

    private SplashActivityContract.Presenter presenter;
    private CommonUtils utils;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.layout_splash);
        utils = new CommonUtils(this);
        sharedPreferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        presenter = new SplashActivityPresenterImpl(this, utils);
    }

    @Override
    protected void onPostResume() {
        boolean noRecommendations = sharedPreferences.getBoolean(Constants.PREFERENCE_NO_RECOMMENDATIONS, false);
        if (noRecommendations) {
            presenter.loadRecents(new HashMap<>());
        } else {
            presenter.loadYoutubeRecommendations();
        }
        super.onPostResume();
    }

    @Override
    public void setPresenter(SplashActivityContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onRecommendationsReady(Map<String, LinkedList<YoutubeSongDto>> recommendations) {
        RecommendationsViewModel.getInstance().getData().setValue(recommendations);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void invokeNoConnectionDialog() {
        DialogInterface.OnClickListener positiveCallback = (dialog, which) -> {
            if (utils.isNetworkAvailable()) {
                new YoutubeRecommendationsFetchUtils(utils, presenter).fetchYoutubeRecommendations();
            } else {
                invokeNoConnectionDialog();
            }
        };
        DialogInterface.OnClickListener negativeCallback = (dialog, which) -> finish();
        utils.createAlertDialog(R.string.no_connection_error_message_title, R.string.network_error_message,
                false, R.string.try_again_message, positiveCallback, R.string.button_cancel, negativeCallback).show();
    }
}
