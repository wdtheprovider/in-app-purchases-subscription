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

public class BuyCodeAdapter extends RecyclerView.Adapter<BuyCodeAdapter.BuyCoinsViewHolder> {

    //Please note that you do not need this Adapter

    public BuyCodeAdapter(Context context, List<ProductDetails> productDetailsList, RecycleViewInterface recycleViewInterface) {

    }

    @NonNull
    @Override
    public BuyCoinsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = null;

        return new BuyCoinsViewHolder(view);
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