package com.example.pichainventory.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.pichainventory.Models.Sale;
import com.example.pichainventory.R;

import java.util.ArrayList;
import java.util.List;

public class SalesRecAdapter extends RecyclerView.Adapter<SalesRecAdapter.ViewHolder> {
    private List<Sale> salesList;
    private List<Sale> originalList;
    private Context context;
    private OnItemClickListener listener;
    private int totalProfit = 0;
    private int totalSp = 0;

    public interface OnItemClickListener {
        void onItemClick(int position, Sale sale);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public SalesRecAdapter(List<Sale> salesList, Context context) {
        this.salesList = salesList;
        this.originalList = new ArrayList<>(salesList);
        this.context = context;
        calculateTotals();
    }

    private void calculateTotals() {
        totalProfit = 0;
        totalSp = 0;
        for (Sale sale : salesList) {
            totalProfit += sale.getmProfit();
            totalSp += sale.getmSp();
        }
    }

    public int getTotalProfit() {
        return totalProfit;
    }

    public int getTotalSp() {
        return totalSp;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sales_rec, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Sale sale = salesList.get(position);
        
        // Set item name
        holder.itemLabel.setText(sale.getmName());
        
        // Set price with currency symbol
        holder.SellingPEditText.setText("Ksh " + sale.getmSp());
        
        // Set units with stock status
        String unitsText = sale.getmUnits() + " units";
        holder.UnitEditText.setText(unitsText);
        holder.UnitEditText.setActivated(sale.getmUnits() == 0); // This will trigger the red border
        
        // Set additional info if available
        if (sale.getmAddInfo() != null && !sale.getmAddInfo().isEmpty()) {
            holder.AdditionalEdiText.setText(sale.getmAddInfo());
            holder.AdditionalEdiText.setVisibility(View.VISIBLE);
        } else {
            holder.AdditionalEdiText.setVisibility(View.GONE);
        }
        
        // Load image with placeholder and error handling
        if (sale.getmImageUrl() != null && !sale.getmImageUrl().isEmpty()) {
            Glide.with(context)
                .load(sale.getmImageUrl())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.profilePhoto);
        } else {
            holder.profilePhoto.setImageResource(R.drawable.placeholder);
        }

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(position, sale);
            }
        });
    }

    @Override
    public int getItemCount() {
        return salesList.size();
    }

    public void filterSales(List<Sale> filteredList) {
        salesList = filteredList;
        notifyDataSetChanged();
    }

    public void resetData() {
        salesList = new ArrayList<>(originalList);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profilePhoto;
        TextView itemLabel;
        TextView SellingPEditText;
        TextView UnitEditText;
        TextView AdditionalEdiText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePhoto = itemView.findViewById(R.id.profilePhoto);
            itemLabel = itemView.findViewById(R.id.itemLabel);
            SellingPEditText = itemView.findViewById(R.id.SellingPEditText);
            UnitEditText = itemView.findViewById(R.id.UnitEditText);
            AdditionalEdiText = itemView.findViewById(R.id.AdditionalEdiText);
        }
    }
}
