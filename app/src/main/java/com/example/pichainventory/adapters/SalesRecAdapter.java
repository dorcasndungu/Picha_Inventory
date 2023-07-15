package com.example.pichainventory.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pichainventory.Models.Sale;
import com.example.pichainventory.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

public class SalesRecAdapter extends FirebaseRecyclerAdapter<Sale, SalesRecAdapter.ViewHolder> {
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(Sale sale);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public SalesRecAdapter(@NonNull FirebaseRecyclerOptions<Sale> options) {
        super(options);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Sale model) {
        holder.bind(model);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
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
                    if (mListener != null) {
                        mListener.onItemClick(getItem( getAbsoluteAdapterPosition()));
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
}
