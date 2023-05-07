package com.wdtheprovider.inapppurchase.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.media.FaceDetector;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;
import com.onesignal.OneSignal;
import com.wdtheprovider.inapppurchase.R;
import com.wdtheprovider.inapppurchase.helpers.FirebaseFunctions;
import com.wdtheprovider.inapppurchase.utilies.Prefs;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    Button btn_subscribe, btn_buy_coins, btn_remove_ads, btn_fb_remove_ads, btn_buy_source_code, btn_show_interstitial, btn_combined;
    Prefs prefs;
    TextView txt_subscribed, coins, onOff, fb_onOff, about, profile, privacy, userId;
    Toolbar toolbar;
    ProgressBar progress_circular;
    AdView mAdView;
    String TAG = "Test123";
    Activity activity;
    private static final String ONESIGNAL_APP_ID = "ffae7918-4da1-49b4-bf0c-4aea56da3d6a";

    private InterstitialAd mInterstitialAd;
    AdRequest adRequest;

    FirebaseFunctions firebaseFunctions;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

        OneSignal.promptForPushNotifications();

        initViews();
        activity = this;

        firebaseFunctions = new FirebaseFunctions(this);

        if (prefs.getPremium() == 1) {
            txt_subscribed.setText(prefs.getString("subType", "You have a subscription"));
        } else {
            txt_subscribed.setText("You are not Subscribed");
        }

        if (prefs.isRemoveAd() || prefs.getPremium() == 1) {
            onOff.setText("ON");
        } else {
            onOff.setText("OFF");
        }

        if (prefs.getBoolean("fb_remove_ads", false)) {
            fb_onOff.setText("ON");
        } else {
            fb_onOff.setText("OFF");
        }

        coins.setText(prefs.getInt("coins", 0) + " Remaining coins");

        //Opening activities.
        btn_subscribe.setOnClickListener(view -> {
            if (prefs.getInt("isCount", 0) == 3) {
                showInterstitial();
            } else {
                increment();
            }
            startActivity(new Intent(this, SubscriptionActivity.class));
            finish();
        });

        btn_buy_coins.setOnClickListener(view -> {
            if (prefs.getInt("isCount", 0) == 3) {
                showInterstitial();
            } else {
                increment();
            }
            startActivity(new Intent(this, BuyCoinActivity.class));
            finish();
        });

        btn_remove_ads.setOnClickListener(view -> {
            if (prefs.getInt("isCount", 0) == 3) {
                showInterstitial();
            } else {
                increment();
            }
            startActivity(new Intent(this, RemoveAdsActivity.class));
            finish();
        });

        btn_fb_remove_ads.setOnClickListener(view -> {
            if (prefs.getInt("isCount", 0) == 3) {
                showInterstitial();
            } else {
                increment();
            }
            startActivity(new Intent(this, FacebookRemoveAdsActivity.class));
            finish();
        });

        btn_buy_source_code.setOnClickListener(view -> {
            startActivity(new Intent(this, BuyCodeActivity.class));
            finish();
        });

        initRemoteConfig();
    }

    void increment() {
        int count = 0;
        if (prefs.getInt("isCount", 0) >= 3) {
            prefs.setInt("isCount", 0);
        } else {
            count = prefs.getInt("isCount", 0);
            count = count + 1;
            prefs.setInt("isCount", count);
        }
    }



    void initRemoteConfig() {

        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(18000)
                .build();

        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);

        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);

        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        boolean updated = task.getResult();
                        Log.d(TAG, "Config params updated: " + updated);
                        prefs.setString("download_url", mFirebaseRemoteConfig.getString("download_url"));
                    } else {
                        prefs.setString("download_url", "https://dingi.icu/Download/privates/SourceCode.zip");
                    }
                    progress_circular.setVisibility(View.GONE);
                    btn_buy_source_code.setVisibility(View.VISIBLE);
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        firebaseFunctions.readUserData();
    }

    private void initViews() {
        prefs = new Prefs(this);
        btn_subscribe = findViewById(R.id.btn_subscribe);
        userId = findViewById(R.id.userId);
        toolbar = findViewById(R.id.toolbar);
        txt_subscribed = findViewById(R.id.txt_subscribed);
        btn_buy_coins = findViewById(R.id.btn_buy_coins);
        btn_remove_ads = findViewById(R.id.btn_remove_ads);
        btn_fb_remove_ads = findViewById(R.id.btn_fb_remove_ads);
        btn_buy_source_code = findViewById(R.id.btn_buy_source_code);
        onOff = findViewById(R.id.OnOff);
        fb_onOff = findViewById(R.id.fb_onOff);
        coins = findViewById(R.id.coins);
        about = findViewById(R.id.about);
        profile = findViewById(R.id.logout);
        progress_circular = findViewById(R.id.progress_circular);
        privacy = findViewById(R.id.privacy);
        mAdView = findViewById(R.id.adView);
        btn_show_interstitial = findViewById(R.id.btn_show_interstitial);

        setSupportActionBar(toolbar);

        //Checks if the user has a premium/subscription if not then show ads.
        if (prefs.getPremium() == 0) {
            if (!prefs.isRemoveAd()) {
                MobileAds.initialize(this, initializationStatus -> {
                });
                adRequest = new AdRequest.Builder().build();
                loadBannerAd();
                loadInterstitialAd();
            } else {
                mAdView.setVisibility(View.GONE);
            }
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

        profile.setOnClickListener(v -> {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(v.getContext());
            builder1.setTitle("Profile");
            builder1.setMessage("User ID\n" + prefs.getString("uid", "null"));
            builder1.setCancelable(true);
            builder1.setNegativeButton(
                    "Delete Profile",
                    (dialog, id) -> {
                        FirebaseUser user = firebaseFunctions.mAuth.getCurrentUser();
                        Log.d("deteleAccout", "deleting");
                        assert user != null;
                        Log.d("deteleAccout", " current " + user.getEmail());

                        user.delete().addOnCompleteListener(task -> {
                            Log.d("deteleAccout", "deleting " + task.isSuccessful());
                            if (task.isSuccessful()) {
                                firebaseFunctions.databaseReference.child(user.getUid()).removeValue().addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Toast.makeText(this, "Account deleted successfully", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(this, AuthActivity.class));
                                    }
                                });
                            } else {
                                Toast.makeText(activity, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    });
            builder1.setPositiveButton(
                    "Log out",
                    (dialog, id) -> {
                        firebaseFunctions.logOut();
                        Toast.makeText(activity, "logged out successfully...", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, AuthActivity.class));
                        finish();
                    });
            AlertDialog alert11 = builder1.create();
            alert11.show();
        });

        privacy.setOnClickListener(v -> {
            String url = "https://dingi.icu/pages/inappdemo_privacy_policy.php";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });

        userId.setText(String.format("uid: %s logged in", prefs.getString("uid", "null")));
    }

    void loadBannerAd() {
        mAdView.loadAd(adRequest);
    }

    void showInterstitial() {
        increment();
        if (prefs.getPremium() == 0) {
            if (!prefs.isRemoveAd()) {
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(this);
                    prefs.setBoolean("adILoaded", false);
                    increment();
                } else {
                    Log.d(TAG, "The interstitial ad wasn't ready yet.");
                    prefs.setBoolean("adILoaded", false);
                }
                loadInterstitialAd();
            } else {
                Log.d(TAG, "nothing to show");
            }
        }
    }

    void loadInterstitialAd() {
        if (!prefs.getBoolean("adILoaded", false)) {
            InterstitialAd.load(this, "ca-app-pub-3940256099942544/1033173712", adRequest,
                    new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                            // The mInterstitialAd reference will be null until
                            // an ad is loaded.
                            mInterstitialAd = interstitialAd;
                            Log.d(TAG, "an ad is loaded.");
                            mInterstitialAd.show(activity);
                            prefs.setBoolean("adILoaded", true);
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error
                            mInterstitialAd = null;
                            Log.d(TAG, "FAILED to load." + loadAdError.getMessage());
                            prefs.setBoolean("adILoaded", false);
                        }
                    });
        }
    }
}