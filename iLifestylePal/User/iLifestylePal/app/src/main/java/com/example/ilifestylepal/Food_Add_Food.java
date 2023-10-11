package com.example.ilifestylepal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Food_Add_Food extends AppCompatActivity {

    //Toolbar Views
    private ImageView ivBack, ivFoodImageBG, ivSave;
    private TextView tvFoodName;
    private TextInputEditText etCalories;
    private String foodName, url, calorie;
    private List<String> ingredientsList;
    private String TAG = "Food_Add_Food";
    private DatabaseReference databaseReference;
    private ListView listViewIngredient;
    private ScrollView SView;
    private AutoCompleteTextView autoComplete_tvMeal, autoComplete_tvServing;
    private String current_userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_add_food_activity);

        Intent foodItent = getIntent();
        foodName  = foodItent.getStringExtra("FoodName");
        listViewIngredient = findViewById(R.id.listViewIngredient);
        InitializeToolbar();
        InitializeView();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Maintenance").child("Food").child("Meal").child(foodName);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    calorie = snapshot.child("calorie").getValue().toString();
                    url = snapshot.child("url").getValue().toString();
                    Picasso.with(getApplicationContext()).load(url).into(ivFoodImageBG);

                    Map<String, Object> ingredientsMap = (Map<String, Object>) snapshot.child("Ingredients").getValue();
                    List<String> ingredientsList = new ArrayList<>(ingredientsMap.keySet());
                    // do something with the retrieved data
                    etCalories.setText(calorie);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, ingredientsList);
                    listViewIngredient.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to read value.", error.toException());
            }
        });


        ivBack.setOnClickListener(view ->{
            Intent intent = new Intent(Food_Add_Food.this, Food_Journal_Activity.class);
            startActivity(intent);
        });

        // Create an ArrayAdapter to display the choices in a dropdown menu
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinnerCategory, android.R.layout.simple_dropdown_item_1line);

        // Set the adapter to the AutoCompleteTextView
        autoComplete_tvMeal.setAdapter(adapter);

        // Set an OnClickListener to display the dropdown menu when the AutoCompleteTextView is clicked
        autoComplete_tvMeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoComplete_tvMeal.showDropDown();
            }
        });

        // Create an ArrayAdapter to display the choices in a dropdown menu
        ArrayAdapter<CharSequence> adapterServing = ArrayAdapter.createFromResource(this,
                R.array.spinnerServing, android.R.layout.simple_dropdown_item_1line);

        // Set the adapter to the AutoCompleteTextView
        autoComplete_tvServing.setAdapter(adapterServing);

        // Set an OnClickListener to display the dropdown menu when the AutoCompleteTextView is clicked
        autoComplete_tvServing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoComplete_tvServing.showDropDown();
            }
        });

        autoComplete_tvServing.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(autoComplete_tvServing.getText().toString().equals("0")){
                    etCalories.setText("1");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(autoComplete_tvServing.getText().toString().equals("1")){
                    etCalories.setText(calorie);
                }
                else{
                    int calorie1 = Integer.parseInt(calorie);
                    int serving = Integer.parseInt(autoComplete_tvServing.getText().toString());
                    int result = calorie1 * serving;
                    etCalories.setText(String.valueOf(result));
                }
            }
        });

        ivSave.setOnClickListener(view ->{
            String title = "Add Food";
            String msg ="Are you sure you want to Add "+ tvFoodName.getText().toString()+ " ?";
            String posButton = "Confirm";
            String negButton = "Cancel";

            DialogBox_Message dialogBox = new DialogBox_Message(this, title, msg, posButton, negButton);
            dialogBox.setPositiveButtonListener(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // User clicked the positive button, do something here
                    Toast.makeText(getApplicationContext(), "Adding food...", Toast.LENGTH_SHORT).show();
                    // Do your action here
                    try{
                        InsertFoodData();
                    }
                    catch (Exception e){
                        Toast.makeText(Food_Add_Food.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            dialogBox.displayDialogBox();

        });
    }

    private void InsertFoodData() {

        // Get today's date
        LocalDate today = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            today = LocalDate.now();
        }

        // Define the desired date format
        DateTimeFormatter formatter = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        }

        // Format the date using the formatter
        String formattedDate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formattedDate = today.format(formatter);
        }

        String date = formattedDate;

        String foodName = tvFoodName.getText().toString();
        String calorie = etCalories.getText().toString();
        String serving = autoComplete_tvServing.getText().toString();
        String meal = autoComplete_tvMeal.getText().toString();
        String foodURL = url;

        //if(foodName.equals(R.string.custom_spinner_ingredient_name_text) && calorie.equals(R.string.tvCalorie_text))
        if(TextUtils.isEmpty(foodName) && TextUtils.isEmpty(calorie)){
            Toast.makeText(getApplicationContext(), "Please Select a Dish", Toast.LENGTH_SHORT).show();
        }
        else{
            //FirebaseReference
            DatabaseReference foodJournalRef = FirebaseDatabase.getInstance().getReference().child("Food Journal");

            String id = foodJournalRef.push().getKey();

            Food_Data_Insert food_data_insert = new Food_Data_Insert(date, calorie, foodName, meal, serving, current_userID, foodURL);

            foodJournalRef.child(id).setValue(food_data_insert)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "Food Successfully Added", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
        }

    }

    private void InitializeView(){

        tvFoodName = findViewById(R.id.tvFoodName);
        etCalories = findViewById(R.id.etCalories);
        ivFoodImageBG = findViewById(R.id.ivFoodImageBG);

        tvFoodName.setText(foodName);
        autoComplete_tvMeal = findViewById(R.id.autoComplete_tvMeal);
        autoComplete_tvServing = findViewById(R.id.autoComplete_tvServing);

    }

    private void InitializeToolbar(){
        View toolbarView = findViewById(R.id.addFood_toolbar);
        toolbarView.requestFocus();
        ivSave = toolbarView.findViewById(R.id.ivSave);
        //btnSave = toolbarView.findViewById(R.id.btnSave);
        ivBack = toolbarView.findViewById(R.id.ivBack);
    }
}