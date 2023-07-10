package com.example.pichainventory;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

import com.example.pichainventory.databinding.ActivityStockBinding;

public class StockActivity extends AppCompatActivity {

    ActivityStockBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityStockBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }
}