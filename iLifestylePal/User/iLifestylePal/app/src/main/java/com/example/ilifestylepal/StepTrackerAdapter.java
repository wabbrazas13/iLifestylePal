package com.example.ilifestylepal;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class StepTrackerAdapter extends RecyclerView.Adapter<StepTrackerAdapter.MyViewHolder> implements Filterable {

    ArrayList<StepTrackerModel> mDataList;
    ArrayList<StepTrackerModel> mDataListFull;

    public StepTrackerAdapter(ArrayList<StepTrackerModel> mDataList) {
        this.mDataListFull = mDataList;
        this.mDataList = new ArrayList<>(mDataListFull);
    }

    @NonNull
    @Override
    public StepTrackerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_step_tracker_records, parent, false);
        return new StepTrackerAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StepTrackerAdapter.MyViewHolder holder, int position) {
        StepTrackerModel model = mDataList.get(position);

        String step_id = model.getStep_id();
        String step_uid = model.getStep_uid();
        String step_weekday = model.getStep_weekday();
        String step_month = model.getStep_month();
        int step_day = model.getStep_day();
        int step_year = model.getStep_year();
        int step_count = model.getStep_count();
        int step_goal = model.getStep_goal();
        long step_timestamp = model.getStep_timestamp();

        // Create Instances
        SharedPreferences sharedPreferences = holder.itemView.getContext().getSharedPreferences("StepTracker", Context.MODE_PRIVATE);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String currentUserID = mAuth.getCurrentUser().getUid();
        DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        // Set Text For Day Today
        holder.tv_stepDate.setText(step_weekday + ", " + step_month + " " + step_day + " " + step_year);

        //Set Text for Steps
        holder.tv_stepCount.setText("" + step_count);

        // Fetch Step Size in local device
        String str_stepSize = sharedPreferences.getString("stepSize", "2.30");
        double savedStepSize = Double.parseDouble(str_stepSize);

        // Calculate and Set Distance to Textiew
        double distance = savedStepSize * step_count * 0.0003048;
        String formattedValue = String.format("%.2f", distance);
        holder.tv_stepDistance.setText(formattedValue + " km");

        // Fetch weight and use it to solve for kcal
        UsersRef.child(currentUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("weight")) {
                    double w = Double.parseDouble(snapshot.child("weight").getValue().toString().trim());
                    double kcal = 0.75 * w * distance;
                    String formattedValue2 = String.format("%.2f", kcal);
                    holder.tv_stepCalories.setText(formattedValue2 + " kcal");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Users", error.getMessage());
            }
        });
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_stepDate;
        TextView tv_stepCount;
        TextView tv_stepDistance;
        TextView tv_stepCalories;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_stepDate = itemView.findViewById(R.id.tv_stepDate);
            tv_stepCount = itemView.findViewById(R.id.tv_stepCount);
            tv_stepDistance = itemView.findViewById(R.id.tv_stepDistance);
            tv_stepCalories = itemView.findViewById(R.id.tv_stepCalories);

        }
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    private final Filter mFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<StepTrackerModel> filteredList = new ArrayList<>();

            filteredList.addAll(mDataListFull);

            FilterResults results = new FilterResults();
            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults results) {
            mDataList.clear();
            mDataList.addAll((ArrayList)results.values);
            notifyDataSetChanged();
        }
    };

}
