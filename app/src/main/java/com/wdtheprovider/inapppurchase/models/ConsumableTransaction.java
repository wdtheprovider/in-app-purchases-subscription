package com.wdtheprovider.inapppurchase.models;

/**
 * File Created by Dingaan Letjane
 * 2023/05/11
 **/

public class ConsumableTransaction {

    private String uid;
    private String orderNumber;
    private String productId;
    private String purchaseToken;
    private String orderDate;
    private String purchasedTime;
    private double price;
    private String item;

    public ConsumableTransaction() {
    }

    public ConsumableTransaction(String uid, String orderNumber, String productId, String purchaseToken, String orderDate, String purchasedTime, double price, String item, int reward, int qty) {
        this.uid = uid;
        this.orderNumber = orderNumber;
        this.productId = productId;
        this.purchaseToken = purchaseToken;
        this.orderDate = orderDate;
        this.purchasedTime = purchasedTime;
        this.price = price;
        this.item = item;
        this.reward = reward;
        this.qty = qty;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    private int reward;
    private int qty;

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getPurchasedTime() {
        return purchasedTime;
    }

    public void setPurchasedTime(String purchasedTime) {
        this.purchasedTime = purchasedTime;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getPurchaseToken() {
        return purchaseToken;
    }

    public void setPurchaseToken(String purchaseToken) {
        this.purchaseToken = purchaseToken;
    }

    public int getReward() {
        return reward;
    }

    public void setReward(int reward) {
        this.reward = reward;
    }
}
