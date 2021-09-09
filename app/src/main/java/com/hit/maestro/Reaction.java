package com.hit.maestro;

public class Reaction {
    String name;
    String image;
    String comment;
    int Rating ;

    public Reaction(String name, String image, String comment, int rating) {
        this.name = name;
        this.image = image;
        this.comment = comment;
        Rating = rating;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getRating() {
        return Rating;
    }

    public void setRating(int rating) {
        Rating = rating;
    }
}
