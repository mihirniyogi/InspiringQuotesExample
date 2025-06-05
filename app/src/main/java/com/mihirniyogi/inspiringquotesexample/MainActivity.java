package com.mihirniyogi.inspiringquotesexample;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.appdistribution.FirebaseAppDistribution;
import com.google.firebase.appdistribution.InterruptionLevel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private final OkHttpClient client = new OkHttpClient();
    private final String API_URL = "https://api.api-ninjas.com/v1/quotes";
    private final String NOTIF_PERM = Manifest.permission.POST_NOTIFICATIONS;
    private TextView quoteTextView;
    private TextView authorTextView;
    private Button fetchQuoteButton;
    private FirebaseAppDistribution distribution;

    private final ActivityResultLauncher<String> permissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    Log.d("Permission", "Notification granted");
                    showFeedbackNotification();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialise all text views, buttons
        initialiseUi();

        // request notification permission
        requestNotifPermission();

        // show notification
        showFeedbackNotification();
    }

    private void initialiseUi() {
        quoteTextView = findViewById(R.id.quoteTextView);
        authorTextView = findViewById(R.id.authorTextView);
        fetchQuoteButton = findViewById(R.id.fetchQuoteButton);
        fetchQuoteButton.setOnClickListener(v -> fetchQuote());
        distribution = FirebaseAppDistribution.getInstance();
    }

    private boolean isNotifPermissionGranted() {
        return ActivityCompat.checkSelfPermission(this, NOTIF_PERM) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestNotifPermission() {
        if (BuildConfig.BUILD_TYPE.equals("release")
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && !isNotifPermissionGranted()) {
            permissionLauncher.launch(NOTIF_PERM);
        }
    }

    private void showFeedbackNotification() {
        if (BuildConfig.BUILD_TYPE.equals("release") && isNotifPermissionGranted()) {
            distribution.showFeedbackNotification("Please leave your feedback here.", InterruptionLevel.HIGH);
        }
    }

    private void fetchQuote() {
        Request request = new Request.Builder()
                .url(API_URL)
                .addHeader("X-Api-Key", BuildConfig.QUOTE_API_KEY)
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> quoteTextView.setText("Error: " + e.getMessage()));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(() -> quoteTextView.setText("Error: " + response.code()));
                    return;
                }

                try {
                    String jsonData = response.body().string();
                    JSONArray jsonArray = new JSONArray(jsonData);
                    if (jsonArray.length() == 0) {
                        runOnUiThread(() -> quoteTextView.setText("No quotes found"));
                        return;
                    }
                    JSONObject quoteObject = jsonArray.getJSONObject(0);
                    final String quote = quoteObject.getString("quote");
                    final String author = quoteObject.getString("author");

                    runOnUiThread(() -> {
                        quoteTextView.setText(quote);
                        authorTextView.setText(author);
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> quoteTextView.setText("Parse error"));
                }
            }
        });
    }
}