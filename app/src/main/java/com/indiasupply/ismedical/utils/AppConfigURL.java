package com.indiasupply.ismedical.utils;

public class AppConfigURL {
    public static String version2 = "v1.0";
    //    public static String BASE_URL2 = "https://project-isdental-cammy92.c9users.io/api/" + version2 + "/";
    public static String BASE_URL2 = "http://34.210.142.70/ismedical/api/" + version2 + "/";
    
//    public static String BASE_URL2 = "http://18.218.99.134/medical_app/api/" + version2 + "/";
    
    public static String URL_HOME_EVENT = BASE_URL2 + "home/events";
    public static String URL_EVENT_DETAILS = BASE_URL2 + "event";
    public static String URL_HOME_COMPANIES = BASE_URL2 + "home/companies";
    
    public static String URL_INIT = BASE_URL2 + "/init/application";
    
    public static String URL_USER_EXIST = BASE_URL2 + "user/exist";
    public static String URL_GETOTP = BASE_URL2 + "user/otp";
    public static String URL_REGISTER = BASE_URL2 + "user/register";
    public static String URL_FAVOURITE = BASE_URL2 + "favourite";
    public static String URL_EVENT_INTERESTED = BASE_URL2 + "event/interested";
    
    
    public static String URL_CONTACT_CALLED = BASE_URL2 + "/contact/called";
    public static String URL_CONTACT_MAILED = BASE_URL2 + "/contact/mailed";
    public static String URL_EVENT_CLICKED = BASE_URL2 + "event/clicked";
}