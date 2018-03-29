package com.indiasupply.ismedical.model;

/**
 * Created by l on 26/09/2017.
 */

public class Event {
    boolean interested;
    int id, icon;
    String image, name, start_date, end_date, venue, type, city;
    
    public Event (boolean interested, int id, int icon, String type, String name, String start_date, String end_date, String venue, String image, String city) {
        this.interested = interested;
        this.id = id;
        this.icon = icon;
        this.image = image;
        this.name = name;
        this.start_date = start_date;
        this.end_date = end_date;
        this.venue = venue;
        this.type = type;
        this.city = city;
    }
    
    public String getCity () {
        return city;
    }
    
    public void setCity (String city) {
        this.city = city;
    }
    
    public boolean isInterested () {
        return interested;
    }
    
    public void setInterested (boolean interested) {
        this.interested = interested;
    }
    
    public String getType () {
        return type;
    }
    
    public void setType (String type) {
        this.type = type;
    }
    
    public int getId () {
        return id;
    }
    
    public void setId (int id) {
        this.id = id;
    }
    
    public int getIcon () {
        return icon;
    }
    
    public void setIcon (int icon) {
        this.icon = icon;
    }
    
    public String getImage () {
        return image;
    }
    
    public void setImage (String image) {
        this.image = image;
    }
    
    public String getName () {
        return name;
    }
    
    public void setName (String name) {
        this.name = name;
    }
    
    public String getStart_date () {
        return start_date;
    }
    
    public void setStart_date (String start_date) {
        this.start_date = start_date;
    }
    
    public String getEnd_date () {
        return end_date;
    }
    
    public void setEnd_date (String end_date) {
        this.end_date = end_date;
    }
    
    public String getVenue () {
        return venue;
    }
    
    public void setVenue (String venue) {
        this.venue = venue;
    }
}
