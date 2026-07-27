/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.groupassignment;

/**
 *
 * @author User
 */
import java.time.*;
public class Announcement {
    //Attributes:
    private String messageID;
    public String content;
    public LocalDate datePosted;
    
    //Constructor:
    public Announcement(String messageID, String content, LocalDate datePosted){
        this.messageID = messageID;
        this.content = content;
        this.datePosted = datePosted;
    }
    
    public String displayMessage(){
        return "(" + datePosted + ")" + content;
    } 
    
    public void setMessageID(String messageID){
        this.messageID = messageID;
    }
    
    public String getMessageID(){
        return this.messageID;
    }
}
