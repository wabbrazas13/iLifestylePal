package com.example.ilifestylepal;

public class CommentsModel {

    public CommentsModel() {}

    private String post_id;
    private String comment_id;
    private String comment_uid;
    private String comment_text;
    private String comment_date;
    private String comment_time;
    private long comment_timestamp;

    public CommentsModel(String post_id, String comment_id, String comment_uid, String comment_text, String comment_date, String comment_time, long comment_timestamp) {
        this.post_id = post_id;
        this.comment_id = comment_id;
        this.comment_uid = comment_uid;
        this.comment_text = comment_text;
        this.comment_date = comment_date;
        this.comment_time = comment_time;
        this.comment_timestamp = comment_timestamp;
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

    public String getComment_uid() {
        return comment_uid;
    }

    public void setComment_uid(String comment_uid) {
        this.comment_uid = comment_uid;
    }

    public String getComment_text() {
        return comment_text;
    }

    public void setComment_text(String comment_text) {
        this.comment_text = comment_text;
    }

    public String getComment_date() {
        return comment_date;
    }

    public void setComment_date(String comment_date) {
        this.comment_date = comment_date;
    }

    public String getComment_time() {
        return comment_time;
    }

    public void setComment_time(String comment_time) {
        this.comment_time = comment_time;
    }

    public long getComment_timestamp() {
        return comment_timestamp;
    }

    public void setComment_timestamp(long comment_timestamp) {
        this.comment_timestamp = comment_timestamp;
    }
}
