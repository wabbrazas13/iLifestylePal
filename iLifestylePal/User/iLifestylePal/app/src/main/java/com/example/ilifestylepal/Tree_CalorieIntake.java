package com.example.ilifestylepal;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Tree_CalorieIntake {

    private FirebaseAuth mAuth;
    private String currentUserID;
    private DatabaseReference DatabaseUsersRef;
    private int age;
    private String gender, activityLevel;
    private  int calorieIntake;

    public Tree_CalorieIntake() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUserID = currentUser.getUid();
        }
        DatabaseUsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        Log.i("TAG", currentUserID);
    }


    public void getUserInfo(OnCalorieIntakeCalculatedListener listener){
        DatabaseUsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.hasChild("age")) {
                        age = Integer.parseInt(snapshot.child("age").getValue().toString());
                        Log.i("TAG3", "age: " + age);
                    }
                    if (snapshot.hasChild("gender")) {
                        gender = snapshot.child("gender").getValue().toString();
                        Log.i("TAG2", "gender: " + gender);
                    }
                    if (snapshot.hasChild("activityLevel")) {
                        activityLevel = snapshot.child("activityLevel").getValue().toString();
                        Log.i("TAG1", "activityLevel: " + activityLevel);
                    }
                    Tree();
                    listener.onCalorieIntakeCalculated(calorieIntake);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public interface OnCalorieIntakeCalculatedListener {
        void onCalorieIntakeCalculated(int calorieIntake);

        void onBackPressed();
    }

    public void Tree(){
        try{
            if(gender.equals("Male")){
                if (age == 13) {
                    calorieIntake = 2000;
                } else if (age <= 14) {
                    if (activityLevel.equals("Sedentary")) {
                        calorieIntake = 2000;
                    } else if (activityLevel.equals("Moderately Active")) {
                        calorieIntake = 2400;
                    } else { // activityLevel == "Active"
                        calorieIntake = 2800;
                    }
                } else if (age <= 16) {
                    if (activityLevel.equals("Sedentary")) {
                        calorieIntake = 2200;
                    } else if (activityLevel.equals("Moderately Active")) {
                        calorieIntake = 2600;
                    } else { // activityLevel == "Active"
                        calorieIntake = 3000;
                    }
                } else if (age <= 18) {
                    if (activityLevel.equals("Sedentary")) {
                        calorieIntake = 2400;
                    } else if (activityLevel.equals("Moderately Active")) {
                        calorieIntake = 2800;
                    } else { // activityLevel == "Active"
                        calorieIntake = 3200;
                    }
                } else if (age <= 35) {
                    if (activityLevel.equals("Sedentary")) {
                        calorieIntake = 2400;
                    } else if (activityLevel.equals("Moderately Active")) {
                        calorieIntake = 2800;
                    } else { // activityLevel == "Active"
                        calorieIntake = 3000;
                    }
                } else if (age <= 45) {
                    if (activityLevel.equals("Sedentary")) {
                        calorieIntake = 2200;
                    } else if (activityLevel.equals("Moderately Active")) {
                        calorieIntake = 2600;
                    } else { // activityLevel == "Active"
                        calorieIntake = 2800;
                    }
                } else if (age <= 55) {
                    if (activityLevel.equals("Sedentary")) {
                        calorieIntake = 2200;
                    } else if (activityLevel.equals("Moderately Active")) {
                        calorieIntake = 2400;
                    } else { // activityLevel == "Active"
                        calorieIntake = 2800;
                    }
                } else if (age <= 60) {
                    if (activityLevel.equals("Sedentary")) {
                        calorieIntake = 2200;
                    } else if (activityLevel.equals("Moderately Active")) {
                        calorieIntake = 2400;
                    } else { // activityLevel == "Active"
                        calorieIntake = 2600;
                    }
                } else if (age <= 75) {
                    if (activityLevel.equals("Sedentary")) {
                        calorieIntake = 2000;
                    } else if (activityLevel.equals("Moderately Active")) {
                        calorieIntake = 2200;
                    } else { // activityLevel == "Active"
                        calorieIntake = 2600;
                    }
                } else { // age > 75
                    if (activityLevel.equals("Sedentary")) {
                        calorieIntake = 2000;
                    } else if (activityLevel.equals("Moderately Active")) {
                        calorieIntake = 2200;
                    } else { // activityLevel == "Active"
                        calorieIntake = 2400;
                    }
                }
            }
            else{
                if (age == 13) {
                    calorieIntake = 1600;
                } else if (age >= 14 && age <= 18) {
                    if (activityLevel.equals("Sedentary")) {
                        calorieIntake = 1800;
                    } else if (activityLevel.equals("Moderately Active")) {
                        calorieIntake = 2000;
                    } else { // activityLevel == "Active"
                        calorieIntake = 2400;
                    }
                } else if (age >= 19 && age <= 25) {
                    if (activityLevel.equals("Sedentary")) {
                        calorieIntake = 2000;
                    } else if (activityLevel.equals("Moderately Active")) {
                        calorieIntake = 2200;
                    } else { // activityLevel == "Active"
                        calorieIntake = 2400;
                    }
                } else if (age >= 26 && age <= 40) {
                    if (activityLevel.equals("Sedentary")) {
                        calorieIntake = 1800;
                    } else if (activityLevel.equals("Moderately Active")) {
                        calorieIntake = 2000;
                    } else { // activityLevel == "Active"
                        calorieIntake = 2400;
                    }
                } else if (age >= 41 && age <= 55) {
                    if (activityLevel.equals("Sedentary")) {
                        calorieIntake = 1800;
                    } else if (activityLevel.equals("Moderately Active")) {
                        calorieIntake = 2000;
                    } else { // activityLevel == "Active"
                        calorieIntake = 2200;
                    }
                } else if (age >= 56 && age <= 75) {
                    if (activityLevel.equals("Sedentary")) {
                        calorieIntake = 1600;
                    } else if (activityLevel.equals("Moderately Active")) {
                        calorieIntake = 1800;
                    } else { // activityLevel == "Active"
                        calorieIntake = 2200;
                    }
                } else { // age >= 76
                    if (activityLevel.equals("Sedentary")) {
                        calorieIntake = 1600;
                    } else if (activityLevel.equals("Moderately Active")) {
                        calorieIntake = 1800;
                    } else { // activityLevel == "Active"
                        calorieIntake = 2000;
                    }
                }

            }

            String msg;
            if (gender.equals("Male")) {
                msg = "Your daily calorie intake is: " + calorieIntake + " calories.";
                Log.i("TAG", "Tree: " + msg);
            } else if (gender.equals("Female")) {
                msg ="Your daily calorie intake is: " + (calorieIntake - 200) + " calories.";
                Log.i("TAG", "Tree: " + msg);
            }

        }
        catch (Exception e){
            Log.i("TAG4", "Tree: " + e.getMessage());
        }

    }

}
