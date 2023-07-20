package com.example.networks.User.Data;

public class FacultyData {
    private String name, email, designation, image, key, category;

    public FacultyData() {
    }

    public FacultyData(String name, String email, String designation, String image, String key, String category) {
        this.name = name;
        this.email = email;
        this.designation = designation;
        this.image = image;
        this.key = key;
        this.category = category;
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

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}

