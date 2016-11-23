package com.lx.teameal.auth;

import com.google.gson.Gson;
import com.lx.teameal.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.lx.teameal.auth.AuthConfig.*;

/**
 * Created by lx on 22/11/2016.
 */
public class AuthManager {
    private static final String TOKEN_MATCH = "oauth_token=(\\S+)&oauth_token_secret=(\\S+)";
    private static final int TYPE_OAUTH = 1;
    private static final int TYPE_XAUTH = 2;

    public static AuthManager instance;

    private TeadFood teadFood;
    private AccessToken accessToken;

    public static AuthManager getInstance() {
        if (instance == null) {
            synchronized (AuthManager.class) {
                if (instance == null)
                    instance = new AuthManager();
            }
        }

        return instance;
    }

    private AuthManager() {
        accessToken = AccessToken.get();
    }

    public String getOAuthToken(){
        return accessToken.getToken();
    }
    public String getSignSecret() {
        return teadFood.getConsumerSecret() + "&" + accessToken.getSecret();
    }

    public String getConsumerKey() {
        return teadFood.getConsumerKey();
    }
    public void startAuth(TeadFood teadFood) {
        this.teadFood = teadFood;

        switch (teadFood.getTypeOauth()) {
            case TYPE_XAUTH:
                startXAuth();
                break;
            case TYPE_OAUTH:
            default:
                startOAuth();
                break;
        }
    }

    private HashMap<String, String> initHeader() {
        HashMap<String, String> result = new HashMap<String, String>();
        result.put(CONSUMER_KEY, teadFood.getConsumerKey());
        result.put(NONCE, Utils.getNonce());
        result.put(SIGNATURE_METHOD, HMAC_SHA1);
        result.put(TIMESTAMP, String.valueOf(System.currentTimeMillis()));
        result.put(VERSION, "1.0");

        return result;
    }

    private void startXAuth() {
        Pattern pattern = Pattern.compile(TOKEN_MATCH);
        HashMap<String, String> headerParams = initHeader();
        headerParams.put(X_MODE, X_MODE_CLIENT);
        headerParams.put(X_NAME, teadFood.getUserName());
        headerParams.put(X_PASSWD, teadFood.getUserPass());

        String baseStr = ApiManager.createBaseString(headerParams, "GET", AuthConfig.ACCESS_TOKEN_URL);
        Log.debug(baseStr);

        String sigStr = Utils.signauture(baseStr, teadFood.getConsumerSecret());
        Log.debug(sigStr);

        headerParams.put(SIGNATURE, sigStr);

        try {
            URL url = new URL(AuthConfig.ACCESS_TOKEN_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            String oauth = "OAuth " + Utils.joinMap(headerParams, ",");
            connection.setRequestProperty("Authorization", oauth);

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String s;
            while ((s = br.readLine()) != null) {
                Log.debug(s);
                Matcher matcher = pattern.matcher(s);
                while (matcher.find()) {
                    Log.debug("find!!!");
                    accessToken.setToken(matcher.group(1));
                    accessToken.setSecret(matcher.group(2));
                }
            }

            Log.debug(AccessToken.get().toString());
            test();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void test(){
        String usrInfoStr = ApiManager.getUserInfo();
        Gson gson = new Gson();
        User user = gson.fromJson(usrInfoStr, User.class);
        Log.debug(user.toString());
    }

    private void startOAuth() {
    }

}
