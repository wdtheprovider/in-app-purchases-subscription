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

    List<ProductDetails> productDetailsList;
    Context context;
    RecycleViewInterface recycleViewInterface;

    public InAppPurchaseAdapter(Context context, List<ProductDetails> productDetailsList, RecycleViewInterface recycleViewInterface) {
        this.productDetailsList = productDetailsList;
        this.context = context;
        this.recycleViewInterface = recycleViewInterface;
    }

    @NonNull
    @Override
    public InAppPurchaseAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item,parent,false);
        return new InAppPurchaseAdapterViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull InAppPurchaseAdapterViewHolder holder, int position) {
        ProductDetails currentItem = productDetailsList.get(position);
        List<ProductDetails.SubscriptionOfferDetails> subDetails = currentItem.getSubscriptionOfferDetails();
        holder.product_name.setText(subDetails.get(0).getPricingPhases().getPricingPhaseList().get(0).getFormattedPrice()+" / "+currentItem.getName());
    }

    @Override
    public int getItemCount() {
        return productDetailsList.size();
    }

    public class InAppPurchaseAdapterViewHolder extends RecyclerView.ViewHolder{
        TextView product_name;

        public InAppPurchaseAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            product_name = itemView.findViewById(R.id.product_name);
            itemView.setOnClickListener(v -> recycleViewInterface.onItemClick(getAdapterPosition()));
        }
    }
}