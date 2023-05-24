package com.wdtheprovider.inapppurchase.utilies;

import android.util.Log;
import com.android.billingclient.api.ProductDetails;
import java.util.List;

/**
 * File Created by Dingaan Letjane
 * 2023/05/24
 **/

public class StoreEngine {

    public static void saveProductPrice(List<ProductDetails> productDetailsList, Prefs prefs){
        for (ProductDetails productDetails : productDetailsList) {
            double price = productDetails.getOneTimePurchaseOfferDetails().getPriceAmountMicros();
            double finalPrice = price / 1000000;
            Log.d("saveProductPriceDebug", "key: " + productDetails.getProductId() + "_price");
            Log.d("saveProductPriceDebug", "" + finalPrice);
            prefs.setString(productDetails.getProductId() + "_price", Double.toString(finalPrice));
            prefs.setString(productDetails.getProductId() + "_item", productDetails.getName());
        }
    }
}
