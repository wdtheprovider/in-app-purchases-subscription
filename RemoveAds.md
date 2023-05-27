# Welcome to in-app-purchase - Remove Ads (Android Studio Java*)

Consumable Item In-App Purchases: https://github.com/wdtheprovider/in-app-purchase

In this repository i'm going to show you how to integrate In-App Purchases of Google Play Billing version 5+ in 8 steps. I follow the officailly google 
 docs, i'm not using any third-party library

<br>

[![Donate](https://img.shields.io/badge/Donate-PayPal-green.svg)](https://www.paypal.com/donate/?hosted_button_id=KPSJWR36UHBV2)

<br>

Pre-requisite
- Google Play Console Account
- Published App on Play Store
- Tester Device with GMS


Configure Your Testing device by adding the gmail account to internal testing testers 
and License testing (Watch the YouTube video for clarity: https://youtu.be/j6wWVMj-fi8 )

```
Setup the in-app purchase product in Google Play Console account
i have already created mine which are 
Product ID: remove_ads_id

```

The following methods (These are the methods you need for the IAP System to work, you can copy and paste)

```java
void establishConnection(){}
void showProducts(){}
void launchPurchaseFlow(){}
void handlePurchase(Purchase purchases){}
void restorePurchase(){}
```

[**Step 1: Add the Google Play Billing Library dependency**](#step-1-add-the-google-play-billing-library-dependency)

[**Step 2: Initialize a BillingClient with PurchasesUpdatedListener**](#step-2-initialize-a-billingclient-with-purchasesupdatedlistener)

[**Step 3: Establish a connection to Google Play**](#step-3-establish-a-connection-to-google-play)

[**Step 4: Show products available to buy**](#step-4-show-products-available-to-buy)

[**Step 5: Launch the purchase flow**](#step-5-launch-the-purchase-flow)

[**Step 6: Processing purchases / Verify Payment**](#step-6-processing-purchases--verify-payment)

[**Step 7: Handling pending transactions**](#step-7-handling-pending-transactions)

<br> Learn More: https://developer.android.com/google/play/billing/integrate

### Step 1: Add the Google Play Billing Library dependency<br>
```gradle
//Add the Google Play Billing Library dependency to your app's build.gradle file as shown:

dependencies {

    def billing_version = "6.0.0"
    implementation "com.android.billingclient:billing:$billing_version"
    implementation 'com.google.guava:listenablefuture:9999.0-empty-to-avoid-conflict-with-guava'
    implementation 'com.google.guava:guava:24.1-jre'
    
}
```

```xml
//And Open Manifest File and add this permission
<uses-permission android:name="com.android.vending.BILLING" />

```
### Step 2: Initialize a BillingClient with PurchasesUpdatedListener<br>

```java
  //Initialize a BillingClient with PurchasesUpdatedListener onCreate method

    billingClient = BillingClient.newBuilder(this)
                .enablePendingPurchases()
                .setListener(
                        (billingResult, list) -> {
                            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null) {
                                for (Purchase purchase : list) {
                                    handlePurchase(purchase);
                                }
                            }
                        }
                ).build();

 //start the connection after initializing the billing client
        establishConnection();
                
```
### Step 3: Establish a connection to Google Play<br>

```java
 

    void establishConnection() {

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    showProducts();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                establishConnection();
            }
        });
    }
    
```
### Step 4: Show products available to buy<br>

```java

  @SuppressLint("SetTextI18n")
    void showProducts() {

        ImmutableList<QueryProductDetailsParams.Product> productList = ImmutableList.of(
                //Product 1
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId("remove_ads_id")
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
        );

        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build();

        billingClient.queryProductDetailsAsync(
                params,
                (billingResult, prodDetailsList) -> {
                    // Process the result
                    productDetailsList.clear();
                    handler.postDelayed(() -> {
                        loadProducts.setVisibility(View.INVISIBLE);
                        productDetailsList.addAll(prodDetailsList);
                        Log.d(TAG, productDetailsList.size() + " number of products");
                        adapter = new RemoveAdsAdapter(getApplicationContext(), productDetailsList, (RecycleViewInterface) RemoveAdsActivity.this);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(RemoveAdsActivity.this, LinearLayoutManager.VERTICAL, false));
                        recyclerView.setAdapter(adapter);
                    }, 2000);

                }
        );

    }
    
```
### Step 5: Launch the purchase flow<br>

```java
   void launchPurchaseFlow(ProductDetails productDetails) {
        ImmutableList<BillingFlowParams.ProductDetailsParams> productDetailsParamsList =
                ImmutableList.of(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                                .setProductDetails(productDetails)
                                .build()
                );
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build();

        BillingResult billingResult = billingClient.launchBillingFlow(activity, billingFlowParams);
    }

```
### Step 6: Processing purchases / Verify Payment<br>

```java

 
    void handlePurchase(Purchase purchases) {

        if(!purchases.isAcknowledged()){
            billingClient.acknowledgePurchase(AcknowledgePurchaseParams
                    .newBuilder()
                    .setPurchaseToken(purchases.getPurchaseToken())
                    .build(), billingResult -> {

                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    //Setting setIsRemoveAd to true
                    // true - No ads
                    // false - showing ads.
                    prefs.setIsRemoveAd(true);
                  //  goBack();
                }
            });
            Log.d(TAG, "Purchase Token: " + purchases.getPurchaseToken());
            Log.d(TAG, "Purchase Time: " + purchases.getPurchaseTime());
            Log.d(TAG, "Purchase OrderID: " + purchases.getOrderId());
        }
    }

    
```

### Step 7: Handling pending transactions<br>

```java
  
 protected void onResume() {
        super.onResume();
        billingClient.queryPurchasesAsync(
                QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.INAPP).build(),
                (billingResult, list) -> {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        for (Purchase purchase : list) {
                            if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged()) {
                                handlePurchase(purchase);
                            }
                        }
                    }
                }
        );
    }

```


### Step 8: Restore Purchase<br>

```java
  
    void restorePurchases() {
        billingClient = BillingClient.newBuilder(this).enablePendingPurchases().setListener((billingResult, list) -> {
        }).build();
        final BillingClient finalBillingClient = billingClient;
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {
                establishConnection();
            }

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    finalBillingClient.queryPurchasesAsync(
                            QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.INAPP).build(), (billingResult1, list) -> {
                                if (billingResult1.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                                    if (list.size() > 0) {
                                        prefs.setIsRemoveAd(true); // set true to activate remove ad feature
                                        showSnackbar(btn_restore_fab, "Successfully restored");
                                    } else {
                                        Log.d(TAG, "Oops, No purchase found.");
                                        showSnackbar(btn_restore_fab, "Oops, No purchase found.");
                                        prefs.setIsRemoveAd(false); // set false to de-activate remove ad feature
                                    }
                                }
                            });
                }
            }
        });
    }

```

<br> 
Buy recyclerviewer adapter: https://dingi.icu/store/
<br>
