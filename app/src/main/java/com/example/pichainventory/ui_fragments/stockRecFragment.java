package com.example.pichainventory.ui_fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pichainventory.R;
import com.example.pichainventory.Models.Upload;
import com.example.pichainventory.SearchableFragment;
import com.example.pichainventory.adapters.StockAdapter;
import com.example.pichainventory.databinding.FragmentStockRecBinding;
import com.example.pichainventory.ui_fragments.StcOrdFragment;
import com.example.pichainventory.ui_fragments.StcSelFragment;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class stockRecFragment extends Fragment implements StockAdapter.OnItemClickListener, SearchableFragment {
    private FragmentStockRecBinding binding;
    private DatabaseReference tDatabaseRef;
    private String searchQuery = "";
    private DatabaseReference bDatabaseRef;
    private DatabaseReference shDatabaseRef;
    private DatabaseReference btDatabaseRef;
    private DatabaseReference clDatabaseRef;
    private DatabaseReference decDatabaseRef;
    private DatabaseReference othDatabaseRef;
    private DatabaseReference fDatabaseRef;
    private StockAdapter tAdapter;
    private StockAdapter bAdapter;
    private StockAdapter shAdapter;
    private StockAdapter btAdapter;
    private StockAdapter clAdapter;
    private StockAdapter decAdapter;
    private StockAdapter othAdapter;
    private StockAdapter fAdapter;
    private List<Upload> tUploads;
    private List<Upload> bUploads;
    private List<Upload> shUploads;
    private List<Upload> btUploads;
    private List<Upload> clUploads;
    private List<Upload> decUploads;
    private List<Upload> othUploads;
    private List<Upload> fUploads;
    private Map<String, Boolean> sectionVisibilityMap;
    private Map<String, ImageView> sectionArrowIconMap;
    private Map<String, RecyclerView> sectionRecyclerViewMap;

    public stockRecFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize RecyclerView and ImageView maps
        // Initialize your DatabaseReference
        tDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads").child("Toys");
        fDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads").child("Flowers");
        bDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads").child("Bedding");
        shDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads").child("Shoes");
        btDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads").child("Beauty");
        clDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads").child("Clothes");
        decDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads").child("Decor");
        othDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads").child("Other");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentStockRecBinding.inflate(inflater, container, false);
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
        tUploads = new ArrayList<>();
        fUploads = new ArrayList<>();
        bUploads = new ArrayList<>();
        shUploads = new ArrayList<>();
        btUploads = new ArrayList<>();
        clUploads = new ArrayList<>();
        decUploads = new ArrayList<>();
        othUploads = new ArrayList<>();

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
        FirebaseRecyclerOptions<Upload> options =
                new FirebaseRecyclerOptions.Builder<Upload>()
                        .setQuery(tDatabaseRef, Upload.class)
                        .build();

        tDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tUploads.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Upload upload = postSnapshot.getValue(Upload.class);
                    tUploads.add(upload);
                }

                tAdapter = new StockAdapter(tUploads, getContext());
                tAdapter.setOnItemClickListener(stockRecFragment.this);
                binding.toysRecycler.setAdapter(tAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        // Set up the FirebaseRecyclerAdapter
        FirebaseRecyclerOptions<Upload> flowersOptions =
                new FirebaseRecyclerOptions.Builder<Upload>()
                        .setQuery(fDatabaseRef, Upload.class)
                        .build();

        fDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fUploads.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Upload upload = postSnapshot.getValue(Upload.class);
                    fUploads.add(upload);
                }

                fAdapter = new StockAdapter(fUploads, getContext());
                fAdapter.setOnItemClickListener(stockRecFragment.this);
                binding.flowersRecycler.setAdapter(fAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        // Set up the beauty FirebaseRecyclerAdapter
        FirebaseRecyclerOptions<Upload> beddingOptions =
                new FirebaseRecyclerOptions.Builder<Upload>()
                        .setQuery(bDatabaseRef, Upload.class)
                        .build();

        bDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bUploads.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Upload upload = postSnapshot.getValue(Upload.class);
                    bUploads.add(upload);
                }

                bAdapter = new StockAdapter(bUploads, getContext());
                bAdapter.setOnItemClickListener(stockRecFragment.this);
                binding.beddingRecycler.setAdapter(bAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Set up the shoesRecyclerAdapter
        FirebaseRecyclerOptions<Upload> shoesOptions =
                new FirebaseRecyclerOptions.Builder<Upload>()
                        .setQuery(shDatabaseRef, Upload.class)
                        .build();

        shDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                shUploads.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Upload upload = postSnapshot.getValue(Upload.class);
                    shUploads.add(upload);
                }

                shAdapter = new StockAdapter(shUploads, getContext());
                shAdapter.setOnItemClickListener(stockRecFragment.this);
                binding.shoesRecycler.setAdapter(shAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        // Set up the FirebaseRecyclerAdapter
        FirebaseRecyclerOptions<Upload> beautyOptions =
                new FirebaseRecyclerOptions.Builder<Upload>()
                        .setQuery(btDatabaseRef, Upload.class)
                        .build();

        btDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                btUploads.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Upload upload = postSnapshot.getValue(Upload.class);
                    btUploads.add(upload);
                }

                btAdapter = new StockAdapter(btUploads, getContext());
                btAdapter.setOnItemClickListener(stockRecFragment.this);
                binding.beautyRecycler.setAdapter(btAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

// Set up the FirebaseRecyclerAdapter
        FirebaseRecyclerOptions<Upload> clothesOptions =
                new FirebaseRecyclerOptions.Builder<Upload>()
                        .setQuery(clDatabaseRef, Upload.class)
                        .build();

        clDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                clUploads.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Upload upload = postSnapshot.getValue(Upload.class);
                    clUploads.add(upload);
                }

                clAdapter = new StockAdapter(clUploads, getContext());
                clAdapter.setOnItemClickListener(stockRecFragment.this);
                binding.clothesRecycler.setAdapter(clAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

// Set up the FirebaseRecyclerAdapter
        FirebaseRecyclerOptions<Upload> decorOptions =
                new FirebaseRecyclerOptions.Builder<Upload>()
                        .setQuery(decDatabaseRef, Upload.class)
                        .build();

        decDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                decUploads.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Upload upload = postSnapshot.getValue(Upload.class);
                    decUploads.add(upload);
                }

                decAdapter = new StockAdapter(decUploads, getContext());
                decAdapter.setOnItemClickListener(stockRecFragment.this);
                binding.decorRecycler.setAdapter(decAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

// Set up the FirebaseRecyclerAdapter
        FirebaseRecyclerOptions<Upload> otherOptions =
                new FirebaseRecyclerOptions.Builder<Upload>()
                        .setQuery(othDatabaseRef, Upload.class)
                        .build();

        othDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                othUploads.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Upload upload = postSnapshot.getValue(Upload.class);
                    othUploads.add(upload);
                }

                othAdapter = new StockAdapter(othUploads, getContext());
                othAdapter.setOnItemClickListener(stockRecFragment.this);
                binding.otherRecycler.setAdapter(othAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainerView, fragment);
        fragmentTransaction.addToBackStack(null); // Add the fragment to the back stack
        fragmentTransaction.commit();


    }

    private int getImageRotation(String imageUrl) {
        // Retrieve the rotation angle from Firebase or any other source as per your image data structure
        // For example, if the rotation angle is stored in the "rotation" field of the image data:
        // int rotationAngle = imageData.getRotation();

        // Assume a fixed rotation angle of 0 for demonstration purposes
        int rotationAngle = 0;

        return rotationAngle;
    }
    private void showDialogWithItems(Bundle bundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_layout, null);
        builder.setView(dialogView);

        // Find views in the dialog layout
        ImageView displayPhoto = dialogView.findViewById(R.id.displayPhoto);
        TextView itemLabel = dialogView.findViewById(R.id.itemLabel);
        TextView buyingP = dialogView.findViewById(R.id.BuyingP);
        TextView sellingPEditText = dialogView.findViewById(R.id.SellingPEditText);
        TextView additionalEdiText = dialogView.findViewById(R.id.AdditionalEdiText);
        TextView UnitEditText = dialogView.findViewById(R.id.UnitEditText);
        Button sellBtn=dialogView.findViewById(R.id.buttonSell);
        Button OrdBtn=dialogView.findViewById(R.id.buttonOrder);

        // Retrieve data from the Bundle
        String itemName = bundle.getString("itemName");
        String buyingPrice = bundle.getString("buyingPrice");
        String imageUrl = bundle.getString("imageUrl");
        String sellingPrice = bundle.getString("sellingPrice");
        String additional = bundle.getString("additional");
        String mCategory = bundle.getString("mCategory");
        String units = bundle.getString("mUnits");

        // Set values to the views
        itemLabel.setText(itemName);
        buyingP.setText(buyingPrice);
        sellingPEditText.setText(sellingPrice);
        additionalEdiText.setText(additional);
        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.placeholder) // Placeholder image until the actual image is loaded
                .fit()
                .centerCrop().rotate(getImageRotation(imageUrl))// Call the method to get the rotation angle
                .into(displayPhoto);


        UnitEditText.setText(units);

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        OrdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String itemName = bundle.getString("itemName");
                String imageUrl = bundle.getString("imageUrl");
                String category=bundle.getString("mCategory");
                StcOrdFragment stcOrdFragment = new StcOrdFragment();
                Bundle args = new Bundle();
                args.putString("itemName", itemName);
                args.putString("imageUrl", imageUrl);
                args.putString("category",category);
                stcOrdFragment.setArguments(args);
                replaceFragment(stcOrdFragment);
                // Close the dialog
                dialog.dismiss();
            }
        });

        sellBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event of the sell button
                String itemName = bundle.getString("itemName");
                String buyingPrice = bundle.getString("buyingPrice");
                String imageUrl = bundle.getString("imageUrl");
                String additional = bundle.getString("additional");
                String units = bundle.getString("mUnits");
                String sellingPrice = bundle.getString("sellingPrice");
                String category=bundle.getString("mCategory");

                StcSelFragment stcSelFragment = new StcSelFragment();
                Bundle args = new Bundle();
                args.putString("itemName", itemName);
                args.putString("buyingPrice", buyingPrice);
                args.putString("imageUrl", imageUrl);
                args.putString("additional", additional);
                args.putString("units", units);
                args.putString("sellingPrice", sellingPrice);

                args.putString("category",category);
                stcSelFragment.setArguments(args);
                replaceFragment(stcSelFragment);

                // Close the dialog
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onItemClick(int position, Upload upload) {
        Bundle bundle = new Bundle();
        bundle.putString("itemName", upload.getmName());
        bundle.putString("buyingPrice", String.valueOf(upload.getmBp()));
        bundle.putString("imageUrl", upload.getmImageUrl());
        bundle.putString("sellingPrice", String.valueOf(upload.getmSp()));
        bundle.putString("additional", upload.getmAddInfo());
        bundle.putString("mCategory", upload.getmCategory());
        bundle.putString("mUnits", String.valueOf(upload.getmUnits()));
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
        List<Upload> filteredTUploads = new ArrayList<>();
        for (Upload upload : tUploads) {
            if (upload.getmName().toLowerCase().contains(searchQuery.toLowerCase())) {
                filteredTUploads.add(upload);
            }
        }
        tAdapter.filterUploads(filteredTUploads);

        // Filter items for fAdapter
        List<Upload> filteredFUploads = new ArrayList<>();
        for (Upload upload : fUploads) {
            if (upload.getmName().toLowerCase().contains(searchQuery.toLowerCase())) {
                filteredFUploads.add(upload);
            }
        }
        fAdapter.filterUploads(filteredFUploads);

        // Filter items for bAdapter
        List<Upload> filteredBUploads = new ArrayList<>();
        for (Upload upload : bUploads) {
            if (upload.getmName().toLowerCase().contains(searchQuery.toLowerCase())) {
                filteredBUploads.add(upload);
            }
        }
        bAdapter.filterUploads(filteredBUploads);

        // Filter items for shAdapter
        List<Upload> filteredShUploads = new ArrayList<>();
        for (Upload upload : shUploads) {
            if (upload.getmName().toLowerCase().contains(searchQuery.toLowerCase())) {
                filteredShUploads.add(upload);
            }
        }
        shAdapter.filterUploads(filteredShUploads);

        // Filter items for btAdapter
        List<Upload> filteredBtUploads = new ArrayList<>();
        for (Upload upload : btUploads) {
            if (upload.getmName().toLowerCase().contains(searchQuery.toLowerCase())) {
                filteredBtUploads.add(upload);
            }
        }
        btAdapter.filterUploads(filteredBtUploads);

        // Filter items for clAdapter
        List<Upload> filteredClUploads = new ArrayList<>();
        for (Upload upload : clUploads) {
            if (upload.getmName().toLowerCase().contains(searchQuery.toLowerCase())) {
                filteredClUploads.add(upload);
            }
        }
        clAdapter.filterUploads(filteredClUploads);

        // Filter items for decAdapter
        List<Upload> filteredDecUploads = new ArrayList<>();
        for (Upload upload : decUploads) {
            if (upload.getmName().toLowerCase().contains(searchQuery.toLowerCase())) {
                filteredDecUploads.add(upload);
            }
        }
        decAdapter.filterUploads(filteredDecUploads);

        // Filter items for othAdapter
        List<Upload> filteredOthUploads = new ArrayList<>();
        for (Upload upload : othUploads) {
            if (upload.getmName().toLowerCase().contains(searchQuery.toLowerCase())) {
                filteredOthUploads.add(upload);
            }
        }
        othAdapter.filterUploads(filteredOthUploads);
    }

}