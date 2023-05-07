package com.wdtheprovider.inapppurchase.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.QueryPurchasesParams;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.wdtheprovider.inapppurchase.R;
import com.wdtheprovider.inapppurchase.helpers.FirebaseFunctions;
import com.wdtheprovider.inapppurchase.models.User;
import com.wdtheprovider.inapppurchase.utilies.Prefs;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    Handler handler;
    BillingClient billingClient;
    Window window;
    Prefs prefs;
    FirebaseFunctions firebaseFunctions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        prefs = new Prefs(this);
        handler = new Handler();

        window = this.getWindow();
        checkSubscription();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.orange));

        firebaseFunctions = new FirebaseFunctions(this);

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseFunctions.mAuth.getCurrentUser();

        if (currentUser == null) {
            startActivity(new Intent(SplashActivity.this, AuthActivity.class));
            finish();
        } else {
            viewMainActivity();
        }
    }

    void viewMainActivity() {
        handler.postDelayed(() -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }, 2000);
    }

    void checkSubscription() {
        billingClient = BillingClient.newBuilder(this).enablePendingPurchases().setListener((billingResult, list) -> {
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
                                        prefs.setString("purchasedToken", list.get(0).getPurchaseToken());
                                        prefs.setString("purchasedProductId", list.get(0).getProducts().get(0));
                                        prefs.setPremium(1); // set 1 to activate premium feature
                                    } else {
                                        prefs.setString("subType", "No Subscription");
                                        prefs.setString("purchasedToken", "");
                                        prefs.setString("purchasedToken", "");
                                        prefs.setPremium(0); // set 0 to de-activate premium feature
                                    }
                                }
                            });
                }
            }
        });
    }
}