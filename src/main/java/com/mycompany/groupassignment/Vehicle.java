/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.groupassignment;

/**
 *
 * @author User
 */
import java.util.List;
import java.util.ArrayList;
public abstract class Vehicle {
    private String vehicleID;
    private String brand;
    private String model;
    private String imagePath;
    private double price;
    private boolean isAvailable;
    
    // not in UML — required by addReview()/getAverageRating()
    private List<Review> reviews;
    
    public Vehicle(String vehicleID, String brand, String model, 
            String imagePath, double price, boolean isAvailable)
        {
        this.vehicleID = vehicleID;
        this.brand = brand;
        this.model = model;
        this.imagePath = imagePath;
        this.price = price;
        this.isAvailable = isAvailable;
        this.reviews = new ArrayList<>();
    }
    
    public String getVehicleID(){
        return vehicleID;
    }
    
    public void setVehicleID(String newVehicleID){
        this.vehicleID = newVehicleID;
    }
    
    public String getBrand(){
        return brand;
    }
    
    public void setBrand(String newBrand){
        this.brand = newBrand;
    }
    
    public String getModel(){
        return model;
    }
    
    public void setModel(String newModel){
        this.model = newModel;
    }
    
    public String getImagePath(){
        return imagePath;
    }
    
    public void setImagePath(String newImagePath){
        this.imagePath = newImagePath;
    }
    
    public boolean getIsAvailable(){
        return isAvailable;
    }
    
    public void setIsAvailable(boolean isAvailable){
    this.isAvailable = isAvailable;
    }
    
    public double getPrice(){
        return price;
    }
    
    public void setPrice(double newPrice){
        if(newPrice > 0){
            this.price = newPrice;
        }else{
            System.out.println("Error. Invalid price given. Try again with a valid price.");
        }
    }
    
    public void addReview(Review review){//(new Review() called?)
        reviews.add(review);
    }
    
    public List<Review> getReviews() {
        return reviews;
    }
    
    public double getAverageRating(){//(take rating from review class and make it average)
        if (reviews.isEmpty()) {
            return 0.0;
        }
        int total = 0;
        for (Review r : reviews) {
            total += r.getRating();
        }
        return (double) total / reviews.size();
    }
    
    public abstract String displayDetails();
}
