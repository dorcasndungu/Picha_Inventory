package com.example.pichainventory.ui_fragments;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
        setHasOptionsMenu(true);
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
//        add.setOnClickListener(new  View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                UploadFragment uploadFragment = new UploadFragment();
//                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.replace(R.id.fragmentContainerView, uploadFragment);
//                fragmentTransaction.addToBackStack(null);
//                fragmentTransaction.commit();
//            }});
        return rootView;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Perform search logic when the user submits the query
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Perform search logic as the user types (optional)
                if (newText.isEmpty()) {
                    resetData(); // reset to the original list if search query is empty
                } else {
                    performSearch(newText);
                }
                return true;
            }
        });
    }
    private void resetData() {
        pagerAdapter.resetData(binding.viewPager.getCurrentItem());
    }
    private void performSearch(String query) {
        pagerAdapter.performSearch(query, binding.viewPager.getCurrentItem());
    }
}