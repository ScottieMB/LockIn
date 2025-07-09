package com.locked.app.services;

import com.locked.app.models.PlaylistDetails;
import com.locked.app.models.SelectedPlaylist;

public class AppSessionManager {
    private MusicService musicService;
    private boolean isSignedIn;
    private PlaylistDetails playlistView; 
    private SelectedPlaylist selectedPlaylist;
    private int studyDurationMinutes;

    public AppSessionManager() {
        
    }
    
    public MusicService getMusicService() {
        return this.musicService;
    }

    public void setMusicService(MusicService musicService) {
        this.musicService = musicService;
    }

    public void setIsSignedIn(boolean authenticated) {
        this.isSignedIn = authenticated;
    }
    
    public void setPlaylistView(PlaylistDetails playlists) {
        this.playlistView = playlists;
    }

    public void setSelectedPlaylist(SelectedPlaylist selected) {
        this.selectedPlaylist = selected;
    }

    public void setDuration(int studyDurationMinutes) {
        this.studyDurationMinutes = studyDurationMinutes;
    }

    public boolean getIsSignedIn() {
        return isSignedIn;
    }

    public PlaylistDetails getPlaylistView() {
        return playlistView;
    }

    public SelectedPlaylist getSelectedPlaylist() {
        return selectedPlaylist;
    }

    public int getDuration() {
        return studyDurationMinutes;
    }
}
