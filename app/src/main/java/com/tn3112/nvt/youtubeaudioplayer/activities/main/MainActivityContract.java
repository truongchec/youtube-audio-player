package com.tn3112.nvt.youtubeaudioplayer.activities.main;

import com.tn3112.nvt.youtubeaudioplayer.activities.BasePresenter;
import com.tn3112.nvt.youtubeaudioplayer.activities.BaseView;
import com.tn3112.nvt.youtubeaudioplayer.data.PlaylistWithSongs;
import com.tn3112.nvt.youtubeaudioplayer.data.models.SearchSuggestionsResponse;
import com.tn3112.nvt.youtubeaudioplayer.db.dto.YoutubeSongDto;

import java.util.List;

import io.reactivex.Observable;


public interface MainActivityContract {

    interface View extends BaseView<Presenter> {

        void showToast(String message, int length);

        void showToast(int resId, int length);

        void showLoadingIndicator(boolean show);

        void showRecommendations();

        void showSearchResults(List<YoutubeSongDto> data);

        void showPlaylistEditingFragment(YoutubeSongDto songDto);

        void initPlayerSlider(YoutubeSongDto data);

        void setPlayerPlayingState(boolean isPlaying);
    }

    interface Presenter extends BasePresenter {

        void startExoPlayerService();

        void prepareAudioStreamAndPlay(YoutubeSongDto model);

        void playPreparedStream(YoutubeSongDto data);

        boolean makeYoutubeSearch(String searchQuery);

        Observable<SearchSuggestionsResponse> getSearchSuggestions(String query);

        void preparePlaybackQueueAndPlay(PlaylistWithSongs playlistWithSongs, int position);

        void playPlaylistItem(int position);

        void playAgain();

        void playNextPlaylistItem();

        void playPreviousPlaylistItem();

        void playRandom();

        void addToPlaylist(YoutubeSongDto videoDataDto);

    }
}
