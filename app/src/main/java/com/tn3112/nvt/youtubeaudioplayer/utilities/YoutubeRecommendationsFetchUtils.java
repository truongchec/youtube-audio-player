package com.tn3112.nvt.youtubeaudioplayer.utilities;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;

import com.tn3112.nvt.youtubeaudioplayer.App;
import com.tn3112.nvt.youtubeaudioplayer.activities.splash.SplashActivityContract;
import com.tn3112.nvt.youtubeaudioplayer.custom.exceptions.VideoIsDeletedException;
import com.tn3112.nvt.youtubeaudioplayer.data.dataSource.RemoteDataSource;
import com.tn3112.nvt.youtubeaudioplayer.data.models.youtube.videos.list.Snippet;
import com.tn3112.nvt.youtubeaudioplayer.data.models.youtube.videos.list.Statistics;
import com.tn3112.nvt.youtubeaudioplayer.data.models.youtube.videos.list.VideoDataItem;
import com.tn3112.nvt.youtubeaudioplayer.data.models.youtube.videos.list.YoutubeVideoListResponse;
import com.tn3112.nvt.youtubeaudioplayer.db.AppDatabase;
import com.tn3112.nvt.youtubeaudioplayer.db.dto.YoutubeSongDto;
import com.tn3112.nvt.youtubeaudioplayer.utilities.common.CommonUtils;
import com.tn3112.nvt.youtubeaudioplayer.utilities.common.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class YoutubeRecommendationsFetchUtils {

    private final static String DELETED_VIDEO = "Deleted video";
    private final String TAG = getClass().getSimpleName();
    private CommonUtils utils;
    private SplashActivityContract.Presenter presenter;
    private RemoteDataSource remoteDataSource;
    private AppDatabase db;

    public YoutubeRecommendationsFetchUtils(CommonUtils commonUtils, SplashActivityContract.Presenter presenter) {
        this.utils = commonUtils;
        this.presenter = presenter;
        remoteDataSource = RemoteDataSource.getInstance();
        db = App.getInstance().getDatabase();
    }

    @SuppressLint("CheckResult")
    public void fetchYoutubeRecommendations() {
        Maybe<YoutubeVideoListResponse> topTracks =
                remoteDataSource.getTopTracksPlaylist().subscribeOn(Schedulers.newThread());
        Maybe<YoutubeVideoListResponse> mostViewed =
                remoteDataSource.getMostViewedPlaylist().subscribeOn(Schedulers.newThread());
        Maybe<YoutubeVideoListResponse> newMusicThisWeek =
                remoteDataSource.getNewMusicThisWeekPlaylist().subscribeOn(Schedulers.newThread());

        Maybe.zip(topTracks, mostViewed, newMusicThisWeek, (topTracksResponse, mostViewedResponse, newMusicResponse) -> {
            HashMap<String, List<String>> rawRecommendations = new HashMap<>();
            List<YoutubeVideoListResponse> responseBodyList = Arrays.asList(topTracksResponse, mostViewedResponse, newMusicResponse);
            for (int i = 0; i < responseBodyList.size(); i++) {
                ArrayList<String> videoIds = new ArrayList<>();
                for (VideoDataItem item : responseBodyList.get(i).getItems()) {
                    videoIds.add(item.getSnippet().getResourceId().getVideoId());
                }
                rawRecommendations.put(Constants.RECOMMENDATIONS_HEADERS_ARR[i], videoIds);
            }
            return rawRecommendations;
        }).map(rawRecommendations -> {
            HashMap<String, LinkedList<YoutubeSongDto>> tempMap = new HashMap<>();
            for (int i = 0; i < rawRecommendations.size(); i++) {
                List<String> idsToFetch = new ArrayList<>();
                LinkedList<YoutubeSongDto> youtubeSongs = new LinkedList<>();
                List<String> videoIds = rawRecommendations.get(Constants.RECOMMENDATIONS_HEADERS_ARR[i]);
                for (String videoId : videoIds) {
                    YoutubeSongDto songDto = db.youtubeSongDao().getByVideoId(videoId);
                    if (songDto == null) {
                        idsToFetch.add(videoId);
                    } else {
                        youtubeSongs.add(songDto);
                    }
                }
                if (idsToFetch.size() > 0) {
                    Response<YoutubeVideoListResponse> response = remoteDataSource.getBasicVideoInfo(TextUtils.join(",", videoIds));
                    if (response != null && response.isSuccessful()) {
                        List<VideoDataItem> responseItemsList = response.body().getItems();
                        for (VideoDataItem videoDataItem : responseItemsList) {
                            Snippet snippet = videoDataItem.getSnippet();
                            if (!snippet.getTitle().equals(DELETED_VIDEO)) {
                                String videoId = videoDataItem.getId();
                                Statistics stats = videoDataItem.getStatistics();
                                String duration = utils.parseISO8601time(videoDataItem.getContentDetails().getDuration());
                                String viewCount = utils.formatYtViewsAndLikesString(stats.getViewCount());
                                String likeCount = utils.formatYtViewsAndLikesString(stats.getLikeCount());
                                String dislikeCount = utils.formatYtViewsAndLikesString(stats.getDislikeCount());
                                YoutubeSongDto songDto = new YoutubeSongDto(videoId, snippet.getTitle(), snippet.getChannelTitle(),
                                        duration, 0, snippet.getThumbnails().getMedium().getUrl(), viewCount,
                                        likeCount, dislikeCount);
                                youtubeSongs.add(songDto);
                                db.youtubeSongDao().insert(songDto);
                            } else {
                                throw new VideoIsDeletedException(
                                        String.format("Video with id %s is deleted, " +
                                                        "audio stream is unavailable. Video data: %s",
                                                videoDataItem.getId(), videoDataItem));
                            }
                        }
                    }
                }
                tempMap.put(Constants.RECOMMENDATIONS_HEADERS_ARR[i], youtubeSongs);
            }
            return tempMap;
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(recommendations -> presenter.loadRecents(recommendations),
                        error -> {
                            presenter.loadRecents(new HashMap<>());
                            error.printStackTrace();
                            Log.e(TAG, error.getMessage());
                        });
    }
}
