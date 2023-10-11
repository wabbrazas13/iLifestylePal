package com.example.ilifestylepal;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.CollationElementIterator;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class DailyActivity_ActivityList_Fragment extends Fragment implements DailyActivity_ActivityList_Interface{

    private RecyclerView recyclerView;
    private List<DailyActivity_Retrieve_Data> list;
    private DailyActivity_Adapter adapter;
    private DatabaseReference databaseReference;
    private EditText edittext_Search;
    private String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private DailyActivity_ActivityList_Interface listener;
    Context context;

    public DailyActivity_ActivityList_Fragment() {
        // Required empty public constructor
    }

    public static DailyActivity_ActivityList_Fragment newInstance() {
        DailyActivity_ActivityList_Fragment fragment = new DailyActivity_ActivityList_Fragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.dailyactivity_activitylist_fragment, container, false);
        recyclerView = view.findViewById(R.id.rv_ActivityList);
        list = new ArrayList<>();
        adapter = new DailyActivity_Adapter(list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Maintenance").child("Daily Activity");

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
                ArrayList<DailyActivity_Retrieve_Data> filteredActivity = new ArrayList<>();
                for (DailyActivity_Retrieve_Data dailyActivity_retrieve_data : list) {
                    if (dailyActivity_retrieve_data.getActivityname().toLowerCase().contains(searchText)) {
                        filteredActivity.add(dailyActivity_retrieve_data);
                    }
                }
                adapter.updateDailyActivityList(filteredActivity);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Code to be executed after text changes
            }
        });
        return view;
    }


    String userWeight;

    @Override
    public void onStart() {
        super.onStart();

        databaseReference.child("Activities").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String activityName = snapshot.child("activityName").getValue(String.class);
                    String met = snapshot.child("met").getValue(String.class);

                    DailyActivity_Retrieve_Data dailyActivity_retrieve_data = new DailyActivity_Retrieve_Data(activityName, met);
                    list.add(dailyActivity_retrieve_data);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public class DailyActivity_Adapter extends RecyclerView.Adapter<DailyActivity_Adapter.DailyActivity_ViewHolder>{

        private List<DailyActivity_Retrieve_Data> dailyactivity_List;
        private Context mContext;

        public DailyActivity_Adapter(List<DailyActivity_Retrieve_Data> dailyActivity_List){this.dailyactivity_List = dailyActivity_List; this.mContext = context;}

        @NonNull
        @Override
        public DailyActivity_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
          View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dailyactivity_list_items, parent, false);
            return new DailyActivity_ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull DailyActivity_ViewHolder holder, int position) {
            DailyActivity_Retrieve_Data dailyActivity_retrieve_data = dailyactivity_List.get(position);
            holder.tv_ActivityName.setText(dailyActivity_retrieve_data.getActivityname());
            holder.tv_MET.setText(dailyActivity_retrieve_data.getMet());

            String activityname = dailyActivity_retrieve_data.getActivityname();
            String met = dailyActivity_retrieve_data.getMet();

            holder.cvContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GetUserWeight();
                    ShowInsertDialog(activityname, met);
                }
            });
        }

        private void GetUserWeight(){
            DatabaseReference weightRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);

            weightRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        if(snapshot.hasChild("weight")){
                            userWeight = snapshot.child("weight").getValue().toString();
                            if (TextUtils.isEmpty(userWeight) || TextUtils.isEmpty(etDuration.getText())) {
                                etBurnedCalorie.setText("");
                            }
                        }
                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        
        final int MINUTES_PER_HOUR = 60;
        TextInputEditText etActivityName, etDuration, etMETValue, etBurnedCalorie;
        AutoCompleteTextView autoComplete_tvDate;
        DatePickerDialog datePickerDialog;

        //Show the Dialog
        private void ShowInsertDialog(String activityname, String met){
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());

            // Inflate the custom layout file
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dailyactivity_insert_dialog, null);

            //textView_Meals = dialogView.findViewById(R.id.textView_Meals);
            etActivityName = dialogView.findViewById(R.id.etActivityName);
            etMETValue = dialogView.findViewById(R.id.etMETValue);
            etDuration = dialogView.findViewById(R.id.etDuration);
            etBurnedCalorie = dialogView.findViewById(R.id.etBurnedCalories);

            etActivityName.setText(activityname);
            etMETValue.setText(met);

            etDuration.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(!TextUtils.isEmpty(etDuration.getText().toString())){
                        try{
                            calculateBurnedCalories();
                        }
                        catch(Exception e){
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                }

                @Override
                public void afterTextChanged(Editable s) {


                }
            });

            autoComplete_tvDate = dialogView.findViewById(R.id.autoComplete_tvDate);

            initDatePicker();
            autoComplete_tvDate.setText(getTodaysDate());
            autoComplete_tvDate.setOnClickListener(view ->{
                // Create a new instance of DatePickerDialog and show it
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                // Set the selected date to the TextView
                                String date = String.format("%d-%02d-%02d", year, monthOfYear + 1, dayOfMonth);
                                autoComplete_tvDate.setText(date);
                            }
                        },
                        // Set the initial date in the dialog to the current date
                        Calendar.getInstance().get(Calendar.YEAR),
                        Calendar.getInstance().get(Calendar.MONTH),
                        Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                );
                // Show the dialog
                datePickerDialog.show();
            });

            // Set the custom layout to the dialog
            dialogBuilder.setView(dialogView);

            // Add the positive button
            dialogBuilder.setPositiveButton("Add Activity", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Perform action for positive button
                    try{
                        InsertActivity();
                    }
                   catch(Exception e){
                       Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                       Log.i("Insert", "onClick: " + e.getMessage());
                   }
                }
            });

            // Add the negative button
            dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Perform action for negative button
                }
            });

            // Create and show the dialog
            AlertDialog alertDialog = dialogBuilder.create();
            alertDialog.show();

        }

        private void InsertActivity(){
            String date = autoComplete_tvDate.getText().toString();
            String activityName = etActivityName.getText().toString();
            String burnedCalorie = etBurnedCalorie.getText().toString();
            String duration = etDuration.getText().toString();

            if (TextUtils.isEmpty(date) || TextUtils.isEmpty(activityName) || TextUtils.isEmpty(burnedCalorie) || TextUtils.isEmpty(duration)) {
                // Handle empty fields
                if (TextUtils.isEmpty(date)) {
                    autoComplete_tvDate.setError("Empty Fields");
                }
                if (TextUtils.isEmpty(activityName)) {
                    etActivityName.setError("Empty Fields");
                }
                if (TextUtils.isEmpty(burnedCalorie)) {
                    etBurnedCalorie.setError("Empty Fields");
                }
                if (TextUtils.isEmpty(duration)) {
                    etDuration.setError("Empty Fields");
                }
                return;
            }
            else{
                //FirebaseReference
                DatabaseReference foodJournalRef = FirebaseDatabase.getInstance().getReference().child("Daily Activity");

                String id = foodJournalRef.push().getKey();

                DailyActivity_Insert_Data dailyActivity_insert_data = new DailyActivity_Insert_Data(date, activityName, burnedCalorie, currentUserID, duration);

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

        }

        private void calculateBurnedCalories() {

            double met = Double.parseDouble(etMETValue.getText().toString());
            double metPerMinute =  met / MINUTES_PER_HOUR;

            double weightInKg = Double.parseDouble(userWeight);
            int durationInMinutes = Integer.parseInt(etDuration.getText().toString());
            double result = metPerMinute * weightInKg;

            result = result*durationInMinutes;
            DecimalFormat df = new DecimalFormat("#.##");
            result = Double.parseDouble(df.format(result));

            etBurnedCalorie.setText(String.valueOf(result));
            Log.i("equation", "weight: "+ weightInKg + " metPerMinute: " + durationInMinutes + " met perminute: "+metPerMinute);

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
                    autoComplete_tvDate.setText(date);
                }

            };

            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            int style = android.app.AlertDialog.THEME_HOLO_LIGHT;
            datePickerDialog = new DatePickerDialog(getContext(), style,dateSetListener, year, month, day);

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

        public void updateDailyActivityList(List<DailyActivity_Retrieve_Data> newDailyActivityList) {
            this.dailyactivity_List = newDailyActivityList;
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return dailyactivity_List.size();
        }

        class DailyActivity_ViewHolder extends RecyclerView.ViewHolder {
            TextView tv_ActivityName, tv_MET;
            CardView cvContainer;

            public DailyActivity_ViewHolder(@NonNull View itemView) {
                super(itemView);
                tv_ActivityName = itemView.findViewById(R.id.tv_ActivityName);
                tv_MET = itemView.findViewById(R.id.tv_MET);
                cvContainer = itemView.findViewById(R.id.cvContainer);
            }
        }
    }


    @Override
    public void onItemClick(DailyActivity_Retrieve_Data dailyActivity_data) {

    }
}