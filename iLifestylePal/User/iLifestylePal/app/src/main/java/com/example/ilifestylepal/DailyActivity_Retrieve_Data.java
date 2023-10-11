package com.example.ilifestylepal;

public class DailyActivity_Retrieve_Data {
    private String activityName;
    private String met;

    public DailyActivity_Retrieve_Data(String activityName, String met) {
        this.activityName = activityName;
        this.met = met;
    }

    public String getActivityname() {
        return activityName;
    }

    public String getMet() {
        return met;
    }
}
