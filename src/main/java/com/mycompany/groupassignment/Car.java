/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.groupassignment;

/**
 *
 * @author User
 */
public class Car extends Vehicle {
    private int numDoors;
    public int mileage;
    public String transmissionType;
        
    public Car(String vehicleID, String brand, String model, double price, 
            boolean isAvailable, int numDoors, String transmissionType, int mileage)
        {
        super(vehicleID, brand, model, price, isAvailable);
        setNumDoors(numDoors);
        this.mileage = mileage;
        this.transmissionType = transmissionType;
    }
    
    public void setNumDoors(int newDoors){
        if(newDoors >= 2 && newDoors <= 6){
            this.numDoors = newDoors;
        }else{
            throw new IllegalArgumentException("Invalid number of doors.");
        }
    }
    
    public int getNumDoors(){
        return numDoors;
    }
            
    @Override
    public String displayDetails(){
        if(getIsAvailable() == true){
            return String.format("\nBrand:%25s\nModel:%25s\nType of Engine:%10s\nMileage:%23d"
                    + "\nNumber of Doors:%9d\nPrice:%25.2f\nState:%25s", getBrand(),getModel(), 
                    transmissionType, mileage, numDoors, getPrice(), "Available");
        }else{
            return String.format("\nBrand:%25s\nModel:%25s\nType of Engine:%10s\nMileage:%23d"
                    + "\nNumber of Doors:%9d\nPrice:%25.2f\nState:%25s", getBrand(),getModel(), 
                    transmissionType, mileage, numDoors, getPrice(), "Not Available");
        }
    }
}
