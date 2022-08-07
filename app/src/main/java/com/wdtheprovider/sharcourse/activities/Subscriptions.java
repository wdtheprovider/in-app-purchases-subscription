package com.wdtheprovider.sharcourse.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.collect.ImmutableList;
import com.wdtheprovider.sharcourse.R;
import com.wdtheprovider.sharcourse.adapters.ProductDetailsAdapter;
import com.wdtheprovider.sharcourse.interfaces.RecycleViewInterface;
import com.wdtheprovider.sharcourse.utilies.Prefs;


import java.util.ArrayList;
import java.util.List;

public class Subscriptions extends AppCompatActivity implements RecycleViewInterface {

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

     ProductDetailsAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscriptions);

        initViews();
        handler = new Handler();

        activity = this;
        prefs = new Prefs(this);

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

        //restore purchases
        btn_restore_fab.setOnClickListener(v -> {
            Log.d(TAG,"CLICKED RESTORE");
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
                        .setProductId("one_week")
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build() ,

                //Product 2
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId("one_month")
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build(),

                //Product 3
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId("one_year")
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build(),

                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId("test_id_shar")
                        .setProductType(BillingClient.ProductType.SUBS)
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
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG,"posted delayed");

                            loadProducts.setVisibility(View.INVISIBLE);

                            productDetailsList.addAll(prodDetailsList);
                            Log.d(TAG,productDetailsList.size()+" number of products");

                            adapter = new ProductDetailsAdapter(getApplicationContext(), productDetailsList, Subscriptions.this);
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setLayoutManager(new LinearLayoutManager(Subscriptions.this, LinearLayoutManager.VERTICAL, false));
                            recyclerView.setAdapter(adapter);
                        }
                    },2000);

                }
        );

    }


    void launchPurchaseFlow(ProductDetails productDetails) {
        assert productDetails.getSubscriptionOfferDetails() != null;
        ImmutableList<BillingFlowParams.ProductDetailsParams> productDetailsParamsList =
                ImmutableList.of(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                                .setProductDetails(productDetails)
                                .setOfferToken(productDetails.getSubscriptionOfferDetails().get(0).getOfferToken())
                                .build()
                );
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build();

        BillingResult billingResult = billingClient.launchBillingFlow(activity, billingFlowParams);
    }



    protected void onResume() {
        super.onResume();
        billingClient.queryPurchasesAsync(
                QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build(),
                (billingResult, list) -> {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        for (Purchase purchase : list) {
                            if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged()) {
                                verifySubPurchase(purchase);
                            }
                        }
                    }
                }
        );

    }






    void verifySubPurchase(Purchase purchases) {

        AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams
                .newBuilder()
                .setPurchaseToken(purchases.getPurchaseToken())
                .build();

        billingClient.acknowledgePurchase(acknowledgePurchaseParams, billingResult -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                //user prefs to set premium
                Toast.makeText(Subscriptions.this, "Subscription activated, Enjoy!", Toast.LENGTH_SHORT).show();
                //Setting premium to 1
                // 1 - premium
                // 0 - no premium
                prefs.setPremium(1);
                startActivity(new Intent(this,MainActivity.class));
                finish();            }
        });

        Log.d(TAG, "Purchase Token: " + purchases.getPurchaseToken());
        Log.d(TAG, "Purchase Time: " + purchases.getPurchaseTime());
        Log.d(TAG, "Purchase OrderID: " + purchases.getOrderId());
    }





    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        productDetailsList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerview);
        btn_restore_fab = findViewById(R.id.fab);
        loadProducts = findViewById(R.id.loadProducts);

    }

    @Override
    public void onItemClick(int pos) {
launchPurchaseFlow(productDetailsList.get(pos));
    }




    void restorePurchases(){

        billingClient = BillingClient.newBuilder(this).enablePendingPurchases().setListener((billingResult, list) -> {}).build();
        final BillingClient finalBillingClient = billingClient;
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {

            }

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {

                if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
                    finalBillingClient.queryPurchasesAsync(
                            QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build(), (billingResult1, list) -> {
                                if (billingResult1.getResponseCode() == BillingClient.BillingResponseCode.OK){

                                    if(list.size()>0){
                                        prefs.setPremium(1); // set 1 to activate premium feature
                                        showSnackbar(btn_restore_fab, "Successfully restored");
                                        Toast.makeText(Subscriptions.this, "Successfully restored", Toast.LENGTH_SHORT).show();
                                        Log.d(TAG,"Successfully restored");


                                        int i = 0;
                                        for (Purchase purchase: list){
                                            //Here you can manage each product, if you have multiple subscription
                                            Log.d("testOffer",purchase.getOriginalJson()); // Get to see the order information
                                            Log.d("testOffer", " index" + i);
                                            i++;
                                        }

                                    }else {
                                        Log.d(TAG,"Oops, No purchase found.");
                                        showSnackbar(btn_restore_fab, "Oops, No purchase found.");
                                        Toast.makeText(Subscriptions.this, "Oops, No purchase found.", Toast.LENGTH_SHORT).show();
                                        prefs.setPremium(0); // set 0 to de-activate premium feature
                                    }
                                }
                            });

                }

            }
        });
    }

    public void showSnackbar(View view, String message)
    {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
    }


}