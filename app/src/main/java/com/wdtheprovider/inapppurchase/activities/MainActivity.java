package com.wdtheprovider.inapppurchase.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.wdtheprovider.inapppurchase.utilies.Prefs;
import com.wdtheprovider.inapppurchase.R;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class MainActivity extends AppCompatActivity {
    Button btn_subscribe, btn_buy_coins, btn_remove_ads,btn_buy_source_code,btn_show_interstitial;
    Prefs prefs;
    TextView txt_subscribed, coins, onOff, about, privacy;
    Toolbar toolbar;
    AdView mAdView;
    String TAG = "test";

    private InterstitialAd mInterstitialAd;
    AdRequest adRequest;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        if (prefs.getPremium() == 1) {
            txt_subscribed.setText("You are a Premium Subscriber");
        } else {
            txt_subscribed.setText("You are not Subscribed");
        }

        if (prefs.isRemoveAd()) {
            onOff.setText("ON");
        } else {
            onOff.setText("OFF");
        }

        coins.setText(prefs.getInt("coins", 0) + " Remaining coins");

        //Opening activities.
        btn_subscribe.setOnClickListener(view -> {
            startActivity(new Intent(this, Subscriptions.class));
            finish();
        });

        btn_buy_coins.setOnClickListener(view -> {
            startActivity(new Intent(this, BuyCoinActivity.class));
            finish();
        });

        btn_remove_ads.setOnClickListener(view -> {
            startActivity(new Intent(this, RemoveAdsActivity.class));
            finish();
        });

        btn_buy_source_code.setOnClickListener(view -> {
            startActivity(new Intent(this, BuyCodeActivity.class));
            finish();
        });

    }

    private void initViews() {
        prefs = new Prefs(this);
        btn_subscribe = findViewById(R.id.btn_subscribe);
        toolbar = findViewById(R.id.toolbar);
        txt_subscribed = findViewById(R.id.txt_subscribed);
        btn_buy_coins = findViewById(R.id.btn_buy_coins);
        btn_remove_ads = findViewById(R.id.btn_remove_ads);
        btn_buy_source_code = findViewById(R.id.btn_buy_source_code);
        onOff = findViewById(R.id.OnOff);
        coins = findViewById(R.id.coins);
        about = findViewById(R.id.about);
        privacy = findViewById(R.id.privacy);
        mAdView    = findViewById(R.id.adView);
        btn_show_interstitial    = findViewById(R.id.btn_show_interstitial);

        setSupportActionBar(toolbar);

        //Checks if the user has a premium/subscription if not then show ads.
        if (prefs.getPremium() == 0) {
            Log.d("Test23","GetPremium "+prefs.getPremium());

            MobileAds.initialize(this, initializationStatus -> {
            });
            adRequest = new AdRequest.Builder().build();
            loadBannerAd();
            loadInterstitialAd();

        } else {
            Log.d("Test23","GetPremium "+prefs.getPremium());
            mAdView.setVisibility(View.GONE);
        }

        about.setOnClickListener(v -> {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(v.getContext());
            builder1.setTitle("In-App Purchases Demo");
            builder1.setMessage(R.string.about);
            builder1.setCancelable(true);
            builder1.setNegativeButton(
                    "OK",
                    (dialog, id) -> dialog.cancel());
            AlertDialog alert11 = builder1.create();
            alert11.show();
        });

        privacy.setOnClickListener(v -> {
            String url = "https://dingi.icu/pages/inappdemo_privacy_policy.php";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });

        btn_show_interstitial.setOnClickListener(v -> {
            if (prefs.getPremium() == 0) {
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(this);
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                }
                loadInterstitialAd();
            }
        });
    }

    void loadBannerAd() {
        mAdView.loadAd(adRequest);
    }

    void loadInterstitialAd() {
        InterstitialAd.load(this,"ca-app-pub-3940256099942544/1033173712", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i("test", "onAdLoaded");
                    }
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.d("test", loadAdError.toString());
                        mInterstitialAd = null;
                    }
                });
    }
}
