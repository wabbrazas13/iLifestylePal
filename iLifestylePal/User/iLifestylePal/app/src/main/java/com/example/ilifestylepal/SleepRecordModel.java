package com.example.ilifestylepal;

public class SleepRecordModel {

    private String sleep_id;
    private String sleep_uid;
    private String sleep_weekday;
    private String sleep_month;
    private int sleep_day;
    private int sleep_year;
    private long sleep_duration;
    private long start_timestamp;
    private long end_timestamp;

    public SleepRecordModel() {
        //DO nothing
    }

    public SleepRecordModel(String sleep_id, String sleep_uid, String sleep_weekday, String sleep_month, int sleep_day, int sleep_year, long sleep_duration, long start_timestamp, long end_timestamp) {
        this.sleep_id = sleep_id;
        this.sleep_uid = sleep_uid;
        this.sleep_weekday = sleep_weekday;
        this.sleep_month = sleep_month;
        this.sleep_day = sleep_day;
        this.sleep_year = sleep_year;
        this.sleep_duration = sleep_duration;
        this.start_timestamp = start_timestamp;
        this.end_timestamp = end_timestamp;
    }

    public String getSleep_id() {
        return sleep_id;
    }

    public void setSleep_id(String sleep_id) {
        this.sleep_id = sleep_id;
    }

    public String getSleep_uid() {
        return sleep_uid;
    }

    public void setSleep_uid(String sleep_uid) {
        this.sleep_uid = sleep_uid;
    }

    public String getSleep_weekday() {
        return sleep_weekday;
    }

    public void setSleep_weekday(String sleep_weekday) {
        this.sleep_weekday = sleep_weekday;
    }

    public String getSleep_month() {
        return sleep_month;
    }

    public void setSleep_month(String sleep_month) {
        this.sleep_month = sleep_month;
    }

    public int getSleep_day() {
        return sleep_day;
    }

    public void setSleep_day(int sleep_day) {
        this.sleep_day = sleep_day;
    }

    public int getSleep_year() {
        return sleep_year;
    }

    public void setSleep_year(int sleep_year) {
        this.sleep_year = sleep_year;
    }

    public long getSleep_duration() {
        return sleep_duration;
    }

    public void setSleep_duration(long sleep_duration) {
        this.sleep_duration = sleep_duration;
    }

    public long getStart_timestamp() {
        return start_timestamp;
    }

    public void setStart_timestamp(long start_timestamp) {
        this.start_timestamp = start_timestamp;
    }

    public long getEnd_timestamp() {
        return end_timestamp;
    }

    public void setEnd_timestamp(long end_timestamp) {
        this.end_timestamp = end_timestamp;
    }
}
