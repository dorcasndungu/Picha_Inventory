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
import com.example.pichainventory.R;
import com.example.pichainventory.Models.Upload;

import java.util.ArrayList;
import java.util.List;

public class StockAdapter extends RecyclerView.Adapter<StockAdapter.StockViewHolder> {

    private List<Upload> mUploads;
    private List<Upload> originalUploads;
    private Context mContext;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position, Upload upload);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public StockAdapter(List<Upload> uploads, Context context) {
        mUploads = uploads;
        mContext = context;
        this.originalUploads = new ArrayList<>(uploads);
    }

    @NonNull
    @Override
    public StockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item, parent, false);
        return new StockViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StockViewHolder holder, int position) {
        Upload upload = mUploads.get(position);
        
        // Set item name
        holder.itemLabel.setText(upload.getmName());
        
        // Set price
        holder.SellingPEditText.setText(String.valueOf(upload.getmSp()));
        
        // Set units with stock status
        holder.UnitEditText.setText(String.valueOf(upload.getmUnits()));
        holder.UnitEditText.setActivated(upload.getmUnits() == 0); // This will trigger the red border
        
        // Load image with placeholder and error handling
        if (upload.getmImageUrl() != null && !upload.getmImageUrl().isEmpty()) {
            Glide.with(mContext)
                .load(upload.getmImageUrl())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.profilePhoto);
        } else {
            // If no image URL is provided, show placeholder
            holder.profilePhoto.setImageResource(R.drawable.placeholder);
        }

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onItemClick(position, upload);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class StockViewHolder extends RecyclerView.ViewHolder {
        ImageView profilePhoto;
        TextView itemLabel;
        TextView SellingPEditText;
        TextView UnitEditText;

        public StockViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePhoto = itemView.findViewById(R.id.profilePhoto);
            itemLabel = itemView.findViewById(R.id.itemLabel);
            SellingPEditText = itemView.findViewById(R.id.SellingPEditText);
            UnitEditText = itemView.findViewById(R.id.UnitEditText);
        }
    }

    public void setData(List<Upload> uploads) {
        this.originalUploads = new ArrayList<>(uploads);
        this.mUploads = uploads;
        notifyDataSetChanged();
    }

    public void filterUploads(List<Upload> filteredUploads) {
        mUploads.clear();
        mUploads.addAll(filteredUploads);
        notifyDataSetChanged();
    }

    public void resetData() {
        if (originalUploads != null) {
            this.mUploads = new ArrayList<>(originalUploads);
        } else {
            this.mUploads = new ArrayList<>();
        }
        notifyDataSetChanged();
    }
}
