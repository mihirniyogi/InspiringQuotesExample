package com.mihirniyogi.inspiringquotesexample;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private final OkHttpClient client = new OkHttpClient();
    private final String API_URL = "https://api.api-ninjas.com/v1/quotes";
    private TextView quoteTextView;
    private TextView authorTextView;
    private Button fetchQuoteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialiseUi();
    }

    private void initialiseUi() {
        quoteTextView = findViewById(R.id.quoteTextView);
        authorTextView = findViewById(R.id.authorTextView);
        fetchQuoteButton = findViewById(R.id.fetchQuoteButton);
        fetchQuoteButton.setOnClickListener(v -> fetchQuote());
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
                    if (jsonArray.length() > 0) {
                        JSONObject quoteObject = jsonArray.getJSONObject(0);
                        final String quote = quoteObject.getString("quote");
                        final String author = quoteObject.getString("author");

                        runOnUiThread(() -> {
                            quoteTextView.setText(quote);
                            authorTextView.setText(author);
                        });
                    } else {
                        runOnUiThread(() -> quoteTextView.setText("No quotes found"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> quoteTextView.setText("Parse error"));
                }
            }
        });
    }
}