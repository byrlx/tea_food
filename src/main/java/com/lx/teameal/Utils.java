package com.lx.teameal;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;

/**
 * Created by lx on 22/11/2016.
 */
public class Utils {
    public static String getNonce() {
        SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(32);
    }

    public static String joinMap(Map<String, String> map, String joiner) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append(joiner);
        }

        sb.deleteCharAt(sb.length() - 1);

        return sb.toString();
    }

    public static String signauture(String base, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            SecretKeySpec spec = new SecretKeySpec(secret.getBytes(), mac.getAlgorithm());
            mac.init(spec);

            byte[] digest = mac.doFinal(base.getBytes());
            return Base64.encodeBase64String(digest);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<String> sortMap(Map<String, String> origin) {
        List<String> keys = new LinkedList<>(origin.keySet());

        Collections.sort(keys);

        List<String> result = new LinkedList<>();
        for (String key : keys) {
            result.add(key + "=" + origin.get(key));
        }

        return result;
    }
}
