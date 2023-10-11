package com.example.ilifestylepal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class Food_FoodLog_Fragment extends Fragment implements Tree_CalorieIntake.OnCalorieIntakeCalculatedListener {

    List<Food_Log_Data> list;
    FoodLogAdapter foodLogAdapter;
    Tree_CalorieIntake tree_calorieIntake;
    private String current_userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private int caloriee= 0;
    public Food_FoodLog_Fragment() {
        // Required empty public constructor
    }

    public static Food_FoodLog_Fragment newInstance() {
        Food_FoodLog_Fragment fragment = new Food_FoodLog_Fragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    TextView tv_TotalCalorie,tvDateRange, tvCalorieLeft, tvRecommended;
    Button btnToday, btnWeek, btnMonth, btnAll;
    LinearLayout linearRec;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.food_foodlog_fragment, container, false);
        RecyclerView rvFoodLog = view.findViewById(R.id.rvFoodLog);
        list = new ArrayList<>();
        foodLogAdapter = new FoodLogAdapter(list);
        rvFoodLog.setLayoutManager(new LinearLayoutManager(getContext()));
        rvFoodLog.setAdapter(foodLogAdapter);
        tvDateRange = view.findViewById(R.id.tvDateRange);
        tvRecommended = view.findViewById(R.id.tvRecommended);

        tv_TotalCalorie = view.findViewById(R.id.tv_TotalCalorie);
        tvCalorieLeft = view.findViewById(R.id.tvCalorieLeft);
        btnToday = view.findViewById(R.id.btnToday);
        btnWeek = view.findViewById(R.id.btnWeek);
        btnMonth = view.findViewById(R.id.btnMonth);
        btnAll = view.findViewById(R.id.btnAll);
        linearRec = view.findViewById(R.id.linearRec);

        tree_calorieIntake = new Tree_CalorieIntake();
        tree_calorieIntake.getUserInfo(this); // pass instance of callback interface

        btnToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                linearRec.setVisibility(View.VISIBLE);
                readData("Today");
            }
        });

        btnWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                linearRec.setVisibility(View.VISIBLE);
                readData("Week");
            }
        });

        btnMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                linearRec.setVisibility(View.VISIBLE);
                readData("Month");
            }
        });

        btnAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                linearRec.setVisibility(View.GONE);
                readData("All");
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        readData("Today");
    }

    int totalCalories = 0;
    final String TAG = "Food";
    private void readData(String selectedDateRange){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Food Journal");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                totalCalories = 0;

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

                        String foodname = snapshot.child("foodName").getValue(String.class);
                        String calorie = snapshot.child("calorie").getValue(String.class);
                        String meal = snapshot.child("meal").getValue(String.class);
                        String serving = snapshot.child("serving").getValue(String.class);
                        String foodURL = snapshot.child("foodURL").getValue(String.class);
                        String foodDate = snapshot.child("date").getValue(String.class);

                        Food_Log_Data food = new Food_Log_Data(foodname, calorie, meal, serving, foodURL, foodDate);
                        list.add(food);
                        totalCalories += Integer.parseInt(calorie);


                    }
                }

                if (selectedDateRange.equals("All")) {
                    tv_TotalCalorie.setText(totalCalories + " Kcal");
                    tvDateRange.setText("All");
                    tvRecommended.setText("N/A");

                } else if (selectedDateRange.equals("Today")) {
                    tv_TotalCalorie.setText(totalCalories + " Kcal");
                    tvDateRange.setText("Today");
                    tvRecommended.setText(String.valueOf(caloriee) + " Kcal");
                    Log.i(TAG, "Today: calorie: " + caloriee);
                    tvCalorieLeft.setText(caloriee - totalCalories + " Kcal");

                } else if (selectedDateRange.equals("Week")) {
                    tv_TotalCalorie.setText(totalCalories + " Kcal");
                    tvDateRange.setText("Week");
                    int recommendedCalories = caloriee;
                    int multipliedCalories = recommendedCalories * 7;
                    tvRecommended.setText(String.valueOf(multipliedCalories) + " Kcal");
                    tvCalorieLeft.setText((multipliedCalories - totalCalories) + " Kcal");
                    Log.i(TAG, "Week: calorie: *7 " + caloriee*7);

                } else if (selectedDateRange.equals("Month")) {
                    tv_TotalCalorie.setText(totalCalories + " Kcal");
                    tvDateRange.setText("Month");
                    int recommendedCalories = caloriee;
                    int multipliedCalories = recommendedCalories * 30;
                    tvRecommended.setText(String.valueOf(multipliedCalories) + " Kcal");
                    tvCalorieLeft.setText((multipliedCalories - totalCalories) + " Kcal");
                    Log.i(TAG, "Month: calorie: *30 " + caloriee*30);
                }

                foodLogAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onCalorieIntakeCalculated(int calorieIntake) {
        caloriee = calorieIntake;
        tvRecommended.setText(caloriee + " Kcal");
        Log.i("TAG", "from another class: " + calorieIntake);

    }
    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStackImmediate();
        } else {
            getActivity().finish();
        }
    }

    public class FoodLogAdapter extends RecyclerView.Adapter<Food_FoodLog_Fragment.FoodLogAdapter.FoodViewHolder> {
        private List<Food_Log_Data> foodList;

        public FoodLogAdapter(List<Food_Log_Data> foodList) {
            this.foodList = foodList;
        }

        @NonNull
        @Override
        public Food_FoodLog_Fragment.FoodLogAdapter.FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_log_items, parent, false);
            return new Food_FoodLog_Fragment.FoodLogAdapter.FoodViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull Food_FoodLog_Fragment.FoodLogAdapter.FoodViewHolder holder, @SuppressLint("RecyclerView") int position) {
            Food_Log_Data food = foodList.get(position);

            holder.tvFoodLogName.setText(food.getFoodname());
            holder.tvFoodLogMeal.setText(food.getMeal());
            holder.tvFoodLogServing.setText(food.getServing());
            holder.tvFoodLogCalorie.setText(food.getCalorie());
            holder.tvFoodLogDate.setText(food.getDate());
            String url = food.getUrl();

            Picasso.with(holder.itemView.getContext()).load(url).into(holder.ivFoodLogImage);

            if(Integer.parseInt(holder.tvFoodLogServing.getText().toString()) > 1){
                holder.tvServingLabel.setText("Servings");
            }

            Log.i("TAG", food.getFoodname() + " " + food.getMeal() + " " +  food.getServing() + " " + food.getCalorie());

            holder.cvFoodLogContainer.setOnClickListener(new View.OnClickListener() {
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
                            Toast.makeText(getContext(), "Adding food...", Toast.LENGTH_SHORT).show();
                            // Do your action here
                            try{
                                Generate_Image generateImage = new Generate_Image();
                                generateImage.saveViewAsImage(v.getContext(), holder.itemView, holder.tvFoodLogName.getText().toString());
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
            return foodList.size();
        }

        class FoodViewHolder extends RecyclerView.ViewHolder {
            TextView tvFoodLogName, tvFoodLogCalorie, tvFoodLogMeal,tvFoodLogServing, tvServingLabel, tvFoodLogDate;
            ImageView ivFoodLogImage;
            CardView cvFoodLogContainer;

            public FoodViewHolder(@NonNull View itemView) {
                super(itemView);
                tvFoodLogName = itemView.findViewById(R.id.tvFoodLogName);
                tvFoodLogMeal = itemView.findViewById(R.id.tvFoodLogMeal);
                tvFoodLogServing = itemView.findViewById(R.id.tvFoodLogServing);
                tvFoodLogCalorie = itemView.findViewById(R.id.tvFoodLogCalorie);
                tvServingLabel = itemView.findViewById(R.id.tvServingLabel);
                tvFoodLogDate = itemView.findViewById(R.id.tvFoodLogDate);

                ivFoodLogImage = itemView.findViewById(R.id.ivFoodLogImage);
                cvFoodLogContainer = itemView.findViewById(R.id.cvFoodLogContainer);
            }
        }
    }
}
