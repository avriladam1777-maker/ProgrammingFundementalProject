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
import java.util.List;
import java.util.UUID;

public class Admin {
    
    // Attributes:
    private String adminId;
    private String username;
    private String password;
    
    
    // Constructor:
    public Admin(String adminId, String username, String password) {
        this.adminId = adminId;
        this.username = username;
        this.password = password;
    }
    
    // The methods.
    
    public void addVehicle(VehicleServiceBookingSystem system, Vehicle vehicle) {
        system.getVehicleList().add(vehicle);
    }
    
    public void removeVehicle(VehicleServiceBookingSystem system, String vehicleId) {
        system.getVehicleList().removeIf(v -> v.getVehicleID().equals(vehicleId));
    }
    
    public boolean updateVehicle(VehicleServiceBookingSystem system, String vehicleId,
                                  String brand, String model, double price, String imagePath) {
        for (Vehicle vehicle : system.getVehicleList()) {
            if (vehicle.getVehicleID().equals(vehicleId)) {
                vehicle.setBrand(brand);
                vehicle.setModel(model);
                vehicle.setPrice(price);
                vehicle.setImagePath(imagePath);
                return true;
            }
        }
        return false;
    }
    
    public Announcement postAnnouncement(VehicleServiceBookingSystem system, String content) {
        Announcement announcement = new Announcement(
                UUID.randomUUID().toString(), content, LocalDate.now());
        system.getAnnouncementList().add(announcement);
        return announcement;
    }
    
    public List<Booking> viewAllBookings(VehicleServiceBookingSystem system) {
        return system.getBookingList();
    }
    
    public List<Vehicle> viewVehicleStatus(VehicleServiceBookingSystem system) {
        return system.getVehicleList();
    }
    
    // Getters / Setters:
    
    public void setAdminID(String adminId) {
        this.adminId = adminId;
    }

    public String getAdminID() {
        return adminId;
    }

    public void setAdminUsername(String username) {
        this.username = username;
    }

    public String getAdminUsername() {
        return username;
    }

    public void setAdminPass(String password) {
        this.password = password;
    }

    public String getAdminPass() {
        return password;
    }
    
}
