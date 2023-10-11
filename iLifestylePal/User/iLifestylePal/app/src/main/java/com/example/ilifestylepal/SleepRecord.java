package com.example.ilifestylepal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class SleepRecord extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference SleepScheduleRef;
    private String currentUserID;

    private RecyclerView mRecyclerView;
    private ArrayList<SleepRecordModel> mDataList;
    private SleepRecordAdapter mAdapter;

    private TextView tv_previousMonth;
    private TextView tv_totalHours;
    private TextView tv_totalAverage;
    private int year, month, day, weekday;

    private TextView tv_sleepDebtTotal;
    private TextView tv_sleepDebtAverage;
    private TextView tv_sleepDebtStatus;
    private TextView tv_sleepStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_record);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.sleepRecord_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("View Records");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        SleepScheduleRef = FirebaseDatabase.getInstance().getReference().child("Sleep Schedule");

        mDataList = new ArrayList<>();
        mRecyclerView = findViewById(R.id.rv_sleep_records);

        mAdapter = new SleepRecordAdapter(mDataList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        tv_previousMonth = findViewById(R.id.tv_previousMonth);
        tv_totalHours = findViewById(R.id.tv_totalHours);
        tv_totalAverage = findViewById(R.id.tv_totalAverage);
        tv_sleepDebtTotal = findViewById(R.id.tv_sleepDebtTotal);
        tv_sleepDebtAverage = findViewById(R.id.tv_sleepDebtAverage);
        tv_sleepDebtStatus = findViewById(R.id.tv_sleepDebtStatus);
        tv_sleepStatus = findViewById(R.id.tv_sleepStatus);

        GetCurrentDate();
        tv_previousMonth.setText(getMonth(month) + " " + year);

        SleepScheduleRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mDataList.clear();
                long totalHours = 0;
                int totalRecord = 0;

                for (DataSnapshot data : snapshot.getChildren()) {
                    String sleep_uid = data.child("sleep_uid").getValue(String.class);
                    long sleep_duration = data.child("sleep_duration").getValue(Long.class);
                    String sleep_month = data.child("sleep_month").getValue(String.class);
                    int sleep_year = data.child("sleep_year").getValue(Integer.class);

                    if (sleep_uid.equals(currentUserID)) {
                        SleepRecordModel myDataModel = data.getValue(SleepRecordModel.class);
                        mDataList.add(myDataModel);

                        if (sleep_month.equals(getMonth(month)) && sleep_year == year) {
                            totalHours = totalHours + sleep_duration;
                            totalRecord++;
                        }
                    }
                }

                Collections.sort(mDataList, new Comparator<SleepRecordModel>() {
                    @Override
                    public int compare(SleepRecordModel o1, SleepRecordModel o2) {
                        long ts1 = o1.getStart_timestamp();
                        long ts2 = o2.getStart_timestamp();
                        Date d1 = new Date(ts1);
                        Date d2 = new Date(ts2);
                        Timestamp t1 = new Timestamp(d1);
                        Timestamp t2 = new Timestamp(d2);
                        return t2.compareTo(t1); // Sort in descending order
                    }
                });

                mAdapter.notifyDataSetChanged();
                mAdapter.getFilter().filter("");

                CalculatePreviousMonthData(totalHours, totalRecord);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Sleep Schedule", error.getMessage());
            }
        });
    }

    private void CalculatePreviousMonthData(long totalHours, int totalRecord) {
        //Set Text For Total Hours
        long hours = totalHours / 60;
        long minutes = totalHours % 60;
        String formattedHours = String.format("%d h", hours);
        String formattedMinutes = String.format("%d m", minutes);
        tv_totalHours.setText(formattedHours + " " + formattedMinutes);

        //Initialize Total Average
        tv_totalAverage.setText("0 h 0 m");
        tv_sleepDebtTotal.setText("0 hours 0 minutes");
        tv_sleepDebtAverage.setText("0 hours 0 minutes");
        tv_sleepDebtStatus.setText("(Undetermined)");
        tv_sleepDebtStatus.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
        tv_sleepStatus.setText(R.string.sleep_status_none);

        if (totalRecord != 0) {
            //Display Total Average
            long total_avg = totalHours / totalRecord;
            long avg_h = total_avg / 60;
            long avg_m = total_avg % 60;
            String formatted_avg_h = String.format("%d h", avg_h);
            String formatted_avg_m = String.format("%d m", avg_m);
            tv_totalAverage.setText(formatted_avg_h + " " + formatted_avg_m);

            //Calculate Total Sleep Debt
            long totalSleepDebt = ((7 * 60) * totalRecord) - totalHours;
            long sd_th = totalSleepDebt / 60;
            long sd_tm = totalSleepDebt % 60;
            String formatted_sdTH = String.format("%d hours", sd_th);
            String formatted_sdTM = String.format("%d minutes", sd_tm);
            tv_sleepDebtTotal.setText(formatted_sdTH + " " + formatted_sdTM);

            //Calculate Average Sleep Debt
            long averageSleepDebt = totalSleepDebt / totalRecord;
            long sd_ah = averageSleepDebt / 60;
            long sd_am = averageSleepDebt % 60;
            String formatted_sdAH = String.format("%d hours", sd_ah);
            String formatted_sdAM = String.format("%d minutes", sd_am);
            tv_sleepDebtAverage.setText(formatted_sdAH + " " + formatted_sdAM);

            //Set Sleep Status
            if (averageSleepDebt >= 60) {
                tv_sleepDebtStatus.setText("(Significant Sleep Debt)");
                tv_sleepDebtStatus.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.delete));
                tv_sleepStatus.setText(R.string.sleep_status_significant);
            } else if (averageSleepDebt >= 30){
                tv_sleepDebtStatus.setText("(Moderate Sleep Debt)");
                tv_sleepDebtStatus.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.cancel_friend));
                tv_sleepStatus.setText(R.string.sleep_status_moderate);
            } else if (averageSleepDebt >= 10){
                tv_sleepDebtStatus.setText("(Minor Sleep Debt)");
                tv_sleepDebtStatus.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.add_friend));
                tv_sleepStatus.setText(R.string.sleep_status_minor);
            } else if (averageSleepDebt > 0){
                tv_sleepDebtStatus.setText("(Little Sleep Debt)");
                tv_sleepDebtStatus.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.ic_launcher_background));
                tv_sleepStatus.setText(R.string.sleep_status_little);
            } else {
                tv_sleepDebtStatus.setText("(Healthy Sleeping Routine)");
                tv_sleepDebtStatus.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
                tv_sleepStatus.setText(R.string.sleep_status_good);
            }
        }
    }

    private void GetCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1; // Month starts from 0, so add 1 to get the actual month
        day = calendar.get(Calendar.DAY_OF_MONTH);
        weekday = calendar.get(Calendar.DAY_OF_WEEK);

        if (month == 1) {
            month = 12;
            year = year - 1;
        } else {
            month = month - 1;
        }
    }

    private String getMonth(int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, month - 1); // Set the month in the calendar object (month starts from 0)
        // Use SimpleDateFormat to format the month name as a string
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM");
        String monthName = dateFormat.format(calendar.getTime());
        return monthName;
    }
}