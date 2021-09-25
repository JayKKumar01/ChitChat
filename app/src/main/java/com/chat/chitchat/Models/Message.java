package com.chat.chitchat.Models;

public class Message {
    String txt,name,email,uid,imgurl;
    Long time;

    public Message(){

    }

    public Message(String txt, String name, String email, String uid, String imgurl, Long time) {
        this.txt = txt;
        this.name = name;
        this.email = email;
        this.uid = uid;
        this.imgurl = imgurl;
        this.time = time;
    }

    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
