package com.example.ilifestylepal;

public class FriendsModel {

    private String uid;
    private String age;
    private String birthdate;
    private String email;
    private String firstname;
    private String fullname;
    private String gender;
    private String lastname;
    private String pic_url;
    private double bmi;
    private double height;
    private double weight;

    public FriendsModel() {

    }

    public FriendsModel(String uid, String age, String birthdate, String email, String firstname, String fullname, String gender, String lastname, String pic_url, double bmi, double height, double weight) {
        this.uid = uid;
        this.age = age;
        this.birthdate = birthdate;
        this.email = email;
        this.firstname = firstname;
        this.fullname = fullname;
        this.gender = gender;
        this.lastname = lastname;
        this.pic_url = pic_url;
        this.bmi = bmi;
        this.height = height;
        this.weight = weight;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPic_url() {
        return pic_url;
    }

    public void setPic_url(String pic_url) {
        this.pic_url = pic_url;
    }

    public double getBmi() {
        return bmi;
    }

    public void setBmi(double bmi) {
        this.bmi = bmi;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
