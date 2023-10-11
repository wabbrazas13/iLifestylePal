package com.example.ilifestylepal;

public class IngredientsModel {

    public IngredientsModel() {}

    private String status;
    private String value;

    public IngredientsModel(String status, String value) {
        this.status = status;
        this.value = value;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
