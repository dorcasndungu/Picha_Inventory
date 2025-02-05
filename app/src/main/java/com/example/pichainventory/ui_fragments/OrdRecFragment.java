package com.example.pichainventory.ui_fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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

import com.example.pichainventory.Models.Order;
import com.example.pichainventory.R;
import com.example.pichainventory.SearchableFragment;
import com.example.pichainventory.adapters.OrderAdapter;
import com.example.pichainventory.databinding.FragmentOrdRecBinding;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OrdRecFragment extends Fragment implements OrderAdapter.OnItemClickListener, SearchableFragment {
    private FragmentOrdRecBinding binding;
    private DatabaseReference tDatabaseRef;
    private DatabaseReference bDatabaseRef;
    private DatabaseReference shDatabaseRef;
    private DatabaseReference btDatabaseRef;
    private DatabaseReference clDatabaseRef;
    private DatabaseReference decDatabaseRef;
    private DatabaseReference othDatabaseRef;
    private DatabaseReference fDatabaseRef;
    private OrderAdapter tAdapter;
    private OrderAdapter bAdapter;
    private OrderAdapter shAdapter;
    private OrderAdapter btAdapter;
    private OrderAdapter clAdapter;
    private OrderAdapter decAdapter;
    private OrderAdapter othAdapter;
    private OrderAdapter fAdapter;
    private List<Order> tOrders;
    private List<Order> bOrders;
    private List<Order> shOrders;
    private List<Order> btOrders;
    private List<Order> clOrders;
    private List<Order> decOrders;
    private List<Order> othOrders;
    private List<Order> fOrders;
    private Map<String, Boolean> sectionVisibilityMap;
    private Map<String, ImageView> sectionArrowIconMap;
    private Map<String, RecyclerView> sectionRecyclerViewMap;
    private String searchQuery = "";
    private String uid;
    public String mDate;

    public OrdRecFragment() {
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
        tDatabaseRef = FirebaseDatabase.getInstance().getReference(uid).child("Orders").child("Toys");
        fDatabaseRef = FirebaseDatabase.getInstance().getReference(uid).child("Orders").child("Flowers");
        bDatabaseRef = FirebaseDatabase.getInstance().getReference(uid).child("Orders").child("Bedding");
        shDatabaseRef = FirebaseDatabase.getInstance().getReference(uid).child("Orders").child("Shoes");
        btDatabaseRef = FirebaseDatabase.getInstance().getReference(uid).child("Orders").child("Beauty");
        clDatabaseRef = FirebaseDatabase.getInstance().getReference(uid).child("Orders").child("Clothes");
        decDatabaseRef = FirebaseDatabase.getInstance().getReference(uid).child("Orders").child("Decor");
        othDatabaseRef = FirebaseDatabase.getInstance().getReference(uid).child("Orders").child("Other");
        mDate = currentDate;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOrdRecBinding.inflate(inflater, container, false);
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
        binding.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToAddOrder();
            }
        });
        return rootView;
    }

    private void navigateToAddOrder() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        StcOrdFragment stcOrdFragment = new StcOrdFragment();

        Bundle args = new Bundle();
        args.putBoolean("isNewOrder", true);
        args.putString("mUid", uid); // Pass the uid here
        stcOrdFragment.setArguments(args);

        fragmentTransaction.replace(R.id.fragmentContainerView, stcOrdFragment); // R.id.fragment_container is the id of your container where fragments are displayed
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
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
        tOrders = new ArrayList<>();
        fOrders = new ArrayList<>();
        bOrders = new ArrayList<>();
        shOrders = new ArrayList<>();
        btOrders = new ArrayList<>();
        clOrders = new ArrayList<>();
        decOrders = new ArrayList<>();
        othOrders = new ArrayList<>();

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

        setupFirebaseListeners();
    }

    private void setupFirebaseListeners() {
        setupFirebaseListener(tDatabaseRef, tOrders, binding.toysRecycler);
        setupFirebaseListener(fDatabaseRef, fOrders, binding.flowersRecycler);
        setupFirebaseListener(bDatabaseRef, bOrders, binding.beddingRecycler);
        setupFirebaseListener(shDatabaseRef, shOrders, binding.shoesRecycler);
        setupFirebaseListener(btDatabaseRef, btOrders, binding.beautyRecycler);
        setupFirebaseListener(clDatabaseRef, clOrders, binding.clothesRecycler);
        setupFirebaseListener(decDatabaseRef, decOrders, binding.decorRecycler);
        setupFirebaseListener(othDatabaseRef, othOrders, binding.otherRecycler);
    }

    private void setupFirebaseListener(DatabaseReference databaseRef, List<Order> ordersList, RecyclerView recyclerView) {
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ordersList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Order order = postSnapshot.getValue(Order.class);
                    if (order != null) {
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                            Date orderDate = sdf.parse(order.getmDate());
                            long diff = new Date().getTime() - orderDate.getTime();
                            order.setmDayCount((int) (diff / (1000 * 60 * 60 * 24)));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        ordersList.add(order);
                    }
                }

                OrderAdapter adapter = new OrderAdapter(ordersList, getContext(), uid);
                adapter.setOnItemClickListener(OrdRecFragment.this);
                recyclerView.setAdapter(adapter);
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

    private void showDialogWithItems(Bundle bundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.orderdialog, null);
        builder.setView(dialogView);

        // Find views in the dialog layout
        ImageView displayPhoto = dialogView.findViewById(R.id.profilePhoto);
        TextView itemLabel = dialogView.findViewById(R.id.nameLabel);
        TextView DescText = dialogView.findViewById(R.id.DescTextView);
        TextView additionalText = dialogView.findViewById(R.id.AdditionalEdiText);
        TextView contactText = dialogView.findViewById(R.id.ContactTextView);
        TextView UnitsText = dialogView.findViewById(R.id.UnitTextView);
        Button backButton = dialogView.findViewById(R.id.buttonBack);
        FloatingActionButton editBtn = dialogView.findViewById(R.id.editButton);
        FloatingActionButton deleteBtn = dialogView.findViewById(R.id.deleteButton);

        // Retrieve data from the Bundle
        String itemName = bundle.getString("itemName");
        String imageUrl = bundle.getString("imageUrl");
        String additional = bundle.getString("additional");
        String mCategory = bundle.getString("mCategory");
        String units = bundle.getString("mUnits");
        String desc = bundle.getString("descrptn");
        String contact = bundle.getString("contact");

        // Set values to the views
        itemLabel.setText(itemName);
        DescText.setText(desc);
        contactText.setText(contact);
        additionalText.setText(additional);
        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.placeholder)
                .fit()
                .centerCrop()
                .into(displayPhoto);

        UnitsText.setText(units);

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
    public void onItemClick(int position, Order order) {
        Bundle bundle = new Bundle();
        bundle.putString("itemName", order.getmName());
        bundle.putString("imageUrl", order.getmImageUrl());
        bundle.putString("descrptn", order.getmDesc());
        bundle.putString("contact", order.getmCont());
        bundle.putString("additional", order.getmAddInfo());
        bundle.putString("mCategory", order.getmCategory());
        bundle.putString("mUnits", String.valueOf(order.getmUnits()));
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
        List<Order> filteredTOrders = new ArrayList<>();
        for (Order order : tOrders) {
            if (order.getmName().toLowerCase().contains(searchQuery.toLowerCase())) {
                filteredTOrders.add(order);
            }
        }
        tAdapter.filterOrders(filteredTOrders);

        // Filter items for fAdapter
        List<Order> filteredFOrders = new ArrayList<>();
        for (Order order : fOrders) {
            if (order.getmName().toLowerCase().contains(searchQuery.toLowerCase())) {
                filteredFOrders.add(order);
            }
        }
        fAdapter.filterOrders(filteredFOrders);

        // Filter items for bAdapter
        List<Order> filteredBOrders = new ArrayList<>();
        for (Order order : bOrders) {
            if (order.getmName().toLowerCase().contains(searchQuery.toLowerCase())) {
                filteredBOrders.add(order);
            }
        }
        bAdapter.filterOrders(filteredBOrders);

        // Filter items for shAdapter
        List<Order> filteredShOrders = new ArrayList<>();
        for (Order order : shOrders) {
            if (order.getmName().toLowerCase().contains(searchQuery.toLowerCase())) {
                filteredShOrders.add(order);
            }
        }
        shAdapter.filterOrders(filteredShOrders);

        // Filter items for btAdapter
        List<Order> filteredBtOrders = new ArrayList<>();
        for (Order order : btOrders) {
            if (order.getmName().toLowerCase().contains(searchQuery.toLowerCase())) {
                filteredBtOrders.add(order);
            }
        }
        btAdapter.filterOrders(filteredBtOrders);

        // Filter items for clAdapter
        List<Order> filteredClOrders = new ArrayList<>();
        for (Order order : clOrders) {
            if (order.getmName().toLowerCase().contains(searchQuery.toLowerCase())) {
                filteredClOrders.add(order);
            }
        }
        clAdapter.filterOrders(filteredClOrders);

        // Filter items for decAdapter
        List<Order> filteredDecOrders = new ArrayList<>();
        for (Order order : decOrders) {
            if (order.getmName().toLowerCase().contains(searchQuery.toLowerCase())) {
                filteredDecOrders.add(order);
            }
        }
        decAdapter.filterOrders(filteredDecOrders);

        // Filter items for othAdapter
        List<Order> filteredOthOrders = new ArrayList<>();
        for (Order order : othOrders) {
            if (order.getmName().toLowerCase().contains(searchQuery.toLowerCase())) {
                filteredOthOrders.add(order);
            }
        }
        othAdapter.filterOrders(filteredOthOrders);
    }
}
