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

    List<ProductDetails> productDetailsList;
    Context context;
    RecycleViewInterface recycleViewInterface;

    public BuyCoinsAdapter(Context context, List<ProductDetails> productDetailsList, RecycleViewInterface recycleViewInterface) {
        this.productDetailsList = productDetailsList;
        this.context = context;
        this.recycleViewInterface = recycleViewInterface;
    }

    @NonNull
    @Override
    public BuyCoinsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.buycoins_item,parent,false);
        return new BuyCoinsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BuyCoinsViewHolder holder, int position) {
        ProductDetails currentItem = productDetailsList.get(position);
        holder.txt_coins.setText(currentItem.getName());
        holder.txt_icon_price.setText(Objects.requireNonNull(currentItem.getOneTimePurchaseOfferDetails()).getFormattedPrice());
    }

    @Override
    public int getItemCount() {
        return productDetailsList.size();
    }

    public class BuyCoinsViewHolder extends RecyclerView.ViewHolder{
        TextView txt_coins, txt_icon_price;

        public BuyCoinsViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_coins = itemView.findViewById(R.id.product_name);
            txt_icon_price = itemView.findViewById(R.id.product_price);
            itemView.setOnClickListener(v -> recycleViewInterface.onItemClick(getAdapterPosition()));
        }

    }
}
