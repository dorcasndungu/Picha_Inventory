package com.example.pichainventory.ui_fragments;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.example.pichainventory.CategoryAdapter;
import com.example.pichainventory.Models.Upload;
import com.example.pichainventory.R;
import com.example.pichainventory.databinding.FragmentUploadBinding;
import com.example.pichainventory.utils.CloudinaryConfig;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import kotlin.Unit;


public class UploadFragment extends Fragment {

    FragmentUploadBinding binding;
    Uri sendUri;
    private StorageTask mUploadTask;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    public UploadFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUploadBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String uid =user.getUid();
        mStorageRef = FirebaseStorage.getInstance().getReference(uid).child("uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference(uid).child("uploads");
        List<String> categories = Arrays.asList("Toys", "Flowers", "Bedding", "Shoes", "Beauty", "Baby", "Clothes", "Decor", "Other");

        CategoryAdapter categoryAdapter = new CategoryAdapter(requireContext(), categories);
        binding.spinner.setAdapter(categoryAdapter);


        binding.productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(UploadFragment.this)	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .createIntent(intent->{startForProfileImageResult.launch(intent);
                            return Unit.INSTANCE;});
            }
        });

        binding.buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(getContext(), "Upload in progress", Toast.LENGTH_SHORT).show();
                } else if (isFormValid()) {
                    uploadFile();
                } else {
                    Toast.makeText(getContext(), "Please fill all required fields", Toast.LENGTH_LONG).show();
                }
            }
        });
        return rootView;}
    // Load image from Gallery

    private ActivityResultLauncher startForProfileImageResult=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        Uri uri=data.getData();
                        sendUri=uri;
                        Picasso.get().load(uri).into(binding.productImage);
                        binding.addImageHint.setVisibility(View.GONE); // Hide the hint when an image is selected
                    }
                }
            });
    private String getFileExtension(Uri uri) {
        ContentResolver cR = requireActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
    private boolean isFormValid() {
        return isNameValid() && isBuyingPriceValid() && isSellingPriceValid() && isUnitsValid();
    }
    private boolean isImageSelected() {
        if (sendUri == null) {
            Toast.makeText(getContext(), "Please select an image", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
    private boolean isNameValid() {
        String name = binding.nameEditText.getText().toString().trim();
        return !TextUtils.isEmpty(name);
    }

    private boolean isBuyingPriceValid() {
        String buyingPrice = binding.BuyingPEditText.getText().toString().trim();

        try {
            int buyingPriceValue = Integer.parseInt(buyingPrice);
            return buyingPriceValue >= 0;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return false;
    }

    private boolean isSellingPriceValid() {
        String sellingPrice = binding.SellingPEditText.getText().toString().trim();

        try {
            int sellingPriceValue = Integer.parseInt(sellingPrice);
            return sellingPriceValue > 0;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return false;
    }

    private boolean isUnitsValid() {
        String units = binding.unitsEditText.getText().toString().trim();

        try {
            int unitsValue = Integer.parseInt(units);
            return unitsValue >= 0;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return false;
    }

    private void uploadFile() {
        String selectedCategory = binding.spinner.getSelectedItem().toString();
        
        // Show progress
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.buttonNext.setEnabled(false);
        
        if (sendUri != null) {
            // Upload to Cloudinary if image is selected
            CloudinaryConfig.uploadImage(requireContext(), sendUri, new CloudinaryConfig.CloudinaryCallback() {
                @Override
                public void onSuccess(String cloudinaryUrl) {
                    saveToDatabase(selectedCategory, cloudinaryUrl);
                }

                @Override
                public void onError(String error) {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.buttonNext.setEnabled(true);
                    Toast.makeText(getContext(), "Upload failed: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Save without image
            saveToDatabase(selectedCategory, null);
        }
    }

    private void saveToDatabase(String selectedCategory, String imageUrl) {
        String uploadId = mDatabaseRef.child(selectedCategory).push().getKey();
        Upload upload = new Upload(
            binding.nameEditText.getText().toString().trim(),
            imageUrl, // This can be null
            Integer.parseInt(binding.BuyingPEditText.getText().toString().trim()),
            Integer.parseInt(binding.SellingPEditText.getText().toString().trim()),
            Integer.parseInt(binding.unitsEditText.getText().toString().trim()),
            selectedCategory,
            new Date(),
            binding.AdditionalEdiText.getText().toString().trim(),
            uploadId
        );

        // Save to Firebase Database
        mDatabaseRef.child(selectedCategory).child(uploadId).setValue(upload)
            .addOnSuccessListener(aVoid -> {
                binding.progressBar.setVisibility(View.GONE);
                binding.buttonNext.setEnabled(true);
                Toast.makeText(getContext(), "Upload successful", Toast.LENGTH_SHORT).show();
                // Clear form or navigate away
                clearForm();
            })
            .addOnFailureListener(e -> {
                binding.progressBar.setVisibility(View.GONE);
                binding.buttonNext.setEnabled(true);
                Toast.makeText(getContext(), "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
    }

    private void clearForm() {
        binding.nameEditText.setText("");
        binding.BuyingPEditText.setText("");
        binding.SellingPEditText.setText("");
        binding.unitsEditText.setText("");
        binding.AdditionalEdiText.setText("");
        binding.productImage.setImageResource(R.drawable.placeholder);
        sendUri = null;
    }
}




