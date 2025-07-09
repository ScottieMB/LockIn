package com.locked.app.services;

import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

// Creates a small server that listens for Spotify's OAuth redirect
public class Server {
    public static String createServer() throws Exception {
        final String[] codeHolder = new String[1];
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Listen on the callback pathway
        server.createContext("/callback", exchange -> {
            String returnedURI = exchange.getRequestURI().getQuery();
            // Extract the code
            if (returnedURI != null && returnedURI.contains("code=")) {
                String code = returnedURI.split("code=")[1].split("&")[0];
                codeHolder[0] = code;
                
                String response = "Authorization complete. You may now close this window.";
                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
            server.stop(1);
        });
        server.start();
        
        // Short polling just to keep the thread free
        while (codeHolder[0] == null) {
            Thread.sleep(100);
        }

        return codeHolder[0];
    }
}
