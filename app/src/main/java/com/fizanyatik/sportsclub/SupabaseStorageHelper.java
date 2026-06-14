package com.fizanyatik.sportsclub;

import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SupabaseStorageHelper {

    private static final String SUPABASE_URL = "https://qtoajoilytgvvaxgncvb.supabase.co";
    private static final String SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InF0b2Fqb2lseXRndnZheGduY3ZiIiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODE0NjAwOTUsImV4cCI6MjA5NzAzNjA5NX0.OmpF4gVy_rtgVhKTCeJ-sMBMmy1GuRxk_RSAr6Y8V-E";

    private static final OkHttpClient client = new OkHttpClient();

    public interface UploadCallback {
        void onSuccess(String publicUrl);
        void onFailure(String error);
    }

    /**
     * Uploads a file to Supabase Storage.
     *
     * @param context  Android context
     * @param bucket   Bucket name: "profile" or "feeds"
     * @param fileUri  URI from image picker
     * @param callback Returns public URL on success, error string on failure
     */
    public static void uploadFile(Context context, String bucket, Uri fileUri, UploadCallback callback) {
        new Thread(() -> {
            try {
                // Detect mime type and extension from URI
                String mimeType = context.getContentResolver().getType(fileUri);
                if (mimeType == null) mimeType = "image/jpeg";
                String ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
                if (ext == null) ext = "jpg";

                String fileName = System.currentTimeMillis() + "." + ext;

                InputStream inputStream = context.getContentResolver().openInputStream(fileUri);
                if (inputStream == null) {
                    callback.onFailure("Cannot open file");
                    return;
                }
                // readAllBytes() is API 26+; use manual read for minSdk 21 compatibility
                java.io.ByteArrayOutputStream buffer = new java.io.ByteArrayOutputStream();
                byte[] chunk = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(chunk)) != -1) {
                    buffer.write(chunk, 0, bytesRead);
                }
                inputStream.close();
                byte[] bytes = buffer.toByteArray();

                String uploadUrl = SUPABASE_URL + "/storage/v1/object/" + bucket + "/" + fileName;

                RequestBody body = RequestBody.create(bytes, MediaType.parse(mimeType));
                Request request = new Request.Builder()
                        .url(uploadUrl)
                        .post(body)
                        .addHeader("Authorization", "Bearer " + SUPABASE_ANON_KEY)
                        .addHeader("Content-Type", mimeType)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful()) {
                        String publicUrl = SUPABASE_URL + "/storage/v1/object/public/"
                                + bucket + "/" + fileName;
                        callback.onSuccess(publicUrl);
                    } else {
                        callback.onFailure("Upload failed: " + response.code() + " " + response.message());
                    }
                }
            } catch (IOException e) {
                callback.onFailure(e.getMessage());
            }
        }).start();
    }

    /**
     * Uploads raw bytes (e.g. an in-memory PDF) to Supabase Storage.
     *
     * @param bucket    Bucket name (e.g. "feeds")
     * @param fileName  Target filename in the bucket (e.g. "scorecard_1234.pdf")
     * @param bytes     Raw file bytes
     * @param mimeType  MIME type (e.g. "application/pdf")
     * @param callback  Returns public URL on success, error string on failure
     */
    public static void uploadBytes(String bucket, String fileName, byte[] bytes,
                                   String mimeType, UploadCallback callback) {
        new Thread(() -> {
            try {
                String uploadUrl = SUPABASE_URL + "/storage/v1/object/" + bucket + "/" + fileName;

                RequestBody body = RequestBody.create(bytes, MediaType.parse(mimeType));
                Request request = new Request.Builder()
                        .url(uploadUrl)
                        .post(body)
                        .addHeader("Authorization", "Bearer " + SUPABASE_ANON_KEY)
                        .addHeader("Content-Type", mimeType)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful()) {
                        String publicUrl = SUPABASE_URL + "/storage/v1/object/public/"
                                + bucket + "/" + fileName;
                        callback.onSuccess(publicUrl);
                    } else {
                        String errBody = response.body() != null ? response.body().string() : "";
                        callback.onFailure("Upload failed: " + response.code() + " " + errBody);
                    }
                }
            } catch (IOException e) {
                callback.onFailure(e.getMessage());
            }
        }).start();
    }
}
