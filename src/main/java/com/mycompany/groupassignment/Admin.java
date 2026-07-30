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
    
    // New: main/sub-admin hierarchy. Exactly one Admin should ever have
    // isMainAdmin == true - enforced by
    // VehicleServiceBookingSystem.ensureMainAdminExists() only ever
    // creating one, and createSubAdmin() below always creating sub-admins
    // with this set to false.
    private boolean isMainAdmin;
    
    // New: the "checkbox" the main admin uses to grant/revoke a
    // sub-admin's access. false = blocked - can no longer log in even
    // with correct credentials (see VehicleServiceBookingSystem.authenticateAdmin()).
    private boolean isActive;
    
    // Constructor:
    public Admin(String adminId, String username, String password,
                 boolean isMainAdmin, boolean isActive) {
        this.adminId = adminId;
        this.username = username;
        this.password = password;
        this.isMainAdmin = isMainAdmin;
        this.isActive = isActive;
    }
    
    // The methods.
    
    public void addVehicle(VehicleServiceBookingSystem system, Vehicle vehicle) {
        system.getVehicleList().add(vehicle);
    }
    
    public void removeVehicle(VehicleServiceBookingSystem system, String vehicleId) {
        system.getVehicleList().removeIf(v -> v.getVehicleID().equals(vehicleId));
    }
    
    public boolean updateVehicle(VehicleServiceBookingSystem system, String vehicleId,
                                  String brand, String model, double price) {
        for (Vehicle vehicle : system.getVehicleList()) {
            if (vehicle.getVehicleID().equals(vehicleId)) {
                vehicle.setBrand(brand);
                vehicle.setModel(model);
                vehicle.setPrice(price);
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
    
    // --- New: main-admin-only sub-admin management ---
    
    /**
     * Creates a new sub-admin. Only works if this Admin object IS the
     * main admin - refuses (returns null) otherwise, so a sub-admin can
     * never create more admins, even by calling this directly.
     */
    public Admin createSubAdmin(VehicleServiceBookingSystem system, String subUsername, String subPassword) {
        if (!isMainAdmin) {
            System.out.println("Only the main admin can create sub-admins.");
            return null;
        }
        Admin subAdmin = new Admin(UUID.randomUUID().toString(), subUsername, subPassword, false, true);
        system.registerAdmin(subAdmin);
        return subAdmin;
    }
    
    /**
     * The "checkbox": grants or revokes a sub-admin's access. Only the
     * main admin can call this, and it can never target another admin
     * with isMainAdmin == true - there should only ever be one main
     * admin, and it can't be blocked through this path.
     */
    public boolean setSubAdminActive(VehicleServiceBookingSystem system, String subAdminId, boolean active) {
        if (!isMainAdmin) {
            System.out.println("Only the main admin can change a sub-admin's access.");
            return false;
        }
        for (Admin a : system.getAdminList()) {
            if (a.getAdminID().equals(subAdminId) && !a.isMainAdmin()) {
                a.setActive(active);
                return true;
            }
        }
        return false;
    }
    
    public boolean deleteAnnouncement(VehicleServiceBookingSystem system, String messageId) {
        return system.getAnnouncementList().removeIf(a -> a.getMessageID().equals(messageId));
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
    
    public boolean isMainAdmin() {
        return isMainAdmin;
    }
    
    public void setMainAdmin(boolean isMainAdmin) {
        this.isMainAdmin = isMainAdmin;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }
}
