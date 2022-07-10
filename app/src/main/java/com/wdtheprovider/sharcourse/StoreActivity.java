package com.wdtheprovider.sharcourse;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

public class StoreActivity extends AppCompatActivity {

    Button txt_price,offer_btn;
    String TAG = "SubTest1";
    Activity activity;
    int selectedOfferIndex;
    Prefs prefs;
    private BillingClient billingClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        initViews();

        activity = this;

        prefs = new Prefs(this);
        //initializing the billing client
        billingClient = BillingClient.newBuilder(this)
                .enablePendingPurchases()
                .setListener(
                        (billingResult, list) -> {
                           if(billingResult.getResponseCode()==BillingClient.BillingResponseCode.OK && list !=null) {
                               for (Purchase purchase: list){
                                   verifySubPurchase(purchase);
                               }
                           }
                        }
                ).build();

        //start the connection after initializing the billing client
        establishConnection();
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
                //Product 1 = index is 0
                QueryProductDetailsParams.Product.newBuilder()
                .setProductId("sub_premium")
                .setProductType(BillingClient.ProductType.SUBS)
                .build(),

                //Product 2 = index is 1
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
                (billingResult, productDetailsList) -> {
                    // Process the result
                    for (ProductDetails productDetails : productDetailsList) {
                        if (productDetails.getProductId().equals("sub_premium")) {
                            List<ProductDetails.SubscriptionOfferDetails> subDetails = productDetails.getSubscriptionOfferDetails();
                            assert subDetails != null;
                            Log.d("testOffer",subDetails.get(0).getOfferToken());
                            txt_price.setText(subDetails.get(0).getPricingPhases().getPricingPhaseList().get(0).getFormattedPrice()+" Per Month");
                            txt_price.setOnClickListener(view -> {
                                launchPurchaseFlow(productDetails);
                            });
                        }

                        if (productDetails.getProductId().equals("test_id_shar")) {
                            List<ProductDetails.SubscriptionOfferDetails> subDetails = productDetails.getSubscriptionOfferDetails();
                            assert subDetails != null;
                            Log.d("testOffer",subDetails.get(1).getOfferToken());
                            offer_btn.setText(subDetails.get(1).getPricingPhases().getPricingPhaseList().get(0).getFormattedPrice()+" Per Month");
                            offer_btn.setOnClickListener(view -> {
                                launchPurchaseFlow(productDetails);
                            });
                        }
                    }
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


    void verifySubPurchase(Purchase purchases) {

        AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams
                .newBuilder()
                .setPurchaseToken(purchases.getPurchaseToken())
                .build();

        billingClient.acknowledgePurchase(acknowledgePurchaseParams, billingResult -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                //user prefs to set premium
                Toast.makeText(StoreActivity.this, "You are a premium user now", Toast.LENGTH_SHORT).show();
                //Setting premium to 1
                // 1 - premium
                // 0 - no premium
                prefs.setPremium(1);
            }
        });

        Log.d(TAG, "Purchase Token: " + purchases.getPurchaseToken());
        Log.d(TAG, "Purchase Time: " + purchases.getPurchaseTime());
        Log.d(TAG, "Purchase OrderID: " + purchases.getOrderId());
    }

    private void initViews() {
        txt_price = findViewById(R.id.txt_price);
        offer_btn = findViewById(R.id.offer_btn);
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
}