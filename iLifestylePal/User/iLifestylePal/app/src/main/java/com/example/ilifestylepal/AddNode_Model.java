package com.example.ilifestylepal;

import java.util.Map;

public class AddNode_Model {
    String calorie;
    String foodname;
    String url;
    private Map<String, Boolean> Ingredients;

    public AddNode_Model() {
    }

    public AddNode_Model(String calorie, String foodname, String url, Map<String, Boolean> Ingredients) {
        this.calorie = calorie;
        this.foodname = foodname;
        this.url = url;
        this.Ingredients = Ingredients;
    }

    public String getCalorie() {
        return calorie;
    }

    public void setCalorie(String calorie) {
        this.calorie = calorie;
    }

    public String getFood_name() {
        return foodname;
    }

    public void setFood_name(String food_name) {
        this.foodname = food_name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, Boolean> getIngredients() {
        return Ingredients;
    }

    public void setIngredients(Map<String, Boolean> Ingredients) {
        this.Ingredients = Ingredients;
    }
}
