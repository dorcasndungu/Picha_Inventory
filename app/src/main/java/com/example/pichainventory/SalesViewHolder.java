package com.example.pichainventory;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class SalesViewHolder extends RecyclerView.ViewHolder {
    TextView nameTextView;
    TextView sPtextView;
    TextView profilePhoto;

    public SalesViewHolder(View itemView) {
        super(itemView);
        nameTextView = itemView.findViewById(R.id.nameTextView);
        sPtextView = itemView.findViewById(R.id.sPtextView);
        profilePhoto = itemView.findViewById(R.id.profilePhoto);
    }
}