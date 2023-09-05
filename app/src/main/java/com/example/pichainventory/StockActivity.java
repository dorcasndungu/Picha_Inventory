package com.example.pichainventory;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.pichainventory.databinding.ActivityStockBinding;
import com.example.pichainventory.ui.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

public class StockActivity extends AppCompatActivity {

    ActivityStockBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityStockBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }
    public void logout(MenuItem item) {
    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
    alertDialogBuilder.setTitle("Logout");
    alertDialogBuilder.setMessage("Are you sure you want to logout?");

    // Set positive button  and its click listener
    alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(StockActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    });

    // Set negative button (No) and its click listener
    alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            // Dismiss the dialog
            dialog.dismiss();
        }
    });

    // Create and show the AlertDialog
    AlertDialog alertDialog = alertDialogBuilder.create();
    alertDialog.show();
}

}