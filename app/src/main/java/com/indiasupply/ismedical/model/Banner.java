package com.indiasupply.ismedical.model;

/**
 * Created by l on 26/09/2017.
 */

public class Banner {
    int id, icon, type_id, type;
    String image, title, url;
    
    public Banner (int id, int type_id, int icon, String image, String title, int type, String url) {
        this.id = id;
        this.type_id = type_id;
        this.icon = icon;
        this.image = image;
        this.title = title;
        this.type = type;
        this.url = url;
    }
    
    public int getType_id () {
        return type_id;
    }
    
    public void setType_id (int type_id) {
        this.type_id = type_id;
    }
    
    public int getType () {
        return type;
    }
    
    public void setType (int type) {
        this.type = type;
    }
    
    public String getUrl () {
        return url;
    }
    
    public void setUrl (String url) {
        this.url = url;
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
    
    public String getImage () {
        return image;
    }
    
    public void setImage (String image) {
        this.image = image;
    }
    
    public String getTitle () {
        return title;
    }
    
    public void setTitle (String title) {
        this.title = title;
    }
}
