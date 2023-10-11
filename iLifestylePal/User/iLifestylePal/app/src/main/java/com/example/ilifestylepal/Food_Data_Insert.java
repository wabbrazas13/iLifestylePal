package com.example.ilifestylepal;

public class Food_Data_Insert {

    private String calorie;
    private String date;
    private String foodName;
    private String currentUserID;
    private String serving;
    private String meal;
    private String foodURL;

    Food_Data_Insert() {
    }

    public Food_Data_Insert(String date, String calorie, String foodName, String meal, String serving, String currentUserID, String foodURL) {
        this.calorie = calorie;
        this.date = date;
        this.foodName = foodName;
        this.currentUserID = currentUserID;
        this.serving = serving;
        this.meal = meal;
        this.foodURL = foodURL;

    }

    public String getFoodURL() {
        return foodURL;
    }

    public void setFoodURL(String foodURL) {
        this.foodURL = foodURL;
    }

    public void setCalorie(String calorie) {
        this.calorie = calorie;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public void setCurrentUserID(String currentUserID) {
        this.currentUserID = currentUserID;
    }

    public String getCalorie() {
        return calorie;
    }

    public String getDate() {
        return date;
    }

    public String getFoodName() {
        return foodName;
    }

    public String getCurrentUserID() {
        return currentUserID;
    }

    public String getMeal() {
        return meal;
    }

    public void setMeal(String meal) {
        this.meal = meal;
    }

    public String getServing() {
        return serving;
    }

    public void setServing(String serving) {
        this.serving = serving;
    }
}
