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

import com.google.api.LogDescriptor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Food_Restricted_Fragment extends Fragment{

    private List<Food_Data> list;
    private FoodAdapter adapter;
    private String current_userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private List<String> ingredientsList;//, ingredientsListElse;

    public Food_Restricted_Fragment() {
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

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

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
        Restrict();
    }

    final String TAG = "Restrict";

    private void Restrict(){
        DatabaseReference firebase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userHealthConditionRef = firebase.child("Users").child(current_userID).child("Health Conditions");
        DatabaseReference userAllergiesRef = firebase.child("Users").child(current_userID).child("Allergies");
        DatabaseReference allFoodRefInIf = firebase.child("Maintenance").child("Food").child("Meal");
        DatabaseReference restrictedFood = firebase.child("Maintenance").child("Food").child("Recommendation");

        list.clear();
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
                        restrictedFood.child(userHealthConditions.getKey()).child("Restricted")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for(DataSnapshot foodRecommendations: snapshot.getChildren()){
                                            Log.d(TAG, "Restricted Food base on Health Condition: " + foodRecommendations.getKey());

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
                                                            }

                                                            boolean isDuplicate = false;

                                                            // Check if the list already contains a food with the same name
                                                            for (Food_Data existingFood : list) {
                                                                String existingFoodname = existingFood.getFoodname();
                                                                if (existingFoodname != null && existingFoodname.equals(foodname)) { // Add null check before invoking equals
                                                                    isDuplicate = true;
                                                                    break;
                                                                }
                                                            }
                                                            // If the food is not a duplicate, add it to the list
                                                            if (!isDuplicate) {
                                                                Food_Data food = new Food_Data(foodname, calorie, url, ingredientsList);
                                                                list.add(food);
                                                            }

                                                            Log.d(TAG, "Adding the Food to Restricted: " + foodname);
                                                            adapter.notifyDataSetChanged();

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
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "getUserHealthCondition:onCancelled", databaseError.toException());
            }
        });

        userAllergiesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Create a set of food names that have already been added
                    Set<String> addedFoods = new HashSet<>();

                    for (DataSnapshot userAllergy : snapshot.getChildren()) {
                        String allergy = userAllergy.getKey();
                        Log.d(TAG, "User is allergic to " + allergy);

                        allFoodRefInIf.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    String foodname = snapshot.child("foodname").getValue(String.class);
                                    String calorie = snapshot.child("calorie").getValue(String.class);
                                    String url = snapshot.child("url").getValue(String.class);

                                    // Get the list of ingredients
                                    final List<String> ingredientsList = new ArrayList<>();
                                    DataSnapshot ingredientsSnapshot = snapshot.child("Ingredients");
                                    for (DataSnapshot ingredientSnapshot : ingredientsSnapshot.getChildren()) {
                                        String ingredientName = ingredientSnapshot.getKey();
                                        ingredientsList.add(ingredientName);
                                        //Log.d(TAG, "Ingredient: " + ingredientName);
                                    }

                                    // Check if the food contains any of the user's allergies
                                    for (String ingredient : ingredientsList) {
                                        if (ingredient.toLowerCase().contains(allergy.toLowerCase())) {
                                            if (!addedFoods.contains(foodname)) {
                                                Log.d(TAG, foodname + " contains " + allergy + ", adding to list of foods to avoid");
                                                Food_Data food = new Food_Data(foodname, calorie, url, ingredientsList);
                                                Log.d(TAG, "FOOD NAME: " + foodname);
                                                Log.d(TAG, "URL: " + url);
                                                Log.d(TAG, "CALORIE: " + calorie);
                                                Log.d(TAG, "INGREDIENT AND ALLERGY: " + allergy + ", " + ingredientsList);
                                                list.add(food);
                                                addedFoods.add(foodname);
                                            }
                                            break;
                                        }
                                    }
                                }
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
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
            final String RESTRICTED = "RESTRICTED";
            final int color = R.color.cancel_friend;
            Food_Data food = foodList.get(position);

            holder.textViewFoodname.setText(food.getFoodname());
            holder.textViewCalorie.setText(food.getCalorie());
            holder.tvIsRecommend.setText(RESTRICTED);
            holder.tvIsRecommend.setTextColor(ContextCompat.getColor(getContext(), color));
            holder.tvIsRecommend.setVisibility(View.VISIBLE);
            Picasso.with(holder.itemView.getContext()).load(food.getUrl()).into(holder.imageViewFood);

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
