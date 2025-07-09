package com.locked.app.models;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class PlaylistDetails {
    ArrayList<String> playlistNames;
    ArrayList<BufferedImage> playlistImages;
    ArrayList<String> playlistURIs;

    public PlaylistDetails(ArrayList<String> playlistNames, ArrayList<BufferedImage> playlistImages, ArrayList<String> playlistURIs) {
        this.playlistNames = playlistNames;
        this.playlistImages = playlistImages;
        this.playlistURIs = playlistURIs;
    }

    public ArrayList<BufferedImage> getPlaylistImages() {
        return playlistImages;
    }

    public ArrayList<String> getPlaylistNames() {
        return playlistNames;
    }

    public ArrayList<String> getPlaylistURIs() {
        return playlistURIs;
    }
}