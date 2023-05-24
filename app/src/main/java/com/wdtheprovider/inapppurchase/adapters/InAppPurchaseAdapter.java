package com.wdtheprovider.inapppurchase.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.ProductDetails;
import com.wdtheprovider.inapppurchase.R;
import com.wdtheprovider.inapppurchase.interfaces.RecycleViewInterface;

import java.util.List;

public class InAppPurchaseAdapter extends RecyclerView.Adapter<InAppPurchaseAdapter.InAppPurchaseAdapterViewHolder>  {

    //You can complete the Adapter or buy the complete one on:  for $2 only
    //https://dingi.icu/store/product/in-app-purchase-recyclerview-productdetails-adapter-code/
    //Or Buy the full complete source code demo
    //https://dingi.icu/store/product/in-app-purchases-template-complete-source-code/

    public InAppPurchaseAdapter(Context context, List<ProductDetails> productDetailsList, RecycleViewInterface recycleViewInterface) {

    }

    @NonNull
    @Override
    public InAppPurchaseAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new InAppPurchaseAdapterViewHolder(null);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull InAppPurchaseAdapterViewHolder holder, int position) {
           }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class InAppPurchaseAdapterViewHolder extends RecyclerView.ViewHolder{
        public InAppPurchaseAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            }
    }
}