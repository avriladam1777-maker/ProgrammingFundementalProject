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
    public int numDoors;
    public int mileage;
    public String transmissionType;
        
    public Car(String vehicleID, String brand, String model, String imagePath, double price, 
            boolean isAvailable, int numDoors, String transmissionType, int mileage)
        {
        super(vehicleID, brand, model, imagePath, price, isAvailable);
        setNumDoors(numDoors);
        this.mileage = mileage;
        this.transmissionType = transmissionType;
    }
    
    public void setNumDoors(int newDoors){
        if(newDoors >= 2 && newDoors <= 6){
            this.numDoors = newDoors;
        }else{
            System.out.println("Invalid number of doors.");//dont forgot to change this to String.format in the future
        }
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
