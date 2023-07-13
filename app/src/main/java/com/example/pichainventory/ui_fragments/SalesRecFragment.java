package com.example.pichainventory.ui_fragments;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
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
import com.example.pichainventory.adapters.OrderAdapter;
import com.example.pichainventory.adapters.SalesRecAdapter;
import com.example.pichainventory.adapters.SalesRecAdapter;
import com.example.pichainventory.databinding.FragmentSalesRecBinding;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    public SalesRecFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());
        tDatabaseRef = FirebaseDatabase.getInstance().getReference("sales").child(currentDate).child("Toys");
        fDatabaseRef = FirebaseDatabase.getInstance().getReference("sales").child(currentDate).child("Flowers");
        bDatabaseRef = FirebaseDatabase.getInstance().getReference("sales").child(currentDate).child("Bedding");
        shDatabaseRef = FirebaseDatabase.getInstance().getReference("sales").child(currentDate).child("Shoes");
        btDatabaseRef = FirebaseDatabase.getInstance().getReference("sales").child(currentDate).child("Beauty");
        clDatabaseRef = FirebaseDatabase.getInstance().getReference("sales").child(currentDate).child("Clothes");
        decDatabaseRef = FirebaseDatabase.getInstance().getReference("sales").child(currentDate).child("Decor");
        othDatabaseRef = FirebaseDatabase.getInstance().getReference("sales").child(currentDate).child("Other");
        mDate=currentDate;

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSalesRecBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        sectionVisibilityMap = new HashMap<>();
        sectionArrowIconMap = new HashMap<>();
        sectionRecyclerViewMap = new HashMap<>();

        setupSection("flowers", R.id.flowersRecycler, R.id.arrowIcon, rootView);
        setupSection("toys", R.id.toysRecycler, R.id.TarrowIcon, rootView);
        setupSection("decoration", R.id.decorRecycler, R.id.DarrowIcon, rootView);
        setupSection("other", R.id.otherRecycler, R.id.OarrowIcon, rootView);
        setupSection("bedding", R.id.beddingRecycler, R.id.BarrowIcon, rootView);
        setupSection("shoes", R.id.shoesRecycler, R.id.SarrowIcon, rootView);
        setupSection("beauty", R.id.beautyRecycler, R.id.BtarrowIcon, rootView);
        setupSection("clothes", R.id.clothesRecycler, R.id.CarrowIcon, rootView);

        return rootView;
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
        // Set up the FirebaseRecyclerAdapter
        FirebaseRecyclerOptions<Sale> options =
                new FirebaseRecyclerOptions.Builder<Sale>()
                        .setQuery(tDatabaseRef, Sale.class)
                        .build();

        tDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tSales.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    toySales = (int) dataSnapshot.getChildrenCount();
                    Sale sale = postSnapshot.getValue(Sale.class);
                    tSales.add(sale);
                    tAdapter = new SalesRecAdapter(tSales, getContext());
                    tAdapter.setOnItemClickListener(SalesRecFragment.this);
                    toyProfit = tAdapter.getTotalProfit();
                    toySp = tAdapter.getTotalSp();
                    binding.toysRecycler.setAdapter(tAdapter);
                    updateTotalDisplay();
                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        // Set up the FirebaseRecyclerAdapter
        FirebaseRecyclerOptions<Sale> flowersOptions =
                new FirebaseRecyclerOptions.Builder<Sale>()
                        .setQuery(fDatabaseRef, Sale.class)
                        .build();

        fDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                flowerSales = (int) dataSnapshot.getChildrenCount();
                List<Sale> newSales = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Sale sale = postSnapshot.getValue(Sale.class);
                    newSales.add(sale);
                    fSales.clear();
                    fSales.addAll(newSales);
                    fAdapter = new SalesRecAdapter(fSales, getContext());
                    fAdapter.setOnItemClickListener(SalesRecFragment.this);
                    flowerProfit = fAdapter.getTotalProfit();
                    flowerSp = fAdapter.getTotalSp();
                    binding.flowersRecycler.setAdapter(fAdapter);
                    updateTotalDisplay();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Set up the beauty FirebaseRecyclerAdapter
        FirebaseRecyclerOptions<Sale> beddingOptions =
                new FirebaseRecyclerOptions.Builder<Sale>()
                        .setQuery(bDatabaseRef, Sale.class)
                        .build();

        bDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                beddingSales = (int) dataSnapshot.getChildrenCount();
                bSales.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Sale sale = postSnapshot.getValue(Sale.class);
                    bSales.add(sale);
                    bAdapter = new SalesRecAdapter(bSales, getContext());
                    bAdapter.setOnItemClickListener(SalesRecFragment.this);
                    binding.beddingRecycler.setAdapter(bAdapter);
                    beddingProfit = bAdapter.getTotalProfit();
                    beddingSp = bAdapter.getTotalSp();
                    updateTotalDisplay();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Set up the shoesRecyclerAdapter
        FirebaseRecyclerOptions<Sale> shoesOptions =
                new FirebaseRecyclerOptions.Builder<Sale>()
                        .setQuery(shDatabaseRef, Sale.class)
                        .build();

        shDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                shoesSales = (int) dataSnapshot.getChildrenCount();
                shSales.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Sale sale = postSnapshot.getValue(Sale.class);
                    shSales.add(sale);
                    shAdapter = new SalesRecAdapter(shSales, getContext());
                    shAdapter.setOnItemClickListener(SalesRecFragment.this);
                    binding.shoesRecycler.setAdapter(shAdapter);
                    shoesProfit = shAdapter.getTotalProfit();
                    shoesSp = shAdapter.getTotalSp();
                    updateTotalDisplay();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Set up the FirebaseRecyclerAdapter
        FirebaseRecyclerOptions<Sale> beautyOptions =
                new FirebaseRecyclerOptions.Builder<Sale>()
                        .setQuery(btDatabaseRef, Sale.class)
                        .build();

        btDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                beautySales = (int) dataSnapshot.getChildrenCount();
                btSales.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Sale sale = postSnapshot.getValue(Sale.class);
                    btSales.add(sale);
                    btAdapter = new SalesRecAdapter(btSales, getContext());
                    btAdapter.setOnItemClickListener(SalesRecFragment.this);
                    binding.beautyRecycler.setAdapter(btAdapter);
                    beautyProfit = btAdapter.getTotalProfit();
                    beautySp = btAdapter.getTotalSp();
                    updateTotalDisplay();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Set up the FirebaseRecyclerAdapter
        FirebaseRecyclerOptions<Sale> clothesOptions =
                new FirebaseRecyclerOptions.Builder<Sale>()
                        .setQuery(clDatabaseRef, Sale.class)
                        .build();

        clDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                clotheSales = (int) dataSnapshot.getChildrenCount();
                clSales.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Sale sale = postSnapshot.getValue(Sale.class);
                    clSales.add(sale);
                    clAdapter = new SalesRecAdapter(clSales, getContext());
                    clAdapter.setOnItemClickListener(SalesRecFragment.this);
                    binding.clothesRecycler.setAdapter(clAdapter);
                    clothesProfit = clAdapter.getTotalProfit();
                    clothesSp = clAdapter.getTotalSp();
                    updateTotalDisplay();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Set up the FirebaseRecyclerAdapter
        FirebaseRecyclerOptions<Sale> decorOptions =
                new FirebaseRecyclerOptions.Builder<Sale>()
                        .setQuery(decDatabaseRef, Sale.class)
                        .build();

        decDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                decorSales = (int) dataSnapshot.getChildrenCount();
                decSales.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Sale sale = postSnapshot.getValue(Sale.class);
                    decSales.add(sale);
                    decAdapter = new SalesRecAdapter(decSales, getContext());
                    decAdapter.setOnItemClickListener(SalesRecFragment.this);
                    binding.decorRecycler.setAdapter(decAdapter);
                    decorProfit = decAdapter.getTotalProfit();
                    decorSp = decAdapter.getTotalSp();
                    updateTotalDisplay();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Set up the FirebaseRecyclerAdapter
        FirebaseRecyclerOptions<Sale> otherOptions =
                new FirebaseRecyclerOptions.Builder<Sale>()
                        .setQuery(othDatabaseRef, Sale.class)
                        .build();

        othDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                otherSales = (int) dataSnapshot.getChildrenCount();
                othSales.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Sale sale = postSnapshot.getValue(Sale.class);
                    othSales.add(sale);
                    othAdapter = new SalesRecAdapter(othSales, getContext());
                    othAdapter.setOnItemClickListener(SalesRecFragment.this);
                    binding.otherRecycler.setAdapter(othAdapter);
                    otherProfit = othAdapter.getTotalProfit();
                    otherSp = othAdapter.getTotalSp();
                    updateTotalDisplay();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateTotalDisplay() {
        totalSales=toySales+flowerSales+beddingSales+shoesSales+beautySales+clotheSales+decorSales+otherSales;
        totalProfit=toyProfit+flowerProfit+beddingProfit+shoesProfit+beautyProfit+clothesProfit+decorProfit+otherProfit;
        totalSp=toySp+flowerSp+beddingSp+shoesSp+beautySp+clothesSp+decorSp+otherSp;
        binding.salesTextView.setText("Sales: " + totalSales);
        binding.textViewProfit.setText(getString(R.string.profit) + totalProfit);
        binding.textViewTotal.setText(getString(R.string.total) + totalSp);
        binding.textViewDate.setText(getString(R.string.date) + mDate);
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
        searchQuery = query;
        filterItems();
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