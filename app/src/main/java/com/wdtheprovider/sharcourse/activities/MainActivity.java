package com.wdtheprovider.sharcourse.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.wdtheprovider.sharcourse.R;
import com.wdtheprovider.sharcourse.utilies.Prefs;

public class MainActivity extends AppCompatActivity  {
    Button btn_subscribe,btn_buy_coins,btn_remove_ads;
    Prefs prefs;
    TextView txt_subscribed,coins,onOff,about,privacy;
    Toolbar toolbar;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        if (prefs.getPremium()==1){
            txt_subscribed.setText("You are a Premium Subscriber");
        } else {
            txt_subscribed.setText("You are not Subscribed");
        }

        if(prefs.isRemoveAd()){
            onOff.setText("ON");
        }else {
            onOff.setText("OFF");
        }

        coins.setText(prefs.getInt("coins",0)+" Remaining coins");

        //Opening activities.
        btn_subscribe.setOnClickListener(view -> startActivity(new Intent(this, Subscriptions.class)));
        btn_buy_coins.setOnClickListener(view -> startActivity(new Intent(this, BuyCoinActivity.class)));
        btn_remove_ads.setOnClickListener(view -> startActivity(new Intent(this, RemoveAdsActivity.class)));
    }

    private void initViews() {
        prefs = new Prefs(this);
        btn_subscribe= findViewById(R.id.btn_subscribe);
        toolbar = findViewById(R.id.toolbar);
        txt_subscribed= findViewById(R.id.txt_subscribed);
        btn_buy_coins= findViewById(R.id.btn_buy_coins);
        btn_remove_ads= findViewById(R.id.btn_remove_ads);
        onOff= findViewById(R.id.OnOff);
        coins= findViewById(R.id.coins);
        about= findViewById(R.id.about);
        privacy= findViewById(R.id.privacy);

        setSupportActionBar(toolbar);

        //This will check if remove ad is true, then not display ads.
        if (!prefs.isRemoveAd() || prefs.getPremium()==0) {
            MobileAds.initialize(this, initializationStatus -> {
            });
            loadBannerAd();
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
    }

    void loadBannerAd(){
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }
}