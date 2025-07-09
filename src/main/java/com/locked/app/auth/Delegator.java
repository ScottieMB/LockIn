package com.locked.app.auth;

import java.awt.image.BufferedImage;
import java.util.Arrays;

import com.locked.app.db.DBConnection;

public class Delegator {
    private final UserRepository userCreds;

    public Delegator(DBConnection dbconn) {
        this.userCreds = new UserRepository(dbconn);
    }

    public boolean register(String username, char[] password) {
        if (userCreds.userExists(username))
        {
            return false;
        }
        String hash = Auth.hashPassword(password);
        userCreds.storeCredentials(username, hash);
        Arrays.fill(password, '\0');
        return true;
    }

    // Dictates whether the user can sign in or not by comparing the hash
    public boolean login(String username, char[] inputPassword) {
        String storedHash = userCreds.grabStoredHash(username);
        return storedHash != null && Auth.verifyPassword(inputPassword, storedHash);
    }

    // Fetch the profile picture from the user
    public BufferedImage getUserProfilePicture(String username) {
        return userCreds.loadProfilePicture(username);
    }
}