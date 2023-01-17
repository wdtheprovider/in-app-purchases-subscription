package com.wdtheprovider.inapppurchase.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.collect.ImmutableList;
import com.wdtheprovider.inapppurchase.R;
import com.wdtheprovider.inapppurchase.adapters.RemoveAdsAdapter;
import com.wdtheprovider.inapppurchase.interfaces.RecycleViewInterface;
import com.wdtheprovider.inapppurchase.utilies.Prefs;

import java.util.ArrayList;
import java.util.List;

public class RemoveAdsActivity extends AppCompatActivity implements RecycleViewInterface {

    Activity activity;
    Prefs prefs;
    private BillingClient billingClient;
    List<ProductDetails> productDetailsList;
    ProgressBar loadProducts;
    RecyclerView recyclerView;
    Toolbar toolbar;
    Handler handler;
    ExtendedFloatingActionButton btn_restore_fab;
    RemoveAdsAdapter adapter;
    AdView mAdView;
    AdRequest adRequest;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_ads);

        initViews();

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

        //restore purchases
        btn_restore_fab.setOnClickListener(v -> {
            restorePurchases();
        });

        //Checks if the user has a removeAd if not then show ads.
        //Checks if the user has a premium/subscription if not then show ads.
        if (prefs.getPremium() == 0) {
            if (!prefs.isRemoveAd()) {
                MobileAds.initialize(this, initializationStatus -> {
                });
                adRequest = new AdRequest.Builder().build();
                loadBannerAd();
                Log.d("RemoveAds", "Remove ads off");
            } else {
                Log.d("RemoveAds", "Remove ads On");
                mAdView.setVisibility(View.GONE);
            }
        }

    }

    void loadBannerAd() {
        mAdView.loadAd(adRequest);
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
                        .setProductId("test_remove_ads1")
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
                        adapter = new RemoveAdsAdapter(getApplicationContext(), productDetailsList, RemoveAdsActivity.this);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(RemoveAdsActivity.this, LinearLayoutManager.VERTICAL, false));
                        recyclerView.setAdapter(adapter);
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

    void handlePurchase(Purchase purchases) {
        if (!purchases.isAcknowledged()) {
            billingClient.acknowledgePurchase(AcknowledgePurchaseParams
                    .newBuilder()
                    .setPurchaseToken(purchases.getPurchaseToken())
                    .build(), billingResult -> {

                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    //Setting setIsRemoveAd to true
                    // true - No ads
                    // false - showing ads.
                    prefs.setIsRemoveAd(true);
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
                                handlePurchase(purchase);
                            }
                        }
                    }
                }
        );
    }

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
                                        showSnackbar(btn_restore_fab, "Oops, No purchase found.");
                                        prefs.setIsRemoveAd(false); // set false to de-activate remove ad feature
                                    }
                                }
                            });
                }
            }
        });
    }

    private void initViews() {

        activity = this;
        handler = new Handler();
        prefs = new Prefs(this);
        productDetailsList = new ArrayList<>();
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerview);
        mAdView = findViewById(R.id.adView);
        btn_restore_fab = findViewById(R.id.fab);
        loadProducts = findViewById(R.id.loadProducts);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(activity, MainActivity.class));
        finish();
    }

    public void showSnackbar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(int pos) {
        launchPurchaseFlow(productDetailsList.get(pos));
    }
}