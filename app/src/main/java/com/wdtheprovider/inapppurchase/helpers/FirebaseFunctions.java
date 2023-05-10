package com.wdtheprovider.inapppurchase.helpers;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wdtheprovider.inapppurchase.models.User;
import com.wdtheprovider.inapppurchase.utilies.Prefs;

import java.util.Objects;

/**
 * File Created by Dingaan Letjane
 * 2023/05/06
 **/

public class FirebaseFunctions {

    FirebaseDatabase firebaseDatabase;
    public final DatabaseReference databaseReference;
    public final String rootPath = "Users";
    public final FirebaseAuth mAuth;
    Context _context;

    public FirebaseFunctions(Context context) {
        _context = context;
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference(rootPath);
        mAuth = FirebaseAuth.getInstance();
    }

    public void updateCoins(String uid, int coins) {
        databaseReference.child(uid).child("coins").setValue(coins).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(_context, "Coins updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(_context, "failed to update coins online.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateSubscribed(String uid, boolean isSubscribed) {
        databaseReference.child(uid).child("subscribed").setValue(isSubscribed).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(_context, "Subscribed updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(_context, "failed to update coins online.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void logOut() {
      mAuth.signOut();
    }
}
