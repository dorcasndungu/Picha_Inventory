package com.example.pichainventory.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;

import java.util.HashMap;
import java.util.Map;

public class CloudinaryConfig {
    private static final String TAG = "CloudinaryConfig";
    private static boolean isInitialized = false;

    public static void init(Context context) {
        if (!isInitialized) {
            Map<String, String> config = new HashMap<>();
            config.put("cloud_name", "du02pkrhf");
            config.put("api_key", "969168282689158");
            config.put("api_secret", "auUdgVgAb3W2LzU5upZ4-0292xs");
            config.put("secure", "true");
            
            MediaManager.init(context, config);
            isInitialized = true;
        }
    }

    public static void uploadImage(Context context, Uri imageUri, final CloudinaryCallback callback) {
        if (!isInitialized) {
            init(context);
        }

        String requestId = MediaManager.get()
                .upload(imageUri)
                .option("transformation", "f_auto,q_auto") // Auto format and quality optimization
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        Log.d(TAG, "Upload started");
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {
                        Log.d(TAG, "Upload progress: " + bytes + "/" + totalBytes);
                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        String secureUrl = (String) resultData.get("secure_url");
                        Log.d(TAG, "Upload success: " + secureUrl);
                        callback.onSuccess(secureUrl);
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Log.e(TAG, "Upload error: " + error.getDescription());
                        callback.onError(error.getDescription());
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {
                        Log.e(TAG, "Upload rescheduled: " + error.getDescription());
                    }
                })
                .dispatch();
    }

    public interface CloudinaryCallback {
        void onSuccess(String imageUrl);
        void onError(String error);
    }
} 