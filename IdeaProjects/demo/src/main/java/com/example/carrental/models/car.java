package com.example.carrental.models;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class car {
    private final SimpleIntegerProperty carId;
    private final SimpleStringProperty model;
    private final SimpleStringProperty brand;
    private final SimpleIntegerProperty year;
    private final SimpleDoubleProperty pricePerDay;
    private final SimpleStringProperty status;
    private final SimpleIntegerProperty seats;
    private final SimpleBooleanProperty special;

    // Constructor with direct fields for seats and isSpecial
    public car(int carId, String model, String brand, int year, double pricePerDay, String status, int seats, boolean isSpecial) {
        this.carId = new SimpleIntegerProperty(carId);
        this.model = new SimpleStringProperty(model);
        this.brand = new SimpleStringProperty(brand);
        this.year = new SimpleIntegerProperty(year);
        this.pricePerDay = new SimpleDoubleProperty(pricePerDay);
        this.status = new SimpleStringProperty(status);
        this.seats = new SimpleIntegerProperty(seats);
        this.special = new SimpleBooleanProperty(isSpecial);
    }

    // Constructor that maintains backward compatibility
    public car(int carId, String model, String brand, int year, double pricePerDay, String status) {
        this(carId, model, brand, year, pricePerDay, status, 4, false);
    }

    // Getters and setters
    public int getCarId() { return carId.get(); }
    public SimpleIntegerProperty carIdProperty() { return carId; }
    
    public String getModel() { return model.get(); }
    public SimpleStringProperty modelProperty() { return model; }
    
    public String getBrand() { return brand.get(); }
    public SimpleStringProperty brandProperty() { return brand; }
    
    public int getYear() { return year.get(); }
    public SimpleIntegerProperty yearProperty() { return year; }
    
    public double getPricePerDay() { return pricePerDay.get(); }
    public SimpleDoubleProperty pricePerDayProperty() { return pricePerDay; }
    
    public String getStatus() { return status.get(); }
    public SimpleStringProperty statusProperty() { return status; }
    public void setStatus(String status) { this.status.set(status); }
    
    public int getSeats() { return seats.get(); }
    public SimpleIntegerProperty seatsProperty() { return seats; }
    public void setSeats(int seats) { this.seats.set(seats); }
    
    public boolean isSpecial() { return special.get(); }
    public SimpleBooleanProperty specialProperty() { return special; }
    public void setSpecial(boolean special) { this.special.set(special); }
}