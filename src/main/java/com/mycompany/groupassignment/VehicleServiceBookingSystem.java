/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.groupassignment;

/**
 *
 * @author User
 */

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VehicleServiceBookingSystem {
    
    // Attributes:
    private List<Vehicle> vehicleList;
    private List<Booking> bookingList;
    private List<Admin> adminList;
    private List<Announcement> announcementList;
    private List<User> userList;
    
    public VehicleServiceBookingSystem() {
        this.vehicleList = new ArrayList<>();
        this.bookingList = new ArrayList<>();
        this.adminList = new ArrayList<>();
        this.announcementList = new ArrayList<>();
        this.userList = new ArrayList<>();
    }
    
    // Read access:
    // All five return the live list, not a copy
    
    public List<Vehicle> getVehicleList() {
        return vehicleList;
    }

    public List<Booking> getBookingList() {
        return bookingList;
    }

    public List<Admin> getAdminList() {
        return adminList;
    }

    public List<Announcement> getAnnouncementList() {
        return announcementList;
    }

    public List<User> getUserList() {
        return userList;
    }
    
    // Registration:
    // Only User and Admin get dedicated add methods.
    // Vehicle and Announcement have no equivalent; Admin already reaches
    // into getVehicleList()/getAnnouncementList() directly instead. Not
    // an inconsistency to fix here - it's the pattern Admin.java already
    // depends on.
    
    public void registerAdmin(Admin admin) {
        adminList.add(admin);
    }

    public void registerUser(User user) {
        userList.add(user);
    }
    
    // Search:
    
    public List<Vehicle> searchVehicles(String keyword, double minPrice, double maxPrice) {
        List<Vehicle> results = new ArrayList<>();
        String lowerKeyword = (keyword == null) ? "" : keyword.toLowerCase();

        for (Vehicle vehicle : vehicleList) {
            if (!vehicle.getIsAvailable()) {
                continue;
            }
            if (vehicle.getPrice() < minPrice || vehicle.getPrice() > maxPrice) {
                continue;
            }
            boolean keywordMatches = lowerKeyword.isEmpty()
                    || vehicle.getBrand().toLowerCase().contains(lowerKeyword)
                    || vehicle.getModel().toLowerCase().contains(lowerKeyword);
            if (keywordMatches) {
                results.add(vehicle);
            }
        }
        return results;
    }
    
    public Booking searchBookingByToken(String bookingToken) {
        for (Booking booking : bookingList) {
            if (booking.getBookingToken().equals(bookingToken)) {
                return booking;
            }
        }
        return null;
    }
    
    // Booking lifecycle:
    
    public Booking createBooking(User customer, String vehicleId, String deliveryAddress,
                                  int rentalDurationDays) {
        Vehicle vehicle = findVehicleById(vehicleId);
        if (vehicle == null || !vehicle.getIsAvailable()) {
            return null; // TODO exception-handling pass: throw instead of returning null
        }

        String bookingToken = UUID.randomUUID().toString();
        Booking booking = new Booking(bookingToken, customer, vehicle, deliveryAddress,
                rentalDurationDays, LocalDate.now(), "CONFIRMED");

        vehicle.setIsAvailable(false);
        bookingList.add(booking);
        return booking;
    }
    
    public boolean cancelBooking(String bookingToken) {
        Booking booking = searchBookingByToken(bookingToken);
        if (booking == null) {
            return false;
        }
        booking.cancel(); // status flip AND freeing the vehicle both live inside Booking itself
        return true;
    }
    
    // Authentication:
    /**
     * AdminFrame needs some way to check a login attempt against
     * adminList - nothing else in the diagram provides this. Returns the
     * matching Admin, or null if no admin matches the given credentials.
     * @param username the admin username to check
     * @param password the admin password to check
     * @return the matching Admin, or null if no admin matches both
     */
    
    public Admin authenticateAdmin(String username, String password) {
        for (Admin admin : adminList) {
            if (admin.getAdminUsername().equals(username)
                    && admin.getAdminPass().equals(password)) {
                return admin;
            }
        }
        return null;
    }
    
    /**
     * Symmetric with findVehicleById below. Worth a deliberate decision:
     * now that User persists across restarts (like Admin), should
     * CustomerFrame look up a returning customer by idNumber instead of
     * always constructing a brand-new User each session? This method
     * exists either way - whether it gets used is a GUI-flow decision,
     * not a code one.
     * @param idNumber the ID number to search for
     * @return the matching User, or null if no user has that ID number
     */
    public User findUserByIdNumber(String idNumber) {
        for (User user : userList) {
            if (user.getIDNumber().equals(idNumber)) {
                return user;
            }
        }
        return null;
    }
    
    public double calculateTotalRevenue() {
        double total = 0.0;
        for (Booking booking : bookingList) {
            if (booking.isActive()) {
                total += booking.calculateTotalCost();
            }
        }
        return total;
    }
    
    
    private Vehicle findVehicleById(String vehicleId) {
        for (Vehicle vehicle : vehicleList) {
            if (vehicle.getVehicleID().equals(vehicleId)) {
                return vehicle;
            }
        }
        return null;
    }
    
    
}
