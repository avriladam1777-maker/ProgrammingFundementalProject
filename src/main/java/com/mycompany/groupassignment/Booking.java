/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.groupassignment;

/**
 *
 * @author md-ishrak
 */

import java.time.LocalDate;

public class Booking {
    
    // Attributes:
    private String bookingToken;
    private String deliveryAddress;
    public  int    rentalDurationDays;
    private LocalDate bookingDate;
    private String status;
    private final User customer;
    private final Vehicle vehicle;
    
    
    // Constructor:
    public Booking(String bookingToken, User customer, Vehicle vehicle,
                    String deliveryAddress, int rentalDurationDays,
                    LocalDate bookingDate, String status) {
        this.bookingToken = bookingToken;
        this.customer = customer;
        this.vehicle = vehicle;
        this.deliveryAddress = deliveryAddress;
        this.rentalDurationDays = rentalDurationDays;
        this.bookingDate = bookingDate;
        this.status = status;
    }
    
    
    // Behaviour methods:
    public void cancel() {
        this.status = "CANCELLED";
        if (vehicle != null) {
            vehicle.setIsAvailable(true);
        }
    }
    
    // Getters / Setters:
    
    public void setBookingToken(String bookingToken) {
        this.bookingToken = bookingToken;
    }

    public String getBookingToken() {
        return bookingToken;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    // No setCustomer()/setVehicle() - by design, see class Javadoc above.
    public User getCustomer() {
        return customer;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }
    
}


