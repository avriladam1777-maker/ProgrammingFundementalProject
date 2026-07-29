/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.groupassignment;

/**
 *
 * @author User
 */
public class Review {
    private final String reviewerName;
    private final int rating;
    private final String comment;
    
    //polymorphism constructor same name but different parameter
    public Review(String reviewerName, int rating){
        this(reviewerName, rating, "");
    }
    
    //reason to have this is to give option to have a comment or not
    //the asking for the comment will have to be written in the GUI later
    public Review(String reviewerName, int rating, String comment){
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5, got: " + rating);
        }
        this.reviewerName = reviewerName;
        this.rating = rating;
        this.comment = (comment == null) ? "" : comment;
    }
    
    public String getReviewerName(){
        return reviewerName;
    }
    
    public int getRating(){
        return rating;
    }
    
    public String getComment(){
        return comment;
    }
    
    public boolean hasComment(){
        return comment != null && !comment.isEmpty();
    }
}
