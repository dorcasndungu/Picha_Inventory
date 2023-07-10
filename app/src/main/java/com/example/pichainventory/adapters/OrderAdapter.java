package com.example.pichainventory.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pichainventory.Models.Order;
import com.example.pichainventory.R;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> mOrders;
    private List<Order> originalOrders;
    private Context mContext;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position, Order order);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public OrderAdapter(List<Order> orders, Context context) {
        mOrders = orders;
        mContext = context;
        this.originalOrders= new ArrayList<>(orders);
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = mOrders.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return mOrders.size();
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {
        private ImageView profilePhoto;
        private TextView nameTextView;
        private TextView sPtextView;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);

            profilePhoto = itemView.findViewById(R.id.profilePhoto);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            sPtextView = itemView.findViewById(R.id.sPtextView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAbsoluteAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && mListener != null) {
                        Order order = mOrders.get(position);
                        mListener.onItemClick(position, order);
                    }
                }
            });
        }

        public void bind(Order order) {
            nameTextView.setText(order.getmName());
            sPtextView.setText(order.getmDesc());

            Picasso.get()
                    .load(order.getmImageUrl())
                    .placeholder(R.drawable.placeholder)
                    .fit()
                    .centerCrop()
                    .into(profilePhoto);
        }
    }
    public void filterOrders(List<Order> filteredOrders) {
        mOrders.clear();
        mOrders.addAll(filteredOrders);
        notifyDataSetChanged();
    }
    public void resetData() {
        if (originalOrders != null) {
            this.mOrders = new ArrayList<>(originalOrders);
        } else {
            this.mOrders = new ArrayList<>();
        }
        notifyDataSetChanged();
    }
}
