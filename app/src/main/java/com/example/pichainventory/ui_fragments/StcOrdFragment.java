package com.example.pichainventory.ui_fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.pichainventory.Models.Order;
import com.example.pichainventory.Models.Sale;
import com.example.pichainventory.R;
import com.example.pichainventory.databinding.FragmentStcordBinding;
import com.example.pichainventory.databinding.FragmentStcsaleBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StcOrdFragment extends Fragment {
    FragmentStcordBinding binding;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    public String ItemName;
    public String ImageUrl;
    public String Category;
    public StcOrdFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentStcordBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        mStorageRef = FirebaseStorage.getInstance().getReference("Orders");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Orders");

        Bundle args = getArguments();
        if (args != null) {
            String itemName = args.getString("itemName");
            String imageUrl = args.getString("imageUrl");
            String category = args.getString("category");

            binding.nameLabel.setText(itemName);

            // Load the image using Picasso or any other image loading library
            Picasso.get()
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder) // Placeholder image until the actual image is loaded
                    .fit()
                    .centerCrop()
                    .into(binding.profilePhoto);
//variables to be used when uploading
            ItemName=itemName;
            ImageUrl=imageUrl;
            Category=category;
        }
        binding.buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFormValid()) {
                    uploadFile();
                }
                else {
                    Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_LONG).show();
                }


            }
        });
        return rootView;
    }

    private boolean isFormValid() {
        return  isDescValid() && isContactValid() && isUnitsValid();
    }

    private boolean isDescValid() {
        String name = binding.DescEditText.getText().toString().trim();
        return !TextUtils.isEmpty(name);
    }

    private boolean isContactValid() {
        String name = binding.ContactEditText.getText().toString().trim();
        return !TextUtils.isEmpty(name);
    }

    private boolean isUnitsValid() {
        String units = binding.UnitEditText.getText().toString().trim();

        try {
            int unitsValue = Integer.parseInt(units);
            return unitsValue > 0;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Please enter valid unit", Toast.LENGTH_LONG).show();
        }

        return false;
    }
    private void uploadFile() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());

        // Create an instance of sale with the retrieved data
        Order order= new Order(ItemName, ImageUrl, binding.DescEditText.getText().toString(), binding.ContactEditText.getText().toString(),Integer.parseInt(binding.UnitEditText.getText().toString()), Category, new Date(), binding.AdditionalEdiText.getText().toString(), false);

        // Disable the button and show the progress bar
        binding.buttonNext.setEnabled(false);
        binding.progressBar.setVisibility(View.VISIBLE);

        // Show toast message for upload in progress
        Toast.makeText(getContext(), "Uploading data. Please wait...", Toast.LENGTH_SHORT).show();

        // Upload the data to the Firebase Realtime Database
        String uploadId = mDatabaseRef.child(currentDate).child(Category).push().getKey();
        mDatabaseRef.child(currentDate).child(Category).child(uploadId).setValue(order)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Data uploaded successfully
                        Toast.makeText(getContext(), "Data uploaded successfully", Toast.LENGTH_SHORT).show();

                        // Enable the button and hide the progress bar
                        binding.buttonNext.setEnabled(true);
                        binding.progressBar.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to upload data
                        Toast.makeText(getContext(), "Failed to upload data: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                        // Enable the button and hide the progress bar
                        binding.buttonNext.setEnabled(true);
                        binding.progressBar.setVisibility(View.GONE);
                    }
                });
    }
}