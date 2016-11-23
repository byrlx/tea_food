package com.lx.teameal.auth;

/**
 * Created by lx on 23/11/2016.
 */
public class AccessToken {
    private static AccessToken instance;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    private String token;
    private String secret;

    public static AccessToken get() {
        if (instance == null) {
            synchronized (AccessToken.class) {
                if (instance == null) {
                    instance = new AccessToken();
                }
            }
        }

        return instance;
    }

    @Override
    public String toString() {
        return "token=" + token + "; secret=" + secret;
    }
}
