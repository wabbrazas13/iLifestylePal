package com.example.ilifestylepal;

public class StepTrackerModel {

    private String step_id;
    private String step_uid;
    private String step_weekday;
    private String step_month;
    private int step_day;
    private int step_year;
    private int step_count;
    private int step_goal;
    private long step_timestamp;

    public StepTrackerModel() {
        //DO nothing
    }

    public StepTrackerModel(String step_id, String step_uid, String step_weekday, String step_month, int step_day, int step_year, int step_count, int step_goal, long step_timestamp) {
        this.step_id = step_id;
        this.step_uid = step_uid;
        this.step_weekday = step_weekday;
        this.step_month = step_month;
        this.step_day = step_day;
        this.step_year = step_year;
        this.step_count = step_count;
        this.step_goal = step_goal;
        this.step_timestamp = step_timestamp;
    }

    public String getStep_id() {
        return step_id;
    }

    public void setStep_id(String step_id) {
        this.step_id = step_id;
    }

    public String getStep_uid() {
        return step_uid;
    }

    public void setStep_uid(String step_uid) {
        this.step_uid = step_uid;
    }

    public String getStep_weekday() {
        return step_weekday;
    }

    public void setStep_weekday(String step_weekday) {
        this.step_weekday = step_weekday;
    }

    public String getStep_month() {
        return step_month;
    }

    public void setStep_month(String step_month) {
        this.step_month = step_month;
    }

    public int getStep_day() {
        return step_day;
    }

    public void setStep_day(int step_day) {
        this.step_day = step_day;
    }

    public int getStep_year() {
        return step_year;
    }

    public void setStep_year(int step_year) {
        this.step_year = step_year;
    }

    public int getStep_count() {
        return step_count;
    }

    public void setStep_count(int step_count) {
        this.step_count = step_count;
    }

    public int getStep_goal() {
        return step_goal;
    }

    public void setStep_goal(int step_goal) {
        this.step_goal = step_goal;
    }

    public long getStep_timestamp() {
        return step_timestamp;
    }

    public void setStep_timestamp(long step_timestamp) {
        this.step_timestamp = step_timestamp;
    }
}
