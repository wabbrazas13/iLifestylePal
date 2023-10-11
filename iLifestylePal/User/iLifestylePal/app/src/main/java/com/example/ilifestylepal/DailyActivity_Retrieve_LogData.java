package com.example.ilifestylepal;

public class DailyActivity_Retrieve_LogData {
    private String activityName;
    private String burnedCalorie;
    private String duration;
    private String date;
    private String goal;

    public DailyActivity_Retrieve_LogData() {
    }


    public DailyActivity_Retrieve_LogData(String activityName, String burnedCalorie, String duration, String date) {
        this.activityName = activityName;
        this.burnedCalorie = burnedCalorie;
        this.duration = duration;
        this.date = date;
    }

    public DailyActivity_Retrieve_LogData(String activityName, String goal, String duration) {
        this.activityName = activityName;
        this.goal = goal;
        this.duration = duration;
    }

    public String getGoal() {
        return goal;
    }

    public String getDate() {
        return date;
    }
    public String getActivityName() {
        return activityName;
    }

    public String getBurnedCalorie() {
        return burnedCalorie;
    }

    public String getDuration() {
        return duration;
    }
}
