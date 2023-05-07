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

    // creating a variable for our
    // Firebase Database.
    FirebaseDatabase firebaseDatabase;

    // creating a variable for our Database
    // Reference for Firebase.
    public final DatabaseReference databaseReference;

    public final String rootPath = "Users";

    public final FirebaseAuth mAuth;

    Context _context;

    Prefs prefs;

    public FirebaseFunctions(Context context) {
        _context = context;
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference(rootPath);
        mAuth = FirebaseAuth.getInstance();
        prefs = new Prefs(context);
    }

    public void updateCoins(Context context, String uid, int coins) {
        databaseReference.child(uid).child("coins").setValue(coins).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(context, "Coins updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "failed to update coins online.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void logOut() {
      mAuth.signOut();
    }

    public  void readUserData(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                User value = dataSnapshot.child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).getValue(User.class);
                assert value != null;
                prefs.setString("uid", value.getId());
                prefs.setInt("coins", value.getCoins());
                prefs.setBoolean("subscribe", value.isSubscribed());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
            }
        });
    }
}
