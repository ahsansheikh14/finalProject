package com.example.carrental.models;

public class car {
    private int carId;
    private String model;
    private String brand;
    private int year;
    private double pricePerDay;
    private String status;

    public car(int carId, String model, String brand, int year, double pricePerDay, String status) {
        this.carId = carId;
        this.model = model;
        this.brand = brand;
        this.year = year;
        this.pricePerDay = pricePerDay;
        this.status = status;
    }

    // Getters and setters
    public int getCarId() { return carId; }
    public String getModel() { return model; }
    public String getBrand() { return brand; }
    public int getYear() { return year; }
    public double getPricePerDay() { return pricePerDay; }
    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }
}