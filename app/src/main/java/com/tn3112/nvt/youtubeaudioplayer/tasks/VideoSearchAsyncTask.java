package com.tn3112.nvt.youtubeaudioplayer.tasks;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.tn3112.nvt.youtubeaudioplayer.activities.main.MainActivityContract;
import com.tn3112.nvt.youtubeaudioplayer.custom.AsyncTaskResult;
import com.tn3112.nvt.youtubeaudioplayer.data.dataSource.RemoteDataSource;
import com.tn3112.nvt.youtubeaudioplayer.data.models.youtube.videos.list.ContentDetails;
import com.tn3112.nvt.youtubeaudioplayer.data.models.youtube.videos.list.Snippet;
import com.tn3112.nvt.youtubeaudioplayer.data.models.youtube.videos.list.Statistics;
import com.tn3112.nvt.youtubeaudioplayer.data.models.youtube.videos.list.VideoDataItem;
import com.tn3112.nvt.youtubeaudioplayer.data.models.youtube.videos.list.YoutubeVideoListResponse;
import com.tn3112.nvt.youtubeaudioplayer.data.models.youtubeSearch.SnippetItem;
import com.tn3112.nvt.youtubeaudioplayer.data.models.youtubeSearch.YoutubeApiSearchResponse;
import com.tn3112.nvt.youtubeaudioplayer.db.dto.YoutubeSongDto;
import com.tn3112.nvt.youtubeaudioplayer.utilities.common.CommonUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class VideoSearchAsyncTask extends AsyncTask<String, Void, AsyncTaskResult<List<YoutubeSongDto>>> {

    private WeakReference<MainActivityContract.Presenter> presenter;
    private WeakReference<MainActivityContract.View> view;
    private CommonUtils commonUtils;
    private RemoteDataSource remoteDataSource;

    public VideoSearchAsyncTask(MainActivityContract.Presenter presenter, WeakReference<MainActivityContract.View> view, CommonUtils commonUtils) {
        this.presenter = new WeakReference<>(presenter);
        this.view = view;
        this.commonUtils = commonUtils;
        remoteDataSource = RemoteDataSource.getInstance();
    }

    @Override
    protected AsyncTaskResult<List<YoutubeSongDto>> doInBackground(String... strings) {
        AsyncTaskResult<List<YoutubeSongDto>> taskResult;
        YoutubeApiSearchResponse searchResponse;
        try {
            Response<YoutubeApiSearchResponse> rawResponse = remoteDataSource.searchForVideo(strings[0]);
            if (rawResponse != null && rawResponse.isSuccessful()) {
                searchResponse = rawResponse.body();
                ArrayList<String> idsList = new ArrayList<>();
                for (SnippetItem item : searchResponse.getItems()) {
                    idsList.add(item.getId().getVideoId());
                }
                List<YoutubeSongDto> ytVideoData = new ArrayList<>();
                YoutubeVideoListResponse videosListResponse;
                videosListResponse = remoteDataSource.getBasicVideoInfo(TextUtils.join(",", idsList)).body();

                for (VideoDataItem item : videosListResponse.getItems()) {
                    Snippet snippet = item.getSnippet();
                    ContentDetails contentDetails = item.getContentDetails();
                    Statistics stats = item.getStatistics();
                    String duration = commonUtils.parseISO8601time(contentDetails.getDuration());
                    String viewCount = commonUtils.formatYtViewsAndLikesString(stats.getViewCount());
                    String likeCount = commonUtils.formatYtViewsAndLikesString(stats.getLikeCount());
                    String dislikeCount = commonUtils.formatYtViewsAndLikesString(stats.getDislikeCount());
                    ytVideoData.add(new YoutubeSongDto(item.getId(), snippet.getTitle(),
                            snippet.getChannelTitle(), duration, 0, snippet.getThumbnails().getHigh().getUrl(),
                            viewCount, likeCount, dislikeCount));
                }
                taskResult = new AsyncTaskResult<>(ytVideoData);
            } else {
                throw new Exception(rawResponse.errorBody().string());
            }
        } catch (Exception e) {
            taskResult = new AsyncTaskResult<>(e);
        }
        return taskResult;

    }

    @Override
    protected void onPostExecute(AsyncTaskResult<List<YoutubeSongDto>> taskResult) {
        Exception exception = taskResult.getError();
        if (exception != null) {
            presenter.get().handleException(exception);
        } else view.get().showSearchResults(taskResult.getResult());
    }
}
