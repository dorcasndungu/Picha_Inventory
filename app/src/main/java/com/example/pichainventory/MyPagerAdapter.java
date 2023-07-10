package com.example.pichainventory;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.pichainventory.ui_fragments.DisplayBoxFragment;
import com.example.pichainventory.ui_fragments.OrdRecFragment;
import com.example.pichainventory.ui_fragments.SalesRecFragment;
import com.example.pichainventory.ui_fragments.StcOrdFragment;
import com.example.pichainventory.ui_fragments.StcSelFragment;
import com.example.pichainventory.ui_fragments.stockRecFragment;

import java.util.ArrayList;
import java.util.List;

//Pager adapter for swipe screens
public class MyPagerAdapter extends FragmentStateAdapter {

    private final List<SearchableFragment> fragments = new ArrayList<>();
    public MyPagerAdapter(Fragment fragment) {
        super(fragment);
        fragments.add(new stockRecFragment());
        fragments.add(new SalesRecFragment());
        fragments.add(new OrdRecFragment());
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return (Fragment) fragments.get(position);
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public void performSearch(String query, int position) {
        fragments.get(position).performSearch(query);
        }
    public void resetData(int position) {
        fragments.get(position).resetData();
    }
    }


