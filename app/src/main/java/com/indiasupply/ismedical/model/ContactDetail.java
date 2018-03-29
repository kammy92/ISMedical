package com.indiasupply.ismedical.model;

public class ContactDetail {
    int id, type, icon;
    boolean is_favourite;
    String name, location, city, state, image, contact_number, email;
    
    public ContactDetail (int id, int type, int icon, boolean is_favourite, String name, String location, String city, String state, String contact_number, String email, String image) {
        this.id = id;
        this.type = type;
        this.icon = icon;
        this.is_favourite = is_favourite;
        this.name = name;
        this.location = location;
        this.city = city;
        this.state = state;
        this.image = image;
        this.email = email;
        this.contact_number = contact_number;
    }
    
    public boolean isIs_favourite () {
        return is_favourite;
    }
    
    public void setIs_favourite (boolean is_favourite) {
        this.is_favourite = is_favourite;
    }
    
    public String getCity () {
        return city;
    }
    
    public void setCity (String city) {
        this.city = city;
    }
    
    public String getState () {
        return state;
    }
    
    public void setState (String state) {
        this.state = state;
    }
    
    public String getEmail () {
        return email;
    }
    
    public void setEmail (String email) {
        this.email = email;
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
    
    public int getType () {
        return type;
    }
    
    public void setType (int type) {
        this.type = type;
    }
    
    public boolean is_favourite () {
        return is_favourite;
    }
    
    public String getName () {
        return name;
    }
    
    public void setName (String name) {
        this.name = name;
    }
    
    public String getLocation () {
        return location;
    }
    
    public void setLocation (String location) {
        this.location = location;
    }
    
    public String getImage () {
        return image;
    }
    
    public void setImage (String image) {
        this.image = image;
    }
    
    public String getContact_number () {
        return contact_number;
    }
    
    public void setContact_number (String contact_number) {
        this.contact_number = contact_number;
    }
}