package com.lx.teameal;

import com.lx.teameal.auth.AuthConfig;
import com.lx.teameal.auth.AuthManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

import static com.lx.teameal.auth.AuthConfig.*;

/**
 * Created by lx on 23/11/2016.
 */
public class ApiManager {
    private static final String POST = "POST";
    private static final String GET = "GET";
    private static String signSecret;
    private static String consumerKey;
    private static String authToken;

    private static String getConsumerKey() {
        if (consumerKey == null) {
            consumerKey = AuthManager.getInstance().getConsumerKey();
        }
        return consumerKey;
    }

    private static String getSignSecret() {
        if (signSecret == null) {
            signSecret = AuthManager.getInstance().getSignSecret();
        }
        return signSecret;
    }

    private static String getOAuthToken() {
        if (authToken == null) {
            authToken = AuthManager.getInstance().getOAuthToken();
        }

        return authToken;
    }

    public static String createBaseString(Map<String, String> params,
                                          String method, String url) {

        StringBuilder result = new StringBuilder();
        result.append(method).append("&");

        List<String> sortedMap = Utils.sortMap(params);

        try {
            result.append(URLEncoder.encode(url, "utf-8")).append('&');
            result.append(URLEncoder.encode(String.join("&", sortedMap), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Log.debug("baseString :" + result);
        return result.toString();
    }

    public static String getAuthString(String method, String url) {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put(CONSUMER_KEY, getConsumerKey());
        headerMap.put(NONCE, Utils.getNonce());
        headerMap.put(SIGNATURE_METHOD, HMAC_SHA1);
        headerMap.put(TIMESTAMP, String.valueOf(System.currentTimeMillis()));
        headerMap.put(TOKEN, getOAuthToken());
        headerMap.put(VERSION, "1.0");

        String baseStr = createBaseString(headerMap, method, url);
        String signature = Utils.signauture(baseStr, getSignSecret());

        headerMap.put(SIGNATURE, signature);
        return "OAuth " + Utils.joinMap(headerMap, ",");
    }

    public static String simpleRequest(String method, String addr) {
        StringBuilder result = new StringBuilder();
        try {
            URL url = new URL(addr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setRequestProperty("Authorization", getAuthString(method, addr));

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String s;
            while ((s = br.readLine()) != null) {
                result.append(s);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return result.toString();
        }
    }

    public static String getUserInfo() {
        return simpleRequest(GET, "http://api.fanfou.com/account/verify_credentials.json");
    }
}
