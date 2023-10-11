package com.example.ilifestylepal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class Food_Foodlist_Fragment extends Fragment {

    private RecyclerView recyclerView;
    private List<Food_Data> list;
    private FoodAdapter adapter;
    private DatabaseReference databaseReference;
    private EditText edittext_Search;
    private String current_userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private List<String> ingredientsList;
    private Context context;

    private DatabaseReference addFoodRef;

    public Food_Foodlist_Fragment() {
        // Required empty public constructor
    }

    public static Food_Foodlist_Fragment newInstance() {
        Food_Foodlist_Fragment fragment = new Food_Foodlist_Fragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.food_foodlist_fragment, container, false);
        recyclerView = view.findViewById(R.id.rvFoodList);
        list = new ArrayList<>();
        adapter = new FoodAdapter(list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        databaseReference = FirebaseDatabase.getInstance().getReference("Maintenance").child("Food").child("Meal");

        addFoodRef = FirebaseDatabase.getInstance().getReference("Maintenance").child("Food").child("Meal");

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        edittext_Search = view.findViewById(R.id.etSearch);
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
        try{
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    list.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String foodname = snapshot.child("foodname").getValue(String.class);
                        String calorie = snapshot.child("calorie").getValue(String.class);
                        String url = snapshot.child("url").getValue(String.class);

                        // Get the list of ingredients
                        ingredientsList = new ArrayList<>();
                        DataSnapshot ingredientsSnapshot = snapshot.child("Ingredients");
                        for (DataSnapshot ingredientSnapshot : ingredientsSnapshot.getChildren()) {
                            String ingredientName = ingredientSnapshot.getKey();
                            ingredientsList.add(ingredientName);
                        }

                        Food_Data food = new Food_Data(foodname, calorie, url);
                        list.add(food);
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        catch (Exception e){
            Log.i("TAG", e.getMessage());
        }

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
            Food_Data food = foodList.get(position);
            holder.textViewFoodname.setText(food.getFoodname());
            holder.textViewCalorie.setText(food.getCalorie());
            try{
                Picasso.with(holder.itemView.getContext()).load(food.getUrl()).into(holder.imageViewFood);
            }
            catch (Exception e){
                Log.i("TAG", e.getMessage());
            }

            holder.cvContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Food_Insert_Dialog foodLogDialog = new Food_Insert_Dialog(getContext(), current_userID);
//                   foodLogDialog.displayDialog(food.getFoodname(), food.getCalorie(), food.getUrl());

//                    AddNode_Inputs ai = new AddNode_Inputs(getContext());
//                    ai.displayDialog();
                try{
                    Intent intent = new Intent(getContext(), Food_Add_Food.class);
                    intent.putExtra("FoodName", holder.textViewFoodname.getText().toString());
                    startActivity(intent);
                }
                    catch (Exception e){
                        Log.d("eyo", e.getMessage());
                    }

                }
            });

        }


        @Override
        public int getItemCount() {
            return foodList.size();
        }

        class FoodViewHolder extends RecyclerView.ViewHolder {
            TextView textViewFoodname, textViewCalorie;
            ImageView imageViewFood;
            CardView cvContainer;

            public FoodViewHolder(@NonNull View itemView) {
                super(itemView);
                textViewFoodname = itemView.findViewById(R.id.tvFoodName);
                imageViewFood = itemView.findViewById(R.id.ivFoodImage);
                textViewCalorie = itemView.findViewById(R.id.tvCalorie);
                cvContainer = itemView.findViewById(R.id.cvContainer);
            }
        }
    }



}
