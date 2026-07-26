/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.groupassignment;

/**
 *
 * @author md-ishrak
 */

import java.util.List;

public class User {
    
    // Attributes:
    private String name;
    private String idNumber;
    private  int    age;
    private String contactNumber;
    private String email;
    
    // Constructor:
    public User(String name, String idNumber, int age, String contactNumber, String email) {
        this.name = name;
        this.idNumber = idNumber;
        this.age = age;
        this.contactNumber = contactNumber;
        this.email = email;
    }
    
    public List<Vehicle> searchVehicles(VehicleServiceBookingSystem system, String keyword,
                                         double minPrice, double maxPrice) {
        return system.searchVehicles(keyword, minPrice, maxPrice);
    }
    
    
    public Booking bookVehicle(VehicleServiceBookingSystem system, String vehicleId,
                                String deliveryAddress, int rentalDurationDays) {
        return system.createBooking(this, vehicleId, deliveryAddress, rentalDurationDays);
    }
    
    public boolean cancelBooking(VehicleServiceBookingSystem system, String bookingToken) {
        return system.cancelBooking(bookingToken);
    }
    
    public void leaveReview(VehicleServiceBookingSystem system, String vehicleId,
                             int rating, String comment) {
        for (Vehicle vehicle : system.getVehicleList()) {
            if (vehicle.getVehicleID().equals(vehicleId)) {
                vehicle.addReview(new Review(this.name, rating, comment));
                return;
            }
        }
    }
    
    
    //  Getters / Setters:
    
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setIDNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getIDNumber() {
        return idNumber;
    }
    
    public void setAge(int age) {
        this.age = age;
    }

    public int getAge() {
        return age;
    }

    public void setContactNum(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getContactNum() {
        return contactNumber;
    }

    public void setUserEmail(String email) {
        this.email = email;
    }

    public String getUserEmail() {
        return email;
    }
    
}
