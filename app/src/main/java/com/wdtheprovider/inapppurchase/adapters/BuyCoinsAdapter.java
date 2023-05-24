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

public class BuyCoinsAdapter extends RecyclerView.Adapter<BuyCoinsAdapter.BuyCoinsViewHolder> {

    //You can complete the Adapter or buy the complete one on:  for $2 only
    //https://dingi.icu/store/product/in-app-purchase-recyclerview-adapter-code/
    //Or Buy the full complete source code demo
    //https://dingi.icu/store/product/in-app-purchases-template-complete-source-code/

    public BuyCoinsAdapter(Context context, List<ProductDetails> productDetailsList, RecycleViewInterface recycleViewInterface) {

    }

    @NonNull
    @Override
    public BuyCoinsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BuyCoinsViewHolder(null);
    }

    @Override
    public void onBindViewHolder(@NonNull BuyCoinsViewHolder holder, int position) {}

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