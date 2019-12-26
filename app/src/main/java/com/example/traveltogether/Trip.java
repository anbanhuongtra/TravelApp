package com.example.traveltogether;

import java.io.Serializable;

public class Trip implements Serializable {
    String title;
    String id;
    String origin;
    String destination;
    int numOfMember;
    String transportation;
    String content;
    String dateStart;
    String dateEnd;
    String host;

    public Trip( String id,String title, String origin, String destination, int numOfMember, String transportation, String content, String dateStart, String dateEnd,String host) {
        this.title = title;
        this.id = id;
        this.origin = origin;
        this.destination = destination;
        this.numOfMember = numOfMember;
        this.transportation = transportation;
        this.content = content;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.host = host;
    }

    public Trip() {
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String departure) {
        this.origin = departure;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public int getNumOfMember() {
        return numOfMember;
    }

    public void setNumOfMember(int numOfMember) {
        this.numOfMember = numOfMember;
    }

    public String getTransportation() {
        return transportation;
    }

    public void setTransportation(String transportation) {
        this.transportation = transportation;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDateStart() {
        return dateStart;
    }

    public void setDateStart(String dateStart) {
        this.dateStart = dateStart;
    }

    public String getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(String dateEnd) {
        this.dateEnd = dateEnd;
    }
}
