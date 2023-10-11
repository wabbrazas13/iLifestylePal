package com.example.ilifestylepal;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.utilities.Tree;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class Food_Recommendation_Fragment extends Fragment{

    private List<Food_Data> list;
    private FoodAdapter adapter;
    private final String CURRENT_USERID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private List<String> ingredientsList;

    private final String TAG = "Recommendation";

    public Food_Recommendation_Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.food_foodlist_fragment, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.rvFoodList);
        list = new ArrayList<>();
        adapter = new FoodAdapter(list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        try{
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }
        catch(Exception e){
            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();

        }
        
        EditText edittext_Search = view.findViewById(R.id.etSearch);
        edittext_Search.setVisibility(View.GONE);
        edittext_Search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Code to be executed before text changes
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String searchText = edittext_Search.getText().toString().toLowerCase();
                ArrayList<Food_Data> filteredFood = new ArrayList<>();
                for (Food_Data food : list) {
                    if (food.getFoodname().toLowerCase().contains(searchText)) {
                        filteredFood.add(food);
                    }
                }
                adapter.updateFoodList(filteredFood);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Code to be executed after text changes
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Recommend();
    }

    boolean hasAllergy = false;
    public void Recommend1(){
        DatabaseReference firebase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userHealthConditionRef = firebase.child("Users").child(CURRENT_USERID).child("Health Conditions");
        DatabaseReference userAllergiesRef = firebase.child("Users").child(CURRENT_USERID).child("Allergies");
        DatabaseReference allFoodRefInIf = firebase.child("Maintenance").child("Food").child("Meal");
        DatabaseReference recommendedFood = firebase.child("Maintenance").child("Food").child("Recommendation");
        DatabaseReference allFoodRefInElse = FirebaseDatabase.getInstance().getReference("Maintenance").child("Food").child("Meal");

        list.clear();
        //Get the Health Condition of the User
        userHealthConditionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // User has a health condition saved in Firebase, retrieve it
                    List<String> healthConditionList = new ArrayList<>();
                    for (DataSnapshot userHealthConditions : dataSnapshot.getChildren()) {
                        healthConditionList.add(userHealthConditions.getKey());
                        Log.d(TAG, "Health Condition: " + userHealthConditions.getKey());

                        //Get the Recommended Food base on the Health Condition of the User
                        recommendedFood.child(userHealthConditions.getKey()).child("Recommended")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for(DataSnapshot foodRecommendations: snapshot.getChildren()){
                                            Log.d(TAG, "Food Recommendation base on Health Condition: " + foodRecommendations.getKey());

                                            String food = foodRecommendations.getKey();
                                            //Get All Information of the Food
                                            allFoodRefInIf.child(food)
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            String foodname = dataSnapshot.child("foodname").getValue(String.class);
                                                            String calorie = dataSnapshot.child("calorie").getValue(String.class);
                                                            String url = dataSnapshot.child("url").getValue(String.class);

                                                            // Get the list of ingredients
                                                            ingredientsList = new ArrayList<>();
                                                            DataSnapshot ingredientsSnapshot = dataSnapshot.child("Ingredients");

                                                            for (DataSnapshot ingredientSnapshot : ingredientsSnapshot.getChildren()) {
                                                                String ingredientName = ingredientSnapshot.getKey();
                                                                ingredientsList.add(ingredientName);
                                                                Log.d(TAG, "Foodname: " + foodname);
                                                                Log.d(TAG, "Ingredient: " + ingredientName);
                                                            }

                                                            // Check for allergies to all the ingredients
                                                            userAllergiesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                    if (snapshot.exists()) {
                                                                        for (DataSnapshot userAllergy : snapshot.getChildren()) {
                                                                            String allergy = userAllergy.getKey();
                                                                            Log.d(TAG, "Fetched Allergy: " + allergy);
                                                                            for (String ingredient : ingredientsList) {
                                                                                if (ingredient.toLowerCase().contains(allergy.toLowerCase())) {
                                                                                    Log.d(TAG, "Ingredient: " + ingredient + ", Allergy: " + allergy);
                                                                                    hasAllergy = true;
                                                                                    break;
                                                                                }
                                                                            }
                                                                            if (hasAllergy) {
                                                                                break;
                                                                            }
                                                                        }
                                                                    }
                                                                    // Check if the food has any of the user's allergies
                                                                    if (hasAllergy) {
                                                                        Log.d(TAG, "User has an allergy to " + foodname);
                                                                        return;
                                                                    } else {
                                                                        Log.d(TAG, "User does not have an allergy to " + foodname);
                                                                    }

                                                                    boolean isDuplicate = false;

                                                                    // Check if the list already contains a food with the same name
                                                                    for (Food_Data existingFood : list) {
                                                                        if (existingFood.getFoodname().equals(foodname)) {
                                                                            isDuplicate = true;
                                                                            break;
                                                                        }
                                                                    }
                                                                    // If the food is not a duplicate, add it to the list
                                                                    if (!isDuplicate) {
                                                                        Food_Data food = new Food_Data(foodname, calorie, url, ingredientsList);
                                                                        list.add(food);
                                                                    }

                                                                    Log.d("Health", "Adding the Food: " + foodname);
                                                                    adapter.notifyDataSetChanged();
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error) {
                                                                    Log.e("Health", "Error checking allergies: " + error.getMessage());
                                                                }
                                                            });
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });


                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                    }
                } else {
                    allFoodRefInElse.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            list.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String foodname = snapshot.child("foodname").getValue(String.class);
                                String calorie = snapshot.child("calorie").getValue(String.class);
                                String url = snapshot.child("url").getValue(String.class);

                                // Get the list of ingredients
                                final List<String> ingredientsListElse = new ArrayList<>(); // initialize inside the loop
                                DataSnapshot ingredientsSnapshot = snapshot.child("Ingredients");
                                for (DataSnapshot ingredientSnapshot : ingredientsSnapshot.getChildren()) {
                                    String ingredientName = ingredientSnapshot.getKey();
                                    ingredientsListElse.add(ingredientName);
                                    Log.d("Health", "Ingredient: " + ingredientName);
                                }

                                // Check for allergies to all the ingredients
                                userAllergiesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        boolean hasAllergy = false; // initialize inside the inner onDataChange method
                                        if (snapshot.exists()) {
                                            for (DataSnapshot userAllergy : snapshot.getChildren()) {
                                                String allergy = userAllergy.getKey();
                                                Log.d("Health", "foreach ng allergy = " + allergy);
                                                for (String ingredient : ingredientsListElse) {
                                                    Log.d("Health", "foreach ng ingredient" + ingredientsListElse);
                                                    if (ingredient.toLowerCase().contains(allergy.toLowerCase())) {
                                                        Log.d("Health", "Allergy: " + allergy.toLowerCase() +
                                                                "\n Ingredient is: " + ingredient.toLowerCase());
                                                        hasAllergy = true;
                                                        break;
                                                    }
                                                }
                                                if (hasAllergy) {
                                                    break;
                                                }
                                            }
                                        }
                                        // Check if the food has any of the user's allergies
                                        if (hasAllergy) {
                                            Log.d("Health", "User has an allergy to " + foodname);
                                            return;
                                        } else {
                                            Log.d("Health", "User does not have an allergy to " + foodname);
                                        }

                                        boolean isDuplicate = false;

                                        // Check if the list already contains a food with the same name
                                        for (Food_Data existingFood : list) {
                                            if (existingFood.getFoodname().equals(foodname)) {
                                                Log.d("Health", "is Food already exist?: ");
                                                isDuplicate = true;
                                                break;
                                            }
                                        }
                                        // If the food is not a duplicate, add it to the list
                                        if (!isDuplicate) {
                                            Log.d("Health", "If food is not duplicate: ");
                                            Food_Data food = new Food_Data(foodname, calorie, url, ingredientsListElse);
                                            list.add(food);


                                            Log.d("Health", "Adding the Food: " + foodname);
                                        }

                                        adapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "getUserHealthCondition:onCancelled", databaseError.toException());
            }
        });
    }

    public void Recommend() {
        DatabaseReference firebase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userHealthConditionRef = firebase.child("Users").child(CURRENT_USERID).child("Health Conditions");
        DatabaseReference userAllergiesRef = firebase.child("Users").child(CURRENT_USERID).child("Allergies");
        DatabaseReference recommendedFood = firebase.child("Maintenance").child("Food").child("Recommendation");
        DatabaseReference allFoodRefInElse = FirebaseDatabase.getInstance().getReference("Maintenance").child("Food").child("Meal");

        list.clear();

        userHealthConditionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<String> healthConditionList = new ArrayList<>();
                    for (DataSnapshot userHealthConditions : dataSnapshot.getChildren()) {
                        String healthCondition = userHealthConditions.getKey();
                        healthConditionList.add(healthCondition);

                        recommendedFood.child(healthCondition).child("Recommended")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot foodRecommendations : snapshot.getChildren()) {
                                            String food = foodRecommendations.getKey();

                                            DatabaseReference allFoodRef = firebase.child("Maintenance").child("Food").child("Meal").child(food);
                                            allFoodRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    String foodname = dataSnapshot.child("foodname").getValue(String.class);
                                                    String calorie = dataSnapshot.child("calorie").getValue(String.class);
                                                    String url = dataSnapshot.child("url").getValue(String.class);

                                                    // Get the list of ingredients
                                                    List<String> ingredientsList = new ArrayList<>();
                                                    DataSnapshot ingredientsSnapshot = dataSnapshot.child("Ingredients");

                                                    for (DataSnapshot ingredientSnapshot : ingredientsSnapshot.getChildren()) {
                                                        String ingredientName = ingredientSnapshot.getKey();
                                                        ingredientsList.add(ingredientName);
                                                    }

                                                    userAllergiesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            boolean hasAllergy = false;
                                                            if (snapshot.exists()) {
                                                                for (DataSnapshot userAllergy : snapshot.getChildren()) {
                                                                    String allergy = userAllergy.getKey();
                                                                    for (String ingredient : ingredientsList) {
                                                                        if (ingredient.toLowerCase().contains(allergy.toLowerCase())) {
                                                                            hasAllergy = true;
                                                                            break;
                                                                        }
                                                                    }
                                                                    if (hasAllergy) {
                                                                        break;
                                                                    }
                                                                }
                                                            }
                                                            if (!hasAllergy) {
                                                                boolean isDuplicate = false;
                                                                for (Food_Data existingFood : list) {
                                                                    String existingFoodname = existingFood.getFoodname();
                                                                    if (existingFoodname != null && existingFoodname.equals(foodname)) { // Add null check before invoking equals
                                                                        isDuplicate = true;
                                                                        break;
                                                                    }
                                                                }
                                                                if (!isDuplicate) {
                                                                    Food_Data food = new Food_Data(foodname, calorie, url, ingredientsList);
                                                                    list.add(food);
                                                                }
                                                            }
                                                            adapter.notifyDataSetChanged();
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                            Log.e("Health", "Error checking allergies: " + error.getMessage());
                                                        }
                                                    });
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                    Log.e("Health", "Error retrieving food details: " + databaseError.getMessage());
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Log.e("Health", "Error retrieving food recommendations: " + error.getMessage());
                                    }
                                });
                    }
                } else {
                    allFoodRefInElse.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            list.clear();
                            List<String> userAllergies = new ArrayList<>(); // Store user allergies

                            // Retrieve user allergies
                            userAllergiesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot allergiesSnapshot) {
                                    if (allergiesSnapshot.exists()) {
                                        for (DataSnapshot userAllergy : allergiesSnapshot.getChildren()) {
                                            userAllergies.add(userAllergy.getKey());
                                        }
                                    }

                                    // Fetch all food from Firebase
                                    for (DataSnapshot foodSnapshot : dataSnapshot.getChildren()) {
                                        String foodname = foodSnapshot.child("foodname").getValue(String.class);
                                        String calorie = foodSnapshot.child("calorie").getValue(String.class);
                                        String url = foodSnapshot.child("url").getValue(String.class);

                                        // Get the list of ingredients
                                        List<String> ingredientsListElse = new ArrayList<>();
                                        DataSnapshot ingredientsSnapshot = foodSnapshot.child("Ingredients");
                                        for (DataSnapshot ingredientSnapshot : ingredientsSnapshot.getChildren()) {
                                            String ingredientName = ingredientSnapshot.getKey();
                                            ingredientsListElse.add(ingredientName);
                                            Log.d("Health", "Ingredient: " + ingredientName);
                                        }

                                        boolean hasAllergy = false;

                                        // Check for allergies to all the ingredients
                                        for (String allergy : userAllergies) {
                                            for (String ingredient : ingredientsListElse) {
                                                if (ingredient.toLowerCase().contains(allergy.toLowerCase())) {
                                                    hasAllergy = true;
                                                    break;
                                                }
                                            }
                                            if (hasAllergy) {
                                                break;
                                            }
                                        }

                                        // Check if the food has any of the user's allergies
                                        if (hasAllergy) {
                                            Log.d("Health", "User has an allergy to " + foodname);
                                            continue; // Skip this food and move to the next iteration
                                        } else {
                                            Log.d("Health", "User does not have an allergy to " + foodname);
                                        }

                                        boolean isDuplicate = false;

                                        // Check if the list already contains a food with the same name
                                        for (Food_Data existingFood : list) {
                                            if (existingFood.getFoodname().equals(foodname)) {
                                                isDuplicate = true;
                                                break;
                                            }
                                        }
                                        // If the food is not a duplicate, add it to the list
                                        if (!isDuplicate) {
                                            Food_Data food = new Food_Data(foodname, calorie, url, ingredientsListElse);
                                            list.add(food);
                                            Log.d("Health", "Adding the Food: " + foodname);
                                        }
                                    }

                                    adapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.e("Health", "Error retrieving user allergies: " + error.getMessage());
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e("Health", "Error retrieving food details: " + databaseError.getMessage());
                        }
                    });
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {
        private List<Food_Data> foodList;

        public FoodAdapter(List<Food_Data> foodList) {
            this.foodList = foodList;
        }

        @NonNull
        @Override
        public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_list_items, parent, false);
            return new FoodViewHolder(view);
        }

        public void updateFoodList(List<Food_Data> newFoodList) {
            this.foodList = newFoodList;
            notifyDataSetChanged();
        }

        @Override
        public void onBindViewHolder(@NonNull FoodViewHolder holder, @SuppressLint("RecyclerView") int position) {
            //Recommend(holder, position);
            final String RECOMMEND = "RECOMMENDED";
            final int color = R.color.green;
            Food_Data food = foodList.get(position);

            //try{
                holder.textViewFoodname.setText(food.getFoodname());
                holder.textViewCalorie.setText(food.getCalorie());
                holder.tvIsRecommend.setText(RECOMMEND);
                holder.tvIsRecommend.setTextColor(ContextCompat.getColor(getContext(), color));
                holder.tvIsRecommend.setVisibility(View.VISIBLE);
                Picasso.with(holder.itemView.getContext()).load(food.getUrl()).into(holder.imageViewFood);
//            }
//           catch (Exception e){
//               Log.i("Holder", "onBindViewHolder: " + e.getMessage());
//           }

            holder.cvContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), Food_Add_Food.class);
                    intent.putExtra("FoodName", holder.textViewFoodname.getText().toString());
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return foodList.size();
        }

        class FoodViewHolder extends RecyclerView.ViewHolder {
            TextView textViewFoodname, textViewCalorie, tvIsRecommend;
            ImageView imageViewFood;
            CardView cvContainer;

            public FoodViewHolder(@NonNull View itemView) {
                super(itemView);
                textViewFoodname = itemView.findViewById(R.id.tvFoodName);
                imageViewFood = itemView.findViewById(R.id.ivFoodImage);
                textViewCalorie = itemView.findViewById(R.id.tvCalorie);
                cvContainer = itemView.findViewById(R.id.cvContainer);
                tvIsRecommend = itemView.findViewById(R.id.tvIsRecommend);
            }
        }
    }

}
