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
import com.wdtheprovider.inapppurchase.models.ConsumableTransaction;

import java.util.List;

/**
 * File Created by Dingaan Letjane
 * 2023/05/13
 **/

public class CoinsTransactionAdapter extends RecyclerView.Adapter<CoinsTransactionAdapter.CoinsTransactionAdapterViewHolder> {

    //You can complete the Adapter or buy the complete one on:  for $2 only
    //https://dingi.icu/store/product/in-app-purchase-CoinsTransactionAdapter
    //
    //Or Buy the full complete source code demo
    //https://dingi.icu/store/product/in-app-purchases-template-complete-source-code/

    public CoinsTransactionAdapter(Context context, List<ConsumableTransaction> transactions, RecycleViewInterface recycleViewInterface) {

    }


    @NonNull
    @Override
    public CoinsTransactionAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CoinsTransactionAdapter.CoinsTransactionAdapterViewHolder(null);
    }

    @Override
    public void onBindViewHolder(@NonNull CoinsTransactionAdapterViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class CoinsTransactionAdapterViewHolder extends RecyclerView.ViewHolder {

        public CoinsTransactionAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
