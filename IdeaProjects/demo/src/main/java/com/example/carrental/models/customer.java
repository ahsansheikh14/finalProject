package com.example.carrental.models;

public class customer {
    private int customerId;
    private String name;
    private String phone;
    private String licenseNo;
    private String email;
    private String password;

    // Constructor with all fields
    public customer(int customerId, String name, String phone, String licenseNo, String email, String password) {
        this.customerId = customerId;
        this.name = name;
        this.phone = phone;
        this.licenseNo = licenseNo;
        this.email = email;
        this.password = password;
    }

    // Constructor without email (backwards compatibility)
    public customer(int customerId, String name, String phone, String licenseNo, String password) {
        this.customerId = customerId;
        this.name = name;
        this.phone = phone;
        this.licenseNo = licenseNo;
        this.password = password;
        this.email = "";  // Default empty email
    }

    // Getters and setters
    public int getCustomerId() { return customerId; }
    public int getId() { return customerId; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getContact() { return phone; }  // For backwards compatibility
    public String getLicenseNo() { return licenseNo; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
}