package com.example.ilifestylepal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class DailyActivity_ActivityLog_Fragment extends Fragment {

    RecyclerView rvActivityLog;
    List<DailyActivity_Retrieve_LogData> list;
    ActivityLogAdapter activityLogAdapter;

    private FirebaseAuth mAuth;
    private String current_userID;
    private DatabaseReference DatabaseUsersRef;
    double caloriesBurned;
    double totalBurnedCalories = 0;
    public DailyActivity_ActivityLog_Fragment() {
        // Required empty public constructor
    }

    public static DailyActivity_ActivityLog_Fragment newInstance() {
        DailyActivity_ActivityLog_Fragment fragment = new DailyActivity_ActivityLog_Fragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        readData("Today");
    }
    TextView tv_TotalBurnedCalorie,tvActivityDateRange, tvCalorieLeft, tv_Label;
    Button btnActivityToday, btnActivityWeek, btnActivityMonth, btnActivityAll;
    LinearLayout linearRecommend;
    EditText etGoal;
    ImageButton ivSave;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.dailyactivity_activity_log_fragment, container, false);

        rvActivityLog = view.findViewById(R.id.rvActivityLog);
        list = new ArrayList<>();
        activityLogAdapter = new ActivityLogAdapter(list);
        rvActivityLog.setLayoutManager(new LinearLayoutManager(getContext()));
        rvActivityLog.setAdapter(activityLogAdapter);
        tvActivityDateRange = view.findViewById(R.id.tvActivityDateRange);
        etGoal = view.findViewById(R.id.etGoal);
        linearRecommend = view.findViewById(R.id.linearRecommend);

        tv_Label = view.findViewById(R.id.tv_Label);
        ivSave = view.findViewById(R.id.ivSave);
        tv_TotalBurnedCalorie = view.findViewById(R.id.tv_TotalBurnedCalorie);
        tvCalorieLeft = view.findViewById(R.id.tvCalorieLeft);
        btnActivityToday = view.findViewById(R.id.btnActivityToday);
        btnActivityWeek = view.findViewById(R.id.btnActivityWeek);
        btnActivityMonth = view.findViewById(R.id.btnActivityMonth);
        btnActivityAll = view.findViewById(R.id.btnActivityAll);

        mAuth = FirebaseAuth.getInstance();
        current_userID = mAuth.getCurrentUser().getUid();


        btnActivityToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearRecommend.setVisibility(View.VISIBLE);
                readData("Today");
            }
        });
        btnActivityWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputFilter[] filters = new InputFilter[1];
                filters[0] = new InputFilter.LengthFilter(20); // Set the desired maximum character length
                linearRecommend.setVisibility(View.VISIBLE);
                readData("Week"); // refresh the list with the new date range filter
            }
        });

        btnActivityMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputFilter[] filters = new InputFilter[1];
                filters[0] = new InputFilter.LengthFilter(20); // Set the desired maximum character length

                etGoal.setFilters(filters);
                linearRecommend.setVisibility(View.VISIBLE);
                readData("Month"); // refresh the list with the new date range filter
            }
        });

        btnActivityAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvActivityDateRange.setText("All");
                linearRecommend.setVisibility(View.GONE);
                readData("All"); // refresh the list with the new date range filter
            }
        });


        etGoal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed for this implementation
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = etGoal.getText().toString();
                if (text.equals("0") || TextUtils.isEmpty(text)) {
                    if (!text.equals("")) {
                        etGoal.setText("");
                    }
                    tv_Label.setVisibility(View.GONE);
                    ivSave.setVisibility(View.GONE);
                } else {
                    tv_Label.setVisibility(View.VISIBLE);
                    ivSave.setVisibility(View.VISIBLE);
                }
            }

        });

        ivSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = "Save Goal";
                String msg ="Are you sure you want to save new Calorie Goal?";
                String posButton = "Confirm";
                String negButton = "Cancel";

                DialogBox_Message dialogBox = new DialogBox_Message(getContext(), title, msg, posButton, negButton);
                dialogBox.setPositiveButtonListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User clicked the positive button, do something here
                        Toast.makeText(getContext(), "Saved", Toast.LENGTH_SHORT).show();
                        // Do your action here
                        try{
                            saveGoal();
                             double sub = Integer.parseInt(etGoal.getText().toString())-totalBurnedCalories;
                            ivSave.setVisibility(View.INVISIBLE);

                            tvCalorieLeft.setText((int) sub + " Kcal");
                            saveCalorieLeft(Integer.parseInt(etGoal.getText().toString()));

                        }
                        catch (Exception e){
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialogBox.displayDialogBox();

            }
        });

        getUserInfo();
        return view;
    }

    int goal;
    public void getUserInfo(){
        DatabaseUsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(current_userID);
        DatabaseUsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.hasChild("goal")) {
                        goal = Integer.parseInt(snapshot.child("goal").getValue().toString());
                        Log.i("Log", "goal: " + goal);
                       // etGoal.setText(String.valueOf(goal));
                        ivSave.setVisibility(View.INVISIBLE);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void saveGoal(){
        String varValue = etGoal.getText().toString();
        DatabaseUsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(current_userID);

        // Use the DatabaseReference to insert the key-value pair into the database
        DatabaseUsersRef.child("goal").setValue(varValue);
    }

    private void readData(String selectedDateRange) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Daily Activity");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                totalBurnedCalories = 0;

                Calendar calendar = Calendar.getInstance();
                String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());
                calendar.add(Calendar.DAY_OF_YEAR, -7);
                String weekAgo = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());
                calendar.add(Calendar.DAY_OF_YEAR, 7);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                String monthStart = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String currentUserID = snapshot.child("currentUserID").getValue(String.class);
                    String date = snapshot.child("date").getValue(String.class);

                    if (currentUserID.equals(current_userID) &&
                            (selectedDateRange.equals("All") ||
                                    (selectedDateRange.equals("Today") && date.equals(today)) ||
                                    (selectedDateRange.equals("Week") && date.compareTo(weekAgo) >= 0) ||
                                    (selectedDateRange.equals("Month") && date.compareTo(monthStart) >= 0))) {

                        String activityName = snapshot.child("activityName").getValue(String.class);
                        String burnedCalorie = snapshot.child("burnedCalorie").getValue(String.class);
                        String duration = snapshot.child("duration").getValue(String.class);
                        String activityDate = snapshot.child("date").getValue(String.class);

                        DailyActivity_Retrieve_LogData activity_retrieve_logData = new DailyActivity_Retrieve_LogData(activityName, burnedCalorie, duration, activityDate);
                        list.add(activity_retrieve_logData);
                        totalBurnedCalories += Double.parseDouble(burnedCalorie);
                    }
                }

                DecimalFormat df = new DecimalFormat("#.##");
                String formattedTotalBurnedCalories = df.format(totalBurnedCalories);

                Intent intent = new Intent(getActivity(), DailyActivity_Recommendation_Fragment.class);

                if (selectedDateRange.equals("All")) {
                    tv_TotalBurnedCalorie.setText(formattedTotalBurnedCalories + " Kcal");
                    tvActivityDateRange.setText("All");

                } else if (selectedDateRange.equals("Today")) {
                    tvActivityDateRange.setText("Today");
                    etGoal.setText(String.valueOf((int) goal));
                    ivSave.setVisibility(View.INVISIBLE);
                    int caloriesLeft = (int) Math.round(goal - totalBurnedCalories);
                    tvCalorieLeft.setText(caloriesLeft + " Kcal");
                    saveCalorieLeft(caloriesLeft);
                    tv_TotalBurnedCalorie.setText(formattedTotalBurnedCalories + " Kcal");

                    //pass calorie left to recommendation
                    intent.putExtra("tobeBurn", caloriesLeft); // Add any data you want to pass


                } else if (selectedDateRange.equals("Week")) {
                    tvActivityDateRange.setText("Week");
                    etGoal.setText(String.valueOf((int) goal * 7));
                    ivSave.setVisibility(View.INVISIBLE);
                    // Calculate caloriesLeft after updating the TextView with the weekly recommended calories
                    int caloriesLeft = (int) Math.round(goal * 7 - totalBurnedCalories);
                    tvCalorieLeft.setText(caloriesLeft + " Kcal");
                    tv_TotalBurnedCalorie.setText(formattedTotalBurnedCalories + " Kcal");

                    //pass calorie left to recommendation
                    intent.putExtra("tobeBurn", caloriesLeft); // Add any data you want to pass

                } else if (selectedDateRange.equals("Month")) {
                    tvActivityDateRange.setText("Month");
                    etGoal.setText(String.valueOf((int) goal * 30));
                    ivSave.setVisibility(View.INVISIBLE);
                    int caloriesLeft = (int) Math.round(goal*30 - totalBurnedCalories);
                    tvCalorieLeft.setText(caloriesLeft + " Kcal");
                    tv_TotalBurnedCalorie.setText(formattedTotalBurnedCalories + " Kcal");

                    //pass calorie left to recommendation
                    intent.putExtra("tobeBurn", caloriesLeft); // Add any data you want to pass
                }

                activityLogAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled if needed
            }
        });
    }

    private void saveCalorieLeft(int leftCalorie){
        DatabaseUsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(current_userID);

        // Use the DatabaseReference to insert the key-value pair into the database
        DatabaseUsersRef.child("calorieLeft").setValue(leftCalorie);
    }

    public class ActivityLogAdapter extends RecyclerView.Adapter<ActivityLogAdapter.ActivityViewHolder> {
        private List<DailyActivity_Retrieve_LogData> activityList;

        public ActivityLogAdapter(List<DailyActivity_Retrieve_LogData> activityList) {
            this.activityList = activityList;
        }

        @NonNull
        @Override
        public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dailyactivity_log_items, parent, false);
            return new ActivityViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ActivityViewHolder holder, @SuppressLint("RecyclerView") int position) {
            DailyActivity_Retrieve_LogData dailyActivity_retrieve_logData = activityList.get(position);

            holder.tvActivityLogName.setText(dailyActivity_retrieve_logData.getActivityName());
            holder.tvActivityLogBurnedCalorie.setText(dailyActivity_retrieve_logData.getBurnedCalorie());
            holder.tvActivityLogDuration.setText(dailyActivity_retrieve_logData.getDuration());
            holder.tvActivityLogDate.setText(dailyActivity_retrieve_logData.getDate());
            if(Integer.parseInt(holder.tvActivityLogDuration.getText().toString()) > 1){
                holder.tvDurationLabel.setText("Minutes");
            }

            holder.cvActivityLogContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String title = "Post Activity";
                    String msg ="Are you sure you want to post selected Activity?";
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
                                Generate_Image generateImage = new Generate_Image();
                                generateImage.saveViewAsImage(v.getContext(), holder.itemView, holder.tvActivityLogName.getText().toString());
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

        @Override
        public int getItemCount() {
            return activityList.size();
        }

        class ActivityViewHolder extends RecyclerView.ViewHolder {
            TextView tvActivityLogName, tvActivityLogBurnedCalorie, tvActivityLogDuration, tvDurationLabel, tvActivityLogDate;
            CardView cvActivityLogContainer;

            public ActivityViewHolder(@NonNull View itemView) {
                super(itemView);
                tvActivityLogName = itemView.findViewById(R.id.tvActivityLogName);
                tvActivityLogBurnedCalorie = itemView.findViewById(R.id.tvActivityLogBurnedCalorie);
                tvActivityLogDuration = itemView.findViewById(R.id.tvActivityLogDuration);
                tvDurationLabel = itemView.findViewById(R.id.tvDurationLabel);
                tvActivityLogDate = itemView. findViewById(R.id.tvActivityLogDate);

                cvActivityLogContainer = itemView.findViewById(R.id.cvActivityLogContainer);
            }
        }
    }
}