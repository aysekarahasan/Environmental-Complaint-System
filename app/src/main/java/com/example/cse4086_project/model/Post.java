package com.example.cse4086_project.model;

public class Post {
    private String username;
    private String description;
    private String image;
    private String location;


    public Post(){

    }

    public Post(String username,String description, String image, String location) {

        this.username = username;
        this.description = description;
        this.image = image;
        this.location = location;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
