package com.mihirniyogi.inspiringquotesexample;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

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

    }
}