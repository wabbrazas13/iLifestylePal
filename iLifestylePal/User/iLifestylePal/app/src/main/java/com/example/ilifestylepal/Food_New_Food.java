package com.example.ilifestylepal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.api.LogDescriptor;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Food_New_Food extends AppCompatActivity {

    private ImageView ivBack, ivSave;
    private ImageButton addIngredientButton;
    private RecyclerView rvIngredients;
    private EditText etCalories, etFoodName, urlEditText;
    private AutoCompleteTextView ingredientEditText;
    private List<String> ingredientsList = new ArrayList<>();
    private AutoCompleteTextView autoComplete_tvMeal;

    private static final int REQUEST_IMAGE = 1;
    private static final int PERMISSION_REQUEST_CODE = 2;

    private ImageView selectedImageView;
    private Button chooseImageButton;

    Food_New_Food.IngredientAdapter adapter;

    final String TAG = "NewFood";
    Uri selectedImageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_new_food);
        InitializeViews();
        InitializeToolbar();


        rvIngredients.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Food_New_Food.IngredientAdapter(ingredientsList);
        rvIngredients.setAdapter(adapter);

        // In the onClickListener of the addIngredientButton
        addIngredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ingredient = ingredientEditText.getText().toString().trim();
                if (!TextUtils.isEmpty(ingredient)) {
                    ingredientsList.add(ingredient);
                    rvIngredients.getAdapter().notifyItemInserted(ingredientsList.size() - 1);
                    ingredientEditText.setText("");
                }
            }
        });

        // Set click listener for chooseImageButton
        chooseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check for permission to access external storage
                if (ContextCompat.checkSelfPermission(Food_New_Food.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    // Request permission if not granted
                    ActivityCompat.requestPermissions(Food_New_Food.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            PERMISSION_REQUEST_CODE);
                } else {
                    // Permission already granted, start image selection
                    startImageSelection();
                }
            }
        });

        ivBack.setOnClickListener(view ->{
            Intent intent = new Intent(Food_New_Food.this, Food_Journal_Activity.class);
            startActivity(intent);
        });

        // Create an ArrayAdapter to display the choices in a dropdown menu
        ArrayAdapter<CharSequence> mealAdapter = ArrayAdapter.createFromResource(this,
                R.array.spinnerCategory, android.R.layout.simple_dropdown_item_1line);

        // Set the adapter to the AutoCompleteTextView
        autoComplete_tvMeal.setAdapter(mealAdapter);

        // Set an OnClickListener to display the dropdown menu when the AutoCompleteTextView is clicked
        autoComplete_tvMeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoComplete_tvMeal.showDropDown();
            }
        });

        ivSave.setOnClickListener(view -> {
            ProgressDialog progressDialog = new ProgressDialog(Food_New_Food.this);
            progressDialog.setMessage("Saving...");

            FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference("Food");
                    String foodName = etFoodName.getText().toString().trim();
                    String calories = etCalories.getText().toString().trim();
                    String category = autoComplete_tvMeal.getText().toString().trim();

                    // Check if food name, calories, selected image, and ingredients list are empty
                    if (foodName.isEmpty() || calories.isEmpty() || selectedImageUri == null || ingredientsList.isEmpty()) {
                        if (foodName.isEmpty()) {
                            etFoodName.setError("Food name is required");
                        }
                        if (calories.isEmpty()) {
                            etCalories.setError("Calories is required");
                        }
                        if (selectedImageUri == null) {
                            // Show an error message for the image selection
                            Toast.makeText(Food_New_Food.this, "Please select an image", Toast.LENGTH_SHORT).show();
                        }
                        if (ingredientsList.isEmpty()) {
                            ingredientEditText.setError("Add at least one ingredient");
                        }
                        return; // Exit the click listener without saving
                    }

                     progressDialog.show();
                    // Upload the file to Firebase Storage
                    storageRef.child(foodName).putFile(selectedImageUri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // Get the download URL of the uploaded image
                                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri downloadUrl) {
                                            String imageUrl = downloadUrl.toString(); // This is the URL of the uploaded image

                                            // Continue with your logic here
                                            insertFoodData(foodName, calories, category, imageUrl, ingredientsList);
                                            insertIngredient(ingredientsList);
                                            clearViews();
                                            Log.i(TAG, "selected image uri: " + selectedImageUri);
                                            Log.i(TAG, "image URL: " + imageUrl);
                                            progressDialog.dismiss();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Handle any errors that occurred while retrieving the download URL
                                            Log.e(TAG, "Failed to get download URL: " + e.getMessage());
                                            progressDialog.dismiss();
                                        }
                                    });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle any errors that occurred while uploading the file
                                    Log.e(TAG, "Failed to upload file: " + e.getMessage());
                                }
                            });
                });

            showIngredientsFromFirebase();


    }

    private void clearViews(){
        // Clear the food name input field
        etFoodName.setText("");
        etCalories.setText("");
        selectedImageUri = null;
        selectedImageView.setImageDrawable(null);
        selectedImageView.setVisibility(View.GONE);
        ingredientsList.clear();
        adapter.notifyDataSetChanged();
        ingredientEditText.setText("");
        Toast.makeText(Food_New_Food.this, "Success", Toast.LENGTH_SHORT).show();
    }
    private void insertFoodData(String foodName, String calorie, String category, String url, List<String> ingredients) {
        // Get a reference to the Firebase Realtime Database
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

        // Create a new map to store the food data
        Map<String, Object> foodData = new HashMap<>();

        // Add the ingredients to the food data map
        Map<String, Boolean> ingredientsMap = new HashMap<>();
        for (String ingredient : ingredients) {
            ingredientsMap.put(ingredient, true);
        }
        foodData.put("Ingredients", ingredientsMap);

        // Add the calorie, food name, category, and URL to the food data map
        foodData.put("calorie", calorie);
        foodData.put("foodname", foodName);
        foodData.put("category", category);
        foodData.put("status", "pending");
        foodData.put("url", url);

        Log.d(TAG, "Food Data: " + foodData.toString());

        // Push the food data to the "Meal" node under the "Food" node under the "Maintenance" node
        databaseRef.child("Maintenance").child("Food").child("Meal").child(foodName).setValue(foodData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Food_New_Food.this, "Food Successfully Added", Toast.LENGTH_SHORT).show();
                            Log.i(TAG, "Success to ADD food");
                        } else {
                            Toast.makeText(Food_New_Food.this, "Failed to add food", Toast.LENGTH_SHORT).show();
                            Log.i(TAG, "Failed to Add Food");
                        }
                    }
                });
    }
    private void insertIngredient(List<String> ingredientsList) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference()
                .child("Maintenance").child("Food").child("Ingredients");

        for (String ingredient : ingredientsList) {
            String key = ingredient; // Use the ingredient name as the key

            // Check if the ingredient already exists in Firebase
            databaseRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        // The ingredient does not exist, add it to Firebase
                        Map<String, Object> ingredientMap = new HashMap<>();
                        ingredientMap.put("value", ingredient);
                        ingredientMap.put("status", "pending");
                        // Add other fields as needed to the ingredientMap

                        databaseRef.child(key).setValue(ingredientMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Ingredient insertion success
                                        // Handle the success event if needed
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "onFailure: " + e.getMessage());
                                        // Ingredient insertion failed
                                        // Handle the failure event if needed
                                    }
                                });
                    } else {
                        // The ingredient already exists in Firebase, handle accordingly
                        Log.d(TAG, "Ingredient already exists: " + ingredient);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d(TAG, "onCancelled: " + databaseError.getMessage());
                    // Handle the cancellation event if needed
                }
            });
        }
    }

    // Handle image selection result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            // Inside onActivityResult() method
            selectedImageUri = data.getData();
            Picasso.with(this).load(selectedImageUri).into(selectedImageView);
            selectedImageView.setVisibility(View.VISIBLE);

        }
    }

    // Start image selection
    private void startImageSelection() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void showIngredientsFromFirebase(){
        // Get a reference to your Firebase database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference().child("Maintenance").child("Food").child("Ingredients");

        // Attach a listener to retrieve the value of the ingredients from Firebase
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> ingredientsList = new ArrayList<>();

                // Loop through the children of the Ingredients node and add them to the list
                for (DataSnapshot ingredientSnapshot : dataSnapshot.getChildren()) {
                    String ingredient = ingredientSnapshot.child("value").getValue(String.class);
                    ingredientsList.add(ingredient);
                }

                // Convert the list of ingredients to an array of strings
                String[] ingredientsArray = ingredientsList.toArray(new String[0]);

                // Set the ingredients array as the completion hint for your AutoCompleteTextView
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, ingredientsArray);
                ingredientEditText.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle errors
            }
        });
    }

    private void InitializeViews(){
        addIngredientButton = findViewById(R.id.addIngredientButton);
        rvIngredients = findViewById(R.id.rvIngredients);
        ingredientEditText = findViewById(R.id.ingredientEditText);
        etCalories = findViewById(R.id.etCalories);
        etFoodName = findViewById(R.id.etFoodName);
        urlEditText = findViewById(R.id.urlEditText);
        chooseImageButton = findViewById(R.id.chooseImageButton);
        selectedImageView = findViewById(R.id.selectedImageView);

    }

    private void InitializeToolbar(){
        View toolbarView = findViewById(R.id.addFood_toolbar);
        toolbarView.requestFocus();
        ivSave = toolbarView.findViewById(R.id.ivSave);
        autoComplete_tvMeal = findViewById(R.id.autoComplete_tvMeal);
        ivBack = toolbarView.findViewById(R.id.ivBack);
    }




    public class IngredientAdapter extends RecyclerView.Adapter<Food_New_Food.IngredientAdapter.IngredientViewHolder> {
        private List<String> ingredientsList;

        public IngredientAdapter(List<String> ingredientsList) {
            this.ingredientsList = ingredientsList;
        }

        @NonNull
        @Override
        public Food_New_Food.IngredientAdapter.IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_ingredient_item, parent, false);
            return new Food_New_Food.IngredientAdapter.IngredientViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull Food_New_Food.IngredientAdapter.IngredientViewHolder holder, int position) {
            String ingredient = ingredientsList.get(position);
            holder.bind(ingredient);

        }
        @Override
        public int getItemCount() {
            return ingredientsList.size();
        }

        class IngredientViewHolder extends RecyclerView.ViewHolder {
            private TextView tvIngredient;

            public IngredientViewHolder(@NonNull View itemView) {
                super(itemView);
                tvIngredient = itemView.findViewById(R.id.tvIngredient);
            }

            public void bind(String ingredient) {
                tvIngredient.setText(ingredient);
            }
        }
    }
}
