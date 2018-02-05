package com.incture.cherrywork.freshdirect.Models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Arun on 22-08-2016.
 */
public class OrderModel implements Serializable {
    public boolean isAlcohol=false;
    public boolean isMeat=false;
    public String status="";
    public String vehicle="";
    public String order_id="";
    public String tip="";
    public String mobile="";
    public String window_start="";
    public String window_end="";
    public String delivery_date="";
    public String delivery_instructions="";
    public String unattended_instructions="";
    public String service_type="";
    public String first_name="";
    public String last_name="";
    public String address_line_1="";
    public String address_line_2="";
    public String apartment="";
    public String city="";
    public String state="";
    public String zip="";
    public double lat=0.0;
    public double lng=0.0;
    public String bag_id="";
    public String bin_loc="";
    public ArrayList<OrderModel> packages=new ArrayList<>();
    public ArrayList<OrderModel> orders=new ArrayList<>();
    public String orderStatus="";

    public boolean isOrdrderStarted=false;
    public double offlinelat=0.0;
    public double offlinelng=0.0;
    public String offlinetime="";
    public String event="";
    public String customerSignature="";
    public boolean isOrderCancel;
    public String erporder_id="";
    public boolean skipToteScan=false;
}
