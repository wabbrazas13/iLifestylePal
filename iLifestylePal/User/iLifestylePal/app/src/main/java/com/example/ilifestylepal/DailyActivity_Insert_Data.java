package com.example.ilifestylepal;

public class DailyActivity_Insert_Data {
    private String date;
    private String activityName;
    private String burnedCalorie;
    private String currentUserID;
    private String duration;

    public DailyActivity_Insert_Data() {
    }

    public DailyActivity_Insert_Data(String date, String activityName, String burnedCalorie, String currentUserID, String duration) {
        this.date = date;
        this.activityName = activityName;
        this.burnedCalorie = burnedCalorie;
        this.currentUserID = currentUserID;
        this.duration = duration;
    }



    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getBurnedCalorie() {
        return burnedCalorie;
    }

    public void setBurnedCalorie(String burnedCalorie) {
        this.burnedCalorie = burnedCalorie;
    }

    public String getCurrentUserID() {
        return currentUserID;
    }

    public void setCurrentUserID(String currentUserID) {
        this.currentUserID = currentUserID;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}