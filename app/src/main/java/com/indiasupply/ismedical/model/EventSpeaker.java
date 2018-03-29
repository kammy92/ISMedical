package com.indiasupply.ismedical.model;

/**
 * Created by sud on 3/10/17.
 */

public class EventSpeaker {
    int id, icon;
    String name, image, qualification;
    
    public EventSpeaker (int id, int icon, String name, String qualification, String image) {
        this.id = id;
        this.icon = icon;
        this.name = name;
        this.image = image;
        this.qualification = qualification;
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
    
    public String getQualification () {
        return qualification;
    }
    
    public void setQualification (String qualification) {
        this.qualification = qualification;
    }
}
