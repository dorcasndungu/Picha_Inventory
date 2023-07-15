package com.example.pichainventory.ui_fragments;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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
import com.example.pichainventory.SalesViewHolder;
import com.example.pichainventory.SearchableFragment;
import com.example.pichainventory.adapters.OrderAdapter;
import com.example.pichainventory.adapters.SalesRecAdapter;
import com.example.pichainventory.adapters.SalesRecAdapter;
import com.example.pichainventory.databinding.FragmentSalesRecBinding;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
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

public class SalesRecFragment extends Fragment implements SearchableFragment {

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
        FirebaseRecyclerOptions<Sale> toysOptions =
                new FirebaseRecyclerOptions.Builder<Sale>()
                        .setQuery(tDatabaseRef, Sale.class)
                        .build();

        FirebaseRecyclerAdapter<Sale, SalesViewHolder> toysAdapter =
                new FirebaseRecyclerAdapter<Sale, SalesViewHolder>(toysOptions) {
                    @Override
                    protected void onBindViewHolder(@NonNull SalesViewHolder holder, int position, @NonNull Sale model) {
                        // Set data to your views for toys instance
                    }

                    @NonNull
                    @Override
                    public SalesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        // Create a new instance of the ViewHolder and return it.
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item, parent, false);
                        return new SalesViewHolder(view);
                    }
                };

        binding.toysRecycler.setAdapter(toysAdapter);
        toysAdapter.startListening();

        tDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int toySales = (int) dataSnapshot.getChildrenCount();
                int totalProfit = 0;
                int totalSp = 0;

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Sale sale = postSnapshot.getValue(Sale.class);
                    totalProfit += sale.getmProfit();
                    totalSp += sale.getmSp();
                }

                toyProfit = totalProfit;
                toySp = totalSp;
                updateTotalDisplay();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

// FirebaseRecyclerOptions and FirebaseRecyclerAdapter for flowers
        FirebaseRecyclerOptions<Sale> flowersOptions =
                new FirebaseRecyclerOptions.Builder<Sale>()
                        .setQuery(fDatabaseRef, Sale.class)
                        .build();

        FirebaseRecyclerAdapter<Sale, SalesViewHolder> flowersAdapter =
                new FirebaseRecyclerAdapter<Sale, SalesViewHolder>(flowersOptions) {
                    @Override
                    protected void onBindViewHolder(@NonNull SalesViewHolder holder, int position, @NonNull Sale model) {
                        // Set data to your views for flowers instance
                    }

                    @NonNull
                    @Override
                    public SalesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        // Create a new instance of the ViewHolder and return it.
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item, parent, false);
                        return new SalesViewHolder(view);
                    }
                };

        binding.flowersRecycler.setAdapter(flowersAdapter);
        flowersAdapter.startListening();

        fDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int flowerSales = (int) dataSnapshot.getChildrenCount();
                int totalProfit = 0;
                int totalSp = 0;

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Sale sale = postSnapshot.getValue(Sale.class);
                    totalProfit += sale.getmProfit();
                    totalSp += sale.getmSp();
                }

                flowerProfit = totalProfit;
                flowerSp = totalSp;
                updateTotalDisplay();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

// Repeat the above code block for each instance with corresponding variable names
// FirebaseRecyclerOptions and FirebaseRecyclerAdapter for bedding
        FirebaseRecyclerOptions<Sale> beddingOptions =
                new FirebaseRecyclerOptions.Builder<Sale>()
                        .setQuery(bDatabaseRef, Sale.class)
                        .build();

        FirebaseRecyclerAdapter<Sale, SalesViewHolder> beddingAdapter =
                new FirebaseRecyclerAdapter<Sale, SalesViewHolder>(beddingOptions) {
                    @Override
                    protected void onBindViewHolder(@NonNull SalesViewHolder holder, int position, @NonNull Sale model) {
                        // Set data to your views for bedding instance
                    }

                    @NonNull
                    @Override
                    public SalesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        // Create a new instance of the ViewHolder and return it.
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item, parent, false);
                        return new SalesViewHolder(view);
                    }
                };

        binding.beddingRecycler.setAdapter(beddingAdapter);
        beddingAdapter.startListening();

        bDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int beddingSales = (int) dataSnapshot.getChildrenCount();
                int totalProfit = 0;
                int totalSp = 0;

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Sale sale = postSnapshot.getValue(Sale.class);
                    totalProfit += sale.getmProfit();
                    totalSp += sale.getmSp();
                }

                beddingProfit = totalProfit;
                beddingSp = totalSp;
                updateTotalDisplay();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

// Repeat the above code block for each instance with corresponding variable names
// FirebaseRecyclerOptions and FirebaseRecyclerAdapter for shoes
        FirebaseRecyclerOptions<Sale> shoesOptions =
                new FirebaseRecyclerOptions.Builder<Sale>()
                        .setQuery(shDatabaseRef, Sale.class)
                        .build();

        FirebaseRecyclerAdapter<Sale, SalesViewHolder> shoesAdapter =
                new FirebaseRecyclerAdapter<Sale, SalesViewHolder>(shoesOptions) {
                    @Override
                    protected void onBindViewHolder(@NonNull SalesViewHolder holder, int position, @NonNull Sale model) {
                        // Set data to your views for shoes instance
                    }

                    @NonNull
                    @Override
                    public SalesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        // Create a new instance of the ViewHolder and return it.
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item, parent, false);
                        return new SalesViewHolder(view);
                    }
                };

        binding.shoesRecycler.setAdapter(shoesAdapter);
        shoesAdapter.startListening();

        shDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int shoesSales = (int) dataSnapshot.getChildrenCount();
                int totalProfit = 0;
                int totalSp = 0;

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Sale sale = postSnapshot.getValue(Sale.class);
                    totalProfit += sale.getmProfit();
                    totalSp += sale.getmSp();
                }

                shoesProfit = totalProfit;
                shoesSp = totalSp;
                updateTotalDisplay();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

// Repeat the above code block for each instance with corresponding variable names
// FirebaseRecyclerOptions and FirebaseRecyclerAdapter for beauty
        FirebaseRecyclerOptions<Sale> beautyOptions =
                new FirebaseRecyclerOptions.Builder<Sale>()
                        .setQuery(btDatabaseRef, Sale.class)
                        .build();

        FirebaseRecyclerAdapter<Sale, SalesViewHolder> beautyAdapter =
                new FirebaseRecyclerAdapter<Sale, SalesViewHolder>(beautyOptions) {
                    @Override
                    protected void onBindViewHolder(@NonNull SalesViewHolder holder, int position, @NonNull Sale model) {
                        // Set data to your views for beauty instance
                    }

                    @NonNull
                    @Override
                    public SalesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        // Create a new instance of the ViewHolder and return it.
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item, parent, false);
                        return new SalesViewHolder(view);
                    }
                };

        binding.beautyRecycler.setAdapter(beautyAdapter);
        beautyAdapter.startListening();

        btDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int beautySales = (int) dataSnapshot.getChildrenCount();
                int totalProfit = 0;
                int totalSp = 0;

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Sale sale = postSnapshot.getValue(Sale.class);
                    totalProfit += sale.getmProfit();
                    totalSp += sale.getmSp();
                }

                beautyProfit = totalProfit;
                beautySp = totalSp;
                updateTotalDisplay();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

// Repeat the above code block for each instance with corresponding variable names
// FirebaseRecyclerOptions and FirebaseRecyclerAdapter for clothes
        FirebaseRecyclerOptions<Sale> clothesOptions =
                new FirebaseRecyclerOptions.Builder<Sale>()
                        .setQuery(clDatabaseRef, Sale.class)
                        .build();

        FirebaseRecyclerAdapter<Sale, SalesViewHolder> clothesAdapter =
                new FirebaseRecyclerAdapter<Sale, SalesViewHolder>(clothesOptions) {
                    @Override
                    protected void onBindViewHolder(@NonNull SalesViewHolder holder, int position, @NonNull Sale model) {
                        // Set data to your views for clothes instance
                    }

                    @NonNull
                    @Override
                    public SalesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        // Create a new instance of the ViewHolder and return it.
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item, parent, false);
                        return new SalesViewHolder(view);
                    }
                };

        binding.clothesRecycler.setAdapter(clothesAdapter);
        clothesAdapter.startListening();

        clDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int clothesSales = (int) dataSnapshot.getChildrenCount();
                int totalProfit = 0;
                int totalSp = 0;

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Sale sale = postSnapshot.getValue(Sale.class);
                    totalProfit += sale.getmProfit();
                    totalSp += sale.getmSp();
                }

                clothesProfit = totalProfit;
                clothesSp = totalSp;
                updateTotalDisplay();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

// Repeat the above code block for each instance with corresponding variable names
// FirebaseRecyclerOptions and FirebaseRecyclerAdapter for decor
        FirebaseRecyclerOptions<Sale> decorOptions =
                new FirebaseRecyclerOptions.Builder<Sale>()
                        .setQuery(decDatabaseRef, Sale.class)
                        .build();

        FirebaseRecyclerAdapter<Sale, SalesViewHolder> decorAdapter =
                new FirebaseRecyclerAdapter<Sale, SalesViewHolder>(decorOptions) {
                    @Override
                    protected void onBindViewHolder(@NonNull SalesViewHolder holder, int position, @NonNull Sale model) {
                        // Set data to your views for decor instance
                    }

                    @NonNull
                    @Override
                    public SalesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        // Create a new instance of the ViewHolder and return it.
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item, parent, false);
                        return new SalesViewHolder(view);
                    }
                };

        binding.decorRecycler.setAdapter(decorAdapter);
        decorAdapter.startListening();

        decDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int decorSales = (int) dataSnapshot.getChildrenCount();
                int totalProfit = 0;
                int totalSp = 0;

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Sale sale = postSnapshot.getValue(Sale.class);
                    totalProfit += sale.getmProfit();
                    totalSp += sale.getmSp();
                }

                decorProfit = totalProfit;
                decorSp = totalSp;
                updateTotalDisplay();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

// Repeat the above code block for each instance with corresponding variable names
// FirebaseRecyclerOptions and FirebaseRecyclerAdapter for other
        FirebaseRecyclerOptions<Sale> otherOptions =
                new FirebaseRecyclerOptions.Builder<Sale>()
                        .setQuery(othDatabaseRef, Sale.class)
                        .build();

        FirebaseRecyclerAdapter<Sale, SalesViewHolder> otherAdapter =
                new FirebaseRecyclerAdapter<Sale, SalesViewHolder>(otherOptions) {
                    @Override
                    protected void onBindViewHolder(@NonNull SalesViewHolder holder, int position, @NonNull Sale model) {
                        // Set data to your views for other instance
                    }

                    @NonNull
                    @Override
                    public SalesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        // Create a new instance of the ViewHolder and return it.
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item, parent, false);
                        return new SalesViewHolder(view);
                    }
                };

        binding.otherRecycler.setAdapter(otherAdapter);
        otherAdapter.startListening();

        othDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int otherSales = (int) dataSnapshot.getChildrenCount();
                int totalProfit = 0;
                int totalSp = 0;

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Sale sale = postSnapshot.getValue(Sale.class);
                    totalProfit += sale.getmProfit();
                    totalSp += sale.getmSp();
                }

                otherProfit = totalProfit;
                otherSp = totalSp;
                updateTotalDisplay();
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
    public void performSearch(String query) {
        searchQuery = query;
        filterItems();
    }

    @Override
    public void resetData() {
//        tAdapter.resetData();
//        bAdapter.resetData();
//        shAdapter.resetData();
//        btAdapter.resetData();
//        clAdapter.resetData();
//        decAdapter.resetData();
//        othAdapter.resetData();
//        fAdapter.resetData();
    }
    private void filterItems() {
//        // Perform filtering logic based on the search query
//
//        // Filter items for tAdapter
//        List<Sale> filteredTSales = new ArrayList<>();
//        for (Sale sale : tSales) {
//            if (sale.getmName().toLowerCase().contains(searchQuery.toLowerCase())) {
//                filteredTSales.add(sale);
//            }
//        }
//        tAdapter.filterSales(filteredTSales);
//
//        // Filter items for fAdapter
//        List<Sale> filteredFSales = new ArrayList<>();
//        for (Sale sale : fSales) {
//            if (sale.getmName().toLowerCase().contains(searchQuery.toLowerCase())) {
//                filteredFSales.add(sale);
//            }
//        }
//        fAdapter.filterSales(filteredFSales);
//
//        // Filter items for bAdapter
//        List<Sale> filteredBSales = new ArrayList<>();
//        for (Sale sale : bSales) {
//            if (sale.getmName().toLowerCase().contains(searchQuery.toLowerCase())) {
//                filteredBSales.add(sale);
//            }
//        }
//        bAdapter.filterSales(filteredBSales);
//
//        // Filter items for shAdapter
//        List<Sale> filteredShSales = new ArrayList<>();
//        for (Sale sale : shSales) {
//            if (sale.getmName().toLowerCase().contains(searchQuery.toLowerCase())) {
//                filteredShSales.add(sale);
//            }
//        }
//        shAdapter.filterSales(filteredShSales);
//
//        // Filter items for btAdapter
//        List<Sale> filteredBtSales = new ArrayList<>();
//        for (Sale sale : btSales) {
//            if (sale.getmName().toLowerCase().contains(searchQuery.toLowerCase())) {
//                filteredBtSales.add(sale);
//            }
//        }
//        btAdapter.filterSales(filteredBtSales);
//
//        // Filter items for clAdapter
//        List<Sale> filteredClSales = new ArrayList<>();
//        for (Sale sale : clSales) {
//            if (sale.getmName().toLowerCase().contains(searchQuery.toLowerCase())) {
//                filteredClSales.add(sale);
//            }
//        }
//        clAdapter.filterSales(filteredClSales);
//
//        // Filter items for decAdapter
//        List<Sale> filteredDecSales = new ArrayList<>();
//        for (Sale sale : decSales) {
//            if (sale.getmName().toLowerCase().contains(searchQuery.toLowerCase())) {
//                filteredDecSales.add(sale);
//            }
//        }
//        decAdapter.filterSales(filteredDecSales);
//
//        // Filter items for othAdapter
//        List<Sale> filteredOthSales = new ArrayList<>();
//        for (Sale sale : othSales) {
//            if (sale.getmName().toLowerCase().contains(searchQuery.toLowerCase())) {
//                filteredOthSales.add(sale);
//            }
//        }
//        othAdapter.filterSales(filteredOthSales);
    }
}