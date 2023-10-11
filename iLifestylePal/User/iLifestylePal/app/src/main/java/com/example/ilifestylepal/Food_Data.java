package com.example.ilifestylepal;

import java.util.List;

public class Food_Data {
    private String foodname;
    private String calorie;
    private String url;
    private List<String> ingredientsList;
    //private String recommendation;

    public Food_Data(String foodname, String calorie, String url) {
        this.foodname = foodname;
        this.calorie = calorie;
        this.url = url;
    }

    public Food_Data(String foodname, String calorie, String url, List<String> ingredientsList) {
        this.foodname = foodname;
        this.calorie = calorie;
        this.url = url;
        this.ingredientsList = ingredientsList;
       // this.recommendation = recommendation;
    }

    public String getFoodname() {
        return foodname;
    }

    public String getCalorie() {
        return calorie;
    }

    public String getUrl() {
        return url;
    }

    public List<String> getIngredientsList() {
        return ingredientsList;
    }

}
