package com.wdtheprovider.inapppurchase.activities;

import static com.wdtheprovider.inapppurchase.helpers.MathsFunctions.roundTo2Decimal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.wdtheprovider.inapppurchase.R;
import com.wdtheprovider.inapppurchase.adapters.BuyCoinsAdapter;
import com.wdtheprovider.inapppurchase.adapters.CoinsTransactionAdapter;
import com.wdtheprovider.inapppurchase.helpers.FirebaseFunctions;
import com.wdtheprovider.inapppurchase.interfaces.RecycleViewInterface;
import com.wdtheprovider.inapppurchase.models.ConsumableTransaction;
import com.wdtheprovider.inapppurchase.models.User;
import com.wdtheprovider.inapppurchase.utilies.Prefs;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CoinsTransactionActivity extends AppCompatActivity implements RecycleViewInterface {

    TextView txt_total, txt_total_amount, txt_total_transactions;
    RecyclerView rv_transaction_recyclerview;
    List<ConsumableTransaction> transactions = new ArrayList<>();
    CoinsTransactionAdapter adapter;
    FirebaseFunctions firebaseFunctions;
    Prefs prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coins_transaction);

        initViews();

        firebaseFunctions = new FirebaseFunctions(this);

        prefs = new Prefs(this);

        firebaseFunctions.transactionReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                transactions.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    if(Objects.equals(ds.child("uid").getValue(String.class), prefs.getString("uid", ""))) {
                        ConsumableTransaction value = ds.getValue(ConsumableTransaction.class);
                        transactions.add(value);
                    }
                }
                int total_coins = 0;
                double amount = 0;

                for (int i = 0; i < transactions.size(); i++) {
                    total_coins += transactions.get(i).getReward() * transactions.get(i).getQty();
                    amount += transactions.get(i).getPrice();
                }

                txt_total.setText(total_coins+"");
                txt_total_amount.setText(roundTo2Decimal(amount)+"");
                txt_total_transactions.setText(transactions.size() + "");

                adapter = new CoinsTransactionAdapter(getApplicationContext(), transactions, CoinsTransactionActivity.this);
                rv_transaction_recyclerview.setHasFixedSize(true);
                rv_transaction_recyclerview.setLayoutManager(new LinearLayoutManager(CoinsTransactionActivity.this, LinearLayoutManager.VERTICAL, false));
                rv_transaction_recyclerview.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
            }
        });


    }

    private void initViews() {

        txt_total = findViewById(R.id.txt_total);
        txt_total_amount = findViewById(R.id.txt_total_amount);
        txt_total_transactions = findViewById(R.id.txt_total_transactions);
        rv_transaction_recyclerview = findViewById(R.id.rv_transaction_recyclerview);
    }

    @Override
    public void onItemClick(int pos) {

    }
}