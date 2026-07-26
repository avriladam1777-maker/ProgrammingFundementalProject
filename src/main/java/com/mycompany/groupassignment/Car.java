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
    public int numDoor;
    public String mileage;
    public String transmissionType;
        
    public Car(String vehicleID, String brand, String model, String imagePath,
            double price, int numDoor, String transmissionType, String mileage)
        {
        super(vehicleID, brand, model, imagePath, price);
        this.numDoor = numDoor;
        this.mileage = mileage;
        this.transmissionType = transmissionType;
    }
    
    public void setNumDoor(int newDoors){
        if(newDoors >= 2 && newDoors <= 6){
            this.numDoor = newDoors;
        }else{
            System.out.println("Invalid number of doors.");
        }
    }
    
    @Override
    public void displayDetails(){
        System.out.printf("\nBrand:%25s", getBrand());
        System.out.printf("\nModel:%25s", getModel());
        System.out.printf("\nType of Engine:%10s", transmissionType);
        System.out.printf("\nNumber of Doors:%9d", numDoor);
        System.out.printf("\nPrice:%25s", getPrice());
        if(getIsAvailable() == true){
            System.out.printf("\nState:%25s", "Available");
        }else{
            System.out.printf("\nState:%25s", "Not Available");
        }
    }
}
