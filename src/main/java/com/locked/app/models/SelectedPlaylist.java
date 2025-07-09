package com.locked.app.models;

import java.awt.image.BufferedImage;

public class SelectedPlaylist {
    private final String name;
    private final BufferedImage image;
    private final String uri;

    public SelectedPlaylist(String name, BufferedImage image, String uri) {
        this.name = name;
        this.image = image;
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public BufferedImage getImage() {
        return image;
    }

    public String getUri() {
        return uri;
    }
}
