package com.locked.app.auth;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

class Auth 
{ 
    private Auth() {
        
    }

    private static final Argon2 argon2 = Argon2Factory.create();

    static String hashPassword(char[] password) {
        return argon2.hash(3, 65536, 1, password);
    }
    
    static boolean verifyPassword(char[] password, String storedHash) {
        return argon2.verify(storedHash, password);
    }
}
