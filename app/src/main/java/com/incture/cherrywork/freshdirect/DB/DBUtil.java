package com.incture.cherrywork.freshdirect.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.incture.cherrywork.freshdirect.Models.OrderModel;

import java.util.ArrayList;

/**
 * Created by Arun on 22-09-2016.
 */

public class DBUtil {

    public static void checkAndOpenDataBase(Context context) {
        if (!DbAdapter.getDbAdapterInstance().isDataBaseOpen()) {
            DbAdapter.getDbAdapterInstance().open(context);
        }
    }

    public static long insert(String tableName, ContentValues contentValues) {

        return DbAdapter.getDbAdapterInstance().insert(tableName,
                contentValues);
    }

    public static int delete(Context context, String tableName, String whereClause,
                             String[] whereArgs) {
        checkAndOpenDataBase(context);
        return DbAdapter.getDbAdapterInstance().delete(tableName, whereClause,
                whereArgs);
    }

    public static boolean isOrderadded(String orderId){

        String queryString = "SELECT * FROM "+"ORDERS_TABLE"+" WHERE "+"order_id"+" = "+"'"+orderId+"'";
        Cursor c = DbAdapter.getDbAdapterInstance().rawQuery(queryString,"");
        if(c.getCount() > 0){
            return true;
        }
        else{
            return false;
        }


    }

    public void addOrders(ArrayList<OrderModel> orderModelList) {


        for (int i = 0; i < orderModelList.size(); i++) {
            ContentValues values = new ContentValues();
            values.put("order_id", orderModelList.get(i).order_id);
            values.put("erporder_id", orderModelList.get(i).erporder_id);
            values.put("orderStatus", orderModelList.get(i).orderStatus);
            values.put("tip", orderModelList.get(i).tip);
            values.put("mobile", orderModelList.get(i).mobile);
            values.put("window_start", orderModelList.get(i).window_start);
            values.put("window_end", orderModelList.get(i).window_end);
            values.put("delivery_date", orderModelList.get(i).delivery_date);
            values.put("delivery_instructions", orderModelList.get(i).delivery_instructions);
            values.put("unattended_instructions", orderModelList.get(i).unattended_instructions);
            values.put("first_name", orderModelList.get(i).first_name);
            values.put("last_name", orderModelList.get(i).last_name);
            values.put("address_line_1",orderModelList.get(i).address_line_1);
            values.put("address_line_2",orderModelList.get(i).address_line_2);
            values.put("apartment",orderModelList.get(i).apartment);
            values.put("city", orderModelList.get(i).city);
            values.put("state", orderModelList.get(i).state);
            values.put("zip", orderModelList.get(i).zip);
            values.put("lat", orderModelList.get(i).lat);
            values.put("lng", orderModelList.get(i).lng);
            values.put("offlinelat", "");
            values.put("offlinelng", "");
            values.put("offlinetime", "");
            addPackages(orderModelList.get(i).order_id, orderModelList.get(i).packages);
            values.put("offlineSyncEnabled", false);
            values.put("event", orderModelList.get(i).event);
            values.put("customerSignature", orderModelList.get(i).customerSignature);
            if(!isOrderadded(orderModelList.get(i).order_id)) {
                values.put("scanCompleted", false);
                DbAdapter.getDbAdapterInstance().insert("ORDERS_TABLE", null, values);
            }else{
                DbAdapter.getDbAdapterInstance().update("ORDERS_TABLE", values,"order_id='" + orderModelList.get(i).order_id + "'");
            }
        }
    }

    public void addPackages(String orderId, ArrayList<OrderModel> orderModelList) {

        ArrayList<OrderModel> packages = orderModelList;
        for (int j = 0; j < packages.size(); j++) {
            ContentValues values = new ContentValues();
            values.put("order_id", orderId);
            values.put("bag_id", packages.get(j).bag_id);
            values.put("bin_loc", packages.get(j).bin_loc);
            values.put("isAlcohol", packages.get(j).isAlcohol);
            values.put("isMeat", packages.get(j).isMeat);
            DbAdapter.getDbAdapterInstance().insert("PACKAGES_TABLE", null, values);
        }

    }

    public void addLocationsOffline(String orderId, double latitude, double longitude, String time) {
        ContentValues values = new ContentValues();
        values.put("order_id", orderId);
        values.put("lat", latitude);
        values.put("lng", longitude);
        values.put("time", time);
        DbAdapter.getDbAdapterInstance().insert("LOCATIONS_TABLE", null, values);
    }

    public OrderModel getOrderDetails(Context c, String orderId) {
        Cursor cursor = DbAdapter.getDbAdapterInstance().getResultSet("ORDERS_TABLE", new String[]{"order_id", "erporder_id","orderStatus", "tip",
                "mobile", "window_start", "window_end", "delivery_date", "delivery_instructions", "unattended_instructions",
                "first_name", "last_name","address_line_1","address_line_2","apartment", "city", "state", "zip", "lat", "lng","event","customerSignature"}, "order_id" + " = '" + orderId + "'", null, null, c);
        OrderModel orderModel = new OrderModel();
        if (null != cursor) {

            if (cursor.moveToFirst()) {
                do {

                    orderModel.order_id = (cursor.getString(cursor.getColumnIndexOrThrow("order_id")));
                    orderModel.erporder_id = (cursor.getString(cursor.getColumnIndexOrThrow("erporder_id")));
                    orderModel.orderStatus = (cursor.getString(cursor.getColumnIndexOrThrow("orderStatus")));
                    orderModel.tip = (cursor.getString(cursor.getColumnIndexOrThrow("tip")));
                    orderModel.mobile = (cursor.getString(cursor.getColumnIndexOrThrow("mobile")));
                    orderModel.window_start = (cursor.getString(cursor.getColumnIndexOrThrow("window_start")));
                    orderModel.window_end = (cursor.getString(cursor.getColumnIndexOrThrow("window_end")));
                    orderModel.delivery_date = (cursor.getString(cursor.getColumnIndexOrThrow("delivery_date")));
                    orderModel.delivery_instructions = (cursor.getString(cursor.getColumnIndexOrThrow("delivery_instructions")));
                    orderModel.unattended_instructions = (cursor.getString(cursor.getColumnIndexOrThrow("unattended_instructions")));
                    orderModel.first_name = (cursor.getString(cursor.getColumnIndexOrThrow("first_name")));
                    orderModel.last_name = (cursor.getString(cursor.getColumnIndexOrThrow("last_name")));
                    orderModel.address_line_1 = (cursor.getString(cursor.getColumnIndexOrThrow("address_line_1")));
                    orderModel.address_line_2 = (cursor.getString(cursor.getColumnIndexOrThrow("address_line_2")));
                    orderModel.apartment = (cursor.getString(cursor.getColumnIndexOrThrow("apartment")));
                    orderModel.city = (cursor.getString(cursor.getColumnIndexOrThrow("city")));
                    orderModel.state = (cursor.getString(cursor.getColumnIndexOrThrow("state")));
                    orderModel.zip = (cursor.getString(cursor.getColumnIndexOrThrow("zip")));
                    orderModel.lat = (cursor.getDouble(cursor.getColumnIndexOrThrow("lat")));
                    orderModel.lng = (cursor.getDouble(cursor.getColumnIndexOrThrow("lng")));
                    orderModel.event = (cursor.getString(cursor.getColumnIndexOrThrow("event")));
                    orderModel.customerSignature = (cursor.getString(cursor.getColumnIndexOrThrow("customerSignature")));
                    //orders.add(orderModel);

                } while (cursor.moveToNext());

            }

            cursor.close();
        }
        return orderModel;
    }

    public boolean isScanCompleted(Context c, String orderId) {
        Cursor cursor = DbAdapter.getDbAdapterInstance().getResultSet("ORDERS_TABLE", new String[]{"scanCompleted"}, "order_id" + " = '" + orderId + "'", null, null, c);
        String scanCompleted;
        if (null != cursor) {

            if (cursor.moveToFirst()) {
                scanCompleted = (cursor.getString(cursor.getColumnIndexOrThrow("scanCompleted")));
                if(scanCompleted.equalsIgnoreCase("1")){
                    return  true;
                }
            }

            cursor.close();
        }
        return false;
    }

    public ArrayList<OrderModel> getAllOrders(Context c) {
        Cursor cursor = DbAdapter.getDbAdapterInstance().getResultSet("ORDERS_TABLE", new String[]{"order_id","erporder_id","orderStatus", "tip",
                        "mobile", "window_start", "window_end", "delivery_date", "delivery_instructions", "unattended_instructions",
                        "first_name", "last_name","address_line_1","address_line_2","apartment","city", "state", "zip", "lat", "lng","event","customerSignature","offlinelat","offlinelng","offlinetime"},null, null, null, c);
        ArrayList<OrderModel> orders = new ArrayList<>();
        if (null != cursor) {

            if (cursor.moveToFirst()) {
                do {
                    OrderModel orderModel = new OrderModel();
                    orderModel.order_id = (cursor.getString(cursor.getColumnIndexOrThrow("order_id")));
                    orderModel.erporder_id = (cursor.getString(cursor.getColumnIndexOrThrow("erporder_id")));
                    orderModel.orderStatus = (cursor.getString(cursor.getColumnIndexOrThrow("orderStatus")));
                    orderModel.tip = (cursor.getString(cursor.getColumnIndexOrThrow("tip")));
                    orderModel.mobile = (cursor.getString(cursor.getColumnIndexOrThrow("mobile")));
                    orderModel.window_start = (cursor.getString(cursor.getColumnIndexOrThrow("window_start")));
                    orderModel.window_end = (cursor.getString(cursor.getColumnIndexOrThrow("window_end")));
                    orderModel.delivery_date = (cursor.getString(cursor.getColumnIndexOrThrow("delivery_date")));
                    orderModel.delivery_instructions = (cursor.getString(cursor.getColumnIndexOrThrow("delivery_instructions")));
                    orderModel.unattended_instructions = (cursor.getString(cursor.getColumnIndexOrThrow("unattended_instructions")));
                    orderModel.first_name = (cursor.getString(cursor.getColumnIndexOrThrow("first_name")));
                    orderModel.last_name = (cursor.getString(cursor.getColumnIndexOrThrow("last_name")));
                    orderModel.address_line_1 = (cursor.getString(cursor.getColumnIndexOrThrow("address_line_1")));
                    orderModel.address_line_2 = (cursor.getString(cursor.getColumnIndexOrThrow("address_line_2")));
                    orderModel.apartment = (cursor.getString(cursor.getColumnIndexOrThrow("apartment")));
                    orderModel.city = (cursor.getString(cursor.getColumnIndexOrThrow("city")));
                    orderModel.state = (cursor.getString(cursor.getColumnIndexOrThrow("state")));
                    orderModel.zip = (cursor.getString(cursor.getColumnIndexOrThrow("zip")));
                    orderModel.lat = (cursor.getDouble(cursor.getColumnIndexOrThrow("lat")));
                    orderModel.lng = (cursor.getDouble(cursor.getColumnIndexOrThrow("lng")));
                    orderModel.event = (cursor.getString(cursor.getColumnIndexOrThrow("event")));
                    orderModel.offlinelat = (cursor.getDouble(cursor.getColumnIndexOrThrow("offlinelat")));
                    orderModel.offlinelng = (cursor.getDouble(cursor.getColumnIndexOrThrow("offlinelng")));
                    orderModel.offlinetime = (cursor.getString(cursor.getColumnIndexOrThrow("offlinetime")));
                    orderModel.customerSignature = (cursor.getString(cursor.getColumnIndexOrThrow("customerSignature")));
                    orderModel.packages=getPackagesForEachOrder(c,orderModel.order_id);
                    orders.add(orderModel);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return orders;
    }

    public ArrayList<OrderModel> getOrdersforOfflineSyncEnabled(Context c) {
        Cursor cursor = DbAdapter.getDbAdapterInstance().getResultSet("ORDERS_TABLE", new String[]{"order_id","event","customerSignature","offlinelat","offlinelng","offlinetime"},
                "offlineSyncEnabled" + " = '" + 1 + "'", null, null, c);
        ArrayList<OrderModel> orders = new ArrayList<>();

        if (null != cursor) {

            if (cursor.moveToFirst()) {
                do {
                    OrderModel orderModel = new OrderModel();
                    orderModel.order_id = (cursor.getString(cursor.getColumnIndexOrThrow("order_id")));
                    orderModel.event = (cursor.getString(cursor.getColumnIndexOrThrow("event")));
                    orderModel.customerSignature = (cursor.getString(cursor.getColumnIndexOrThrow("customerSignature")));
                    orderModel.offlinelat=(cursor.getDouble(cursor.getColumnIndexOrThrow("offlinelat")));
                    orderModel.offlinelng=(cursor.getDouble(cursor.getColumnIndexOrThrow("offlinelng")));
                    orderModel.offlinetime=(cursor.getString(cursor.getColumnIndexOrThrow("offlinetime")));
                    orders.add(orderModel);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return orders;
    }

    public ArrayList<OrderModel> getOrderIDSforOfflineLocations(Context c) {
        Cursor cursor = DbAdapter.getDbAdapterInstance().getResultSet("LOCATIONS_TABLE", new String[]{"order_id"},
                null, null, null, c);
        ArrayList<OrderModel> orders = new ArrayList<>();
        if (null != cursor) {

            if (cursor.moveToFirst()) {
                do {
                    OrderModel orderModel = new OrderModel();
                    orderModel.order_id = (cursor.getString(cursor.getColumnIndexOrThrow("order_id")));
                    orders.add(orderModel);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return orders;
    }

    public ArrayList<OrderModel> getLatLongsForEachOrder(Context c, String orderID) {
        Cursor cursor = DbAdapter.getDbAdapterInstance().getResultSet("LOCATIONS_TABLE", new String[]{"lat",
                        "lng", "time"},
                "order_id" + " = '" + orderID + "'", null, null, c);
        ArrayList<OrderModel> locations = new ArrayList<>();
        if (null != cursor) {

            if (cursor.moveToFirst()) {
                do {
                    OrderModel orderModel = new OrderModel();
                    orderModel.offlinelat=(cursor.getDouble(cursor.getColumnIndexOrThrow("lat")));
                    orderModel.offlinelng=(cursor.getDouble(cursor.getColumnIndexOrThrow("lng")));
                    orderModel.offlinetime=(cursor.getString(cursor.getColumnIndexOrThrow("time")));
                    locations.add(orderModel);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return locations;
    }

    public ArrayList<OrderModel> getPackagesForEachOrder(Context c, String orderID) {
        Cursor cursor = DbAdapter.getDbAdapterInstance().getResultSet("PACKAGES_TABLE", new String[]{"bag_id",
                        "bin_loc", "isAlcohol", "isMeat"},
                "order_id" + " = '" + orderID + "'", null, null, c);
        ArrayList<OrderModel> packages = new ArrayList<>();
        if (null != cursor) {

            if (cursor.moveToFirst()) {
                do {
                    OrderModel orderModel = new OrderModel();
                    orderModel.bag_id = (cursor.getString(cursor.getColumnIndexOrThrow("bag_id")));
                    orderModel.bin_loc = (cursor.getString(cursor.getColumnIndexOrThrow("bin_loc")));
                    if(cursor.getInt(cursor.getColumnIndexOrThrow("isAlcohol"))==1){
                        orderModel.isAlcohol=true;
                    }
                    if(cursor.getInt(cursor.getColumnIndexOrThrow("isMeat"))==1){
                        orderModel.isMeat=true;
                    }
                    packages.add(orderModel);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return packages;
    }


}