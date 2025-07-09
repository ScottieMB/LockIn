package com.locked.app.services;

import com.locked.app.models.PlaylistDetails;

public interface MusicService {
    boolean authenticate() throws Exception;
    PlaylistDetails fetchUserPlaylists() throws Exception;
    void playPlaylist(String uri) throws Exception;
}
