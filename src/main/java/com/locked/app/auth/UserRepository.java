package com.locked.app.auth;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.imageio.ImageIO;

import com.locked.app.db.DBConnection;

class UserRepository {

    private final DBConnection dbconn;

    public UserRepository(DBConnection dbconn) {
        this.dbconn = dbconn;
    }

    void storeCredentials(String username, String hashedPass) {
        String sqlQuery = "INSERT INTO user (username, password_hash) VALUES (?, ?)";

        try (Connection connection = dbconn.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(sqlQuery)) {

            pstmt.setString(1, username);
            pstmt.setString(2, hashedPass);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    BufferedImage loadProfilePicture(String username) {
        String sqlQuery = "SELECT pfp FROM user WHERE username = ?";

        try (Connection conn = dbconn.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sqlQuery)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                InputStream pfpStream = rs.getBinaryStream("pfp");
                if (pfpStream != null) {
                    return ImageIO.read(pfpStream); // Load actual PFP from DB
                }
            }

            // If no image in DB, load the default profile picture
            InputStream defaultStream = UserRepository.class.getResourceAsStream("/defaultpfp.jpg");
            if (defaultStream != null) {
                return ImageIO.read(defaultStream);
            } else {
                System.out.println("Cannot find default image!");
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    String grabStoredHash(String username) {
        String storedHash = null;
        String sqlQuery = "SELECT password_hash FROM user WHERE username = ?";

        try (Connection connection = dbconn.getConnection();
        PreparedStatement pstmt = connection.prepareStatement(sqlQuery)) {

            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    storedHash = rs.getString("password_hash");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return storedHash;
    }

    // Check if hashed password matches with input password
    boolean passMatch(String user, char[] inputPass)
    {
        String storedHash = grabStoredHash(user);
        return storedHash != null && Auth.verifyPassword(inputPass, storedHash);
    }

    boolean userExists(String user)
    {
        String sqlQuery = "SELECT username FROM User WHERE username = ?";
        try (Connection connection = dbconn.getConnection();
        PreparedStatement pstmt = connection.prepareStatement(sqlQuery)) {
            pstmt.setString(1, user);

            try(ResultSet rs = pstmt.executeQuery()) {
                if (rs.next())
                {
                    return true;
                }
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }
}