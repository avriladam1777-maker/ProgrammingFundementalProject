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
    
    // Constructor:
    // REQUIRED FIX (found during GUI testing): this constructor was
    // missing entirely before. Without it, all five lists default to
    // null, and MainMenuFrame.loadAllData() throws a
    // NullPointerException the instant it tries to add anything.
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
            return null;
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
     * matching Admin, or null if no admin matches the given credentials
     * OR if that admin has been deactivated by the main admin.
     * @param username the admin username to check
     * @param password the admin password to check
     * @return the matching, active Admin, or null
     */
    
    public Admin authenticateAdmin(String username, String password) {
        for (Admin admin : adminList) {
            if (admin.getAdminUsername().equals(username)
                    && admin.getAdminPass().equals(password)) {
                if (!admin.isActive()) {
                    // Blocked sub-admin - treated exactly like a failed
                    // login, doesn't reveal the account exists but is disabled.
                    return null;
                }
                return admin;
            }
        }
        return null;
    }
    
    /**
     * Guarantees exactly one main admin always exists. Called once from
     * MainMenuFrame.loadAllData(), right after admins are loaded from
     * file, before anyone gets a chance to log in. Never creates a
     * second main admin if one is already present - this is the "hard
     * code: one and only main admin" rule.
     */
    public void ensureMainAdminExists() {
        for (Admin a : adminList) {
            if (a.isMainAdmin()) {
                return;
            }
        }
        // Hardcoded main admin credentials - change these before your demo/submission.
        Admin mainAdmin = new Admin("MAIN-ADMIN", "mainadmin", "asdf123", true, true);
        adminList.add(mainAdmin);
    }
    
    /**
     * Kept for symmetry/possible future use - CustomerFrame no longer
     * calls this directly (login now goes by name, see findUserByName()
     * below), but it's still correct and still findable if a login-by-ID
     * flow is ever needed again.
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
    
    /**
     * New: CustomerFrame's actual login lookup now goes through this -
     * a returning customer only has to type their name, never their
     * auto-generated ID. Case-insensitive, trimmed, first match wins.
     *
     * Known, accepted limitation: names aren't guaranteed unique. Two
     * different customers who register with the exact same name will
     * collide here - the first one found "owns" that name from then on.
     * Deliberate simplification given the project timeline; a production
     * system would need a stronger identity check (e.g. contact number
     * or a proper login/password) to fully solve this.
     * @param name the name to search for
     * @return the first matching User, or null if no user has that name
     */
    public User findUserByName(String name) {
        for (User user : userList) {
            if (user.getName().equalsIgnoreCase(name)) {
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