package com.wdtheprovider.inapppurchase.models;

/**
 * File Created by Dingaan Letjane
 * 2023/05/06
 **/

public class User {

    String id;
    int coins;
    boolean subscribed;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public boolean isSubscribed() {
        return subscribed;
    }

    public void setSubscribed(boolean subscribed) {
        this.subscribed = subscribed;
    }
}
