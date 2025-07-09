package com.locked.app.services;

import io.github.cdimascio.dotenv.Dotenv;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.specification.Image;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.pkce.AuthorizationCodePKCERequest;
import se.michaelthelin.spotify.requests.data.player.StartResumeUsersPlaybackRequest;
import se.michaelthelin.spotify.requests.data.playlists.GetListOfCurrentUsersPlaylistsRequest;

import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.*;
import java.util.ArrayList;
import java.util.Base64;

import javax.imageio.ImageIO;

import com.locked.app.models.PlaylistDetails;

public class SpotifyService implements MusicService {
    private static final Dotenv dotenv = Dotenv.load();
    private static final String CLIENT_ID = dotenv.get("SPOTIFY_CLIENT_ID", System.getenv("SPOTIFY_CLIENT_ID"));
    private static final String STATE = dotenv.get("SPOTIFY_STATE", System.getenv("SPOTIFY_STATE"));
    private static final URI REDIRECT_URI = SpotifyHttpManager.makeUri("http://127.0.0.1:8080/callback");

    private SpotifyApi spotifyApi;
    private String codeVerifier;

    public SpotifyService() {
        this.spotifyApi = new SpotifyApi.Builder()
                .setClientId(CLIENT_ID)
                .setRedirectUri(REDIRECT_URI)
                .build();
    }

    // PKCE style
    private String generateCodeVerifier() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] codeVerifierBytes = new byte[64];
        secureRandom.nextBytes(codeVerifierBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(codeVerifierBytes);
    }

    private String generateCodeChallenge(String codeVerifier) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        byte[] bytes = codeVerifier.getBytes("US-ASCII");
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(bytes, 0, bytes.length);
        byte[] digest = messageDigest.digest();
        return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
    }

    @Override
    public boolean authenticate() throws Exception {
        this.codeVerifier = generateCodeVerifier();
        String codeChallenge = generateCodeChallenge(codeVerifier);

        // creates url for pkce flow
        AuthorizationCodeUriRequest uriRequest = spotifyApi.authorizationCodePKCEUri(codeChallenge)
                .state(STATE)
                .scope("playlist-read-private playlist-read-collaborative user-modify-playback-state user-read-playback-state")
                .show_dialog(true)
                .build();

        // open user's default browser
        URI uri = uriRequest.execute();
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(uri);
        }

        // start small server to get user authentication response
        String code = Server.createServer();
        if (code == null) {
            return false;
        }

        // after authorized, request a token and set them in the spotifyApi object
        try {
            AuthorizationCodePKCERequest tokenRequest = spotifyApi.authorizationCodePKCE(code, codeVerifier).build();
            AuthorizationCodeCredentials credentials = tokenRequest.execute();
            spotifyApi.setAccessToken(credentials.getAccessToken());
            spotifyApi.setRefreshToken(credentials.getRefreshToken());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public PlaylistDetails fetchUserPlaylists() throws Exception {
        if (spotifyApi.getAccessToken() == null) throw new IllegalStateException("Spotify not authenticated");

        // ugly, but it's just third party library stuff
        GetListOfCurrentUsersPlaylistsRequest request = spotifyApi.getListOfCurrentUsersPlaylists().limit(10).build();
        Paging<PlaylistSimplified> paging = request.execute();

        ArrayList<String> playlistNames = new ArrayList<>();
        ArrayList<BufferedImage> playlistImages = new ArrayList<>();
        ArrayList<String> playlistURIs = new ArrayList<>();

        // add everything to their respective arrays
        for (PlaylistSimplified playlistDetails : paging.getItems()) {
            playlistNames.add(playlistDetails.getName());
            playlistURIs.add(playlistDetails.getUri());
        
            Image[] images = playlistDetails.getImages();
            if (images.length > 0) {
                String imageUrl = images[0].getUrl();
                BufferedImage img = ImageIO.read(URI.create(imageUrl).toURL());
                playlistImages.add(img);
            } else {
                playlistImages.add(null);
            }
        }
        return new PlaylistDetails(playlistNames, playlistImages, playlistURIs);
    }    
    
    @Override
    public void playPlaylist(String uri) throws Exception {
        if (spotifyApi.getAccessToken() == null) throw new IllegalStateException("Spotify not authenticated");

        StartResumeUsersPlaybackRequest playRequest = spotifyApi
                .startResumeUsersPlayback()
                .context_uri(uri)
                .build();

        playRequest.execute();
    }
}