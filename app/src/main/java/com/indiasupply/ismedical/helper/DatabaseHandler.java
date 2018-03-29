package com.indiasupply.ismedical.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.indiasupply.ismedical.utils.AppConfigTags;
import com.indiasupply.ismedical.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DatabaseHandler extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 2;
    // Database Name
    private static final String DATABASE_NAME = "ismedical";
    
    private static final String TABLE_EVENTS = "tbl_events";
    
    private static final String TABLE_FILTER = "tbl_filter";
    
    
    
    private static final String EVNT_ID = "evnt_id";
    private static final String EVNT_DETAILS = "evnt_details";
    private static final String EVNT_FLOOR_PLAN = "evnt_floor_plan";
    
    
    private static final String FILTER_CATEGORY = "filter_category";
    
    // Notes table Create Statements
    private static final String CREATE_TABLE_EVENTS = "CREATE TABLE "
            + TABLE_EVENTS + "(" +
            EVNT_ID + " INTEGER," +
            EVNT_DETAILS + " TEXT," +
            EVNT_FLOOR_PLAN + " TEXT" + ")";
    
    
    // Notes table Create Statements
    private static final String CREATE_TABLE_FILTERS = "CREATE TABLE "
            + TABLE_FILTER + "(" +
            FILTER_CATEGORY + " TEXT" + ")";
    
    Context mContext;
    private boolean LOG_FLAG = false;
    
    public DatabaseHandler (Context context) {
        super (context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }
    
    @Override
    public void onCreate (SQLiteDatabase db) {
        db.execSQL (CREATE_TABLE_EVENTS);
        db.execSQL (CREATE_TABLE_FILTERS);
    }
    
    @Override
    public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL ("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        db.execSQL ("DROP TABLE IF EXISTS " + TABLE_FILTER);
        onCreate (db);
    }
    
    
    public void closeDB () {
        SQLiteDatabase db = this.getReadableDatabase ();
        if (db != null && db.isOpen ())
            db.close ();
    }
    
    private String getDateTime () {
        SimpleDateFormat dateFormat = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss", Locale.getDefault ());
        Date date = new Date ();
        return dateFormat.format (date);
    }
    
    
    public long insertEvent (int event_id, String event_details) {
        SQLiteDatabase db = this.getWritableDatabase ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Creating Event", LOG_FLAG);
        ContentValues values = new ContentValues ();
        values.put (EVNT_ID, event_id);
        values.put (EVNT_DETAILS, event_details);
        values.put (EVNT_FLOOR_PLAN, "");
        return db.insert (TABLE_EVENTS, null, values);
    }
    
    public boolean isEventExist (int event_id) {
        String countQuery = "SELECT * FROM " + TABLE_EVENTS + " WHERE " + EVNT_ID + " = " + event_id;
        SQLiteDatabase db = this.getReadableDatabase ();
        Cursor cursor = db.rawQuery (countQuery, null);
        int count = cursor.getCount ();
        cursor.close ();
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }
    
    public int updateEventDetails (int event_id, String details) {
        SQLiteDatabase db = this.getWritableDatabase ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Update event details in event id = " + event_id, LOG_FLAG);
        ContentValues values = new ContentValues ();
        values.put (EVNT_DETAILS, details);
        return db.update (TABLE_EVENTS, values, EVNT_ID + " = ?", new String[] {String.valueOf (event_id)});
    }
    
    public String getEventDetails (int event_id) {
        SQLiteDatabase db = this.getReadableDatabase ();
        String selectQuery = "SELECT * FROM " + TABLE_EVENTS + " WHERE " + EVNT_ID + " = " + event_id;
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Get event details where event ID = " + event_id, LOG_FLAG);
        Cursor c = db.rawQuery (selectQuery, null);
        if (c != null)
            c.moveToFirst ();
        return c.getString (c.getColumnIndex (EVNT_DETAILS));
    }
    
    public int updateEventFloorPlan (int event_id, String floor_plan) {
        SQLiteDatabase db = this.getWritableDatabase ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Update event floor plan in event id = " + event_id, LOG_FLAG);
        ContentValues values = new ContentValues ();
        values.put (EVNT_FLOOR_PLAN, floor_plan);
        return db.update (TABLE_EVENTS, values, EVNT_ID + " = ?", new String[] {String.valueOf (event_id)});
    }
    
    public String getEventFloorPlan (int event_id) {
        SQLiteDatabase db = this.getReadableDatabase ();
        String selectQuery = "SELECT * FROM " + TABLE_EVENTS + " WHERE " + EVNT_ID + " = " + event_id;
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Get event floor plan where event ID = " + event_id, LOG_FLAG);
        Cursor c = db.rawQuery (selectQuery, null);
        if (c != null)
            c.moveToFirst ();
        return c.getString (c.getColumnIndex (EVNT_FLOOR_PLAN));
    }
    
    public void deleteEvent (int event_id) {
        SQLiteDatabase db = this.getWritableDatabase ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Delete event where id = " + event_id, LOG_FLAG);
        db.execSQL ("DELETE FROM " + TABLE_EVENTS + " WHERE " + EVNT_ID + " = " + event_id);
    }
    
    
    
    public long insertFilter (String category) {
        SQLiteDatabase db = this.getWritableDatabase ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Creating filter", LOG_FLAG);
        ContentValues values = new ContentValues ();
        values.put (FILTER_CATEGORY, category);
        return db.insert (TABLE_FILTER, null, values);
    }
    
    public boolean isFilterExist (String category) {
        String countQuery = "SELECT * FROM " + TABLE_FILTER + " WHERE " + FILTER_CATEGORY + " = '" + category + "'";
        SQLiteDatabase db = this.getReadableDatabase ();
        Cursor cursor = db.rawQuery (countQuery, null);
        int count = cursor.getCount ();
        cursor.close ();
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }
    
    public ArrayList<String> getAllFilters () {
        ArrayList<String> filterList = new ArrayList<String> ();
        SQLiteDatabase db = this.getReadableDatabase ();
        String selectQuery = "SELECT  * FROM " + TABLE_FILTER;
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Get all filters", LOG_FLAG);
        Cursor c = db.rawQuery (selectQuery, null);
        if (c.moveToFirst ()) {
            do {
                filterList.add (c.getString ((c.getColumnIndex (FILTER_CATEGORY))));
            } while (c.moveToNext ());
        }
        return filterList;
    }
    
    public void deleteFilter (String category) {
        SQLiteDatabase db = this.getWritableDatabase ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Delete filter where filter = " + category, LOG_FLAG);
        db.execSQL ("DELETE FROM " + TABLE_FILTER + " WHERE " + FILTER_CATEGORY + " = '" + category + "'");
    }
    
    public void deleteAllFilters () {
        SQLiteDatabase db = this.getWritableDatabase ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Delete all filters", LOG_FLAG);
        db.execSQL ("delete from " + TABLE_FILTER);
    }
}