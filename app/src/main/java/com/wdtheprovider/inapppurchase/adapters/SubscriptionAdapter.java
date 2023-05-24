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

public class SubscriptionAdapter extends RecyclerView.Adapter<SubscriptionAdapter.ProductDetailsViewHolder>  {

    //You can complete the Adapter or buy the complete one on:  for $2 only
    //https://dingi.icu/store/product/in-app-purchase-recyclerview-subscription-adapter-code/
    //Or Buy the full complete source code demo
    //https://dingi.icu/store/product/in-app-purchases-template-complete-source-code/

    public SubscriptionAdapter(Context context, List<ProductDetails> productDetailsList, RecycleViewInterface recycleViewInterface) {

    }

    @NonNull
    @Override
    public ProductDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductDetailsViewHolder(null);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ProductDetailsViewHolder holder, int position) {
        }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ProductDetailsViewHolder extends RecyclerView.ViewHolder{

        public ProductDetailsViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}