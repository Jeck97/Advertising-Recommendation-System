package com.utem.ftmk.ws2.arsclient.model.plan;

import java.io.Serializable;

public class Plan implements Serializable {

    private String id;
    private String name;
    private String description;
    private int durationYear;
    private int durationMonth;
    private int durationDay;
    private double price;

    public Plan() {

    }

    public Plan(String id, String name, String description, int durationYear, int durationMonth, int durationDay, double price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.durationYear = durationYear;
        this.durationMonth = durationMonth;
        this.durationDay = durationDay;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDurationYear() {
        return durationYear;
    }

    public void setDurationYear(int durationYear) {
        this.durationYear = durationYear;
    }

    public int getDurationMonth() {
        return durationMonth;
    }

    public void setDurationMonth(int durationMonth) {
        this.durationMonth = durationMonth;
    }

    public int getDurationDay() {
        return durationDay;
    }

    public void setDurationDay(int durationDay) {
        this.durationDay = durationDay;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
