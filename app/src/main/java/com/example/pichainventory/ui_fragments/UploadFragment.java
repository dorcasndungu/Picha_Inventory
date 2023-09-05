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
import com.example.pichainventory.databinding.FragmentUploadBinding;
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

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import kotlin.Unit;


public class UploadFragment extends Fragment {

    FragmentUploadBinding binding;
    Uri sendUri;
    String key="";
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


        binding.profilePhoto.setOnClickListener(new View.OnClickListener() {
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
                    if (sendUri != null) {
                        uploadFile();
                    } else {
                        Toast.makeText(getContext(), "No file chosen", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_LONG).show();
                }
            }
        });
        return rootView;}
    private ActivityResultLauncher startForProfileImageResult=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        Uri uri=data.getData();
                        sendUri=uri;
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
        return isNameValid() && isBuyingPriceValid() && isSellingPriceValid() && isUnitsValid();
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
        if (sendUri != null) {
            String selectedCategory = binding.spinner.getSelectedItem().toString();
            StorageReference fileReference = mStorageRef.child(selectedCategory).child(System.currentTimeMillis()
                    + "." + getFileExtension(sendUri));

            mUploadTask = fileReference.putFile(sendUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            // Show the progress bar and set initial progress
                            binding.progressBar.setVisibility(View.VISIBLE);
                            binding.progressBar.setProgress(0);

                            Toast.makeText(getContext(), "Upload started", Toast.LENGTH_SHORT).show();

                            // Observe the upload progress
                            mUploadTask = fileReference.putFile(sendUri)
                                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                            // Calculate the progress percentage
                                            double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();

                                            // Update the progress bar
                                            binding.progressBar.setProgress((int) progress);
                                        }
                                    })
                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            binding.progressBar.setVisibility(View.GONE);
                                            Toast.makeText(getContext(), "Upload successful", Toast.LENGTH_LONG).show();

                                            // Get the image download URL
                                            Task<Uri> downloadUrlTask = taskSnapshot.getStorage().getDownloadUrl();
                                            downloadUrlTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    String imageUrl = uri.toString();
                                                    String selectedCategory = binding.spinner.getSelectedItem().toString();
                                                    String uploadId = mDatabaseRef.child(selectedCategory).push().getKey();
                                                    Upload upload = new Upload(binding.nameEditText.getText().toString().trim(),
                                                            imageUrl,
                                                            Integer.parseInt(binding.BuyingPEditText.getText().toString().trim()),
                                                            Integer.parseInt(binding.SellingPEditText.getText().toString().trim()),
                                                            Integer.parseInt(binding.unitsEditText.getText().toString().trim()),
                                                            binding.spinner.getSelectedItem().toString(),
                                                            new Date(),
                                                            binding.AdditionalEdiText.getText().toString().trim(),uploadId);
                                                    mDatabaseRef.child(selectedCategory).child(uploadId).setValue(upload);
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                        }
                                    });
                        }
                    });
        }
    }}




