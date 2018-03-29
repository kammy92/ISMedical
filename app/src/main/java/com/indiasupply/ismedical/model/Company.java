package com.indiasupply.ismedical.model;

public class Company {
    
    int id, icon, no_of_contacts;
    String name, desciption, category, email, website, image, contacts;
    
    public Company (int id, int icon, int no_of_contacts, String name, String description, String category, String email, String website, String image, String contacts) {
        this.id = id;
        this.icon = icon;
        this.no_of_contacts = no_of_contacts;
        this.name = name;
        this.desciption = description;
        this.category = category;
        this.email = email;
        this.website = website;
        this.image = image;
        this.contacts = contacts;
    }
    
    public int getNo_of_contacts () {
        return no_of_contacts;
    }
    
    public void setNo_of_contacts (int no_of_contacts) {
        this.no_of_contacts = no_of_contacts;
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
    
    public String getContacts () {
        return contacts;
    }
    
    public void setContacts (String contacts) {
        this.contacts = contacts;
    }
    
    public String getCategory () {
        return category;
    }
    
    public void setCategory (String category) {
        this.category = category;
    }
    
    public String getImage () {
        return image;
    }
    
    public void setImage (String image) {
        this.image = image;
    }
    
    public String getEmail () {
        return email;
    }
    
    public void setEmail (String email) {
        this.email = email;
    }
    
    public String getWebsite () {
        return website;
    }
    
    public void setWebsite (String website) {
        this.website = website;
    }
    
}
