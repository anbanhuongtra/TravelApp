package com.example.traveltogether;

public class Food {
    String name;
    Float price;
    String address;
    String cafe;

    public Food(String name, Float price, String address, String cafe) {
        this.name = name;
        this.price = price;
        this.address = address;
        this.cafe = cafe;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCafe() {
        return cafe;
    }

    public void setCafe(String cafe) {
        this.cafe = cafe;
    }
}
