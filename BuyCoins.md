# Welcome to in-app-purchase - Buy coins (Android Studio Java*)

Consumable Item In-App Purchases: https://github.com/wdtheprovider/in-app-purchase


In this repository i'm going to show you how to integrate In-App Purchases of Google Play Billing version 4+ in 7 steps. I follow the officailly google 
 docs, i'm not using any third-party library

<br>

[![Donate](https://img.shields.io/badge/Donate-PayPal-green.svg)](https://www.paypal.com/donate/?hosted_button_id=KPSJWR36UHBV2)

<br>

Pre-requisite
- Google Play Console Account
- Published App on Play Store
- Tester Device with GMS

YouTube Video: Part-1 | Intro Demo: [https://youtu.be/nQrsVB7quKw ](https://www.youtube.com/watch?v=Ym1olBce2MI)<br>
<br>YouTube Video: Part-2 | Configure Testing Device: https://youtu.be/j6wWVMj-fi8 <br>
<br>YouTube Video: Part-3 | Integrating The Methods to purchase the products: Uploading soon <br>

```
Configure Your Testing device by adding the gmail account to internal testing testers 
and License testing (Watch the YouTube video for clarity: https://youtu.be/j6wWVMj-fi8 )


Setup the in-app purchase subscription product in Google Play Console account
i have already created mine which are 
Product IDs:
  
        productIds = new ArrayList<>();
        coins = new ArrayList<>();

        productIds.add("10_coins_id ");
        coins.add(10);

        productIds.add("20_coins_id ");
        coins.add(2);

        productIds.add("50_coins_id ");
        coins.add(0);


```

The following methods (These are the methods you need for the IAP System to work, you can copy and paste)

```java
void establishConnection(){}
void showProducts(){}
void launchPurchaseFlow(){}
void verifySubPayment(Purchase purchases){}
void checkSubscription(){}
void giveUserCoins (){}
```

[**Step 1: Add the Google Play Billing Library dependency**](#step-1-add-the-google-play-billing-library-dependency)

[**Step 2: Initialize a BillingClient with PurchasesUpdatedListener**](#step-2-initialize-a-billingclient-with-purchasesupdatedlistener)

[**Step 3: Establish a connection to Google Play**](#step-3-establish-a-connection-to-google-play)

[**Step 4: Show products available to buy**](#step-4-show-products-available-to-buy)

[**Step 5: Launch the purchase flow**](#step-5-launch-the-purchase-flow)

[**Step 6: Processing purchases / Verify Payment**](#step-6-processing-purchases--verify-payment)

[**Step 7: Handling pending transactions**](#step-7-handling-pending-transactions)

[**Step 8: Give user coins **](#Give-user-coins)

<br> Learn More: https://developer.android.com/google/play/billing/integrate

### Step 1: Add the Google Play Billing Library dependency<br>
```gradle
//Add the Google Play Billing Library dependency to your app's build.gradle file as shown:

dependencies {

    def billing_version = "5.0.0"
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
                        new PurchasesUpdatedListener() {
                            @Override
                            public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
                               if(billingResult.getResponseCode()==BillingClient.BillingResponseCode.OK && list !=null) {
                                   for (Purchase purchase: list){
                                       verifySubPurchase(purchase);
                                   }
                               }
                            }
                        }
                ).build();

        //start the connection after initializing the billing client
        establishConnection();
                
```
### Step 3: Establish a connection to Google Play<br>

```java
 
    void connectGooglePlayBilling() {
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {
                connectGooglePlayBilling();
            }

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    showProducts();
                }
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
                        .setProductId("coins_id")
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
        );

        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build();

        billingClient.queryProductDetailsAsync(params, (billingResult, list) -> {
            productDetailsList.clear();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "posted delayed");
                    loadProducts.setVisibility(View.INVISIBLE); //
                    productDetailsList.addAll(list);
                    Log.d(TAG, productDetailsList.size() + " number of products");
                    adapter = new BuyCoinsAdapter(getApplicationContext(), productDetailsList, BuyCoinActivity.this);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(BuyCoinActivity.this, LinearLayoutManager.VERTICAL, false));
                    recyclerView.setAdapter(adapter);
                }
            }, 2000);
        });
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
  void verifyPurchase(Purchase purchase) {
        ConsumeParams consumeParams = ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.getPurchaseToken())
                .build();
        ConsumeResponseListener listener = (billingResult, s) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                giveUserCoins(purchase);
            }
        };

        billingClient.consumeAsync(consumeParams, listener);


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
                                verifyPurchase(purchase);
                            }
                        }
                    }
                }
        );

    }

    
```


### Step 8: Give user coins <br>

```java

  
    @SuppressLint("SetTextI18n")
    void giveUserCoins(Purchase purchase) {

        Log.d("TestINAPP", purchase.getProducts().get(0));
        Log.d("TestINAPP", purchase.getQuantity() + " Quantity");

        for(int i=0;i<productIds.size();i++){
            if(purchase.getProducts().get(0).equals(productIds.get(i))){
                Log.d(TAG,"Balance "+prefs.getInt("coins",0)+ " Coins");
                Log.d(TAG,"Allocating "+coins.get(i) + " Coins");

                //set coins
                prefs.setInt("coins",coins.get(i) + prefs.getInt("coins",0));

                Log.d(TAG,"New Balance "+prefs.getInt("coins",0)+ " Coins");

                //Update UI
                txt_coins.setText(prefs.getInt("coins",0)+"");
            }
        }
    }
 
```

<br> 
Buy recyclerviewer adapter: https://dingi.icu/store/
<br>
