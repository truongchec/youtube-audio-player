package com.tn3112.nvt.youtubeaudioplayer.custom.exceptions;

public class VideoIsDeletedException extends Exception {

    public VideoIsDeletedException(String message, Throwable t) {
        super(message, t);
    }

    public VideoIsDeletedException(String message) {
        super(message);
    }
}
