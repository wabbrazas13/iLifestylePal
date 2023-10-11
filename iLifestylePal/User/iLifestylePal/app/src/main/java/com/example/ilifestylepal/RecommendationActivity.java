package com.example.ilifestylepal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

public class RecommendationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendation);

        SetupToolbar();
    }

    private void SetupToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.recommendation_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Recommendation");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}