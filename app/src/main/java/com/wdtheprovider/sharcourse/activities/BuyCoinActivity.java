package com.wdtheprovider.sharcourse.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.collect.ImmutableList;
import com.wdtheprovider.sharcourse.R;
import com.wdtheprovider.sharcourse.adapters.BuyCoinsAdapter;
import com.wdtheprovider.sharcourse.interfaces.RecycleViewInterface;
import com.wdtheprovider.sharcourse.utilies.Prefs;

import java.util.ArrayList;
import java.util.List;

public class BuyCoinActivity extends AppCompatActivity implements RecycleViewInterface {

    BillingClient billingClient;
    Button btn_use_coins;
    RecyclerView recyclerView;
    TextView txt_coins;
    List<ProductDetails> productDetailsList;
    String TAG = "TestINAPP";
    Activity activity;
    Prefs prefs;
    Toolbar toolbar;
    Handler handler;
    ProgressBar loadProducts;
    BuyCoinsAdapter adapter;
    ArrayList<Integer> coins;
    ArrayList<String> productIds;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_coin);

        initViews();



        billingClient = BillingClient.newBuilder(this)
                .enablePendingPurchases()
                .setListener(
                        new PurchasesUpdatedListener() {
                            @Override
                            public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
                                if(billingResult.getResponseCode()==BillingClient.BillingResponseCode.OK && list !=null) {
                                    for (Purchase purchase: list){
                                        verifyPurchase(purchase);
                                    }
                                }
                            }
                        }
                ).build();

        //start the connection after initializing the billing client
        connectGooglePlayBilling();


        btn_use_coins.setOnClickListener(v -> {
            //get the current saved coins in the sharePrefs - prefs.getInt("coins",0)
            // and minus - 1 then setInt() - like updating the coins
            prefs.setInt("coins", prefs.getInt("coins", 0) - 1);
            //setText to the UI
            txt_coins.setText(prefs.getInt("coins", 0)+"");
        });
    }


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



    @SuppressLint("SetTextI18n")

    void showProducts() {

        ImmutableList<QueryProductDetailsParams.Product> productList = ImmutableList.of(
                //Product 1
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId("10_coins_id")
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build(),

                //Product 2
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId("20_coins_id")
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build(),

                //Product 3
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId("50_coins_id")
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




    @SuppressLint("SetTextI18n")
    void giveUserCoins(Purchase purchase) {

        Log.d("TestINAPP", purchase.getProducts().get(0));
        Log.d("TestINAPP", purchase.getQuantity() + " Quantity");


        /*
        productIds.add("10_coins_id");
        coins.add(10);

        productIds.add("20_coins_id");
        coins.add(20);

        productIds.add("50_coins_id");
        coins.add(50);
         */


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



    @SuppressLint("SetTextI18n")
    private void initViews() {

        handler = new Handler();
        activity = this;
        prefs = new Prefs(this);

        btn_use_coins = findViewById(R.id.btn_use);
        recyclerView = findViewById(R.id.recyclerview);
        txt_coins = findViewById(R.id.txt_coins);
        loadProducts = findViewById(R.id.loadProducts);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        productDetailsList = new ArrayList<>();

        txt_coins.setText(""+prefs.getInt("coins",0));

        productIds = new ArrayList<>();
        coins = new ArrayList<>();

        productIds.add("10_coins_id");
        coins.add(10);

        productIds.add("20_coins_id");
        coins.add(20);

        productIds.add("50_coins_id");
        coins.add(50);

    }


    @Override
    public void onItemClick(int pos) {
        launchPurchaseFlow(productDetailsList.get(pos));
    }
}