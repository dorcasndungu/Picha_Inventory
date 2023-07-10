package com.example.pichainventory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class CategoryAdapter extends ArrayAdapter<String> {
    private LayoutInflater inflater;

    public CategoryAdapter(Context context, List<String> categories) {
        super(context, 0, categories);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(android.R.layout.simple_spinner_item, parent, false);
        }

        String category = getItem(position);

        TextView categoryTextView = convertView.findViewById(android.R.id.text1);
        categoryTextView.setText(category);

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        }

        String category = getItem(position);

        TextView categoryTextView = convertView.findViewById(android.R.id.text1);
        categoryTextView.setText(category);

        return convertView;
    }
}
