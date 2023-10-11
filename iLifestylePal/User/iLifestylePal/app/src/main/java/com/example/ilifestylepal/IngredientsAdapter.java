package com.example.ilifestylepal;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.MyViewHolder> implements Filterable {

    ArrayList<IngredientsModel> mIngredientsList;
    ArrayList<IngredientsModel> mIngredientsListFull;

    public IngredientsAdapter(ArrayList<IngredientsModel> mIngredientsList) {
        this.mIngredientsListFull = mIngredientsList;
        this.mIngredientsList = new ArrayList<>(mIngredientsListFull);
    }

    @NonNull
    @Override
    public IngredientsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_health_conditions, parent, false);
        return new IngredientsAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientsAdapter.MyViewHolder holder, int position) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String currentUserID = mAuth.getCurrentUser().getUid();
        DatabaseReference DatabaseUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);

        IngredientsModel model = mIngredientsList.get(position);
        holder.cb_ingredient.setText(String.valueOf(model.getValue()));

        DatabaseUserRef.child("Allergies").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(model.getValue())) {
                    holder.cb_ingredient.setChecked(true);
                } else {
                    holder.cb_ingredient.setChecked(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Ingredients", "Error: " + error.getMessage());
            }
        });

        holder.cb_ingredient.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                DatabaseUserRef.child("Allergies").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (isChecked) {
                            // perform some action when checkbox is checked
                            if (!snapshot.hasChild(model.getValue())) {
                                DatabaseUserRef.child("Allergies").child(model.getValue()).setValue(true);
                            }
                        } else {
                            // perform some action when checkbox is unchecked
                            if (snapshot.hasChild(model.getValue())) {
                                DatabaseUserRef.child("Allergies").child(model.getValue()).removeValue();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Ingredients", "Error: " + error.getMessage());
                    }
                });
            }
        });
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        CheckBox cb_ingredient;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cb_ingredient = itemView.findViewById(R.id.cb_health_condition);
        }
    }

    @Override
    public int getItemCount() {
        return mIngredientsList.size();
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    private final Filter mFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<IngredientsModel> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(mIngredientsListFull);
            } else {
                // Filter the data based on the constraint
                for (IngredientsModel data : mIngredientsListFull) {
                    if (data.getValue().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        filteredList.add(data);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults results) {
            mIngredientsList.clear();
            mIngredientsList.addAll((ArrayList)results.values);
            notifyDataSetChanged();
        }
    };
}
