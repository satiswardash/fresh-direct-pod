package com.incture.cherrywork.freshdirect.DB;

/**
 * Created by Arun on 22-09-2016.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBHelper extends SQLiteOpenHelper {


    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Cherrywork";


    String CREATE_TABLE_ORDERS = "CREATE TABLE IF NOT EXISTS " + "ORDERS_TABLE" + "("
            + "order_id" + " VARCHAR," + "erporder_id" + " VARCHAR,"
            + "orderStatus" + " VARCHAR," + "window_start" + " VARCHAR,"
            + "mobile" + " VARCHAR," + "tip" + " VARCHAR,"
            + "window_end" + " VARCHAR," + "delivery_date" + " VARCHAR,"
            + "delivery_instructions" + " VARCHAR," + "unattended_instructions" + " VARCHAR,"
            + "first_name" + " VARCHAR," + "last_name" + " VARCHAR," + "offlinetime" + " VARCHAR," + "address_line_1" + " VARCHAR," + "address_line_2" + " VARCHAR," + "apartment" + " VARCHAR,"
            + "city" + " VARCHAR," + "state" + " VARCHAR," + "zip" + " VARCHAR," + "lat" + " DOUBLE," + "lng" + " DOUBLE," + "offlinelat" + " DOUBLE," + "offlinelng" + " DOUBLE,"
            + "event" + " VARCHAR," + "customerSignature" + " VARCHAR," + "scanCompleted" + " BOOL," + "offlineSyncEnabled" + " BOOL" + ")";

    String CREATE_TABLE_PACKAGES = "CREATE TABLE IF NOT EXISTS " + "PACKAGES_TABLE" + "("
            + "order_id" + " VARCHAR,"
            + "bag_id" + " VARCHAR," + "bin_loc" + " VARCHAR,"
            + "isAlcohol" + " BOOL," + "isMeat" + " BOOL" + ")";


    String CREATE_TABLE_LOCATIONS = "CREATE TABLE IF NOT EXISTS " + "LOCATIONS_TABLE" + "("
            + "order_id" + " VARCHAR,"
            + "lat" + " DOUBLE," + "lng" + " DOUBLE,"
            + "time" + " VARCHAR" + ")";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_ORDERS);
        db.execSQL(CREATE_TABLE_PACKAGES);
        db.execSQL(CREATE_TABLE_LOCATIONS);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}