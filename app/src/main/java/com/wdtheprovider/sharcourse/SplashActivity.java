package com.wdtheprovider.sharcourse;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;

import java.util.List;

public class SplashActivity extends AppCompatActivity {

    Handler handler;
    BillingClient billingClient;

    Prefs prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        prefs = new Prefs(this);

        checkSubscription();
        handler = new Handler();
        handler.postDelayed(this::viewMainActivity,2000);

        checkSubscription();

    }

    void viewMainActivity(){
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }

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
}