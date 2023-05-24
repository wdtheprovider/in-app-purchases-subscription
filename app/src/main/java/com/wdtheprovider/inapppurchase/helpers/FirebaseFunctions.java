package com.wdtheprovider.inapppurchase.helpers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.billingclient.api.Purchase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wdtheprovider.inapppurchase.models.ConsumableTransaction;
import com.wdtheprovider.inapppurchase.utilies.Prefs;

import java.text.SimpleDateFormat;
import java.util.Objects;

/**
 * File Created by Dingaan Letjane
 * 2023/05/06
 **/

public class FirebaseFunctions {
    public FirebaseDatabase firebaseDatabase;
    public final DatabaseReference usersReference;
    public final DatabaseReference transactionReference;
    public final String rootPath = "Users";
    public final String transactionPath = "Transactions";
    public final FirebaseAuth mAuth;
    Context _context;
    Prefs prefs;

    public FirebaseFunctions(Context context) {
        _context = context;
        prefs = new Prefs(context);
        firebaseDatabase = FirebaseDatabase.getInstance();
        usersReference = firebaseDatabase.getReference(rootPath);
        transactionReference = firebaseDatabase.getReference(transactionPath);
        mAuth = FirebaseAuth.getInstance();
    }

    public void updateCoins(String uid, int coins) {
        usersReference.child(uid).child("coins").setValue(coins).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(_context, "Coins updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(_context, "failed to update coins online.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateSubscribed(String uid, boolean isSubscribed) {
        usersReference.child(uid).child("subscribed").setValue(isSubscribed).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(_context, "Subscribed updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(_context, "failed to update coins online.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void saveTransaction(Purchase purchase, String uid, int reward) {
        String orderNumber = purchase.getOrderId();
        String productId = purchase.getProducts().get(0);
        String purchaseToken = purchase.getPurchaseToken();
        String orderDate = new SimpleDateFormat("dd:MM:yyyy").format(new java.util.Date());
        String item = prefs.getString(purchase.getProducts().get(0)+"_item","");
        int qty = purchase.getQuantity();
        String time = new SimpleDateFormat("HH:mm").format(new java.util.Date());
        double price = Double.parseDouble(prefs.getString(purchase.getProducts().get(0)+"_price",""));

        ConsumableTransaction transactionToAdd = new ConsumableTransaction();

        transactionToAdd.setItem(item);
        transactionToAdd.setUid(uid);
        transactionToAdd.setProductId(productId);
        transactionToAdd.setOrderDate(orderDate);
        transactionToAdd.setOrderNumber(orderNumber);
        transactionToAdd.setPurchasedTime(time);
        transactionToAdd.setPrice(price);
        transactionToAdd.setQty(qty);
        transactionToAdd.setReward(reward);
        transactionToAdd.setPurchaseToken(purchaseToken);

        transactionReference.push().setValue(transactionToAdd).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("DebugTrans", "Record added");
            } else {
                Log.d("DebugTrans", "something went wrong " + Objects.requireNonNull(task.getException()).getMessage());
            }
        });
    }

    public void logOut() {
        mAuth.signOut();
    }
}
