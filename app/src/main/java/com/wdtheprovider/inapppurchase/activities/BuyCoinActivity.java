package com.wdtheprovider.inapppurchase.activities;

import static com.wdtheprovider.inapppurchase.utilies.StoreEngine.saveProductPrice;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.collect.ImmutableList;
import com.wdtheprovider.inapppurchase.adapters.BuyCoinsAdapter;
import com.wdtheprovider.inapppurchase.helpers.FirebaseFunctions;
import com.wdtheprovider.inapppurchase.interfaces.RecycleViewInterface;
import com.wdtheprovider.inapppurchase.utilies.Prefs;
import com.wdtheprovider.inapppurchase.R;

import java.util.ArrayList;
import java.util.List;

public class BuyCoinActivity extends AppCompatActivity implements RecycleViewInterface {

    BillingClient billingClient;
    Button btn_use_coins, btn_transaction;
    RecyclerView recyclerView;
    TextView txt_coins;
    List<ProductDetails> productDetailsList;
    Activity activity;
    Prefs prefs;
    Toolbar toolbar;
    Handler handler;
    ProgressBar loadProducts;
    BuyCoinsAdapter adapter;
    ArrayList<Integer> coins;
    ArrayList<String> productIds;
    FirebaseFunctions firebaseFunctions;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_coin);

        firebaseFunctions = new FirebaseFunctions(this);

        initViews();


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
        connectGooglePlayBilling();

        btn_use_coins.setOnClickListener(v -> {
            if (prefs.getInt("coins", 0) > 0) {
                //get the current saved coins in the sharePrefs - prefs.getInt("coins",0)
                // and minus - 1 then setInt() - like updating the coins
                prefs.setInt("coins", prefs.getInt("coins", 0) - 1);
                //setText to the UI
                txt_coins.setText(prefs.getInt("coins", 0) + " Coin(s)");
                firebaseFunctions.updateCoins(prefs.getString("uid", ""), prefs.getInt("coins", 0));
            } else {
                showSnackBar(btn_use_coins, "Ran out of coins, please recharge.");
            }
        });

        btn_transaction.setOnClickListener(v -> {
            startActivity(new Intent(this, CoinsTransactionActivity.class));
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
                        .setProductId("test_coins_111")
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build(),

                //Product 2
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId("test_coins_201")
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build(),

                //Product 3
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId("test_coins_30")
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
        );

        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build();

        billingClient.queryProductDetailsAsync(params, (billingResult, list) -> {
            Log.d("ProDuct8", list.toString());
            productDetailsList.clear();
            handler.postDelayed(() -> {
                loadProducts.setVisibility(View.INVISIBLE); //
                productDetailsList.addAll(list);
                saveProductPrice(list, prefs);
                adapter = new BuyCoinsAdapter(getApplicationContext(), productDetailsList, BuyCoinActivity.this);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(BuyCoinActivity.this, LinearLayoutManager.VERTICAL, false));
                recyclerView.setAdapter(adapter);
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

        billingClient.launchBillingFlow(activity, billingFlowParams);
    }

    void verifyPurchase(Purchase purchase) {
        Log.d("testCoins", "Verify Purchase " + purchase.toString());
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
    private void initViews() {

        handler = new Handler();
        activity = this;
        prefs = new Prefs(this);

        btn_use_coins = findViewById(R.id.btn_use);
        recyclerView = findViewById(R.id.recyclerview);
        txt_coins = findViewById(R.id.txt_coins);
        loadProducts = findViewById(R.id.loadProducts);
        btn_transaction = findViewById(R.id.btn_transaction);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        productDetailsList = new ArrayList<>();

        txt_coins.setText(prefs.getInt("coins", 0) + " Coin(s)");

        productIds = new ArrayList<>();
        coins = new ArrayList<>();
        productIds.add("test_coins_111");
        coins.add(10);
        productIds.add("test_coins_201");
        coins.add(20);
        productIds.add("test_coins_30");
        coins.add(30);
    }

    @SuppressLint("SetTextI18n")
    void giveUserCoins(Purchase purchase) {

        int boughtCoins = 0;

        for (int i = 0; i < productIds.size(); i++) {
            if (purchase.getProducts().get(0).equals(productIds.get(i))) {
                //set coins
                boughtCoins = coins.get(i) * purchase.getQuantity();
                int myCoins = prefs.getInt("coins", 0);
                int finalCoins = myCoins + boughtCoins;

                Log.d("testCoins", "bought Coins: " + boughtCoins);
                Log.d("testCoins", "My Coins: " + myCoins);
                Log.d("testCoins", "Final Coins: " + finalCoins);

                prefs.setInt("coins", finalCoins);
                //Update UI
                txt_coins.setText(prefs.getInt("coins", 0) + " Coin(s)");

                firebaseFunctions.updateCoins(prefs.getString("uid", ""), finalCoins);

                reloadScreen();
            }
        }

        if (purchase != null) {
            firebaseFunctions.saveTransaction(purchase, prefs.getString("uid", ""), boughtCoins);
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

    private void reloadScreen() {
        //Reload the screen to activate the removeAd and remove the actual Ad off the screen.
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(activity, MainActivity.class));
        finish();
    }

    @Override
    public void onItemClick(int pos) {
        launchPurchaseFlow(productDetailsList.get(pos));
    }

    public void showSnackBar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
    }
}
