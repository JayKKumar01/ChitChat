package com.chat.chitchat.Models;

public class User {
    String uid, name, email, image;
    public User(){

    }

    public User(String uid, String name, String email, String image) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.image = image;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
