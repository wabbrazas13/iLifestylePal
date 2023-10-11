package com.example.ilifestylepal;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.utils.widget.ImageFilterButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

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

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.*;
public class DailyActivity_Recommendation_Fragment extends Fragment {


    final int MINUTES_PER_HOUR = 60;
    private final String CURRENT_USERID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    List<DailyActivity_Retrieve_Data> list = new ArrayList<>();
    private String weight;
    private String goal;

    private final String TAG = "Recommendation";

    public DailyActivity_Recommendation_Fragment() {
        // Required empty public constructor
    }

    public static DailyActivity_Recommendation_Fragment newInstance() {
        DailyActivity_Recommendation_Fragment fragment = new DailyActivity_Recommendation_Fragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private TextView tvTip, tvTip1;
    private CardView tipCardView1, tipCardView2;
    private ImageFilterButton btnNextTip,btnPrevTip;
    int currentTipIndex = 0;


    ViewFlipper viewFlipper;
    RecyclerView rvActivityLog;
    List<DailyActivity_Retrieve_LogData> listLog;
    ActivityLogAdapter activityLogAdapter;
    DatabaseReference firebase = FirebaseDatabase.getInstance().getReference();
    CardView cardView;
    TextView tvMessage, tvMessage1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.dailyactivity_recommendation_fragment, container, false);

        tvMessage = view.findViewById(R.id.tvMessage);
        tvMessage1 = view.findViewById(R.id.tvMessage1);
        viewFlipper = view.findViewById(R.id.viewFlipper);
        tipCardView1 = view.findViewById(R.id.tipCardView1);
        tipCardView2 = view.findViewById(R.id.tipCardView2);
        tvTip1 = view.findViewById(R.id.tvTip1);
        tvTip = view.findViewById(R.id.tvTip);
        btnNextTip = view.findViewById(R.id.btnNextTip);
        btnPrevTip = view.findViewById(R.id.btnPrevTip);
        cardView = view.findViewById(R.id.cardView);

        rvActivityLog = view.findViewById(R.id.rvActivityLog);
        listLog = new ArrayList<>();
        activityLogAdapter = new ActivityLogAdapter(listLog);
        rvActivityLog.setLayoutManager(new LinearLayoutManager(getContext()));
        rvActivityLog.setAdapter(activityLogAdapter);

        getHealthCondition();
        retrieveTip(new TipCallback() {
            @Override
            public void onTipReceived( List<String> tips) {
                // Handle the received tips
                final int numTips = tips.size();
                // Display the initial tip
                tvTip.setText(tips.get(currentTipIndex));
                tvTip1.setText(tips.get(currentTipIndex));

                // Set up click listeners for the next and previous buttons
                btnNextTip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Increment the tip index
                        currentTipIndex++;

                        // Check if we have reached the end of the tips and loop back to the beginning
                        if (currentTipIndex >= numTips) {
                            currentTipIndex = 0;
                        }

                        viewFlipper.setInAnimation(getContext(), R.anim.slide_right);
                        viewFlipper.setOutAnimation(getContext(), R.anim.slide_left);
                        viewFlipper.showNext();
                        // Display the next tip
                        tvTip.setText(tips.get(currentTipIndex));
                        tvTip1.setText(tips.get(currentTipIndex));
                    }
                });

                btnPrevTip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Decrement the tip index
                        currentTipIndex--;

                        // Check if we have reached the beginning of the tips and loop back to the end
                        if (currentTipIndex < 0) {
                            currentTipIndex = numTips - 1;
                        }

                        viewFlipper.setInAnimation(getContext(), android.R.anim.slide_in_left);
                        viewFlipper.setOutAnimation(getContext(), android.R.anim.slide_out_right);
                        viewFlipper.showPrevious();
                        // Display the previous tip
                        tvTip.setText(tips.get(currentTipIndex));
                        tvTip1.setText(tips.get(currentTipIndex));

                    }
                });

            }
        });

        return view;
    }

    List<String> tips = new ArrayList<>();

    private void retrieveTip(final TipCallback callback) {
        DatabaseReference firebase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference reference = firebase.child("Maintenance").child("Daily Activity Tips");

        tips.add(getString(R.string.tip1));
        tips.add(getString(R.string.tip2));
        tips.add(getString(R.string.tip3));

        DatabaseReference userHealthConditionRef = firebase.child("Users").child(CURRENT_USERID).child("Health Conditions");

        userHealthConditionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Health conditions exist
                    List<String> healthConditions = new ArrayList<>();

                    for (DataSnapshot conditionSnapshot : dataSnapshot.getChildren()) {
                        String condition = conditionSnapshot.getKey();
                        healthConditions.add(condition);

                        reference.child(condition).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    for (DataSnapshot tipSnapshot : snapshot.getChildren()) {
                                        String tip = tipSnapshot.getValue().toString();
                                        tips.add(tip);
                                        Log.i(TAG, "TIP: " + tip);
                                    }
                                    callback.onTipReceived(tips);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Handle the error if needed
                            }
                        });
                    }

                }
                else{
                    callback.onTipReceived(tips);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public interface TipCallback {
        void onTipReceived( List<String> tips);
    }

    private void getHealthCondition() {
        DatabaseReference userHealthConditionRef = firebase.child("Users").child(CURRENT_USERID).child("Health Conditions");

        userHealthConditionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Health conditions exist
                    List<String> healthConditions = new ArrayList<>();

                    for (DataSnapshot conditionSnapshot : dataSnapshot.getChildren()) {
                        String condition = conditionSnapshot.getKey();
                        healthConditions.add(condition);
                    }

                    // Do something with the healthConditions list
                    // For example, you can pass it to another method or display the data
                    getRecommendedActivities(healthConditions);
                } else {
                    Log.i("Health", "No Health Condition: ");
                    DatabaseReference userGoal = firebase.child("Users").child(CURRENT_USERID);
                    userGoal.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            //if user has Goal compute for Duration else compute for burned calorie
                            int durationOrGoal;
                            if(snapshot.exists()){
                                if(snapshot.hasChild("calorieLeft")){
                                    //Compute for Duration
                                    durationOrGoal = Integer.parseInt(snapshot.child("calorieLeft").getValue().toString());
                                    Log.i("Health", "Goal: " + durationOrGoal);

                                    if(durationOrGoal <= 0){
                                        //Empty code block
                                        tvMessage.setText("Set up your Goal First");
                                        String message = "<font color='#FF0000'>Set up your Goal First</font>";
                                        tvMessage.setText(Html.fromHtml(message));
                                        tvMessage1.setVisibility(View.INVISIBLE);
                                        cardView.setVisibility(View.VISIBLE);
                                        Log.i("Health", "healthcondition: ");
                                    }
                                    else{
                                        //Show All Activity with duration
                                        showAllActivities(durationOrGoal);
                                    }
                                }
                                else{
                                    //NO HEALTH CONDITION AND NO GOAL
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle the error gracefully
            }
        });
    }

    private void getRecommendedActivities(List<String> healthConditions) {
        DatabaseReference recommendedActivity;

        for (String condition : healthConditions) {
            Log.i("Health", "processHealthConditions: " + condition);
            recommendedActivity = firebase.child("Maintenance").child("Daily Activity").child("Recommendation").child(condition);
            recommendedActivity.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    List<String> recommendedActivity = new ArrayList<>();

                    for(DataSnapshot activityRecommend: snapshot.getChildren()) {
                        Log.d("Health", "Activity Recommendation base on Health Condition: " + activityRecommend.getKey());
                        String recommended = activityRecommend.getKey();
                       recommendedActivity.add(recommended);
                    }
                    try{
                        getGoal(recommendedActivity);
                    }
                   catch (Exception e){
                       Log.i("Health", "catch Goal: " + e.getMessage());
                   }
                    //getRecommendActivity(recommendedActivity);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void getGoal(List<String> recommendedActivity){
        DatabaseReference userGoal = firebase.child("Users").child(CURRENT_USERID);
        userGoal.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //if user has Goal compute for Duration else compute for burned calorie
                int durationOrGoal;
                if(snapshot.exists()){
                    if(snapshot.hasChild("calorieLeft")){
                        //Compute for Duration
                        durationOrGoal = Integer.parseInt(snapshot.child("calorieLeft").getValue().toString());
                        Log.i("Health", "Goal: " + durationOrGoal);

                        if(durationOrGoal <= 0){
                            cardView.setVisibility(View.VISIBLE);
                        }
                        else{
                            getRecommendActivity(recommendedActivity, durationOrGoal, true);
                        }

                    }
                    else{
                        //Compute for Calorie
                        durationOrGoal = 20;
                       Log.i("Health", "No Goal: " + durationOrGoal);
                        getRecommendActivity(recommendedActivity, durationOrGoal, false);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showAllActivities(int durationOrGoal){
        //Getting more information about the recommended activity such as met
        DatabaseReference getRecommendActivity = firebase.child("Maintenance").child("Daily Activity").child("Activities");
        getRecommendActivity.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String activityName = snapshot.child("activityName").getValue(String.class);
                    String met = snapshot.child("met").getValue(String.class);

                    Log.i("Health", "getRecommend: " + activityName);
                    Log.i("Health", "getRecommend: " + met);

                    DailyActivity_Retrieve_Data dailyActivity_retrieve_data = new DailyActivity_Retrieve_Data(activityName, met);
                    list.add(dailyActivity_retrieve_data);

                    computeForDuration(list, durationOrGoal);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getRecommendActivity(List<String> recommendedActivity, int durationOrGoal, boolean hasGoal){
        //Getting more information about the recommended activity such as met
        for(String getRecommended: recommendedActivity){
            Log.i("Health", "processHealthConditions: " + getRecommended);
            DatabaseReference getRecommendActivity = firebase.child("Maintenance").child("Daily Activity").child("Activities");
            getRecommendActivity.child(getRecommended).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String activityName = snapshot.child("activityName").getValue(String.class);
                    String met = snapshot.child("met").getValue(String.class);

                    Log.i("Health", "getRecommend: " + activityName);
                    Log.i("Health", "getRecommend: " + met);

                    DailyActivity_Retrieve_Data dailyActivity_retrieve_data = new DailyActivity_Retrieve_Data(activityName, met);
                    list.add(dailyActivity_retrieve_data);

                    if(hasGoal){
                        //Compute for duration
                        Log.i("Health", "onDataChange: meron Goal");
                        computeForDuration(list, durationOrGoal);
                    }
                    else{
                        //Compute for Burned Calorie
                        computeForBurnedCalorie(list, durationOrGoal);
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    double dblWeight;

    private void computeForDuration(List<DailyActivity_Retrieve_Data> list, int compGoal) {

        DatabaseReference userHealthConditionRef = firebase.child("Users").child(CURRENT_USERID);

        userHealthConditionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("weight")) {
                    dblWeight = Double.parseDouble(dataSnapshot.child("weight").getValue().toString());

                    for (DailyActivity_Retrieve_Data activity : list) {
                        String activityName = activity.getActivityname();
                        String met = activity.getMet();

                        double metPerMinute = Double.parseDouble(met) / MINUTES_PER_HOUR;
                        double resultPerMinute = metPerMinute * dblWeight;

                        // Calculate the duration required to achieve the desired result
                        double durationInMinutes = Double.valueOf(compGoal) / resultPerMinute;

                        DecimalFormat df = new DecimalFormat("#.##");
                        durationInMinutes = Double.parseDouble(df.format(durationInMinutes));

                        // Round off the duration to the nearest integer
                        int duration = (int) Math.round(durationInMinutes);

                        Log.i("Recommendation", "Activity: " + activityName);
                        Log.i("Recommendation", "Goal: " + String.valueOf(compGoal));
                        Log.i("Recommendation", "Duration: " + duration);

                        boolean isDuplicate = false;
                        for (DailyActivity_Retrieve_LogData existingActivity : listLog) {
                            if (existingActivity.getActivityName().equals(activityName)) {
                                isDuplicate = true;
                                break;
                            }

                        }

                        if (!isDuplicate) {
                            // Add the data to the list
                            DailyActivity_Retrieve_LogData activity_retrieve_logData = new DailyActivity_Retrieve_LogData(activityName, String.valueOf(compGoal), String.valueOf(duration));
                            listLog.add(activity_retrieve_logData);
                        }
                        activityLogAdapter.notifyDataSetChanged();

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle the error gracefully
            }
        });
    }
    private void computeForBurnedCalorie(List<DailyActivity_Retrieve_Data> list, int duration) {

        Log.i("Health", "Did you read this before you mess up: ");

        DatabaseReference userHealthConditionRef = firebase.child("Users").child(CURRENT_USERID);

        userHealthConditionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("weight")) {
                    dblWeight = Double.parseDouble(dataSnapshot.child("weight").getValue().toString());

                    //Computing for Duration
                    for (DailyActivity_Retrieve_Data activity : list) {
                        String activityName = activity.getActivityname();
                        String met = activity.getMet();

                        double metPerMinute = Double.parseDouble(met) / MINUTES_PER_HOUR;
                        double resultPerMinute = metPerMinute * dblWeight;

                        // Calculate the burned calorie
                        double burnedCalorie = resultPerMinute * duration;

                        DecimalFormat df = new DecimalFormat("#.##");
                        burnedCalorie = Double.parseDouble(df.format(burnedCalorie));

                        // Round off the burned calorie to the nearest integer
                        int goal = (int) Math.round(burnedCalorie);

                        Log.i("Health", "Activity: " + activityName);
                        Log.i("Health", "Duration: " + duration);
                        Log.i("Health", "Burned Calorie: " + burnedCalorie);
                        int durationInt = (int) Math.round(duration);

                        boolean isDuplicate = false;
                        for (DailyActivity_Retrieve_LogData existingActivity : listLog) {
                            if (existingActivity.getActivityName().equals(activityName)) {
                                isDuplicate = true;
                                break;
                            }

                        }

                        if (!isDuplicate) {
                            // Add the data to the list
                            DailyActivity_Retrieve_LogData activity_retrieve_logData = new DailyActivity_Retrieve_LogData(activityName, String.valueOf(burnedCalorie), String.valueOf(durationInt));
                            listLog.add(activity_retrieve_logData);
                        }
                        activityLogAdapter.notifyDataSetChanged();
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle the error gracefully
            }
        });
    }

    public class ActivityLogAdapter extends RecyclerView.Adapter<ActivityLogAdapter.ViewHolder> {

        private List<DailyActivity_Retrieve_LogData> logDataList;

        // Constructor to initialize the list
        public ActivityLogAdapter(List<DailyActivity_Retrieve_LogData> logDataList) {
            this.logDataList = logDataList;
        }

        // Create ViewHolder class
        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvActivityLogName, tvActivityLogBurnedCalorie, tvActivityLogDuration, tvDurationLabel, tvActivityLogDate;
            CardView cvActivityLogContainer;

            public ViewHolder(View itemView) {
                super(itemView);
                tvActivityLogName = itemView.findViewById(R.id.tvActivityLogName);
                tvActivityLogBurnedCalorie = itemView.findViewById(R.id.tvActivityLogBurnedCalorie);
                tvActivityLogDuration = itemView.findViewById(R.id.tvActivityLogDuration);
                tvDurationLabel = itemView.findViewById(R.id.tvDurationLabel);
                tvActivityLogDate = itemView. findViewById(R.id.tvActivityLogDate);

                cvActivityLogContainer = itemView.findViewById(R.id.cvActivityLogContainer);
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dailyactivity_log_items, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            DailyActivity_Retrieve_LogData logData = logDataList.get(position);

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

            String date = formattedDate; // Assign the value of formattedDate to date
            String activityName = logData.getActivityName();
            String goal = logData.getGoal();
            String duration = logData.getDuration();

            holder.tvActivityLogName.setText(activityName);
            holder.tvActivityLogBurnedCalorie.setText(goal);
            holder.tvActivityLogDuration.setText(duration);


            Log.i("onBindViewHolder", "Activity: " + logData.getActivityName());
            Log.i("onBindViewHolder", "Goal: " + logData.getGoal());
            Log.i("onBindViewHolder", "Duration: " + logData.getDuration());

            if(Double.parseDouble(holder.tvActivityLogDuration.getText().toString()) > 1){
                holder.tvDurationLabel.setText("Minutes");
            }
            holder.tvActivityLogDate.setVisibility(View.GONE);

            holder.cvActivityLogContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String title = "Add Recommended Activity";
                    String msg ="Are you sure you want to Add selected Activity?";
                    String posButton = "Confirm";
                    String negButton = "Cancel";

                    DialogBox_Message dialogBox = new DialogBox_Message(getContext(), title, msg, posButton, negButton);
                    dialogBox.setPositiveButtonListener(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // User clicked the positive button, do something here
                            Toast.makeText(getContext(), "Adding Activity...", Toast.LENGTH_SHORT).show();
                            // Do your action here
                            try{
                                InsertActivity(date, activityName, goal, duration);
                            }
                            catch (Exception e){
                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    dialogBox.displayDialogBox();
                }
            });

        }

        private void InsertActivity(String date, String activityName, String goal, String duration){
            //FirebaseReference
            DatabaseReference foodJournalRef = FirebaseDatabase.getInstance().getReference().child("Daily Activity");

            String id = foodJournalRef.push().getKey();

            DailyActivity_Insert_Data dailyActivity_insert_data = new DailyActivity_Insert_Data(date, activityName, goal, CURRENT_USERID, duration);

            foodJournalRef.child(id).setValue(dailyActivity_insert_data)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getContext(), "Activity Successfully Added", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
        }

        @Override
        public int getItemCount() {
            Log.i("onBindViewHolder", "size: " + logDataList.size());
            return logDataList.size();

        }
    }


}