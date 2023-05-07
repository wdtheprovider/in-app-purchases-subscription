package com.wdtheprovider.inapppurchase.utilies;

import android.view.View;

import com.google.android.material.snackbar.Snackbar;

public class Helper {

  static  public void showSnackBar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
    }
}
