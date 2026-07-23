/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.groupassignment;

/**
 *
 * @author User
 */
public class Vehicle {
    private String vehicleID;
    private String brand;
    private String model;
    private String imagePath;
    private double price;
    private boolean isAvailable;
    
    public Vehicle(String vehicleID, String brand, String model, String imagePath, double price){
        this.vehicleID = vehicleID;
        this.brand = brand;
        this.model = model;
        this.imagePath = imagePath;
        this.price = price;
        this.isAvailable = false;
    }
    
    public String getVehicleID(){
        return this.vehicleID;
    }
    
    public void setVehicleID(String newVehicleID){
        this.vehicleID = newVehicleID;
    }
    
    public String getBrand(){
        return this.brand;
    }
    
    public void setBrand(String newBrand){
        this.brand = newBrand;
    }
    
    public String getModel(){
        return this.model;
    }
    
    public void setModel(String newModel){
        this.model = newModel;
    }
    
    public String getImagePath(){
        return this.imagePath;
    }
    
    public void setImagePath(String newImagePath){
        this.imagePath = newImagePath;
    }
    
    public boolean getIsAvailable(){
        return this.isAvailable;
    }
    
    public void setIsAvailable(){
        this.isAvailable = true;
    }
    
    public double getPrice(){
        return this.price;
    }
    
    public void setPrice(double newPrice){
        if(newPrice > 0){
            this.price = newPrice;
        }else{
            System.out.println("Error. Invalid price given. Try again with a valid price.");
        }
    }
    
    public void addReview(){//(new Review() called?)
        
    }
    
    public void displayDetails(){
        System.out.printf("\nVehicleID:%20s", vehicleID);
        System.out.printf("\nBrand:%25s", brand);
        System.out.printf("\nModel:%25s", model);
        System.out.printf("\nImage Path:%19s", imagePath);
        System.out.printf("\nPrice:%25s", vehicleID);
        if(isAvailable == true){
            System.out.printf("\nState:%25s", "Available");
        }else{
            System.out.printf("\nState:%25s", "Not Available");
        }
    }
}
