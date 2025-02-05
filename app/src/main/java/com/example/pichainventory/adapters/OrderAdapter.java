package com.example.pichainventory.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pichainventory.Models.Order;
import com.example.pichainventory.R;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> mOrders;
    private List<Order> originalOrders;
    private Context mContext;
    private OnItemClickListener mListener;
    private String uid; // Add a field for UID

    public interface OnItemClickListener {
        void onItemClick(int position, Order order);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public OrderAdapter(List<Order> orders, Context context, String uid) { // Add UID to the constructor
        mOrders = orders;
        mContext = context;
        this.uid = uid; // Initialize the UID
        this.originalOrders = new ArrayList<>(orders);
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_order, parent, false);
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
        private TextView dayCountTextView;
        private RadioGroup radioGroup;
        private RadioButton radioButtonRed;
        private RadioButton radioButtonAmber;
        private RadioButton radioButtonGreen;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);

            profilePhoto = itemView.findViewById(R.id.profilePhoto);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            sPtextView = itemView.findViewById(R.id.sPtextView);
            dayCountTextView = itemView.findViewById(R.id.textView2);
            radioGroup = itemView.findViewById(R.id.radioGroup);
            radioButtonRed = itemView.findViewById(R.id.radioButtonRed);
            radioButtonAmber = itemView.findViewById(R.id.radioButtonAmber);
            radioButtonGreen = itemView.findViewById(R.id.radioButtonGreen);

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
            dayCountTextView.setText(String.format("%d days ago", order.getmDayCount()));

            Picasso.get()
                    .load(order.getmImageUrl())
                    .placeholder(R.drawable.placeholder)
                    .fit()
                    .centerCrop()
                    .into(profilePhoto);

            if (order.getmStatus() == null || order.getmStatus().isEmpty()) {
                order.setmStatus("Red");
            }

            switch (order.getmStatus()) {
                case "Red":
                    radioButtonRed.setChecked(true);
                    break;
                case "Amber":
                    radioButtonAmber.setChecked(true);
                    break;
                case "Green":
                    radioButtonGreen.setChecked(true);
                    break;
            }

            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId) {
                        case R.id.radioButtonRed:
                            order.setmStatus("Red");
                            break;
                        case R.id.radioButtonAmber:
                            order.setmStatus("Amber");
                            break;
                        case R.id.radioButtonGreen:
                            order.setmStatus("Green");
                            break;
                    }
                    // Update status in database
                    FirebaseDatabase.getInstance().getReference(uid)
                            .child("Orders")
                            .child(order.getmCategory())
                            .child(order.getmKey())
                            .child("mStatus")
                            .setValue(order.getmStatus());
                }
            });
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
