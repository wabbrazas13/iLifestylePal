package com.example.ilifestylepal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class DailyActivity_Activity extends AppCompatActivity {

    private Button btn_ActivityList, btn_ActivityLog, btn_DailyActivityRecommendation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dailyactivity_activity);
        SetupToolbar();

        btn_ActivityList = findViewById(R.id.btn_ActivityList);
        btn_ActivityList.setOnClickListener(view ->{
            getSupportActionBar().setTitle("Activity List");
            replaceFragment(new DailyActivity_ActivityList_Fragment());});
        btn_ActivityLog = findViewById(R.id.btn_ActivityLog);
        btn_ActivityLog.setOnClickListener(view -> {
            getSupportActionBar().setTitle("Activity Log");
            replaceFragment(new DailyActivity_ActivityLog_Fragment());});
        btn_DailyActivityRecommendation = findViewById(R.id.btn_DailyActivityRecommendation);
        btn_DailyActivityRecommendation.setOnClickListener(view ->{
            getSupportActionBar().setTitle("Recommendation");
            replaceFragment(new DailyActivity_Recommendation_Fragment());
        });

    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void SetupToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.daily_activity_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Daily Activity");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}