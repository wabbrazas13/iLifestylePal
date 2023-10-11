package com.example.ilifestylepal;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class SleepRecordAdapter extends RecyclerView.Adapter<SleepRecordAdapter.MyViewHolder> implements Filterable {

    ArrayList<SleepRecordModel> mSleepRecordList;
    ArrayList<SleepRecordModel> mSleepRecordListFull;

    public SleepRecordAdapter(ArrayList<SleepRecordModel> mSleepRecordList) {
        this.mSleepRecordListFull = mSleepRecordList;
        this.mSleepRecordList = new ArrayList<>(mSleepRecordListFull);
    }

    @NonNull
    @Override
    public SleepRecordAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_sleep_records, parent, false);
        return new SleepRecordAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SleepRecordAdapter.MyViewHolder holder, int position) {
        SleepRecordModel model = mSleepRecordList.get(position);

        long startTimestamp = model.getStart_timestamp();
        long endTimestamp = model.getEnd_timestamp();
        long sleepDuration = model.getSleep_duration();
        String sleepWeekday = model.getSleep_weekday();
        int sleepDay = model.getSleep_day();
        String sleepMonth = model.getSleep_month();
        int sleepYear = model.getSleep_year();

        // Set Text For Day Today
        holder.tv_sleepDate.setText(sleepWeekday + ", " + sleepMonth + " " + sleepDay + " " + sleepYear);

        // SetText For Sleep At
        Calendar sleepAtCal = Calendar.getInstance();
        sleepAtCal.setTimeInMillis(startTimestamp);
        sleepAtCal.setTimeZone(TimeZone.getDefault());
        String formattedTime = DateFormat.format("hh:mm aa", sleepAtCal).toString();
        holder.tv_sleepingTime.setText(formattedTime);

        // SetText For Wake Up At
        Calendar wakeUpAtCal = Calendar.getInstance();
        wakeUpAtCal.setTimeInMillis(endTimestamp);
        wakeUpAtCal.setTimeZone(TimeZone.getDefault());
        String formattedTime2 = DateFormat.format("hh:mm aa", wakeUpAtCal).toString();
        holder.tv_wakingTime.setText(formattedTime2);

        // Set Text For Sleep Duration
        long hours = sleepDuration / 60;
        long minutes = sleepDuration % 60;
        String formattedHours = String.format("%d h", hours);
        String formattedMinutes = String.format("%d m", minutes);
        holder.tv_sleepDuration.setText(formattedHours + " " + formattedMinutes);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_sleepDate;
        TextView tv_sleepDuration;
        TextView tv_sleepingTime;
        TextView tv_wakingTime;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_sleepDate = itemView.findViewById(R.id.tv_sleepDate);
            tv_sleepDuration = itemView.findViewById(R.id.tv_sleepDuration);
            tv_sleepingTime = itemView.findViewById(R.id.tv_sleepingTime);
            tv_wakingTime = itemView.findViewById(R.id.tv_wakingTime);

        }
    }

    @Override
    public int getItemCount() {
        return mSleepRecordList.size();
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    private final Filter mFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<SleepRecordModel> filteredList = new ArrayList<>();

            filteredList.addAll(mSleepRecordListFull);

            FilterResults results = new FilterResults();
            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults results) {
            mSleepRecordList.clear();
            mSleepRecordList.addAll((ArrayList)results.values);
            notifyDataSetChanged();
        }
    };
}
