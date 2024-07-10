package com.example.pichainventory.ui_fragments;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.pichainventory.CategoryAdapter;
import com.example.pichainventory.Models.Order;
import com.example.pichainventory.R;
import com.example.pichainventory.databinding.FragmentStcordBinding;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StcOrdFragment extends Fragment {
    FragmentStcordBinding binding;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private Uri sendUri;
    private String ItemName;
    private String ImageUrl;
    private String Category;
    private String uid;
    private boolean isNewOrder = false;

    public StcOrdFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentStcordBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();
        List<String> categories = Arrays.asList("Toys", "Flowers", "Bedding", "Shoes", "Beauty", "Baby", "Clothes", "Decor", "Other");

        CategoryAdapter categoryAdapter = new CategoryAdapter(requireContext(), categories);
        binding.spinner.setAdapter(categoryAdapter);

        Bundle args = getArguments();
        if (args != null) {
            isNewOrder = args.getBoolean("isNewOrder", false);
            uid = args.getString("mUid");
            if (uid == null || uid.isEmpty()) {
                throw new IllegalArgumentException("UID must not be null or empty");
            }

            mStorageRef = FirebaseStorage.getInstance().getReference(uid).child("Orders");
            mDatabaseRef = FirebaseDatabase.getInstance().getReference(uid).child("Orders");

            if (!isNewOrder) {
                String itemName = args.getString("itemName");
                String imageUrl = args.getString("imageUrl");
                String category = args.getString("category");

                binding.nameLabel.setText(itemName);
                Picasso.get()
                        .load(imageUrl)
                        .placeholder(R.drawable.placeholder)
                        .fit()
                        .centerCrop()
                        .into(binding.profilePhoto);

                ItemName = itemName;
                ImageUrl = imageUrl;
                Category = category;
            }
        }

        binding.profilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(StcOrdFragment.this)
                        .compress(1024)
                        .maxResultSize(1080, 1080)
                        .createIntent(intent -> {
                            startForProfileImageResult.launch(intent);
                            return null;
                        });
            }
        });

        binding.buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFormValid()) {
                    if (sendUri != null) {
                        uploadFile();
                    } else {
                        saveOrderToDatabase(); // Save without image
                    }
                } else {
                    Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_LONG).show();
                }
            }
        });

        return rootView;
    }

    private final ActivityResultLauncher<Intent> startForProfileImageResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Uri uri = data.getData();
                        sendUri = uri;
                        Picasso.get().load(uri).into(binding.profilePhoto);
                    }
                }
            });

    private String getFileExtension(Uri uri) {
        ContentResolver cR = requireActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private boolean isFormValid() {
        return isNameValid() && isDescValid() && isContactValid() && isUnitsValid();
    }

    private boolean isNameValid() {
        String name = binding.nameLabel.getText().toString().trim();
        return !TextUtils.isEmpty(name);
    }

    private boolean isDescValid() {
        String desc = binding.DescEditText.getText().toString().trim();
        return !TextUtils.isEmpty(desc);
    }

    private boolean isContactValid() {
        String contact = binding.ContactEditText.getText().toString().trim();
        return !TextUtils.isEmpty(contact);
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
        String uploadId = mDatabaseRef.push().getKey(); // Generate unique key for new orders
        StorageReference fileReference = mStorageRef.child(uploadId)
                .child(System.currentTimeMillis() + "." + getFileExtension(sendUri));

        fileReference.putFile(sendUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                ImageUrl = uri.toString();
                                saveOrderToDatabase(uploadId);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveOrderToDatabase() {
        String uploadId = mDatabaseRef.push().getKey();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());

        ItemName = binding.nameLabel.getText().toString().trim();
        Category = binding.spinner.getSelectedItem().toString();

        Order order = new Order(
                ItemName,
                ImageUrl,
                binding.DescEditText.getText().toString().trim(),
                binding.ContactEditText.getText().toString().trim(),
                Integer.parseInt(binding.UnitEditText.getText().toString().trim()),
                Category,
                new Date(),
                binding.AdditionalEdiText.getText().toString().trim(),
                "Red",
                0,
                uploadId
        );

        binding.buttonNext.setEnabled(false);
        binding.progressBar.setVisibility(View.VISIBLE);

        mDatabaseRef.child(uploadId).setValue(order)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Order saved successfully", Toast.LENGTH_SHORT).show();
                        binding.buttonNext.setEnabled(true);
                        binding.progressBar.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed to save order: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        binding.buttonNext.setEnabled(true);
                        binding.progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private void saveOrderToDatabase(String uploadId) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());

        ItemName = binding.nameLabel.getText().toString().trim();
        Category = binding.spinner.getSelectedItem().toString();

        Order order = new Order(
                ItemName,
                ImageUrl,
                binding.DescEditText.getText().toString().trim(),
                binding.ContactEditText.getText().toString().trim(),
                Integer.parseInt(binding.UnitEditText.getText().toString().trim()),
                Category,
                new Date(),
                binding.AdditionalEdiText.getText().toString().trim(),
                "Red",
                0,
                uploadId
        );

        binding.buttonNext.setEnabled(false);
        binding.progressBar.setVisibility(View.VISIBLE);

        mDatabaseRef.child(uploadId).setValue(order)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Order saved successfully", Toast.LENGTH_SHORT).show();
                        binding.buttonNext.setEnabled(true);
                        binding.progressBar.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed to save order: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        binding.buttonNext.setEnabled(true);
                        binding.progressBar.setVisibility(View.GONE);
                    }
                });
    }
}
