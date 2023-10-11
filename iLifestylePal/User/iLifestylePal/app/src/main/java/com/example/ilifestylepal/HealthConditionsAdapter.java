package com.example.ilifestylepal;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class HealthConditionsAdapter extends RecyclerView.Adapter<HealthConditionsAdapter.MyViewHolder> implements Filterable {

    ArrayList<HealthConditionsModel> mHealthConditions;
    ArrayList<HealthConditionsModel> mHealthConditionsFull;

    public HealthConditionsAdapter(ArrayList<HealthConditionsModel> mHealthConditions) {
        this.mHealthConditionsFull = mHealthConditions;
        this.mHealthConditions = new ArrayList<>(mHealthConditionsFull);
    }

    @NonNull
    @Override
    public HealthConditionsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_health_conditions, parent, false);
        return new HealthConditionsAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HealthConditionsAdapter.MyViewHolder holder, int position) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String currentUserID = mAuth.getCurrentUser().getUid();
        DatabaseReference DatabaseUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);

        HealthConditionsModel model = mHealthConditions.get(position);
        holder.cb_health_condition.setText(String.valueOf(model.getValue()));

        DatabaseUserRef.child("Health Conditions").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(model.getValue())) {
                    holder.cb_health_condition.setChecked(true);
                } else {
                    holder.cb_health_condition.setChecked(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DB Health Conditions", "Error: " + error.getMessage());
            }
        });

        holder.cb_health_condition.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                DatabaseUserRef.child("Health Conditions").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (isChecked) {
                            // perform some action when checkbox is checked
                            if (!snapshot.hasChild(model.getValue())) {
                                DatabaseUserRef.child("Health Conditions").child(model.getValue()).setValue(true);
                            }
                        } else {
                            // perform some action when checkbox is unchecked
                            if (snapshot.hasChild(model.getValue())) {
                                DatabaseUserRef.child("Health Conditions").child(model.getValue()).removeValue();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("DB Health Conditions", "Error: " + error.getMessage());
                    }
                });
            }
        });
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        CheckBox cb_health_condition;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cb_health_condition = itemView.findViewById(R.id.cb_health_condition);
        }
    }

    @Override
    public int getItemCount() {
        return mHealthConditions.size();
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    private final Filter mFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<HealthConditionsModel> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(mHealthConditionsFull);
            } else {
                // Filter the data based on the constraint
                for (HealthConditionsModel data : mHealthConditionsFull) {
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
            mHealthConditions.clear();
            mHealthConditions.addAll((ArrayList)results.values);
            notifyDataSetChanged();
        }
    };
}
