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
    
    public Motorcycle(String vehicleID, String brand, String model,
            String imagePath, double price, int engineCC)
        {
        super(vehicleID, brand, model, imagePath, price);
        this.engineCC = engineCC;
    }
    
    public int getEngineCC(){
        return this.engineCC;
    }
    
    public void setEngineCC(int newCC){
        if(newCC > 50 && newCC <= 2500){
            this.engineCC = newCC;
        } 
        else if(newCC <= 50){
            System.out.println("Invalid CC for the Motorcycle. Please increase the CC of the engine.");
        }
        else if(newCC > 2500){
            System.out.println("Invalid CC for the Motorcycle. Please lower the CC of the engine.");
        }
        else{
            System.out.println("Invalid CC for the Motorcycle. Please enter a valid number of CC.");
        }
    }
    
    public void displayDetails(){
        System.out.printf("\nBrand:%25s", getBrand());
        System.out.printf("\nModel:%25s", getModel());
        System.out.printf("\nCC of Engine:%13s", engineCC);
        System.out.printf("\nPrice:%25s", getPrice());
        if(getIsAvailable() == true){
            System.out.printf("\nState:%25s", "Available");
        }else{
            System.out.printf("\nState:%25s", "Not Available");
        }
    }
}
