package com.wdtheprovider.sharcourse.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.wdtheprovider.sharcourse.R;
import com.wdtheprovider.sharcourse.utilies.Prefs;

import java.util.Objects;

public class MainActivity extends AppCompatActivity  {
    Button btn_subscribe,btn_buy_coins,btn_remove_ads;
    Prefs prefs;
    TextView txt_subscribed,coins,onOff, menu;
    private AdView mAdView;
    AlertDialog.Builder builder;

    public DrawerLayout drawerLayout;
    Toolbar toolbar;
    public ActionBarDrawerToggle actionBarDrawerToggle;

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
        menu= findViewById(R.id.menu);

        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);

        actionBarDrawerToggle.syncState();
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        //This will check if remove ad is true, then not display ads.
        if (!prefs.isRemoveAd()) {
            MobileAds.initialize(this, initializationStatus -> {
            });
            loadBannerAd();
        }
    }

    void loadBannerAd(){

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}