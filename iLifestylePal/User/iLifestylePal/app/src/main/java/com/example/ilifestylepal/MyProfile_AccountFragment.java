package com.example.ilifestylepal;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MyProfile_AccountFragment extends Fragment {

    private TextView tv_firstname, edit_firstname;
    private TextView tv_lastname, edit_lastname;
    private TextView tv_birthdate, edit_birthdate;
    private TextView tv_activityLevel, edit_activityLevel;
    private TextView tv_gender, edit_gender;
    private TextView tv_age;
    private TextView tv_height, edit_height;
    private TextView tv_weight, edit_weight;
    private TextView tv_bmi;
    private TextView tv_health_conditions, edit_health_conditions, tv_allergies, edit_allergies;

    private FirebaseAuth mAuth;
    private DatabaseReference DatabaseUsersRef, DatabaseHealthConditionsRef;
    private String currentUserID;

    private DatePickerDialog datePickerDialog;

    private ArrayList<HealthConditionsModel> mHealthConditions;
    private HealthConditionsAdapter mAdapter;

    private ArrayList<IngredientsModel> mIngredientList;
    private IngredientsAdapter mAdapter2;

    public MyProfile_AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_my_profile_account, container, false);


        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        DatabaseUsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        DatabaseHealthConditionsRef = FirebaseDatabase.getInstance().getReference().child("Maintenance").child("My Profile").child("Health Conditions");

        tv_firstname = view.findViewById(R.id.tv_firstname);
        edit_firstname = view.findViewById(R.id.edit_firstname);
        tv_lastname = view.findViewById(R.id.tv_lastname);
        edit_lastname = view.findViewById(R.id.edit_lastname);
        tv_birthdate = view.findViewById(R.id.tv_birthdate);
        edit_birthdate = view.findViewById(R.id.edit_birthdate);
        tv_gender = view.findViewById(R.id.tv_gender);
        edit_gender = view.findViewById(R.id.edit_gender);
        tv_age = view.findViewById(R.id.tv_age);
        tv_height = view.findViewById(R.id.tv_height);
        edit_height = view.findViewById(R.id.edit_height);
        tv_weight = view.findViewById(R.id.tv_weight);
        edit_weight = view.findViewById(R.id.edit_weight);
        tv_bmi = view.findViewById(R.id.tv_bmi);
        tv_health_conditions = view.findViewById(R.id.tv_health_conditions);
        edit_health_conditions = view.findViewById(R.id.edit_health_conditions);
        tv_activityLevel = view.findViewById(R.id.tv_activityLevel);
        edit_activityLevel = view.findViewById(R.id.edit_activityLevel);

        tv_allergies = view.findViewById(R.id.tv_allergies);
        edit_allergies = view.findViewById(R.id.edit_allergies);

        mHealthConditions = new ArrayList<>();
        mAdapter = new HealthConditionsAdapter(mHealthConditions);

        mIngredientList = new ArrayList<>();
        mAdapter2 = new IngredientsAdapter(mIngredientList);

        initDatePicker();
        FetchUserInfo();
        FetchHealthConditions();
        FetchFoodIngredients();
        FetchUserHealthConditions();
        FetchUserAllergies();

        edit_firstname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditFirstName();
            }
        });

        edit_lastname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditLastName();
            }
        });

        edit_birthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });

        edit_gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditGender();
            }
        });

        edit_height.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditHeight();
            }
        });

        edit_weight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditWeight();
            }
        });

        edit_activityLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String one = "Sedentary";
                String two = "Moderately Active";
                String three = "Active";
                edit(one, two, three, tv_activityLevel);
            }
        });

        edit_health_conditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowHealthConditionsDialog();
            }
        });

        edit_allergies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowAllergiesDialog();
            }
        });

        return view;
    }

    private void FetchUserAllergies() {
        DatabaseUsersRef.child("Allergies").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = (int) snapshot.getChildrenCount();
                if (count == 0) {
                    tv_allergies.setText("None");
                } else {
                    String allergies = "";
                    int i = 0;
                    for (DataSnapshot data : snapshot.getChildren()) {
                        String hc_key = data.getKey();
                        if (i == 0) {
                            allergies = hc_key;
                        } else {
                            allergies = allergies + ", " + hc_key;
                        }
                        i++;
                    }
                    tv_allergies.setText(allergies);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Allergies", "Error: " + error.getMessage());
            }
        });
    }

    private void FetchFoodIngredients() {
        DatabaseReference IngredientsRef = FirebaseDatabase.getInstance().getReference().child("Maintenance").child("Food").child("Ingredients");

        IngredientsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mIngredientList.clear();

                for (DataSnapshot data : snapshot.getChildren()) {
                    IngredientsModel myDataModel = data.getValue(IngredientsModel.class);
                    mIngredientList.add(myDataModel);
                }

                mAdapter2.notifyDataSetChanged();
                mAdapter2.getFilter().filter("");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Ingredients", "Error: " + error.getMessage());
            }
        });
    }

    private void ShowAllergiesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select Allergies");
        builder.setCancelable(false);

        View dialogLayout = getLayoutInflater().inflate(R.layout.dialog_health_conditions, null);
        RecyclerView mRecyclerView = dialogLayout.findViewById(R.id.rv_health_conditions);
        SearchView mSearchField = dialogLayout.findViewById(R.id.sv_search);
        mRecyclerView.setAdapter(mAdapter2);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        builder.setView(dialogLayout);

        mSearchField.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                mAdapter2.getFilter().filter(s);
                return true;
            }
        });

        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void FetchUserHealthConditions() {
        DatabaseUsersRef.child("Health Conditions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = (int) snapshot.getChildrenCount();
                if (count == 0) {
                    tv_health_conditions.setText("None");
                } else {
                    String health_conditions = "";
                    int i = 0;
                    for (DataSnapshot data : snapshot.getChildren()) {
                        String hc_key = data.getKey();
                        if (i == 0) {
                            health_conditions = hc_key;
                        } else {
                            health_conditions = health_conditions + ", " + hc_key;
                        }
                        i++;
                    }
                    tv_health_conditions.setText(health_conditions);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DB Health Conditions", "Error: " + error.getMessage());
            }
        });
    }

    private void FetchHealthConditions() {
        DatabaseHealthConditionsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mHealthConditions.clear();

                for (DataSnapshot data : snapshot.getChildren()) {
                    HealthConditionsModel myDataModel = data.getValue(HealthConditionsModel.class);
                    mHealthConditions.add(myDataModel);
                }

                mAdapter.notifyDataSetChanged();
                mAdapter.getFilter().filter("");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DB Health Conditions", "Error: " + error.getMessage());
            }
        });
    }

    private void ShowHealthConditionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Health Conditions");
        builder.setCancelable(false);

        View dialogLayout = getLayoutInflater().inflate(R.layout.dialog_health_conditions, null);
        RecyclerView mRecyclerView = dialogLayout.findViewById(R.id.rv_health_conditions);
        SearchView mSearchField = dialogLayout.findViewById(R.id.sv_search);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        builder.setView(dialogLayout);

        mSearchField.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                mAdapter.getFilter().filter(s);
                return true;
            }
        });

        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void FetchUserInfo() {
        DatabaseUsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.hasChild("firstname")) {
                        tv_firstname.setText(snapshot.child("firstname").getValue().toString());
                    }
                    if (snapshot.hasChild("lastname")) {
                        tv_lastname.setText(snapshot.child("lastname").getValue().toString());
                    }
                    if (snapshot.hasChild("birthdate")) {
                        tv_birthdate.setText(snapshot.child("birthdate").getValue().toString());
                    } else {
                        VerifyUserAge();
                    }
                    if (snapshot.hasChild("gender")) {
                        tv_gender.setText(snapshot.child("gender").getValue().toString());
                    }
                    if (snapshot.hasChild("age")) {
                        tv_age.setText(snapshot.child("age").getValue().toString());
                    }
                    if (snapshot.hasChild("height")) {
                        tv_height.setText(snapshot.child("height").getValue().toString());
                    }
                    if (snapshot.hasChild("weight")) {
                        tv_weight.setText(snapshot.child("weight").getValue().toString());
                    }
                    if (snapshot.hasChild("bmi")) {
                        double bmi = Double.parseDouble(snapshot.child("bmi").getValue().toString().trim());
                        SetBMI(bmi);
                    }
                    if(snapshot.hasChild("activityLevel")){
                        tv_activityLevel.setText(snapshot.child("activityLevel").getValue().toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DB Users", "Error: " + error.getMessage());
            }
        });
    }

    private void VerifyUserAge() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("User Age Verification");
        builder.setMessage("Are you 18 years old or above?");
        builder.setCancelable(false);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                datePickerDialog.show();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getContext(), "Sorry, you are not eligible to use this app.", Toast.LENGTH_SHORT).show();
                mAuth.signOut();
                Intent loginIntent = new Intent(getContext(), LoginActivity.class);
                loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(loginIntent);
                getActivity().finish();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void edit(String strOne, String strTwo, String strThree, TextView textView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_my_profile_account2, null);

        Button btnSedentary = dialogView.findViewById(R.id.btn_male);
        Button btnModerately = dialogView.findViewById(R.id.btn_female);
        Button btnActive = dialogView.findViewById(R.id.btnThree);

        btnSedentary.setText(strOne);
        btnModerately.setText(strTwo);
        btnActive.setVisibility(View.VISIBLE);
        btnActive.setText(strThree);

        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        btnSedentary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textView.setText(strOne);
                Update("activityLevel", strOne);
                alertDialog.dismiss();
            }
        });

        btnModerately.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textView.setText(strTwo);
                Update("activityLevel", strTwo);
                alertDialog.dismiss();
            }
        });
        btnActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setText(strThree);
                Update("activityLevel",strThree);
                alertDialog.dismiss();
            }
        });
    }

    private void EditFirstName() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Edit FirstName");
        builder.setCancelable(false);

        LayoutInflater inflater = getLayoutInflater();
        View customLayout = inflater.inflate(R.layout.dialog_my_profile_account, null);
        EditText editText = customLayout.findViewById(R.id.editText);
        editText.setText(tv_firstname.getText().toString());
        builder.setView(customLayout);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String fn = editText.getText().toString().trim();
                if (TextUtils.isEmpty(fn)) {
                    Toast.makeText(getContext(), "Invalid Input.", Toast.LENGTH_SHORT).show();
                } else {
                    tv_firstname.setText(editText.getText().toString());
                    UpdateUserInfo("firstname");
                    dialogInterface.dismiss();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void EditLastName() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Edit LastName");
        builder.setCancelable(false);

        LayoutInflater inflater = getLayoutInflater();
        View customLayout = inflater.inflate(R.layout.dialog_my_profile_account, null);
        EditText editText = customLayout.findViewById(R.id.editText);
        editText.setText(tv_lastname.getText().toString());
        builder.setView(customLayout);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String ln = editText.getText().toString().trim();
                if (TextUtils.isEmpty(ln)) {
                    Toast.makeText(getContext(), "Invalid Input.", Toast.LENGTH_SHORT).show();
                } else {
                    tv_lastname.setText(editText.getText().toString());
                    UpdateUserInfo("lastname");
                    dialogInterface.dismiss();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void EditGender() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_my_profile_account2, null);

        Button btn_male = dialogView.findViewById(R.id.btn_male);
        Button btn_female = dialogView.findViewById(R.id.btn_female);

        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        btn_male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_gender.setText("Male");
                UpdateUserInfo("gender");
                alertDialog.dismiss();
            }
        });

        btn_female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_gender.setText("Female");
                UpdateUserInfo("gender");
                alertDialog.dismiss();
            }
        });
    }

    private void EditHeight() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Edit Height (cm/s)");
        builder.setCancelable(false);

        LayoutInflater inflater = getLayoutInflater();
        View customLayout = inflater.inflate(R.layout.dialog_my_profile_account3, null);
        EditText editText = customLayout.findViewById(R.id.editText);
        editText.setText(tv_height.getText().toString());
        builder.setView(customLayout);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String h = editText.getText().toString().trim();
                if (TextUtils.isEmpty(h)) {
                    Toast.makeText(getContext(), "Invalid Input.", Toast.LENGTH_SHORT).show();
                } else {
                    tv_height.setText(editText.getText().toString());
                    UpdateUserInfo("height");
                    dialogInterface.dismiss();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void EditWeight() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Edit Weight (kg/s)");
        builder.setCancelable(false);

        LayoutInflater inflater = getLayoutInflater();
        View customLayout = inflater.inflate(R.layout.dialog_my_profile_account3, null);
        EditText editText = customLayout.findViewById(R.id.editText);
        editText.setText(tv_weight.getText().toString());
        builder.setView(customLayout);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String w = editText.getText().toString().trim();
                if (TextUtils.isEmpty(w)) {
                    Toast.makeText(getContext(), "Invalid Input.", Toast.LENGTH_SHORT).show();
                } else {
                    tv_weight.setText(editText.getText().toString());
                    UpdateUserInfo("weight");
                    dialogInterface.dismiss();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void Update(String child, String value){
        DatabaseUsersRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DatabaseUsersRef.child(child).setValue(value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void UpdateUserInfo(String label) {
        DatabaseUsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (label.equals("firstname")) {
                    DatabaseUsersRef.child("firstname").setValue(tv_firstname.getText().toString());
                    DatabaseUsersRef.child("fullname").setValue(tv_firstname.getText().toString() + " " + tv_lastname.getText().toString());
                }
                if (label.equals("lastname")) {
                    DatabaseUsersRef.child("lastname").setValue(tv_lastname.getText().toString());
                    DatabaseUsersRef.child("fullname").setValue(tv_firstname.getText().toString() + " " + tv_lastname.getText().toString());
                }
                if (label.equals("birthdate")) {
                    DatabaseUsersRef.child("birthdate").setValue(tv_birthdate.getText().toString());
                    DatabaseUsersRef.child("age").setValue(tv_age.getText().toString());
                }
                if (label.equals("gender")) {
                    DatabaseUsersRef.child("gender").setValue(tv_gender.getText().toString());
                }
                if (label.equals("height")) {
                    Double h = Double.parseDouble(tv_height.getText().toString().trim());
                    Double w = Double.parseDouble(tv_weight.getText().toString().trim());
                    Double bmi = (w / h / h ) * 10000;
                    DatabaseUsersRef.child("height").setValue(h);
                    DatabaseUsersRef.child("bmi").setValue(bmi);
                    SetBMI(bmi);
                }
                if (label.equals("weight")) {
                    Double h = Double.parseDouble(tv_height.getText().toString().trim());
                    Double w = Double.parseDouble(tv_weight.getText().toString().trim());
                    Double bmi = (w / h / h ) * 10000;
                    DatabaseUsersRef.child("weight").setValue(w);
                    DatabaseUsersRef.child("bmi").setValue(bmi);
                    SetBMI(bmi);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DB Users", "Error: " + error.getMessage());
            }
        });
    }

    private void SetBMI(Double bmi) {
        String bmi_remarks = "";

        if(bmi < 18.5){
            bmi_remarks = "Underweight";
        }else if(bmi >= 18.5 && bmi <= 24.9){
            bmi_remarks = "Normal";
        }else if(bmi >= 25 && bmi <= 29.9){
            bmi_remarks = "Overweight";
        }else{
            bmi_remarks = "Obese";
        }

        tv_bmi.setText(String.format("%.2f", bmi) + " - " + bmi_remarks);
    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                Calendar dob = Calendar.getInstance();
                dob.set(Calendar.YEAR, year);
                dob.set(Calendar.MONTH, month);
                dob.set(Calendar.DAY_OF_MONTH, day);
                String format = new SimpleDateFormat("dd MMM yyyy").format(dob.getTime());
                month = month+1;
                String date = makeDateString(day, month, year);
                tv_birthdate.setText(date);
                tv_age.setText(Integer.toString(getAge(dob.getTimeInMillis())));
                UpdateUserInfo("birthdate");
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        Calendar calAgo = Calendar.getInstance();
        calAgo.add(Calendar.YEAR, -18);

        int style = AlertDialog.THEME_HOLO_LIGHT;
        datePickerDialog = new DatePickerDialog(getContext(), style,dateSetListener, year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(calAgo.getTimeInMillis());
    }

    private String makeDateString(int day, int month, int year)
    {
        return getMonthFormat(month) + " " + day + ", " + year;
    }

    private String getMonthFormat(int month)
    {
        if(month == 1){
            return "January";
        }else if(month == 2){
            return "February";
        }else if(month == 3){
            return "March";
        }else if(month == 4){
            return "April";
        }else if(month == 5){
            return "May";
        }else if(month == 6){
            return "June";
        }else if(month == 7){
            return "July";
        }else if(month == 8){
            return "August";
        }else if(month == 9){
            return "September";
        }else if(month == 10){
            return "October";
        }else if(month == 11){
            return "November";
        }else {
            return "December";
        }
    }

    private int getAge(long date) {
        Calendar dob = Calendar.getInstance();
        dob.setTimeInMillis(date);
        Calendar now = Calendar.getInstance();

        int age = now.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if(now.get(Calendar.MONTH) < dob.get(Calendar.MONTH)){
            age--;
        }else if((now.get(Calendar.MONTH) == dob.get(Calendar.MONTH))){
            if(now.get(Calendar.DAY_OF_MONTH) < dob.get(Calendar.DAY_OF_MONTH)){
                age--;
            }
        }
        return age;
    }
}