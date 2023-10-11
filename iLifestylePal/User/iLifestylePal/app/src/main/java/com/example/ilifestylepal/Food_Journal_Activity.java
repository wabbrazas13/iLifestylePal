package com.example.ilifestylepal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

public class Food_Journal_Activity extends AppCompatActivity {

    private Button btnFoodList, btnFoodEaten, btnRecommendation, btnFoodRestricted;
    private HorizontalScrollView hsv;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_journal);
        SetupToolbar();
        InitializeViews();

        btnRecommendation = findViewById(R.id.btn_FoodRecommendation);

        btnFoodList.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                int[] location = new int[2];
                btnFoodList.getLocationInWindow(location);
                int x = location[0];
                hsv.smoothScrollTo(x, 0);
                getSupportActionBar().setTitle("Food List");
                replaceFragment(new Food_Foodlist_Fragment());
            }
        });

        btnFoodEaten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[] location = new int[2];
                btnFoodEaten.getLocationInWindow(location);
                int x = location[0];
                hsv.smoothScrollTo(x, 0);
                getSupportActionBar().setTitle("Food Log");
                replaceFragment(new Food_FoodLog_Fragment());
            }
        });

        btnRecommendation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[] location = new int[2];
                btnRecommendation.getLocationInWindow(location);
                int x = location[0];
                hsv.smoothScrollTo(x, 0);
                getSupportActionBar().setTitle("Food Recommendation");
                replaceFragment(new Food_Recommendation_Fragment());
            }
        });

        btnFoodRestricted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[] location = new int[2];
                btnFoodRestricted.getLocationInWindow(location);
                int x = location[0];
                getSupportActionBar().setTitle("Restricted Foods");
                hsv.smoothScrollTo(x, 0);
                replaceFragment(new Food_Restricted_Fragment());
            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.commit();
    }
    private void InitializeViews() {
        btnFoodList = findViewById(R.id.btn_FoodList);
        btnFoodRestricted = findViewById(R.id.btn_FoodRestricted);
        btnFoodEaten = findViewById(R.id.btn_FoodEaten);
        hsv = findViewById(R.id.hsv);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_new_food) {
//            AddNode_Inputs ai = new AddNode_Inputs(this);
//
//            ai.displayDialog();

            Intent intent = new Intent(this, Food_New_Food.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        FragmentManager fragmentManager = this.getSupportFragmentManager();

        String title = "Back";
        String msg ="Are you sure you want to go back?";
        String posButton = "Confirm";
        String negButton = "Cancel";

        if (fragmentManager.getBackStackEntryCount() > 0) {
            DialogBox_Message dialogBox = new DialogBox_Message(this, title, msg, posButton, negButton);
            dialogBox.setPositiveButtonListener(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    fragmentManager.popBackStackImmediate();
                }
            });
            dialogBox.displayDialogBox();
        } else {
            this.finish();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.food_overflow_menu, menu);
        return true;
    }
    private void SetupToolbar() {
        Toolbar mToolbar = findViewById(R.id.foodjournal_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Foods");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}