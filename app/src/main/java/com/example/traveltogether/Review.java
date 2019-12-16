package com.example.traveltogether;

public class Review {
    String id;
    String name;
    String content;
    String title;
    String address;
    String time;
    String author;
    String img;

    public Review() {
    }

    public Review(String id, String name, String content, String title, String address, String time, String author, String img) {
        this.id = id;
        this.name = name;
        this.content = content;
        this.title = title;
        this.address = address;
        this.time = time;
        this.author = author;
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
