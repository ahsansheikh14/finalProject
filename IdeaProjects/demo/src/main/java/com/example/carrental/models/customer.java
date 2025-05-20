package com.example.carrental.models;

public class customer {
    private int customerId;
    private String name;
    private String phone;
    private String licenseNo;
    private String password;
    private String email;

    // Constructor with all fields (order matches DB and controller)
    public customer(int customerId, String name, String phone, String licenseNo, String password, String email) {
        this.customerId = customerId;
        this.name = name;
        this.phone = phone;
        this.licenseNo = licenseNo;
        this.password = password;
        this.email = email;
    }

    // Getters and setters
    public int getCustomerId() { return customerId; }
    public int getId() { return customerId; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getContact() { return phone; }
    public String getLicenseNo() { return licenseNo; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
}