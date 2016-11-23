package com.lx.teameal;

import com.lx.teameal.auth.AuthManager;

import java.io.*;
import java.util.Properties;
import java.util.Scanner;

/**
 * Created by lx on 22/11/2016.
 * <p>
 * 茶饭的主类
 */
public class TeadFood {

    private static Scanner scanner = new Scanner(System.in);

    public String getConsumerKey() {
        return consumerKey;
    }

    public String getConsumerSecret() {
        return consumerSecret;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPass() {
        return userPass;
    }

    public int getTypeOauth() {
        return authType;
    }

    private int authType;
    private String consumerKey;
    private String consumerSecret;
    private String userName;
    private String userPass;

    private TeadFood(int type, String key, String secret, String name, String pass) {
        this.authType = type;
        this.consumerKey = key;
        this.consumerSecret = secret;
        this.userName = name;
        this.userPass = pass;
    }

    public static class Builder {
        private int authType;
        private String consumerKey;
        private String consumerSecret;
        private String userName;
        private String userPass;

        Builder() {
        }

        Builder type(int type) {
            this.authType = type;
            return Builder.this;
        }

        Builder key(String key) {
            this.consumerKey = key;
            return Builder.this;
        }

        Builder secret(String secret) {
            this.consumerSecret = secret;
            return Builder.this;
        }

        Builder userName(String name) {
            this.userName = name;
            return Builder.this;
        }

        Builder userPass(String pass) {
            this.userPass = pass;
            return Builder.this;
        }

        TeadFood build() {
            return new TeadFood(authType, consumerKey, consumerSecret, userName, userPass);
        }
    }

    public static void start() {
        TeadFood teadFood;
        TeadFood.Builder builder = new Builder();

        String[] consumerConfig = getConsumerConfig();
        builder.key(consumerConfig[0]).secret(consumerConfig[1]);

        int typeInt = getAuthType();
        builder.type(typeInt);

        if (typeInt == 2) {
            String[] account = getUserAccount();
            builder.userName(account[0]).userPass(account[1]);
        }

        teadFood = builder.build();
        AuthManager.getInstance().startAuth(teadFood);
    }

    private static int getAuthType() {
        System.out.println("Please choose the authorition type:\n1. OAuth; \n2. XAuth");
        int typeInt = scanner.nextInt();

        if (typeInt <= 0 || typeInt >= 3) typeInt = 1;

        return typeInt;
    }

    private static String[] getUserAccount() {
        System.out.println("\nXAuth may cause your account info leaked !!!!\n");
        System.out.println("Please input your username and password:");

        String userName = scanner.next();
        String passwd = scanner.next();
        Log.debug("username:" + userName + "/ passwd:" + passwd);

        return new String[]{userName, passwd};
    }

    private static String[] getConsumerConfig() {
        File file = new File("key_and_secret");
        String key = null;
        String secret = null;

        while (!file.exists()) {
            Log.info("file that saves consumer key and secret are not found, please input the right file path:");

            String filePath = scanner.next();
            file = new File(filePath);
        }

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));

            Properties properties = new Properties();
            properties.load(br);

            key = properties.getProperty("CONSUMER_KEY");
            secret = properties.getProperty("CONSUMER_SECRET");

            Log.debug("key:" + key + "/ secret:" + secret);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (key == null || key.isEmpty()) throw new IllegalArgumentException("Consumer Key is null");
        if (secret == null || secret.isEmpty()) throw new IllegalArgumentException("Consumer Secret is null");

        return new String[]{key, secret};
    }
}
