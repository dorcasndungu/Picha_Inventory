package com.example.pichainventory.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pichainventory.R;
import com.example.pichainventory.Models.Upload;
import com.squareup.picasso.Picasso;

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
        holder.bind(upload);
    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class StockViewHolder extends RecyclerView.ViewHolder {
        private ImageView profilePhoto;
        private TextView nameTextView;
        private TextView sPtextView;

        public StockViewHolder(@NonNull View itemView) {
            super(itemView);

            profilePhoto = itemView.findViewById(R.id.profilePhoto);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            sPtextView = itemView.findViewById(R.id.sPtextView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAbsoluteAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && mListener != null) {
                        Upload upload = mUploads.get(position);
                        mListener.onItemClick(position, upload);
                    }
                }
            });
        }

        public void bind(Upload upload) {
            nameTextView.setText(upload.getmName());
            sPtextView.setText(String.valueOf(upload.getmSp()));

            Picasso.get()
                    .load(upload.getmImageUrl())
                    .placeholder(R.drawable.placeholder)
                    .fit()
                    .centerCrop()
                    .into(profilePhoto);
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
