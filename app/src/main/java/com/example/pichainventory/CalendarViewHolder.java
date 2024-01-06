//package com.example.pichainventory;
//
//import android.view.View;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//public class CalendarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//    TextView dayOfMonth;
//    View parentView;
//    OnItemListener onItemListener;
//
//    public CalendarViewHolder(@NonNull View itemView, OnItemListener onItemListener) {
//        super(itemView);
//        dayOfMonth = itemView.findViewById(R.id.dayOfMonth);
//        parentView = itemView.findViewById(R.id.parentView);
//        this.onItemListener = onItemListener;
//        parentView.setOnClickListener(this);
//    }
//
//    @Override
//    public void onClick(View view) {
//        int position = getAdapterPosition();
//        if (position != RecyclerView.NO_POSITION && onItemListener != null) {
//            LocalDate date = days.get(position);
//            onItemListener.onItemClick(position, date);
//        }
//    }
//}
