package com.wdtheprovider.sharcourse.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.QueryPurchasesParams;
import com.wdtheprovider.sharcourse.R;
import com.wdtheprovider.sharcourse.utilies.Prefs;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    Handler handler;
    BillingClient billingClient;
    Window window;
    Prefs prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        prefs = new Prefs(this);
        window = this.getWindow();
        checkSubscription();
        handler = new Handler();
        handler.postDelayed(this::viewMainActivity,2000);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.orange));
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
                    finalBillingClient.queryPurchasesAsync(
                            QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build(), (billingResult1, list) -> {
                        if (billingResult1.getResponseCode() == BillingClient.BillingResponseCode.OK){
                          //   Log.d("testOffer",list.size() +" size");
                             if(list.size()>0){
                                 prefs.setPremium(1); // set 1 to activate premium feature
                                 int i = 0;
                                 for (Purchase purchase: list){
                                     //Here you can manage each product, if you have multiple subscription
                                     Log.d("testOffer",purchase.getOriginalJson()); // Get to see the order information
                                     Log.d("testOffer", " index" + i);
                                     i++;
                                 }
                             }else {
                                 prefs.setPremium(0); // set 0 to de-activate premium feature
                             }
                        }
                    });

                }

            }
        });
    }
}