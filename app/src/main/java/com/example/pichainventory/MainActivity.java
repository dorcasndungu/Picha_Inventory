package com.example.pichainventory;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.pichainventory.databinding.ActivityMainBinding;
import com.example.pichainventory.ui.LoginActivity;
import com.example.pichainventory.utils.CloudinaryConfig;
import com.example.pichainventory.utils.LogManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            binding = ActivityMainBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            // Initialize Cloudinary
            CloudinaryConfig.init(this);

            // Add test logs
            LogManager.getInstance(this).log("App started");
            LogManager.getInstance(this).log("Device: " + android.os.Build.MODEL);
            LogManager.getInstance(this).log("Android Version: " + android.os.Build.VERSION.RELEASE);
            
            // Log Firebase auth state
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                LogManager.getInstance(this).log("User logged in: " + currentUser.getEmail());
            } else {
                LogManager.getInstance(this).log("No user logged in");
                // Redirect to login if no user
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
            Toast.makeText(this, "Error initializing app: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}