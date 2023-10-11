package com.example.ilifestylepal;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class Food_Insert_Dialog {

    private Context context;
    private TextView textView_Serving, textView_FoodName, textView_CalorieValue;
    private LinearLayout btnDate, spinMeals;
    private TextView textViewDate;
    private Spinner spinner;
    private DatePickerDialog datePickerDialog;
    private String current_userID;

    public Food_Insert_Dialog(Context context, String current_userID) {
        this.context = context;
        this.current_userID = current_userID;
    }

    public void displayDialog(String foodname, String calorie, String url) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.food_log_dialog, null);

        textView_FoodName = dialogView.findViewById(R.id.textView_FoodName);
        textView_CalorieValue = dialogView.findViewById(R.id.textView_CalorieValue);
        textView_Serving = dialogView.findViewById(R.id.tvServingValue);

        textView_Serving.setOnClickListener(view ->{
            ShowServingDialogBox();
        });

        textView_FoodName.setText(foodname);
        textView_CalorieValue.setText(calorie);

        textView_Serving.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(textView_Serving.getText().toString().equals("0")){
                    textView_Serving.setText("1");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(textView_Serving.getText().toString().equals("1")){
                    textView_CalorieValue.setText(calorie);
                }
                else{
                    int calorie1 = Integer.parseInt(calorie);
                    int serving = Integer.parseInt(textView_Serving.getText().toString());
                    int result = calorie1 * serving;
                    textView_CalorieValue.setText(String.valueOf(result));
                }
            }
        });

        spinner = dialogView.findViewById(R.id.spinner_Meals);
        spinMeals = dialogView.findViewById(R.id.linearSpinner_Meals);

        //Array for meals -Breakfast, Lunch, etc.
        try{
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.spinnerCategory, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);

            spinMeals.setOnClickListener(view -> spinner.performClick());
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    // Handle spinner item selection here
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // Handle case when nothing is selected
                }
            });
        }
        catch (Exception e){
            Log.e("TAG", e.getMessage() );
        }

        textViewDate = dialogView.findViewById(R.id.textViewDate);
        btnDate = dialogView.findViewById(R.id.btnDate);

        initDatePicker();
        textViewDate.setText(getTodaysDate());
        btnDate.setOnClickListener(view ->{
            try{
                // Create a new instance of DatePickerDialog and show it
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        context,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                // Set the selected date to the TextView
                                String date = String.format("%d-%02d-%02d", year, monthOfYear + 1, dayOfMonth);
                                textViewDate.setText(date);
                            }
                        },
                        // Set the initial date in the dialog to the current date
                        Calendar.getInstance().get(Calendar.YEAR),
                        Calendar.getInstance().get(Calendar.MONTH),
                        Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                );
                // Show the dialog
                datePickerDialog.show();
            }
            catch (Exception e){
                Log.e("TAG",  e.getMessage());
            }

        });

        // Set the custom layout to the dialog
        dialogBuilder.setView(dialogView);

        // Add the positive button
        dialogBuilder.setPositiveButton("Add Food", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Perform action for positive button
                InsertFoodData(url);
            }
        });

        // Add the negative button
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Perform action for negative button
            }
        });


        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    private void InsertFoodData(String foodURL) {
        //Initialize View
        String date = textViewDate.getText().toString();
        String foodName = textView_FoodName.getText().toString();
        String calorie = textView_CalorieValue.getText().toString();
        String serving = textView_Serving.getText().toString();
        String meal = spinner.getSelectedItem().toString();

        //if(foodName.equals(R.string.custom_spinner_ingredient_name_text) && calorie.equals(R.string.tvCalorie_text))
        if(TextUtils.isEmpty(foodName) && TextUtils.isEmpty(calorie)){
            Toast.makeText(context, "Please Select a Dish", Toast.LENGTH_SHORT).show();
        }
        else{
            //FirebaseReference
            DatabaseReference foodJournalRef = FirebaseDatabase.getInstance().getReference().child("Food Journal");

            String id = foodJournalRef.push().getKey();

            Food_Data_Insert food_data_insert = new Food_Data_Insert(date, calorie, foodName, meal, serving, current_userID, foodURL);

            foodJournalRef.child(id).setValue(food_data_insert)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(context, "Food Successfully Added", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
        }

    }

    private void ShowServingDialogBox() {

        final int[] selectedValue = {0};

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.number_picker, null);
        NumberPicker picker = view.findViewById(R.id.number_picker);

        picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                selectedValue[0] = newVal;
            }
        });

        picker.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                textView_Serving.setText(String.valueOf(selectedValue[0]));
                androidx.appcompat.app.AlertDialog.Builder builder = null;
                builder.create().dismiss();
                return true;
            }
            return false;
        });

        picker.setMinValue(1);
        picker.setMaxValue(1000);
        picker.setWrapSelectorWheel(false);

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        builder.setTitle(R.string.serving_dialog_title)
                .setView(view)
                .setPositiveButton(R.string.confirm, (dialog, which) -> {
                    textView_Serving.setText(String.valueOf(selectedValue[0]));//String.valueOf(picker.getValue())
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    dialog.cancel();
                });

        builder.create().show();
    }

    //get Today's Date
    private String getTodaysDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month+1;
        int day = cal.get(Calendar.DAY_OF_MONTH);

        String strmonth = Integer.toString(month);
        String strday = Integer.toString(day);
        String date = year+"-"+strmonth+"-"+strday;

        if(String.valueOf(month).length() == 1){
            strmonth = "0" + month;
            date = year+"-"+strmonth+"-"+ strday;
        }
        if(String.valueOf(day).length() == 1){
            strday = "0" + day;
            date = year+"-"+strmonth+"-"+strday;
        }

        return date;
    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month+1;
                String date = makeDateString(day, month, year);
                textViewDate.setText(date);
            }

        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = android.app.AlertDialog.THEME_HOLO_LIGHT;
        datePickerDialog = new DatePickerDialog(context, style,dateSetListener, year, month, day);

    }
    //Format the Date
    private String makeDateString(int day, int month, int year) {
        return getMonthFormat(month) + " " + day + " " + year;
    }

    //Convert month to String
    private String getMonthFormat(int month) {
        if(month == 1){
            return "JAN";
        }
        else if(month == 1){
            return "JAN";
        }
        else if(month == 2){
            return "FEB";
        }
        else if(month == 3){
            return "MAR";
        }
        else if(month == 4){
            return "APR";
        }
        else if(month == 5){
            return "MAY";
        }
        else if(month == 6){
            return "JUN";
        }
        else if(month == 7){
            return "JUL";
        }
        else if(month == 8){
            return "AUG";
        }
        else if(month == 9){
            return "SEP";
        }
        else if(month == 10){
            return "OCT";
        }
        else if(month == 11){
            return "NOV";
        }
        else {
            return "DEC";
        }
    }
}
