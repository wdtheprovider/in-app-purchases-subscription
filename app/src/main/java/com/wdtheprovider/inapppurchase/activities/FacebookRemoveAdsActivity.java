package com.wdtheprovider.inapppurchase.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.collect.ImmutableList;
import com.wdtheprovider.inapppurchase.R;
import com.facebook.ads.*;
import com.wdtheprovider.inapppurchase.adapters.RemoveAdsAdapter;
import com.wdtheprovider.inapppurchase.utilies.Helper;
import com.wdtheprovider.inapppurchase.utilies.Prefs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FacebookRemoveAdsActivity extends AppCompatActivity {
    private AdView adView;
    CardView mainCard;
    BillingClient billingClient;
    Activity activity;
    List<ProductDetails> productDetailsList;
    Prefs prefs;
    Handler handler;
    ProgressBar loadingProducts;
    TextView product;
    ExtendedFloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_remove_ads);

        mainCard = findViewById(R.id.mainCard);
        fab = findViewById(R.id.fab);
        loadingProducts = findViewById(R.id.loadingProducts);
        product = findViewById(R.id.product);

        activity = this;
        productDetailsList = new ArrayList<>();
        prefs = new Prefs(this);
        handler = new Handler();

        if (!prefs.getBoolean("fb_remove_ads", false)) {
            fbBannerAd();
            getAAID();
        }

        fab.setOnClickListener(v -> {
            restorePurchases();
        });

        //Initialize a BillingClient with PurchasesUpdatedListener onCreate method
        billingClient = BillingClient.newBuilder(this)
                .enablePendingPurchases()
                .setListener(
                        (billingResult, list) -> {
                            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null) {
                                for (Purchase purchase : list) {
                                    verifyPurchase(purchase);
                                }
                            }
                        }
                ).build();

        //start the connection after initializing the billing client
        establishConnection();

        //Open PurchaseFlow
        mainCard.setOnClickListener(v -> {
            launchPurchaseFlow(productDetailsList.get(0));
        });
    }

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

    @SuppressLint("SetTextI18n")
    void showProducts() {
        ImmutableList<QueryProductDetailsParams.Product> productList = ImmutableList.of(
                //Product 1
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId("fb_remove_ads_lifetime")
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
                        productDetailsList.addAll(prodDetailsList);
                        if (!productDetailsList.isEmpty()) {
                            loadingProducts.setVisibility(View.INVISIBLE);
                            mainCard.setVisibility(View.VISIBLE);
                            //Set the product details on the screen.
                            String price = Objects.requireNonNull(productDetailsList.get(0).getOneTimePurchaseOfferDetails()).getFormattedPrice();
                            String productName = productDetailsList.get(0).getName();
                            if (prefs.getBoolean("fb_remove_ads", false)) {
                                product.setText("Product Purchased");
                            } else {
                                product.setText(price + " " + productName);
                            }
                        } else {
                            Toast.makeText(activity, "No products available", Toast.LENGTH_SHORT).show();
                        }
                    }, 2000);
                }
        );
    }

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

        billingClient.launchBillingFlow(activity, billingFlowParams);
    }

    void verifyPurchase(Purchase purchases) {
        if (!purchases.isAcknowledged()) {
            billingClient.acknowledgePurchase(AcknowledgePurchaseParams
                    .newBuilder()
                    .setPurchaseToken(purchases.getPurchaseToken())
                    .build(), billingResult -> {

                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    //Setting setBoolean to true
                    // key  - fb_remove_ads
                    // true - No ads
                    // false - showing ads.
                    prefs.setBoolean("fb_remove_ads", true);
                    reloadScreen();
                }
            });
        }
    }

    private void reloadScreen() {
        //Reload the screen to activate the removeAd and remove the actual Ad off the screen.
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }

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

    public void getAAID() {
        AsyncTask.execute(() -> {
            try {
                AdvertisingIdClient.Info adInfo = AdvertisingIdClient.getAdvertisingIdInfo(FacebookRemoveAdsActivity.this);
                String myId = adInfo.getId();

                Log.d("UIDMY", myId);
            } catch (Exception e) {
                Log.d("error", e.getMessage());
            }
        });
    }

    void fbBannerAd() {

        AudienceNetworkAds.initialize(FacebookRemoveAdsActivity.this);
        //Do this only if you are using google android emulators, real phones its not needed.
        //The hash ID, will come the first time you load the app and request the ad.
        AdSettings.setTestMode(true);
        AdSettings.addTestDevice("184d46d7-c142-4729-b40c-ed9b32509c47");
        AdSettings.turnOnSDKDebugger(this);

        adView = new AdView(this, "563838032361652_563841815694607", AdSize.BANNER_HEIGHT_50);
        LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);
        adContainer.addView(adView);
        adView.loadAd();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(activity, MainActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }


    void restorePurchases() {

        billingClient = BillingClient.newBuilder(this)
                .enablePendingPurchases()
                .setListener((billingResult, list) -> {
                }).build();

        final BillingClient finalBillingClient = billingClient;
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {
            }

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {

                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    finalBillingClient.queryPurchasesAsync(
                            QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build(), (billingResult1, list) -> {
                                if (billingResult1.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                                    if (list.size() > 0) {
                                        for (Purchase purchase : list) {
                                            if (purchase.getProducts().equals("fb_remove_ads_lifetime")) {
                                                Helper.showSnackBar(fab, "Successfully restored");
                                                prefs.setBoolean("fb_remove_ads", true);
                                            }
                                        }
                                    } else {
                                        Helper.showSnackBar(fab, "Oops, No purchase found");
                                        prefs.setBoolean("fb_remove_ads", false);
                                        // set 0 to de-activate premium feature
                                    }
                                }
                            });
                }
            }
        });
    }
}