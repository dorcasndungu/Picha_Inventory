package com.example.pichainventory.ui_fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pichainventory.Models.Sale;
import com.example.pichainventory.Models.Sale;
import com.example.pichainventory.Models.Sale;
import com.example.pichainventory.R;
import com.example.pichainventory.Models.Sale;
import com.example.pichainventory.SearchableFragment;
import com.example.pichainventory.StockActivity;
import com.example.pichainventory.adapters.OrderAdapter;
import com.example.pichainventory.adapters.SalesRecAdapter;
import com.example.pichainventory.adapters.SalesRecAdapter;
import com.example.pichainventory.databinding.FragmentSalesRecBinding;
import com.example.pichainventory.ui.LoginActivity;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SalesRecFragment extends Fragment implements SalesRecAdapter.OnItemClickListener, SearchableFragment {

    private FragmentSalesRecBinding binding;
    private DatabaseReference tDatabaseRef;
    private DatabaseReference bDatabaseRef;
    private DatabaseReference shDatabaseRef;
    private DatabaseReference btDatabaseRef;
    private DatabaseReference clDatabaseRef;
    private DatabaseReference decDatabaseRef;
    private DatabaseReference othDatabaseRef;
    private DatabaseReference fDatabaseRef;
    private SalesRecAdapter tAdapter;
    private SalesRecAdapter bAdapter;
    private SalesRecAdapter shAdapter;
    private SalesRecAdapter btAdapter;
    private SalesRecAdapter clAdapter;
    private SalesRecAdapter decAdapter;
    private SalesRecAdapter othAdapter;
    private SalesRecAdapter fAdapter;
    private List<Sale> tSales;
    private List<Sale> bSales;
    private List<Sale> shSales;
    private List<Sale> btSales;
    private List<Sale> clSales;
    private List<Sale> decSales;
    private List<Sale> othSales;
    private List<Sale> fSales;
    private Map<String, Boolean> sectionVisibilityMap;
    private Map<String, ImageView> sectionArrowIconMap;
    private Map<String, RecyclerView> sectionRecyclerViewMap;

    String mDate;
    //there was an error with the following vars before
    int toySales;
    int toyProfit;
    int toySp;
    int flowerSales;
    int flowerProfit;
    int flowerSp;
    int beddingSales;
    int beddingProfit;
    int beddingSp;
    int shoesSales;
    int shoesProfit;
    int shoesSp;
    int beautySales;
    int beautyProfit;
    int beautySp;
    int clotheSales;
    int clothesProfit;
    int clothesSp;
    int decorSales;
    int decorProfit;
    int decorSp;
    int otherSales;
    int otherProfit;
    int otherSp;
    public int totalSales = 0;
    public int totalProfit = 0;
    public int totalSp = 0;
    private String searchQuery = "";
    String uid;
    private String highestProfitCategory = "";
    private String highestSalesCategory = "";
    public SalesRecFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        uid = user.getUid();
        mDate = currentDate;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSalesRecBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        // Initialize maps
        sectionVisibilityMap = new HashMap<>();
        sectionArrowIconMap = new HashMap<>();
        sectionRecyclerViewMap = new HashMap<>();

        // Set up sections
        setupSection("flowers", R.id.flowersRecycler, R.id.arrowIcon, rootView);
        setupSection("toys", R.id.toysRecycler, R.id.TarrowIcon, rootView);
        setupSection("decoration", R.id.decorRecycler, R.id.DarrowIcon, rootView);
        setupSection("other", R.id.otherRecycler, R.id.OarrowIcon, rootView);
        setupSection("bedding", R.id.beddingRecycler, R.id.BarrowIcon, rootView);
        setupSection("shoes", R.id.shoesRecycler, R.id.SarrowIcon, rootView);
        setupSection("beauty", R.id.beautyRecycler, R.id.BtarrowIcon, rootView);
        setupSection("clothes", R.id.clothesRecycler, R.id.CarrowIcon, rootView);

        // Update database references to match the current structure
        DatabaseReference salesRef = FirebaseDatabase.getInstance().getReference(uid).child("sales").child(mDate);
        
        // Set up listeners for each category
        setupCategoryListener(salesRef, "Toys", "tSales", "tAdapter", binding.toysRecycler);
        setupCategoryListener(salesRef, "Flowers", "fSales", "fAdapter", binding.flowersRecycler);
        setupCategoryListener(salesRef, "Bedding", "bSales", "bAdapter", binding.beddingRecycler);
        setupCategoryListener(salesRef, "Shoes", "shSales", "shAdapter", binding.shoesRecycler);
        setupCategoryListener(salesRef, "Beauty", "btSales", "btAdapter", binding.beautyRecycler);
        setupCategoryListener(salesRef, "Clothes", "clSales", "clAdapter", binding.clothesRecycler);
        setupCategoryListener(salesRef, "Decor", "decSales", "decAdapter", binding.decorRecycler);
        setupCategoryListener(salesRef, "Other", "othSales", "othAdapter", binding.otherRecycler);

        return rootView;
    }

    private void setupCategoryListener(DatabaseReference salesRef, String category, String salesListName, String adapterName, RecyclerView recyclerView) {
        salesRef.child(category).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Sale> salesList = new ArrayList<>();
                for (DataSnapshot timeSnapshot : dataSnapshot.getChildren()) {
                    Sale sale = timeSnapshot.getValue(Sale.class);
                    if (sale != null) {
                        salesList.add(sale);
                    }
                }
                
                // Update the appropriate sales list and adapter
                switch (salesListName) {
                    case "tSales":
                        tSales = salesList;
                        toySales = salesList.size();
                        tAdapter = new SalesRecAdapter(tSales, getContext());
                        tAdapter.setOnItemClickListener(SalesRecFragment.this);
                        toyProfit = tAdapter.getTotalProfit();
                        toySp = tAdapter.getTotalSp();
                        recyclerView.setAdapter(tAdapter);
                        break;
                    case "fSales":
                        fSales = salesList;
                        flowerSales = salesList.size();
                        fAdapter = new SalesRecAdapter(fSales, getContext());
                        fAdapter.setOnItemClickListener(SalesRecFragment.this);
                        flowerProfit = fAdapter.getTotalProfit();
                        flowerSp = fAdapter.getTotalSp();
                        recyclerView.setAdapter(fAdapter);
                        break;
                    case "bSales":
                        bSales = salesList;
                        beddingSales = salesList.size();
                        bAdapter = new SalesRecAdapter(bSales, getContext());
                        bAdapter.setOnItemClickListener(SalesRecFragment.this);
                        beddingProfit = bAdapter.getTotalProfit();
                        beddingSp = bAdapter.getTotalSp();
                        recyclerView.setAdapter(bAdapter);
                        break;
                    case "shSales":
                        shSales = salesList;
                        shoesSales = salesList.size();
                        shAdapter = new SalesRecAdapter(shSales, getContext());
                        shAdapter.setOnItemClickListener(SalesRecFragment.this);
                        shoesProfit = shAdapter.getTotalProfit();
                        shoesSp = shAdapter.getTotalSp();
                        recyclerView.setAdapter(shAdapter);
                        break;
                    case "btSales":
                        btSales = salesList;
                        beautySales = salesList.size();
                        btAdapter = new SalesRecAdapter(btSales, getContext());
                        btAdapter.setOnItemClickListener(SalesRecFragment.this);
                        beautyProfit = btAdapter.getTotalProfit();
                        beautySp = btAdapter.getTotalSp();
                        recyclerView.setAdapter(btAdapter);
                        break;
                    case "clSales":
                        clSales = salesList;
                        clotheSales = salesList.size();
                        clAdapter = new SalesRecAdapter(clSales, getContext());
                        clAdapter.setOnItemClickListener(SalesRecFragment.this);
                        clothesProfit = clAdapter.getTotalProfit();
                        clothesSp = clAdapter.getTotalSp();
                        recyclerView.setAdapter(clAdapter);
                        break;
                    case "decSales":
                        decSales = salesList;
                        decorSales = salesList.size();
                        decAdapter = new SalesRecAdapter(decSales, getContext());
                        decAdapter.setOnItemClickListener(SalesRecFragment.this);
                        decorProfit = decAdapter.getTotalProfit();
                        decorSp = decAdapter.getTotalSp();
                        recyclerView.setAdapter(decAdapter);
                        break;
                    case "othSales":
                        othSales = salesList;
                        otherSales = salesList.size();
                        othAdapter = new SalesRecAdapter(othSales, getContext());
                        othAdapter.setOnItemClickListener(SalesRecFragment.this);
                        otherProfit = othAdapter.getTotalProfit();
                        otherSp = othAdapter.getTotalSp();
                        recyclerView.setAdapter(othAdapter);
                        break;
                }
                
                // Calculate totals
                totalSales = toySales + flowerSales + beddingSales + shoesSales + beautySales + clotheSales + decorSales + otherSales;
                totalProfit = toyProfit + flowerProfit + beddingProfit + shoesProfit + beautyProfit + clothesProfit + decorProfit + otherProfit;
                totalSp = toySp + flowerSp + beddingSp + shoesSp + beautySp + clothesSp + decorSp + otherSp;
                
                // Update the display
                calculateHighestProfitAndMostSoldCategories();
                updateTotalDisplay();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Add this method to calculate and update the highest profit and most sold categories
    private void calculateHighestProfitAndMostSoldCategories() {
        Map<String, Integer> categoryProfitMap = new HashMap<>();
        Map<String, Integer> categorySalesMap = new HashMap<>();

        // Calculate profit and sales for each category
        categoryProfitMap.put("Toys", toyProfit);
        categoryProfitMap.put("Flowers", flowerProfit);
        categoryProfitMap.put("Bedding", beddingProfit);
        categoryProfitMap.put("Shoes", shoesProfit);
        categoryProfitMap.put("Beauty", beautyProfit);
        categoryProfitMap.put("Clothes", clothesProfit);
        categoryProfitMap.put("Decor", decorProfit);
        categoryProfitMap.put("Other", otherProfit);

        categorySalesMap.put("Toys", toySales);
        categorySalesMap.put("Flowers", flowerSales);
        categorySalesMap.put("Bedding", beddingSales);
        categorySalesMap.put("Shoes", shoesSales);
        categorySalesMap.put("Beauty", beautySales);
        categorySalesMap.put("Clothes", clotheSales);
        categorySalesMap.put("Decor", decorSales);
        categorySalesMap.put("Other", otherSales);

        // Find the category with the highest profit
        int maxProfit = Integer.MIN_VALUE;
        highestProfitCategory = "";
        for (Map.Entry<String, Integer> entry : categoryProfitMap.entrySet()) {
            if (entry.getValue() > maxProfit) {
                maxProfit = entry.getValue();
                highestProfitCategory = entry.getKey();
            }
        }

        // Find the category with the most sold items
        int maxSales = Integer.MIN_VALUE;
        highestSalesCategory = "";
        for (Map.Entry<String, Integer> entry : categorySalesMap.entrySet()) {
            if (entry.getValue() > maxSales) {
                maxSales = entry.getValue();
                highestSalesCategory = entry.getKey();
            }
        }

        // Update the UI with the highest profit and most sold categories
        if (isAdded()) {
            if (highestProfitCategory.isEmpty()) {
                binding.highestPrft.setText("No sales yet");
            } else {
                binding.highestPrft.setText("Most Profit: " + highestProfitCategory);
            }

            if (highestSalesCategory.isEmpty()) {
                binding.highestSel.setText("No sales yet");
            } else {
                binding.highestSel.setText("Most Sold: " + highestSalesCategory);
            }
        }
    }

    private void setupSection(final String sectionName, int recyclerViewId, int arrowIconId, View rootView) {
        ImageView arrowIcon = rootView.findViewById(arrowIconId);
        RecyclerView recyclerView = rootView.findViewById(recyclerViewId);

        sectionVisibilityMap.put(sectionName, true);
        sectionArrowIconMap.put(sectionName, arrowIcon);
        sectionRecyclerViewMap.put(sectionName, recyclerView);

        arrowIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSection(sectionName);
            }
        });
        // Add the ImageView and RecyclerView pairs to the maps
        tSales = new ArrayList<>();
        fSales = new ArrayList<>();
        bSales = new ArrayList<>();
        shSales = new ArrayList<>();
        btSales = new ArrayList<>();
        clSales = new ArrayList<>();
        decSales = new ArrayList<>();
        othSales = new ArrayList<>();

        // Set up the RecyclerView
        int numberOfColumns = 3; // Set the desired number of columns in the grid
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), numberOfColumns);
        GridLayoutManager fLayoutManager = new GridLayoutManager(getContext(), numberOfColumns);
        GridLayoutManager bLayoutManager = new GridLayoutManager(getContext(), numberOfColumns);
        GridLayoutManager shLayoutManager = new GridLayoutManager(getContext(), numberOfColumns);
        GridLayoutManager btLayoutManager = new GridLayoutManager(getContext(), numberOfColumns);
        GridLayoutManager clLayoutManager = new GridLayoutManager(getContext(), numberOfColumns);
        GridLayoutManager decLayoutManager = new GridLayoutManager(getContext(), numberOfColumns);
        GridLayoutManager othLayoutManager = new GridLayoutManager(getContext(), numberOfColumns);

        binding.flowersRecycler.setLayoutManager(fLayoutManager);
        binding.beddingRecycler.setLayoutManager(bLayoutManager);
        binding.toysRecycler.setLayoutManager(layoutManager);
        binding.shoesRecycler.setLayoutManager(shLayoutManager);
        binding.beautyRecycler.setLayoutManager(btLayoutManager);
        binding.clothesRecycler.setLayoutManager(clLayoutManager);
        binding.decorRecycler.setLayoutManager(decLayoutManager);
        binding.otherRecycler.setLayoutManager(othLayoutManager);

        // Show the progress bar and waiting message
        binding.progressBar.setVisibility(View.VISIBLE);

        // Simulate a delay or perform your background task
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // Hide the progress bar
                binding.progressBar.setVisibility(View.GONE);
            }
        }, 1000);
        calculateHighestProfitAndMostSoldCategories();
    }

    private void updateTotalDisplay() {
        if (!isAdded()) return;
        
        try {
            // Update the date display
            binding.textViewDate.setText("Date: " + mDate);

            // Update the sales, profit, and total displays
            binding.salesTextView.setText(String.valueOf(totalSales));
            binding.textViewProfit.setText(String.valueOf(totalProfit));
            binding.textViewTotal.setText(String.valueOf(totalSp));

            // Update the best performing category
            if (highestProfitCategory != null && !highestProfitCategory.isEmpty()) {
                binding.highestPrft.setText("Most Profit: " + highestProfitCategory);
            } else {
                binding.highestPrft.setText("No sales yet");
            }

            if (highestSalesCategory != null && !highestSalesCategory.isEmpty()) {
                binding.highestSel.setText("Most Sold: " + highestSalesCategory);
            } else {
                binding.highestSel.setText("No sales yet");
            }
        } catch (Exception e) {
            Log.e("SalesRecFragment", "Error updating display", e);
        }
    }


    private void toggleSection(String sectionName) {
        boolean isSectionVisible = sectionVisibilityMap.get(sectionName);
        ImageView arrowIcon = sectionArrowIconMap.get(sectionName);
        RecyclerView recyclerView = sectionRecyclerViewMap.get(sectionName);

        if (isSectionVisible) {
            recyclerView.setVisibility(View.GONE);
            arrowIcon.setImageResource(R.drawable.ic_arrow_down);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            arrowIcon.setImageResource(R.drawable.ic_arrow_up);
        }

        sectionVisibilityMap.put(sectionName, !isSectionVisible);
    }

    private void showDialogWithItems(Bundle bundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.saledialog, null);
        builder.setView(dialogView);

        // Find views in the dialog layout
        ImageView displayPhoto = dialogView.findViewById(R.id.profilePhoto);
        TextView itemLabel = dialogView.findViewById(R.id.itemLabel);
        TextView sellingPEditText = dialogView.findViewById(R.id.SellingPEditText);
        TextView additionalEdiText = dialogView.findViewById(R.id.AdditionalEdiText);
        TextView UnitEditText = dialogView.findViewById(R.id.UnitEditText);
        Button backButton= dialogView.findViewById(R.id.backBtn);

        // Retrieve data from the Bundle
        String itemName = bundle.getString("itemName");
        String imageUrl = bundle.getString("imageUrl");
        String sellingPrice = bundle.getString("sellingPrice");
        String additional = bundle.getString("additional");
        String mCategory = bundle.getString("mCategory");
        String units = bundle.getString("mUnits");

        // Set values to the views
        itemLabel.setText(itemName);
        sellingPEditText.setText(sellingPrice);
        additionalEdiText.setText(additional);
        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.placeholder) // Placeholder image until the actual image is loaded
                .fit()
                .centerCrop()
                .into(displayPhoto);


        UnitEditText.setText(units);

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close the dialog
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onItemClick(int position, Sale sale) {
        Bundle bundle = new Bundle();
        bundle.putString("itemName", sale.getmName());
        bundle.putString("imageUrl", sale.getmImageUrl());
        bundle.putString("sellingPrice", String.valueOf(sale.getmSp()));
        bundle.putString("additional", sale.getmAddInfo());
        bundle.putString("mCategory", sale.getmCategory());
        bundle.putString("mUnits", String.valueOf(sale.getmUnits()));
        showDialogWithItems(bundle);
    }

    @Override
    public void performSearch(String query) {
        try {
            if (query == null || query.trim().isEmpty()) {
                // Show a message to the user indicating that the search query is empty
                Toast.makeText(getContext(), "Please enter a valid search query.", Toast.LENGTH_SHORT).show();
                return; // Exit the method to prevent further processing
            }

            searchQuery = query;
            filterItems();
        } catch (Exception e) {
            e.printStackTrace();
            // Handle the exception gracefully, e.g., show an error message to the user
            Toast.makeText(getContext(), "An error occurred during the search.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void resetData() {
        tAdapter.resetData();
        bAdapter.resetData();
        shAdapter.resetData();
        btAdapter.resetData();
        clAdapter.resetData();
        decAdapter.resetData();
        othAdapter.resetData();
        fAdapter.resetData();
    }
    private void filterItems() {
        // Perform filtering logic based on the search query

        // Filter items for tAdapter
        List<Sale> filteredTSales = new ArrayList<>();
        for (Sale sale : tSales) {
            if (sale.getmName().toLowerCase().contains(searchQuery.toLowerCase())) {
                filteredTSales.add(sale);
            }
        }
        tAdapter.filterSales(filteredTSales);

        // Filter items for fAdapter
        List<Sale> filteredFSales = new ArrayList<>();
        for (Sale sale : fSales) {
            if (sale.getmName().toLowerCase().contains(searchQuery.toLowerCase())) {
                filteredFSales.add(sale);
            }
        }
        fAdapter.filterSales(filteredFSales);

        // Filter items for bAdapter
        List<Sale> filteredBSales = new ArrayList<>();
        for (Sale sale : bSales) {
            if (sale.getmName().toLowerCase().contains(searchQuery.toLowerCase())) {
                filteredBSales.add(sale);
            }
        }
        bAdapter.filterSales(filteredBSales);

        // Filter items for shAdapter
        List<Sale> filteredShSales = new ArrayList<>();
        for (Sale sale : shSales) {
            if (sale.getmName().toLowerCase().contains(searchQuery.toLowerCase())) {
                filteredShSales.add(sale);
            }
        }
        shAdapter.filterSales(filteredShSales);

        // Filter items for btAdapter
        List<Sale> filteredBtSales = new ArrayList<>();
        for (Sale sale : btSales) {
            if (sale.getmName().toLowerCase().contains(searchQuery.toLowerCase())) {
                filteredBtSales.add(sale);
            }
        }
        btAdapter.filterSales(filteredBtSales);

        // Filter items for clAdapter
        List<Sale> filteredClSales = new ArrayList<>();
        for (Sale sale : clSales) {
            if (sale.getmName().toLowerCase().contains(searchQuery.toLowerCase())) {
                filteredClSales.add(sale);
            }
        }
        clAdapter.filterSales(filteredClSales);

        // Filter items for decAdapter
        List<Sale> filteredDecSales = new ArrayList<>();
        for (Sale sale : decSales) {
            if (sale.getmName().toLowerCase().contains(searchQuery.toLowerCase())) {
                filteredDecSales.add(sale);
            }
        }
        decAdapter.filterSales(filteredDecSales);

        // Filter items for othAdapter
        List<Sale> filteredOthSales = new ArrayList<>();
        for (Sale sale : othSales) {
            if (sale.getmName().toLowerCase().contains(searchQuery.toLowerCase())) {
                filteredOthSales.add(sale);
            }
        }
        othAdapter.filterSales(filteredOthSales);
    }

}