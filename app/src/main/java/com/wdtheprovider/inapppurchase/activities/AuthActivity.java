package com.wdtheprovider.inapppurchase.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.wdtheprovider.inapppurchase.R;
import com.wdtheprovider.inapppurchase.helpers.FirebaseFunctions;
import com.wdtheprovider.inapppurchase.models.User;
import com.wdtheprovider.inapppurchase.utilies.Prefs;

public class AuthActivity extends AppCompatActivity {

    EditText email, password, confirmPassword;

    SwitchCompat switchCompat;

    Button btnSubmit;

    Handler handler;

    FirebaseFunctions firebaseFunctions;

    Prefs prefs;

    ProgressBar loadAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        prefs = new Prefs(this);

        firebaseFunctions = new FirebaseFunctions(this);

        handler = new Handler();

        btnSubmit = findViewById(R.id.submit);
        email = findViewById(R.id.email);
        loadAccount = findViewById(R.id.loadAccount);
        password = findViewById(R.id.password);
        switchCompat = findViewById(R.id.signToggle);
        confirmPassword = findViewById(R.id.confirmPassword);

        switchCompat.setOnClickListener(v -> {
            if (switchCompat.isChecked()) {
                Log.d("Checked", "Checked");
                switchCompat.setText("Sign in");
                btnSubmit.setText("Sign in");
                confirmPassword.setVisibility(View.GONE);
                confirmPassword.setText("");
            } else {
                Log.d("Checked", "UnChecked");
                switchCompat.setText("Sign Up");
                btnSubmit.setText("Sign Up");
                confirmPassword.setVisibility(View.VISIBLE);
            }
        });

        loadAccount.setVisibility(View.GONE);

        btnSubmit.setOnClickListener(v -> {
            if (email.getText().toString().isEmpty()) {
                Toast.makeText(AuthActivity.this, "Email cannot be empty.", Toast.LENGTH_SHORT).show();
            } else if (password.getText().toString().isEmpty()) {
                Toast.makeText(AuthActivity.this, "Password cannot be empty.", Toast.LENGTH_SHORT).show();
            } else if (password.getText().toString().length() < 5) {
                Toast.makeText(AuthActivity.this, "Password must contain 6 or more characters.", Toast.LENGTH_SHORT).show();
            } else if (!password.getText().toString().equals(confirmPassword.getText().toString()) && !switchCompat.isChecked()) {
                Toast.makeText(AuthActivity.this, "Password doesn't match.", Toast.LENGTH_SHORT).show();
            } else {
                User userToAdd = new User();
                String emailV = email.getText().toString();
                String passwordV = password.getText().toString();
                if (switchCompat.isChecked()) {
                    loadAccount.setVisibility(View.VISIBLE);
                    firebaseFunctions.mAuth.signInWithEmailAndPassword(emailV, passwordV).addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser loggedUser = firebaseFunctions.mAuth.getCurrentUser();
                            assert loggedUser != null;
                            Toast.makeText(this, "Sign in successfully...", Toast.LENGTH_SHORT).show();
                            firebaseFunctions.databaseReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    // This method is called once with the initial value and again
                                    // whenever data at this location is updated.
                                    User value = dataSnapshot.child(loggedUser.getUid()).getValue(User.class);

                                    assert value != null;
                                    Log.d("TempGet123", value.getId());

                                    prefs.setString("uid", value.getId());
                                    prefs.setInt("coins", value.getCoins());
                                    prefs.setBoolean("subscribe", value.isSubscribed());
                                    loadAccount.setVisibility(View.GONE);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    // Failed to read value
                                }
                            });
                            goHome();
                        } else {
                            Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    loadAccount.setVisibility(View.VISIBLE);
                    //creating account
                    firebaseFunctions.mAuth.createUserWithEmailAndPassword(emailV, passwordV).addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Sign up successfully...", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = firebaseFunctions.mAuth.getCurrentUser();
                            assert user != null;
                            userToAdd.setId(user.getUid());
                            userToAdd.setCoins(0);
                            userToAdd.setSubscribed(false);

                            //Adding record to realtime database
                            firebaseFunctions.databaseReference.child(user.getUid()).setValue(userToAdd).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    prefs.setString("uid", user.getUid());
                                    prefs.setInt("coins", 0);
                                    prefs.setBoolean("subscribe", false);
                                    loadAccount.setVisibility(View.GONE);
                                    Toast.makeText(this, "Account added.", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(AuthActivity.this, MainActivity.class));
                                    finish();
                                }
                            });
                        } else {
                            Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    void goHome() {
        handler.postDelayed(() -> {
            startActivity(new Intent(AuthActivity.this, MainActivity.class));
            finish();
        }, 2000);
    }
}