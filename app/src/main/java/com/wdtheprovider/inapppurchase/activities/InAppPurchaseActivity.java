package com.wdtheprovider.inapppurchase.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.collect.ImmutableList;
import com.wdtheprovider.inapppurchase.R;
import com.wdtheprovider.inapppurchase.adapters.InAppProductAdapter;
import com.wdtheprovider.inapppurchase.adapters.InAppPurchaseAdapter;
import com.wdtheprovider.inapppurchase.adapters.SubscriptionAdapter;
import com.wdtheprovider.inapppurchase.interfaces.RecycleViewInterface;
import com.wdtheprovider.inapppurchase.utilies.Prefs;

import java.util.ArrayList;
import java.util.List;

public class InAppPurchaseActivity extends AppCompatActivity implements RecycleViewInterface {

     BillingClient billingClient;
     List<ProductDetails> productDetailsList;
     List<ProductDetails> productInAppDetailsList;
     ProgressBar loadProducts;
     Handler handler;
     InAppPurchaseAdapter adapter;
    InAppProductAdapter InAppAdapter;
     RecyclerView recyclerView,inAppRecyclerview;
     Prefs prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_app_purchase);

        initViews();

        billingClient = BillingClient.newBuilder(this)
                .enablePendingPurchases()
                .setListener(
                        (billingResult, list) -> {
                            if(billingResult.getResponseCode()==BillingClient.BillingResponseCode.OK && list !=null) {
                                for (Purchase purchase: list){
                                    //verifyPurchase(purchase);
                                }
                            }
                        }
                ).build();

        //start the connection after initializing the billing client
        establishConnection();
    }

    private void initViews() {
        prefs =  new Prefs(getApplicationContext());
        handler = new Handler();
        productDetailsList = new ArrayList<>();
        productInAppDetailsList = new ArrayList<>();
        loadProducts = findViewById(R.id.loadProducts);
        recyclerView = findViewById(R.id.recyclerview);
        inAppRecyclerview = findViewById(R.id.inAppRecyclerview);
    }

    void establishConnection() {

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    Log.d("testing","Connected");
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

        Log.d("testing","Querying Products");

        ImmutableList<QueryProductDetailsParams.Product> productList = ImmutableList.of(
                //Product 1
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId("test_sub_weekly1")
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build() ,

                //Product 2
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId("test_sub_monthly1")
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build()
                //Product 1

        );

        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build();

        Log.d("testing","prod ");

        billingClient.queryProductDetailsAsync(
                params,
                (billingResult, prodDetailsList) -> {
                    Log.d("testing","prod "+ prodDetailsList.toString());
                    // Process the result
                    productDetailsList.clear();
                    handler.postDelayed(() -> {
                        loadProducts.setVisibility(View.INVISIBLE);
                        productDetailsList.addAll(prodDetailsList);
                        adapter = new InAppPurchaseAdapter(getApplicationContext(), productDetailsList, InAppPurchaseActivity.this);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(InAppPurchaseActivity.this, LinearLayoutManager.VERTICAL, false));
                        recyclerView.setAdapter(adapter);
                        showInAppProducts();
                    },2000);

                }
        );

    }


        @SuppressLint("SetTextI18n")
    void showInAppProducts() {

        Log.d("testing","Querying Products");
            ImmutableList<QueryProductDetailsParams.Product> productListInApp = ImmutableList.of(
                    //Product 1
                    QueryProductDetailsParams.Product.newBuilder()
                            .setProductId("test_coins_111")
                            .setProductType(BillingClient.ProductType.INAPP)
                            .build(),

                    //Product 2
                    QueryProductDetailsParams.Product.newBuilder()
                            .setProductId("test_coins_201")
                            .setProductType(BillingClient.ProductType.INAPP)
                            .build(),

                    QueryProductDetailsParams.Product.newBuilder()
                            .setProductId("test_remove_ads1")
                            .setProductType(BillingClient.ProductType.INAPP)
                            .build()

            );

        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                .setProductList(productListInApp)
                .build();

        Log.d("testing","prod ");

        billingClient.queryProductDetailsAsync(
                params,
                (billingResult, prodInAppDetailsList) -> {
                    Log.d("testing","prod "+ prodInAppDetailsList.toString());
                    // Process the result
                    productInAppDetailsList.clear();
                    handler.postDelayed(() -> {
                        loadProducts.setVisibility(View.INVISIBLE);
                        productInAppDetailsList.addAll(prodInAppDetailsList);
                        InAppAdapter = new InAppProductAdapter(getApplicationContext(), productInAppDetailsList, InAppPurchaseActivity.this);
                        inAppRecyclerview.setHasFixedSize(true);
                        inAppRecyclerview.setLayoutManager(new LinearLayoutManager(InAppPurchaseActivity.this, LinearLayoutManager.VERTICAL, false));
                        inAppRecyclerview.setAdapter(adapter);
                    },2000);

                }
        );

    }




    @Override
    public void onItemClick(int pos) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }
}