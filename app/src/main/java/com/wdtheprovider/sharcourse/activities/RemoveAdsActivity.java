package com.wdtheprovider.sharcourse.activities;

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
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.collect.ImmutableList;
import com.wdtheprovider.sharcourse.R;
import com.wdtheprovider.sharcourse.adapters.RemoveAdsAdapter;
import com.wdtheprovider.sharcourse.interfaces.RecycleViewInterface;
import com.wdtheprovider.sharcourse.utilies.Prefs;

import java.util.ArrayList;
import java.util.List;

public class RemoveAdsActivity extends AppCompatActivity implements RecycleViewInterface {


    String TAG = "TestINAPP";
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
            Log.d(TAG, "CLICKED RESTORE");
           restorePurchases();
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

    private void initViews() {

        activity = this;
        handler = new Handler();
        prefs = new Prefs(this);
        productDetailsList = new ArrayList<>();
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerview);
        btn_restore_fab = findViewById(R.id.fab);
        loadProducts = findViewById(R.id.loadProducts);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(activity,MainActivity.class));
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