package com.chat.chitchat.Models;

public class Group {
    String name, imgUrl, uid, time;

    public Group(){

    }

    public Group(String name, String imgUrl, String uid, String time) {
        this.name = name;
        this.imgUrl = imgUrl;
        this.uid = uid;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
