package com.wdtheprovider.sharcourse;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    Button btn_subscribe;
    Prefs prefs;
    TextView txt_subscribed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        prefs = new Prefs(this);

        if (prefs.getPremium()==1){
            txt_subscribed.setText("You are a Premium Subscriber");
        } else {
            txt_subscribed.setText("You are not Subscribed");
        }

        //Opening the Store activity.
        btn_subscribe.setOnClickListener(view -> startActivity(new Intent(this,StoreActivity.class)));
    }

    private void initViews() {

        btn_subscribe= findViewById(R.id.btn_subscribe);
        txt_subscribed= findViewById(R.id.txt_subscribed);
    }
}