package com.locked.app.models;

import java.awt.image.BufferedImage;

public class UserSession {
    private final String username;
    private final BufferedImage pfp;

    public UserSession(String username, BufferedImage pfp) {
        this.username = username;
        this.pfp = pfp;
    }

    public String getUsername() {
        return username;
    }

    public BufferedImage getPfp() {
        return pfp;
    }
}
