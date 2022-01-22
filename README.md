# Welcome to in-app-purchase (Android Studio Java*)

In this repository i'm going to show you how to integrate In-App Purchase for Subscription of Google Play Billing version 4+ in 7 steps. I follow the officailly google 
 docs, i'm not using any third-party library

Demo <br>

<span> <img src="https://github.com/wdtheprovider/in-app-purchases-subscription/blob/master/app/src/main/res/drawable/s1.png" width="290" height="600">
 <span> <img src="https://github.com/wdtheprovider/in-app-purchases-subscription/blob/master/app/src/main/res/drawable/s2.png" width="290" height="600">
<span> <img src="https://github.com/wdtheprovider/in-app-purchases-subscription/blob/master/app/src/main/res/drawable/s3.png" width="290" height="600">
<span> <img src="https://github.com/wdtheprovider/in-app-purchases-subscription/blob/master/app/src/main/res/drawable/s4.png" width="290" height="600">
<span> <img src="https://github.com/wdtheprovider/in-app-purchases-subscription/blob/master/app/src/main/res/drawable/s5.png" width="290" height="600">
<span> <img src="https://github.com/wdtheprovider/in-app-purchases-subscription/blob/master/app/src/main/res/drawable/s6.png" width="290" height="600">

</span>

Pre-requisite
- Google Play Console Account
- Published App on Play Store
- Tester Device with GMS

YouTube Video: Part-1 | Intro Demo: Uploading soon <br>
<br>YouTube Video: Part-2 | Configure Testing Device: https://youtu.be/j6wWVMj-fi8 <br>
<br>YouTube Video: Part-3 | Integrating The Methods to purchase the products: Uploading soon <br>

```
Configure Your Testing device by adding the gmail account to internal testing testers 
and License testing (Watch the YouTube video for clarity: https://youtu.be/j6wWVMj-fi8 )


Setup the in-app purchase subscription product in Google Play Console account
i have already created mine which are 
Product ID: sub_premium

```

The following methods (These are the methods you need for the IAP System to work, you can copy and paste)

void establishConnection(){}<br>
void showProducts(){}<br>
void launchPurchaseFlow(){}<br>
void verifySubPayment(Purchase purchases){}<br>
void checkSubscription(){}<br>

Step 0: //Add the Google Play Billing Library dependency<br>
Step 1: //Initialize a BillingClient with PurchasesUpdatedListener<br>
Step 2: //Establish a connection to Google Play<br>
Step 3: //Show products available to buy<br>
Step 4: //Launch the purchase flow<br>
Step 5: //Processing purchases / Verify Payment<br>
Step 6: //Handling pending transactions<br>
Step 7: //Check the subscriptions on SplashScreenActivity<br>

<br> Learn More: https://developer.android.com/google/play/billing/integrate

Step 0: //Add the Google Play Billing Library dependency<br>
```
//Add the Google Play Billing Library dependency to your app's build.gradle file as shown:

dependencies {
    def billing_version = "4.0.0"

    implementation "com.android.billingclient:billing:$billing_version"
}

And Open Manifest File and add this permission
<uses-permission android:name="com.android.vending.BILLING" />

```
Step 1: //Initialize a BillingClient with PurchasesUpdatedListener<br>

```
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
Step 2: //Establish a connection to Google Play<br>

```
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
Step 3: //Show products available to buy<br>

```
   void showProducts() {
        List<String> skuList = new ArrayList<>();
        skuList.add("sub_premium");
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS);
        billingClient.querySkuDetailsAsync(params.build(),
                new SkuDetailsResponseListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSkuDetailsResponse(@NonNull BillingResult billingResult,
                                                     List<SkuDetails> skuDetailsList) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                            // Process the result.
                            for (SkuDetails skuDetails : skuDetailsList) {
                                if (skuDetails.getSku().equals("sub_premium")) {
                                    //Now update the UI
                                    txt_price.setText(skuDetails.getPrice() + " Per Month");
                                    txt_price.setOnClickListener(view -> {
                                        launchPurchaseFlow(skuDetails);
                                    });
                                }
                            }
                        }
                    }
                });
    }
    
```
Step 4: //Launch the purchase flow<br>

```
  void launchPurchaseFlow(SkuDetails skuDetails) {

        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(skuDetails)
                .build();

        billingClient.launchBillingFlow(YourAcivity.this, billingFlowParams);
    }
    
```
Step 5: //Processing purchases / Verify Payment<br>

```
 void verifySubPurchase(Purchase purchases) {

        AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams
                .newBuilder()
                .setPurchaseToken(purchases.getPurchaseToken())
                .build();

        billingClient.acknowledgePurchase(acknowledgePurchaseParams, new AcknowledgePurchaseResponseListener() {
            @Override
            public void onAcknowledgePurchaseResponse(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    //Toast.makeText(SubscriptionActivity.this, "Item Consumed", Toast.LENGTH_SHORT).show();
                    // Handle the success of the consume operation.
                    //user prefs to set premium
                    Toast.makeText(StoreActivity.this, "You are a premium user now", Toast.LENGTH_SHORT).show();
                    //updateUser();

                    //Setting premium to 1
                    // 1 - premium
                    //0 - no premium
                    prefs.setPremium(1);
                }
            }
        });

        Log.d(TAG, "Purchase Token: " + purchases.getPurchaseToken());
        Log.d(TAG, "Purchase Time: " + purchases.getPurchaseTime());
        Log.d(TAG, "Purchase OrderID: " + purchases.getOrderId());
    }
    
```

Step 6: //Handling pending transactions<br>

```
    protected void onResume() {
        super.onResume();

        billingClient.queryPurchasesAsync(
                BillingClient.SkuType.SUBS,
                new PurchasesResponseListener() {
                    @Override
                    public void onQueryPurchasesResponse(@NonNull BillingResult billingResult, @NonNull List<Purchase> list) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            for (Purchase purchase : list) {
                                if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged()) {
                                    verifySubPurchase(purchase);
                                }
                            }
                        }
                    }
                }
        );

    }
```


Step 7: //Check Subscription <br>

```
    void checkSubscription(){

        billingClient = BillingClient.newBuilder(this).enablePendingPurchases().setListener((billingResult, list) -> {}).build();

        final BillingClient finalBillingClient = billingClient;

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {

            }

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {

                if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
                    finalBillingClient.queryPurchasesAsync(BillingClient.SkuType.SUBS, (billingResult1, list) -> {
                        if (billingResult1.getResponseCode() ==BillingClient.BillingResponseCode.OK && list != null){
                            int i = 0;
                            for (Purchase purchase: list){
                                if (purchase.getSkus().get(i).equals("sub_premium")) {
                                    Log.d("SubTest", purchase + "");
                                    prefs.setPremium(1);
                                } else {
                                    prefs.setPremium(0);
                                }
                                i++;
                            }
                        }
                    });

                }

            }
        });
    }
```




        

