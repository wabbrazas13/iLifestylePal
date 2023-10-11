package com.example.ilifestylepal;

import com.google.firebase.Timestamp;

public class PostsModel {

    private String post_id;
    private String post_date;
    private String post_description;
    private String post_privacy;
    private String post_time;
    private String post_type;
    private String post_uid;
    private String post_url;
    private long post_timestamp;

    public PostsModel() {
    }

    public PostsModel(String post_id, String post_date, String post_description, String post_privacy, String post_time, String post_type, String post_uid, String post_url, long post_timestamp) {
        this.post_id = post_id;
        this.post_date = post_date;
        this.post_description = post_description;
        this.post_privacy = post_privacy;
        this.post_time = post_time;
        this.post_type = post_type;
        this.post_uid = post_uid;
        this.post_url = post_url;
        this.post_timestamp = post_timestamp;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getPost_date() {
        return post_date;
    }

    public void setPost_date(String post_date) {
        this.post_date = post_date;
    }

    public String getPost_description() {
        return post_description;
    }

    public void setPost_description(String post_description) {
        this.post_description = post_description;
    }

    public String getPost_privacy() {
        return post_privacy;
    }

    public void setPost_privacy(String post_privacy) {
        this.post_privacy = post_privacy;
    }

    public String getPost_time() {
        return post_time;
    }

    public void setPost_time(String post_time) {
        this.post_time = post_time;
    }

    public String getPost_type() {
        return post_type;
    }

    public void setPost_type(String post_type) {
        this.post_type = post_type;
    }

    public String getPost_uid() {
        return post_uid;
    }

    public void setPost_uid(String post_uid) {
        this.post_uid = post_uid;
    }

    public String getPost_url() {
        return post_url;
    }

    public void setPost_url(String post_url) {
        this.post_url = post_url;
    }

    public long getPost_timestamp() {
        return post_timestamp;
    }

    public void setPost_timestamp(long post_timestamp) {
        this.post_timestamp = post_timestamp;
    }
}
