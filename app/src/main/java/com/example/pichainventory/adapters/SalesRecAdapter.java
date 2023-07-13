package com.example.pichainventory.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pichainventory.Models.Order;
import com.example.pichainventory.Models.Sale;
import com.example.pichainventory.R;
import com.example.pichainventory.Models.Sale;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SalesRecAdapter extends RecyclerView.Adapter<SalesRecAdapter.ViewHolder> {

    private static List<Sale> salesList;
    private List<Sale> originalSales;
    private Context mContext;
    private int totalProfit = 0;
    private int totalSp= 0;
    private static SalesRecAdapter.OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position, Sale sale);
    }

    public void setOnItemClickListener(SalesRecAdapter.OnItemClickListener listener) {
        mListener = listener;
    }
    public SalesRecAdapter(List<Sale> sales, Context context) {
        salesList = sales;
        mContext = context;
        this.originalSales = new ArrayList<>(sales);

        // Calculate total profit and total SP
        for (Sale sale : salesList) {
            totalProfit += sale.getmProfit();
            totalSp += sale.getmSp();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Sale sale = salesList.get(position);
        holder.bind(sale);

    }
    public int getTotalProfit() {
        return totalProfit;
    }
    public int getTotalSp() {
        return totalSp;
    }

    @Override
    public int getItemCount() {
        return salesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView sPtextView;
        ShapeableImageView profilePhoto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            sPtextView = itemView.findViewById(R.id.sPtextView);
            profilePhoto = itemView.findViewById(R.id.profilePhoto);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAbsoluteAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && mListener != null) {
                        Sale sale = salesList.get(position);
                        mListener.onItemClick(position, sale);
                    }
                }
            });

        }
        public void bind(Sale sale) {
            nameTextView.setText(sale.getmName());
            sPtextView.setText(String.valueOf(sale.getmProfit()));

            Picasso.get()
                    .load(sale.getmImageUrl())
                    .placeholder(R.drawable.placeholder)
                    .fit()
                    .centerCrop()
                    .into(profilePhoto);
        }
    }

    public void filterSales(List<Sale> filteredSales) {
        salesList.clear();
        salesList.addAll(filteredSales);
        notifyDataSetChanged();
    }
    public void resetData() {
        if (originalSales != null) {
            this.salesList = new ArrayList<>(originalSales);
        } else {
            this.salesList = new ArrayList<>();
        }
        notifyDataSetChanged();
    }
}

