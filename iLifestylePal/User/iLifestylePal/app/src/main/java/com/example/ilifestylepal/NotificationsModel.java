package com.example.ilifestylepal;

public class NotificationsModel {

    private String notif_id;
    private String notif_category;
    private long notif_timestamp;
    private String notif_date;
    private String notif_time;
    private String notif_description;
    private String notif_status;
    private String notif_uid;
    private String sender_uid;
    private String post_id;
    private String comment_id;

    public NotificationsModel() {
    }

    public NotificationsModel(String notif_id, String notif_category, long notif_timestamp, String notif_date, String notif_time, String notif_description, String notif_status, String notif_uid, String sender_uid, String post_id, String comment_id) {
        this.notif_id = notif_id;
        this.notif_category = notif_category;
        this.notif_timestamp = notif_timestamp;
        this.notif_date = notif_date;
        this.notif_time = notif_time;
        this.notif_description = notif_description;
        this.notif_status = notif_status;
        this.notif_uid = notif_uid;
        this.sender_uid = sender_uid;
        this.post_id = post_id;
        this.comment_id = comment_id;
    }

    public String getNotif_id() {
        return notif_id;
    }

    public void setNotif_id(String notif_id) {
        this.notif_id = notif_id;
    }

    public String getNotif_category() {
        return notif_category;
    }

    public void setNotif_category(String notif_category) {
        this.notif_category = notif_category;
    }

    public long getNotif_timestamp() {
        return notif_timestamp;
    }

    public void setNotif_timestamp(long notif_timestamp) {
        this.notif_timestamp = notif_timestamp;
    }

    public String getNotif_date() {
        return notif_date;
    }

    public void setNotif_date(String notif_date) {
        this.notif_date = notif_date;
    }

    public String getNotif_time() {
        return notif_time;
    }

    public void setNotif_time(String notif_time) {
        this.notif_time = notif_time;
    }

    public String getNotif_description() {
        return notif_description;
    }

    public void setNotif_description(String notif_description) {
        this.notif_description = notif_description;
    }

    public String getNotif_status() {
        return notif_status;
    }

    public void setNotif_status(String notif_status) {
        this.notif_status = notif_status;
    }

    public String getNotif_uid() {
        return notif_uid;
    }

    public void setNotif_uid(String notif_uid) {
        this.notif_uid = notif_uid;
    }

    public String getSender_uid() {
        return sender_uid;
    }

    public void setSender_uid(String sender_uid) {
        this.sender_uid = sender_uid;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }
}
