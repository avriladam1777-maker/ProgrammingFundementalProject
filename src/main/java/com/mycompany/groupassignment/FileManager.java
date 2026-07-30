/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.groupassignment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author User
 */
public class FileManager {
    private String vehicleFile;
    private String bookingFile;
    private String adminFile;
    private String announcementFile;
    private String userFile;
    
    private static final String SEP = "\\|"; // regex form, for split()
    private static final String SEP_OUT = "|"; // literal form, for building lines
    
    public FileManager(){
        this.vehicleFile = "vehicles.txt";
        this.bookingFile = "bookings.txt";
        this.adminFile = "admins.txt";
        this.announcementFile = "announcements.txt";
        this.userFile = "users.txt";
    }
    
    public void saveAll(VehicleServiceBookingSystem system) {
        saveVehicles(system.getVehicleList());
        saveBookings(system.getBookingList());
        saveAdmins(system.getAdminList());
        saveAnnouncements(system.getAnnouncementList());
        saveUsers(system.getUserList());
    }
    
    //-------------------------------- Vehicle Files -------------------------------------
    
    public void saveVehicles(List<Vehicle> vehicles){
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(vehicleFile))) {
            for (Vehicle v : vehicles) {
                String reviewsBlock = reviewsToBlock(v.getReviews());
                if (v instanceof Car) {
                    Car c = (Car) v;
                    bw.write("CAR" + SEP_OUT + c.getVehicleID() + SEP_OUT + c.getBrand() + SEP_OUT
                            + c.getModel() + SEP_OUT + c.getImagePath() + SEP_OUT + c.getPrice() + SEP_OUT
                            + c.getIsAvailable() + SEP_OUT + c.getNumDoors() + SEP_OUT + c.transmissionType
                            + SEP_OUT + c.mileage + SEP_OUT + reviewsBlock);
                } else if (v instanceof Motorcycle) {
                    Motorcycle m = (Motorcycle) v;
                    bw.write("MOTO" + SEP_OUT + m.getVehicleID() + SEP_OUT + m.getBrand() + SEP_OUT
                            + m.getModel() + SEP_OUT + m.getImagePath() + SEP_OUT + m.getPrice() + SEP_OUT
                            + m.getIsAvailable() + SEP_OUT + m.getEngineCC() + SEP_OUT + m.mileage
                            + SEP_OUT + reviewsBlock);
                }
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("An error occurred while saving vehicles: " + e.getMessage());
        }
    } 
    
    public List<Vehicle> loadVehicles(){
        List<Vehicle> vehicles = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(vehicleFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                // split(regex, -1) is required here: without it, a vehicle with
                // zero reviews (empty trailing field) drops that last element
                // and every index below shifts.
                String[] p = line.split(SEP, -1);
                String type = p[0];
                boolean isAvailable = Boolean.parseBoolean(p[6]);
                double price = Double.parseDouble(p[5]);
 
                if (type.equals("CAR")) {
                    int numDoors = Integer.parseInt(p[7]);
                    String transmissionType = p[8];
                    int mileage = Integer.parseInt(p[9]);
                    Car c = new Car(p[1], p[2], p[3], p[4], price, isAvailable,
                            numDoors, transmissionType, mileage);
                    for (Review r : parseReviewsBlock(p[10])) {
                        c.addReview(r);
                    }
                    vehicles.add(c);
                } else if (type.equals("MOTO")) {
                    int engineCC = Integer.parseInt(p[7]);
                    int mileage = Integer.parseInt(p[8]);
                    Motorcycle m = new Motorcycle(p[1], p[2], p[3], p[4], price, isAvailable,
                            engineCC, mileage);
                    for (Review r : parseReviewsBlock(p[9])) {
                        m.addReview(r);
                    }
                    vehicles.add(m);
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred while loading vehicles: " + e.getMessage());
        }
        return vehicles;
    }
    
    private String reviewsToBlock(List<Review> reviews) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < reviews.size(); i++) {
            Review r = reviews.get(i);
            sb.append(r.getReviewerName()).append(":").append(r.getRating())
                    .append(":").append(r.getComment());
            if (i < reviews.size() - 1) {
                sb.append(";");
            }
        }
        return sb.toString();
    }
    
    private List<Review> parseReviewsBlock(String block) {
        List<Review> reviews = new ArrayList<>();
        if (block == null || block.isEmpty()) {
            return reviews;
        }
        String[] entries = block.split(";", -1);
        for (String entry : entries) {
            String[] f = entry.split(":", -1);
            try {
                reviews.add(new Review(f[0], Integer.parseInt(f[1]), f[2]));
            } catch (IllegalArgumentException e) {
                System.out.println("Skipping corrupted review entry: " + entry + " (" + e.getMessage() + ")");
            }
        }
        return reviews;
    }
    
    //-------------------------------- Booking Files -------------------------------------
    
    public void saveBookings(List<Booking> bookings){
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(bookingFile))){
            for (Booking b : bookings) {
                bw.write(b.getBookingToken() + SEP_OUT + b.getCustomer().getIDNumber() + SEP_OUT
                        + b.getVehicle().getVehicleID() + SEP_OUT + b.getDeliveryAddress() + SEP_OUT
                        + b.rentalDurationDays + SEP_OUT + b.getBookingDate() + SEP_OUT + b.getStatus());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("An error occurred while saving bookings: " + e.getMessage());
        }
    }
    
    public List<Booking> loadBookings(List<User> userList, List<Vehicle> vehicleList){
        List<Booking> bookings = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(bookingFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(SEP, -1);
                String bookingToken = p[0];
                String customerId = p[1];
                String vehicleId = p[2];
                String deliveryAddress = p[3];
                int rentalDurationDays = Integer.parseInt(p[4]);
                LocalDate bookingDate = LocalDate.parse(p[5]);
                String savedStatus = p[6];
 
                User customer = findUserById(userList, customerId);
                Vehicle vehicle = findVehicleById(vehicleList, vehicleId);
 
                if (customer == null || vehicle == null) {
                    System.out.println("Skipping booking " + bookingToken
                            + ": customer or vehicle no longer exists.");
                    continue;
                }
 
                // Booking's real constructor takes status directly (confirmed
                // against Booking.java) — no need to construct-then-overwrite,
                // the saved status goes straight in.
                Booking booking = new Booking(bookingToken, customer, vehicle,
                        deliveryAddress, rentalDurationDays, bookingDate, savedStatus);
                bookings.add(booking);
            }
        } catch (IOException e) {
            System.out.println("An error occurred while loading bookings: " + e.getMessage());
        }
        return bookings;
    }
    
    private User findUserById(List<User> userList, String idNumber) {
        for (User u : userList) {
            if (u.getIDNumber().equals(idNumber)) {
                return u;
            }
        }
        return null;
    }
    
    private Vehicle findVehicleById(List<Vehicle> vehicleList, String vehicleId) {
        for (Vehicle v : vehicleList) {
            if (v.getVehicleID().equals(vehicleId)) {
                return v;
            }
        }
        return null;
    }
    
    //--------------------------------- Admin Files --------------------------------------
    
    public void saveAdmins(List<Admin> admins){
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(adminFile))){
            for (Admin a : admins){
                // Two new fields appended: isMainAdmin, isActive - needed
                // for the main/sub-admin hierarchy.
                bw.write(a.getAdminID() + SEP_OUT + a.getAdminUsername() + SEP_OUT + a.getAdminPass()
                        + SEP_OUT + a.isMainAdmin() + SEP_OUT + a.isActive());
                bw.newLine();
            }
        }catch(IOException e){
            System.out.println("An error occurred while saving admins: " + e.getMessage());
        }
    }
    
    public List<Admin> loadAdmins(){
        List<Admin> admins = new ArrayList<>(); //this is the temp array to store users
        try (BufferedReader br = new BufferedReader(new FileReader(adminFile))){
            String line;
            while ((line = br.readLine()) != null){
                String[] p = line.split(SEP, -1);
                // Backward-compatible: an old admins.txt saved before this
                // change only has 3 fields. If the new columns aren't
                // there, default to sub-admin/active rather than crashing -
                // ensureMainAdminExists() will still create the one real
                // main admin on top of this if none is found.
                boolean isMainAdmin = (p.length > 3) && Boolean.parseBoolean(p[3]);
                boolean isActive = (p.length > 4) ? Boolean.parseBoolean(p[4]) : true;
                admins.add(new Admin(p[0], p[1], p[2], isMainAdmin, isActive));
            }
        }
        catch (IOException e){
            System.out.println("An error occurred while loading admins: " + e.getMessage());
        }
        return admins;
    }
    
    //------------------------------ Announcement Files ----------------------------------
    
    public void saveAnnouncements(List<Announcement> announcements){
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(announcementFile))){
            for (Announcement a : announcements) {
                bw.write(a.getMessageID() + SEP_OUT + a.content + SEP_OUT + a.datePosted);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("An error occurred while saving announcements: " + e.getMessage());
        }
    }
    
    public List<Announcement> loadAnnouncements(){
        List<Announcement> announcements = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(announcementFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(SEP, -1);
                announcements.add(new Announcement(p[0], p[1], LocalDate.parse(p[2])));
            }
        } catch (IOException e) {
            System.out.println("An error occurred while loading announcements: " + e.getMessage());
        }
        return announcements;
    }
    
    //---------------------------------- User Files --------------------------------------
    
    public void saveUsers(List<User> users){
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(userFile))) {
            for (User u : users) {
                bw.write(u.getName() + SEP_OUT + u.getIDNumber() + SEP_OUT + u.getAge()
                        + SEP_OUT + u.getContactNum() + SEP_OUT + u.getUserEmail());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("An error occurred while saving users: " + e.getMessage());
        }
    }
    
    public List<User> loadUsers(){
        List<User> users = new ArrayList<>(); //this is the temp array to store users
        try (BufferedReader br = new BufferedReader(new FileReader(userFile))){
            String line;
            while ((line = br.readLine()) != null){
                String[] p = line.split(SEP, -1);
                users.add(new User(p[0], p[1], Integer.parseInt(p[2]), p[3], p[4]));
            }
        }
        catch (IOException e){
            System.out.println("An error occurred while loading users: " + e.getMessage());
        }
        return users;
    }
}
