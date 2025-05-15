package com.example.carrental.models;

public class PaymentAnalyticsData {
    private String period;
    private double totalRevenue;
    private int bookingsCount;
    private double avgBookingValue;
    private double lateFees;

    public PaymentAnalyticsData() {
        // No-arg constructor for FXML
    }

    public PaymentAnalyticsData(String period, double totalRevenue, int bookingsCount, double avgBookingValue, double lateFees) {
        this.period = period;
        this.totalRevenue = totalRevenue;
        this.bookingsCount = bookingsCount;
        this.avgBookingValue = avgBookingValue;
        this.lateFees = lateFees;
    }

    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }
    public double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }
    public int getBookingsCount() { return bookingsCount; }
    public void setBookingsCount(int bookingsCount) { this.bookingsCount = bookingsCount; }
    public double getAvgBookingValue() { return avgBookingValue; }
    public void setAvgBookingValue(double avgBookingValue) { this.avgBookingValue = avgBookingValue; }
    public double getLateFees() { return lateFees; }
    public void setLateFees(double lateFees) { this.lateFees = lateFees; }
} 