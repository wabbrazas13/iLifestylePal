package com.example.ilifestylepal;

public class Food_Log_Data {
    private String foodname;
    private String calorie;
    private String meal;
    private String serving;
    private String url;
    private String date;

    public Food_Log_Data() {
    }

    public Food_Log_Data(String foodname, String calorie, String meal, String serving, String url, String date) {
        this.foodname = foodname;
        this.calorie = calorie;
        this.meal = meal;
        this.serving = serving;
        this.url = url;
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public String getFoodname() {
        return foodname;
    }

    public String getCalorie() {
        return calorie;
    }

    public String getMeal() {
        return meal;
    }

    public String getServing() {
        return serving;
    }

    public String getUrl() {
        return url;
    }
}

