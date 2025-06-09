package com.example.pichainventory;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.pichainventory.databinding.ActivityStockBinding;
import com.example.pichainventory.ui.LoginActivity;
import com.example.pichainventory.ui_fragments.DisplayBoxFragment;
import com.example.pichainventory.utils.LogManager;
import com.google.firebase.auth.FirebaseAuth;

public class StockActivity extends AppCompatActivity {
    private static final String TAG = "StockActivity";
    private ActivityStockBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            binding = ActivityStockBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            // Set up toolbar
            setSupportActionBar(binding.toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle("Stock Management");

            // Log activity creation
            LogManager.getInstance(this).log("StockActivity created");

            // Initialize fragment
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerView, new DisplayBoxFragment())
                    .commit();
                LogManager.getInstance(this).log("DisplayBoxFragment added to StockActivity");
            }
        } catch (Exception e) {
            LogManager.getInstance(this).logError("Error in StockActivity onCreate", e);
            Toast.makeText(this, "Error initializing activity: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            getMenuInflater().inflate(R.menu.menu_main, menu);
            LogManager.getInstance(this).log("Options menu created in StockActivity");
            return true;
        } catch (Exception e) {
            LogManager.getInstance(this).logError("Error creating options menu", e);
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            int id = item.getItemId();
            if (id == R.id.action_export_logs) {
                LogManager.getInstance(this).log("User initiated log export");
                LogManager.getInstance(this).exportLogs(this);
                return true;
            } else if (id == R.id.account) {
                LogManager.getInstance(this).log("User clicked account menu item");
                logout(item);
                return true;
            }
            return super.onOptionsItemSelected(item);
        } catch (Exception e) {
            LogManager.getInstance(this).logError("Error handling menu selection", e);
            Toast.makeText(this, "Error handling menu action: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public void logout(MenuItem item) {
        try {
            LogManager.getInstance(this).log("User logging out");
            FirebaseAuth.getInstance().signOut();
            // Redirect to login screen
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            LogManager.getInstance(this).logError("Error during logout", e);
            Toast.makeText(this, "Error logging out: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}