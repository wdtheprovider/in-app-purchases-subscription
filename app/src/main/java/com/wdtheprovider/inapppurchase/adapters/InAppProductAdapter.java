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

public class InAppProductAdapter extends RecyclerView.Adapter<InAppProductAdapter.InAppProductViewHolder> {


    //You can complete the Adapter or buy the complete one on:  for $2 only
    //https://dingi.icu/store/product/in-app-purchase-recyclerview-productdetails-adapter-code/
    //Or Buy the full complete source code demo
    //https://dingi.icu/store/product/in-app-purchases-template-complete-source-code/

    public InAppProductAdapter(Context context, List<ProductDetails> productDetailsList, RecycleViewInterface recycleViewInterface) {

    }

    @NonNull
    @Override
    public InAppProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new InAppProductViewHolder(null);
    }

    @Override
    public void onBindViewHolder(@NonNull InAppProductViewHolder holder, int position) {
          }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class InAppProductViewHolder extends RecyclerView.ViewHolder{

        public InAppProductViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}