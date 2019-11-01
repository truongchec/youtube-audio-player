package com.tn3112.nvt.youtubeaudioplayer.data.dataSource;

import com.tn3112.nvt.youtubeaudioplayer.data.models.SearchSuggestionsResponse;
import com.tn3112.nvt.youtubeaudioplayer.data.models.youtube.videos.list.YoutubeVideoListResponse;
import com.tn3112.nvt.youtubeaudioplayer.data.models.youtubeSearch.YoutubeApiSearchResponse;
import com.tn3112.nvt.youtubeaudioplayer.network.SearchSuggestionsNetwork;
import com.tn3112.nvt.youtubeaudioplayer.network.YoutubeApiNetwork;
import com.tn3112.nvt.youtubeaudioplayer.utilities.common.Constants;

import java.io.IOException;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class RemoteDataSource {

    private static RemoteDataSource instance;
    private YoutubeApiNetwork youtubeApiNetwork;
    private SearchSuggestionsNetwork searchSuggestionsNetwork;

    private RemoteDataSource() {
        youtubeApiNetwork = YoutubeApiNetwork.getInstance();
        searchSuggestionsNetwork = SearchSuggestionsNetwork.getInstance();
    }

    public static synchronized RemoteDataSource getInstance() {
        if (instance == null) {
            instance = new RemoteDataSource();
            return instance;
        } else return instance;
    }

    public Response<YoutubeApiSearchResponse> searchForVideo(String videoId) throws IOException {
        return youtubeApiNetwork.getSearchResults(videoId);
    }

    public Observable<YoutubeApiSearchResponse> searchForVideoRx(String videoId) {
        return youtubeApiNetwork.getSearchResultsRx(videoId);
    }

    public Response<YoutubeVideoListResponse> getVideoInfo(String key, String part, String videoId) throws IOException {
        return youtubeApiNetwork.getVideoInfo(key, part, videoId);
    }

    public Observable<YoutubeVideoListResponse> getVideoInfoRx(String key, String part, String videoId, String maxResults) {
        return youtubeApiNetwork.getVideoInfoRx(key, part, videoId, maxResults);
    }

    public Response<YoutubeApiSearchResponse> getVideoInfo(String key, String part, String chart, String videoCategoryId, Integer maxResults) throws IOException {
        return youtubeApiNetwork.getVideoInfo(key, part, chart, videoCategoryId, maxResults);
    }

    public Response<YoutubeApiSearchResponse> getVideoInfo(String key, String part, String chart, String videoCategoryId, String countryCode, Integer maxResults) throws IOException {
        return youtubeApiNetwork.getVideoInfo(key, part, chart, videoCategoryId, countryCode, maxResults);
    }

    public Maybe<YoutubeVideoListResponse> getPlaylistRx(String apiKey, String part, String playlistId, String maxResults) {
        return youtubeApiNetwork.getPlaylistRx(apiKey, part, playlistId, maxResults);
    }

    public Response<ResponseBody> getSuggestions(String query) throws IOException {
        return searchSuggestionsNetwork.getSuggestions(Constants.QUERY_SUGGESTIONS_OUTPUT, Constants.QUERY_SUGGESTIONS_DS, query);
    }

    public Observable<SearchSuggestionsResponse> getSuggestionsRx(String query) {
        return searchSuggestionsNetwork.getSuggestionsRx(Constants.QUERY_SUGGESTIONS_OUTPUT, Constants.QUERY_SUGGESTIONS_DS, query);
    }

    public Maybe<YoutubeVideoListResponse> getTopTracksPlaylist() {
        return getPlaylistRx(Constants.PLAYLIST_TOP_TRACKS_CHANNEL_ID);
    }

    public Maybe<YoutubeVideoListResponse> getMostViewedPlaylist() {
        return getPlaylistRx(Constants.PLAYLIST_MOST_VIEWED_CHANNEL_ID);
    }

    public Maybe<YoutubeVideoListResponse> getNewMusicThisWeekPlaylist() {
        return getPlaylistRx(Constants.PLAYLIST_NEW_MUSIC_THIS_WEEK_CHANNEL_ID);
    }

    public Response<YoutubeVideoListResponse> getBasicVideoInfo(String videoId) throws IOException {
        return youtubeApiNetwork.getVideoInfo(Constants.YOUTUBE_API_KEY,
                String.format("%s,%s,%s", Constants.QUERY_PART_SNIPPET, Constants.QUERY_PART_CONTENT_DETAILS, Constants.QUERY_PART_STATISTICS), videoId);
    }

    public Observable<YoutubeVideoListResponse> getBasicVideoInfoRx(String videoId) {
        return youtubeApiNetwork.getVideoInfoRx(Constants.YOUTUBE_API_KEY,
                String.format("%s,%s,%s", Constants.QUERY_PART_SNIPPET, Constants.QUERY_PART_CONTENT_DETAILS, Constants.QUERY_PART_STATISTICS), videoId, Constants.QUERY_PLAYLIST_MAX_RESULTS);
    }

    private Maybe<YoutubeVideoListResponse> getPlaylistRx(String channelId) {
        return youtubeApiNetwork.getPlaylistRx(Constants.YOUTUBE_API_KEY, Constants.QUERY_PART_SNIPPET, channelId, Constants.QUERY_PLAYLIST_MAX_RESULTS);
    }


}
