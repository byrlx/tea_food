package com.lx.teameal.auth;

/**
 * Created by lx on 22/11/2016.
 */
public class AuthConfig {
    public static final String CONSUMER_KEY = "oauth_consumer_key";
    public static final String NONCE = "oauth_nonce";
    public static final String VERSION = "oauth_version";
    public static final String TOKEN = "oauth_token";
    public static final String SIGNATURE = "oauth_signature";
    public static final String SIGNATURE_METHOD = "oauth_signature_method";
    public static final String TIMESTAMP = "oauth_timestamp";

    public static final String X_MODE = "x_auth_mode";
    public static final String X_PASSWD = "x_auth_password";
    public static final String X_NAME = "x_auth_username";

    public static final String X_MODE_CLIENT = "client_auth";
    public static final String HMAC_SHA1 = "HMAC-SHA1";

    public static final String REQUEST_TOKEN_URL = "http://www.fanfou.com/oauth/request_token";
    public static final String ACCESS_TOKEN_URL = "http://www.fanfou.com/oauth/access_token";
    public static final String AUTHORIZE_URL = "http://www.fanfou.com/oauth/authorize";
}
