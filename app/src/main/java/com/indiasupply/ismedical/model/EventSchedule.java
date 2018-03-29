package com.indiasupply.ismedical.model;

/**
 * Created by l on 05/10/2017.
 */

public class EventSchedule {
    int id, icon, day_id;
    String start_time, end_time, description, location, image, date;
    
    public EventSchedule (int id, int icon, int day_id, String date, String start_time, String end_time, String description, String location, String image) {
        this.id = id;
        this.icon = icon;
        this.day_id = day_id;
        this.start_time = start_time;
        this.end_time = end_time;
        this.description = description;
        this.location = location;
        this.image = image;
        this.date = date;
    }
    
    public int getDay_id () {
        return day_id;
    }
    
    public void setDay_id (int day_id) {
        this.day_id = day_id;
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
    
    public String getDate () {
        return date;
    }
    
    public void setDate (String date) {
        this.date = date;
    }
    
    public int getId () {
        return id;
    }
    
    public void setId (int id) {
        this.id = id;
    }
    
    public String getStart_time () {
        return start_time;
    }
    
    public void setStart_time (String start_time) {
        this.start_time = start_time;
    }
    
    public String getEnd_time () {
        return end_time;
    }
    
    public void setEnd_time (String end_time) {
        this.end_time = end_time;
    }
    
    public String getDescription () {
        return description;
    }
    
    public void setDescription (String description) {
        this.description = description;
    }
    
    public String getLocation () {
        return location;
    }
    
    public void setLocation (String location) {
        this.location = location;
    }
}
