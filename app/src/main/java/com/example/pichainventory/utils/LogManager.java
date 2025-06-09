package com.example.pichainventory.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LogManager {
    private static final String TAG = "LogManager";
    private static final String LOG_FILE_NAME = "app_log.txt";
    private static final int MAX_LOG_SIZE = 1024 * 1024; // 1MB max log size
    private static LogManager instance;
    private final Context context;
    private final File logFile;
    private final List<String> logs;
    private final SimpleDateFormat dateFormat;

    private LogManager(Context context) {
        this.context = context.getApplicationContext();
        this.logFile = new File(context.getFilesDir(), LOG_FILE_NAME);
        this.logs = new ArrayList<>();
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        ensureLogFileExists();
        
        // Add initial logs
        log("App started");
        log("Device: " + Build.MODEL);
        log("Android Version: " + Build.VERSION.RELEASE);
        log("SDK Level: " + Build.VERSION.SDK_INT);
        log("Manufacturer: " + Build.MANUFACTURER);
        log("Device: " + Build.DEVICE);
        log("Product: " + Build.PRODUCT);
        try {
            log("App Version: " + context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName);
        } catch (Exception e) {
            logError("Error getting app version", e);
        }
    }

    private void ensureLogFileExists() {
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                Log.e(TAG, "Error creating log file", e);
            }
        }
    }

    public static synchronized LogManager getInstance(Context context) {
        if (instance == null) {
            instance = new LogManager(context);
        }
        return instance;
    }

    public void log(String message) {
        try {
            String timestamp = dateFormat.format(new Date());
            String logMessage = String.format("[%s] INFO: %s", timestamp, message);
            logs.add(logMessage);
            Log.d(TAG, logMessage);
        } catch (Exception e) {
            Log.e(TAG, "Error logging message", e);
        }
    }

    public void logError(String message, Throwable error) {
        try {
            String timestamp = dateFormat.format(new Date());
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            error.printStackTrace(pw);
            
            String errorDetails = String.format("[%s] ERROR: %s\nStack Trace:\n%s", 
                timestamp, message, sw.toString());
            logs.add(errorDetails);
            Log.e(TAG, errorDetails);
        } catch (Exception e) {
            Log.e(TAG, "Error logging error message", e);
        }
    }

    public void logWarning(String message) {
        try {
            String timestamp = dateFormat.format(new Date());
            String logMessage = String.format("[%s] WARNING: %s", timestamp, message);
            logs.add(logMessage);
            Log.w(TAG, logMessage);
        } catch (Exception e) {
            Log.e(TAG, "Error logging warning message", e);
        }
    }

    private void rotateLogFile() {
        try {
            // Create backup file with timestamp
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                    .format(new Date());
            File backupFile = new File(context.getFilesDir(), "app_log_" + timestamp + ".txt");
            if (!logFile.renameTo(backupFile)) {
                Log.e(TAG, "Failed to rename log file for rotation");
                return;
            }
            
            // Create new log file
            if (!logFile.createNewFile()) {
                Log.e(TAG, "Failed to create new log file after rotation");
            }
        } catch (IOException e) {
            Log.e(TAG, "Error rotating log file", e);
        }
    }

    public void exportLogs(Activity activity) {
        try {
            // Create logs directory if it doesn't exist
            File logsDir = new File(context.getCacheDir(), "logs");
            if (!logsDir.exists()) {
                logsDir.mkdirs();
            }

            // Create the log file
            File logFile = new File(logsDir, "exported_logs.txt");
            FileWriter writer = new FileWriter(logFile);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);

            // Write logs to file
            bufferedWriter.write("=== Picha Inventory App Logs ===\n\n");
            bufferedWriter.write("Log Entries:\n");
            bufferedWriter.write("----------------------------------------\n");
            for (String log : logs) {
                bufferedWriter.write(log);
                bufferedWriter.newLine();
            }
            bufferedWriter.close();

            // Get URI using FileProvider
            Uri logFileUri = FileProvider.getUriForFile(
                context,
                context.getPackageName() + ".provider",
                logFile
            );

            // Create share intent
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_STREAM, logFileUri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            // Start share activity using the Activity context
            activity.startActivity(Intent.createChooser(shareIntent, "Share Logs"));
        } catch (Exception e) {
            Log.e(TAG, "Error exporting logs", e);
            Toast.makeText(context, "Error exporting logs: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void clearLogs() {
        try {
            if (logFile.exists()) {
                if (!logFile.delete()) {
                    Log.e(TAG, "Failed to delete log file");
                    return;
                }
                if (!logFile.createNewFile()) {
                    Log.e(TAG, "Failed to create new log file after clearing");
                    return;
                }
                Toast.makeText(context, "Logs cleared successfully", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Log.e(TAG, "Error clearing logs", e);
            Toast.makeText(context, "Error clearing logs: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
} 