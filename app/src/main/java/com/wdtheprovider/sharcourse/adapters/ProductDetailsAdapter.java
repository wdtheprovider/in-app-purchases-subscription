package com.wdtheprovider.sharcourse.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.ProductDetails;
import com.wdtheprovider.sharcourse.interfaces.RecycleViewInterface;
import com.wdtheprovider.sharcourse.R;
import java.util.List;

public class ProductDetailsAdapter extends RecyclerView.Adapter<ProductDetailsAdapter.ProductDetailsViewHolder>  {

    List<ProductDetails> productDetailsList;
    Context context;
    RecycleViewInterface recycleViewInterface;
    String TAG = "TestINAPP";


    public ProductDetailsAdapter(Context context, List<ProductDetails> productDetailsList, RecycleViewInterface recycleViewInterface) {
        this.productDetailsList = productDetailsList;
        this.context = context;
        this.recycleViewInterface = recycleViewInterface;
        Log.d(TAG, "constructor " + productDetailsList.size());
    }

    @NonNull
    @Override
    public ProductDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item,parent,false);
        return new ProductDetailsViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ProductDetailsViewHolder holder, int position) {
        ProductDetails currentItem = productDetailsList.get(position);
        List<ProductDetails.SubscriptionOfferDetails> subDetails = currentItem.getSubscriptionOfferDetails();

        assert subDetails != null;
        holder.product_name.setText(subDetails.get(0).getPricingPhases().getPricingPhaseList().get(0).getFormattedPrice()+"/"+currentItem.getName());
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "Returned size " + productDetailsList.size());
        return productDetailsList.size();
    }


    public class ProductDetailsViewHolder extends RecyclerView.ViewHolder{
        TextView product_name;

        public ProductDetailsViewHolder(@NonNull View itemView) {
            super(itemView);
            product_name = itemView.findViewById(R.id.product_name);
            itemView.setOnClickListener(v -> recycleViewInterface.onItemClick(getAdapterPosition()));
        }
    }
}
