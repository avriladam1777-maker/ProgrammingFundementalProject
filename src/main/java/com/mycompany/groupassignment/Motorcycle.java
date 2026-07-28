/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.groupassignment;

/**
 *
 * @author User
 */
public class Motorcycle extends Vehicle{
    private int engineCC;
    public int mileage;
    
    public Motorcycle(String vehicleID, String brand, String model, String imagePath, 
            double price, boolean isAvailable, int engineCC, int mileage)
        {
        super(vehicleID, brand, model, imagePath, price, isAvailable);
        this.engineCC = engineCC;
        this.mileage = mileage;
    }
    
    public void setEngineCC(int newCC){
        if(newCC > 50 && newCC <= 2500){
            this.engineCC = newCC;
        } 
        else if(newCC <= 50){//make sure this will throw an error when compiling later
            System.out.println("Invalid CC for the Motorcycle. Please increase the CC of the engine.");
        }
        else if(newCC > 2500){
            System.out.println("Invalid CC for the Motorcycle. Please lower the CC of the engine.");
        }
        else{
            System.out.println("Invalid CC for the Motorcycle. Please enter a valid number of CC.");
        }
    }
    
    public int getEngineCC(){
        return engineCC;
    }
    
    @Override
    public String displayDetails(){
        if(getIsAvailable() == true){
            return String.format("\nBrand:%25s\nModel:%25s\nEngine CC:%16d\nMileage:%23d\nPrice:%25.2f"
                    + "\nState:%25s", getBrand(),getModel(), engineCC, mileage, getPrice(), "Available");
        }else{
            return String.format("\nBrand:%25s\nModel:%25s\nEngine CC:%16d\nMileage:%23d\nPrice:%25.2f"
                    + "\nState:%25s", getBrand(),getModel(), engineCC, mileage, getPrice(), "Not Available");
        }
    }
}
