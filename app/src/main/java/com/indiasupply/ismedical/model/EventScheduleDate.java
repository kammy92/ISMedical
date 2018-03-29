package com.indiasupply.ismedical.model;


public class EventScheduleDate {
    boolean selected;
    int id, icon;
    String logo, date, title;
    
    public EventScheduleDate (boolean selected, int id, int icon, String date, String title, String logo) {
        this.selected = selected;
        this.id = id;
        this.icon = icon;
        this.logo = logo;
        this.title = title;
        this.date = date;
    }
    
    public String getTitle () {
        return title;
    }
    
    public void setTitle (String title) {
        this.title = title;
    }
    
    public int getIcon () {
        return icon;
    }
    
    public void setIcon (int icon) {
        this.icon = icon;
    }
    
    public String getDate () {
        return date;
    }
    
    public void setDate (String date) {
        this.date = date;
    }
    
    public boolean isSelected () {
        return selected;
    }
    
    public void setSelected (boolean selected) {
        this.selected = selected;
    }
    
    public int getId () {
        return id;
    }
    
    public void setId (int id) {
        this.id = id;
    }
    
    public String getLogo () {
        return logo;
    }
    
    public void setLogo (String logo) {
        this.logo = logo;
    }
}