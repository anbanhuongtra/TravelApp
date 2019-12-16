package com.example.traveltogether;

import java.io.Serializable;

public class Place implements Serializable {
    String name;
    String province;
    int img;

    public Place(String name, String province, int img) {
        this.name = name;
        this.province = province;
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }
}
