package com.indiasupply.ismedical.model;


public class CategoryFilter {
    
    int id;
    String group_name, name;
    boolean is_selected;
    
    public CategoryFilter (int id, String group_name, String name) {
        this.id = id;
        this.group_name = group_name;
        this.name = name;
    }
    
    public CategoryFilter () {
    }
    
    public boolean is_selected () {
        return is_selected;
    }
    
    public void setIs_selected (boolean is_selected) {
        this.is_selected = is_selected;
    }
    
    public int getId () {
        return id;
    }
    
    public void setId (int id) {
        this.id = id;
    }
    
    public String getGroup_name () {
        return group_name;
    }
    
    public void setGroup_name (String group_name) {
        this.group_name = group_name;
    }
    
    public String getName () {
        return name;
    }
    
    public void setName (String name) {
        this.name = name;
    }
}