package com.example.pichainventory.ui_fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.pichainventory.MyPagerAdapter;
import com.example.pichainventory.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.pichainventory.databinding.FragmentDisplayBoxBinding;
import com.google.android.material.tabs.TabLayoutMediator;

public class DisplayBoxFragment extends Fragment {
    private ViewPager2 viewPager;
    FragmentDisplayBoxBinding binding;
    private MyPagerAdapter pagerAdapter;

    public DisplayBoxFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDisplayBoxBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();
        FloatingActionButton add = rootView.findViewById(R.id.addButton);
        
        // Inflate the layout for this fragment
        pagerAdapter = new MyPagerAdapter(this);
        binding.viewPager.setAdapter(pagerAdapter);
        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> {
            // Set the tab titles
            switch (position) {
                case 0:
                    tab.setText("Stock");
                    break;
                case 1:
                    tab.setText("Sales");
                    break;
                case 2:
                    tab.setText("Orders");
                    break;
            }
        }).attach();

        return rootView;
    }

    private void resetData() {
        pagerAdapter.resetData(binding.viewPager.getCurrentItem());
    }

    private void performSearch(String query) {
        pagerAdapter.performSearch(query, binding.viewPager.getCurrentItem());
    }
}