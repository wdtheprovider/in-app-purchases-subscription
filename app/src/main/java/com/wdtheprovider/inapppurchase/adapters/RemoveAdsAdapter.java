package com.wdtheprovider.inapppurchase.adapters;

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
import java.util.Objects;

public class RemoveAdsAdapter extends RecyclerView.Adapter<RemoveAdsAdapter.BuyCoinsViewHolder> {

    //You can complete the Adapter or buy the complete one on:  for $2 only
    //https://dingi.icu/store/product/in-app-purchase-recyclerview-remove-ads-adapter-code/
    //Or Buy the full complete source code demo
    //https://dingi.icu/store/product/in-app-purchases-template-complete-source-code/

    public RemoveAdsAdapter(Context context, List<ProductDetails> productDetailsList, RecycleViewInterface recycleViewInterface) {
    }

    @NonNull
    @Override
    public BuyCoinsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.remove_ads_item,parent,false);
        return new BuyCoinsViewHolder(null);
    }

    @Override
    public void onBindViewHolder(@NonNull BuyCoinsViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class BuyCoinsViewHolder extends RecyclerView.ViewHolder{

        public BuyCoinsViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}