package com.example.carrental.models;

import java.sql.Date;

public class BookingEntry {
    private int bookingId;
    private String carInfo;
    private Date startDate;
    private Date endDate;
    private double totalPrice;
    private String status;
    private double paymentAmount;
    private String paymentStatus;

    public BookingEntry(int bookingId, String carInfo, Date startDate, Date endDate, double totalPrice, String status, double paymentAmount, String paymentStatus) {
        this.bookingId = bookingId;
        this.carInfo = carInfo;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalPrice = totalPrice;
        this.status = status;
        this.paymentAmount = paymentAmount;
        this.paymentStatus = paymentStatus;
    }

    public int getBookingId() { return bookingId; }
    public String getCarInfo() { return carInfo; }
    public Date getStartDate() { return startDate; }
    public Date getEndDate() { return endDate; }
    public double getTotalPrice() { return totalPrice; }
    public String getStatus() { return status; }
    public double getPaymentAmount() { return paymentAmount; }
    public String getPaymentStatus() { return paymentStatus; }
} 