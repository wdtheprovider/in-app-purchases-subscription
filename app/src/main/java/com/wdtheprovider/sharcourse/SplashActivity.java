package com.wdtheprovider.sharcourse;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends AppCompatActivity {

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        checkSubscription();

        handler.postDelayed(this::viewMainActivity,2000);
    }

    private void checkSubscription() {
        //checking if there is a subscription active for logged on
        //gmail account on play store
    }

    void viewMainActivity(){
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }
}