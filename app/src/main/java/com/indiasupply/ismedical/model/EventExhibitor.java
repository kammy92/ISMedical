package com.indiasupply.ismedical.model;

/**
 * Created by sud on 3/10/17.
 */

public class EventExhibitor {
    int id, icon;
    String name, image, stall;
    
    public EventExhibitor (int id, int icon, String name, String stall, String image) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.image = image;
        this.stall = stall;
    }
    
    public int getIcon () {
        return icon;
    }
    
    public void setIcon (int icon) {
        this.icon = icon;
    }
    
    public int getId () {
        return id;
    }
    
    public void setId (int id) {
        this.id = id;
    }
    
    public String getName () {
        return name;
    }
    
    public void setName (String name) {
        this.name = name;
    }
    
    public String getImage () {
        return image;
    }
    
    public void setImage (String image) {
        this.image = image;
    }
    
    public String getStall () {
        return stall;
    }
    
    public void setStall (String stall) {
        this.stall = stall;
    }
}
