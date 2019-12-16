package com.example.traveltogether;

public class User {
    String email;
    String hoTen;
    String uid;
    String src;
    String status;

    public User() {
    }

    public User(String email, String hoTen, String uID, String src) {
        this.email = email;
        this.hoTen = hoTen;
        this.uid = uID;
        this.src =src;
        this.status="offline";

    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getUID() {
        return uid;
    }

    public void setUID(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }


}
