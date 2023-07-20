package com.example.networks.Admin.Activities.Data;

public class AdminData {
    String name, email, id, gender, position, mobile;

    public AdminData() {
    }

    public AdminData(String name, String email, String id, String gender, String position, String mobile) {
        this.name = name;
        this.email = email;
        this.id = id;
        this.gender = gender;
        this.position = position;
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}

