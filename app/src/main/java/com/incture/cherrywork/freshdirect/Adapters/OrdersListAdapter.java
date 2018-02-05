package com.incture.cherrywork.freshdirect.Adapters;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.incture.cherrywork.freshdirect.Activities.AllOrdersActivity;
import com.incture.cherrywork.freshdirect.Activities.LocateActivity;
import com.incture.cherrywork.freshdirect.Activities.OrderDetailActivity;
import com.incture.cherrywork.freshdirect.DB.DbAdapter;
import com.incture.cherrywork.freshdirect.Models.OrderModel;
import com.incture.cherrywork.freshdirect.R;
import com.incture.cherrywork.freshdirect.Utils.Constants;
import com.incture.cherrywork.freshdirect.Utils.GPSTracker;
import com.incture.cherrywork.freshdirect.Utils.TimeUtils;
import com.incture.cherrywork.freshdirect.Utils.Util;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by Arun on 22-08-2016.
 */
public class OrdersListAdapter extends RecyclerView.Adapter<OrdersListAdapter.ViewHolder> {
    private static final int PERMISSIONS_REQUEST_PHONE_CALL = 121;
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 333;
    private Context _context;
    private boolean isOrderEnded = true;
    private int isstarted = 0;
    private double sourcelatitude = 0.0;
    private String address2="";
    private double sourcelongitude = 0.0;
    private ArrayList<OrderModel> orderModelArrayList = new ArrayList<>();
    private ArrayList<OrderModel> packagesList = new ArrayList<>();
    private OrderModel orderModel = new OrderModel();

    public OrdersListAdapter(Context _context, ArrayList<OrderModel> data) {
        this._context = _context;
        this.orderModelArrayList = data;
    }

    @Override
    public OrdersListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_list_item, parent, false);
        return new ViewHolder(itemLayoutView);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(final OrdersListAdapter.ViewHolder holder, final int position) {
        //setdata to views
        orderModel = orderModelArrayList.get(position);
        packagesList = orderModelArrayList.get(position).packages;

        holder.current_status.setText(Util.capitalizeSentence(orderModel.orderStatus));
        if (orderModel.orderStatus.equalsIgnoreCase(Constants.PENDING)) {
            holder.orderstatus.setVisibility(View.INVISIBLE);

        }
        if (orderModel.orderStatus.equalsIgnoreCase(Constants.ENROUTE)) {
            holder.orderstatus.setVisibility(View.VISIBLE);

        }

        if (orderModelArrayList.get(position).orderStatus.equalsIgnoreCase(Constants.DELIVERED) || orderModelArrayList.get(position).orderStatus.equalsIgnoreCase(Constants.INVALID)
                || orderModelArrayList.get(position).orderStatus.equalsIgnoreCase(Constants.CUSTOMER_NOT_HOME) || orderModelArrayList.get(position).orderStatus.equalsIgnoreCase(Constants.PARTIAL_REJECTION)
                || orderModelArrayList.get(position).orderStatus.equalsIgnoreCase(Constants.ORDER_REJECTION)) {
            holder.parent.setCardBackgroundColor(Color.parseColor("#f7f3f3"));
            if (orderModelArrayList.get(position).orderStatus.equalsIgnoreCase(Constants.DELIVERED)) {
                holder.action_layout.setVisibility(View.GONE);
                holder.start_order.setVisibility(View.GONE);
                holder.status_layout.setVisibility(View.VISIBLE);
                holder.status_layout.setBackgroundColor(_context.getResources().getColor(R.color.delivered));
                holder.current_status.setVisibility(View.INVISIBLE);
                holder.orderstatus.setVisibility(View.INVISIBLE);
                holder.status.setText("Delivered");
            }
            if (orderModelArrayList.get(position).orderStatus.equalsIgnoreCase(Constants.INVALID)) {
                holder.action_layout.setVisibility(View.GONE);
                holder.start_order.setVisibility(View.GONE);
                holder.orderstatus.setVisibility(View.INVISIBLE);
                holder.status_layout.setVisibility(View.VISIBLE);
                holder.current_status.setVisibility(View.INVISIBLE);
                holder.status_layout.setBackgroundColor(_context.getResources().getColor(R.color.invalid));
                holder.status.setText("Invalid");
            }
            if (orderModelArrayList.get(position).orderStatus.equalsIgnoreCase(Constants.CUSTOMER_NOT_HOME)) {
                holder.action_layout.setVisibility(View.GONE);
                holder.start_order.setVisibility(View.GONE);
                holder.orderstatus.setVisibility(View.INVISIBLE);
                holder.status_layout.setVisibility(View.VISIBLE);
                holder.current_status.setVisibility(View.INVISIBLE);
                holder.status_layout.setBackgroundColor(_context.getResources().getColor(R.color.customerNotHome));
                holder.status.setText("Customer Not Home");
            }
            if (orderModelArrayList.get(position).orderStatus.equalsIgnoreCase(Constants.PARTIAL_REJECTION)) {
                holder.action_layout.setVisibility(View.GONE);
                holder.start_order.setVisibility(View.GONE);
                holder.orderstatus.setVisibility(View.INVISIBLE);
                holder.status_layout.setVisibility(View.VISIBLE);
                holder.current_status.setVisibility(View.INVISIBLE);
                holder.status_layout.setBackgroundColor(_context.getResources().getColor(R.color.partialRejection));
                holder.status.setText("Partial Rejection");
            }
            if (orderModelArrayList.get(position).orderStatus.equalsIgnoreCase(Constants.ORDER_REJECTION)) {
                holder.action_layout.setVisibility(View.GONE);
                holder.start_order.setVisibility(View.GONE);
                holder.orderstatus.setVisibility(View.INVISIBLE);
                holder.status_layout.setVisibility(View.VISIBLE);
                holder.current_status.setVisibility(View.INVISIBLE);
                holder.status_layout.setBackgroundColor(_context.getResources().getColor(R.color.orderRejection));
                holder.status.setText("Order Rejected");
            }
        }
        if (orderModelArrayList.get(position).orderStatus.equalsIgnoreCase(Constants.ENROUTE)) {
            GPSTracker tracker = new GPSTracker(_context);
            if (tracker.canGetLocation()) {
                if (_context instanceof AllOrdersActivity) {
                    ((AllOrdersActivity) _context).startLocationUpdates(orderModelArrayList.get(position).order_id, position);
                }
                holder.start_order.setVisibility(View.GONE);
                holder.action_layout.setVisibility(View.VISIBLE);
                //holder.current_status.setText(Constants.ENROUTE);
                //holder.current_status.setTextColor(_context.getResources().getColor(R.color.enroute));
                holder.orderstatus.setVisibility(View.VISIBLE);
                orderModelArrayList.get(position).isOrderCancel = false;
                isstarted = position;
                isOrderEnded = false;
                orderModel.isOrdrderStarted = true;
            } else {
                tracker.showSettingsAlert();
            }
        }

        for (int i = 0; i < packagesList.size(); i++) {
            if (packagesList.get(i).isAlcohol) {
                holder.alcohol.setImageResource(R.mipmap.alcoholblue);
                break;
            } else {
                holder.alcohol.setImageResource(R.mipmap.alcoholgrey);
            }
        }
        for (int i = 0; i < packagesList.size(); i++) {
            if (packagesList.get(i).isMeat) {
                holder.meat.setImageResource(R.mipmap.frozenblue);
                break;
            } else {
                holder.meat.setImageResource(R.mipmap.frozengrey);
            }
        }


        Spannable orderId = new SpannableString(orderModelArrayList.get(position).order_id);
        orderId.setSpan(new ForegroundColorSpan(Color.BLACK), 0, orderId.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.order_id.setText("Order Id : ");
        holder.order_id.append(orderId);
        if(orderModelArrayList.get(position).address_line_2.equalsIgnoreCase("null")){
            address2="";
        }
        else{
            address2=orderModelArrayList.get(position).address_line_2+", ";
        }
        holder.customer_address.setText(orderModelArrayList.get(position).address_line_1 + ", " + address2+orderModelArrayList.get(position).apartment + ", "
                + Util.capitalizeWords(orderModelArrayList.get(position).city) + ", " + Util.capitalizeWords(orderModelArrayList.get(position).state)+ ", " + orderModelArrayList.get(position).zip);
        holder.customer_name.setText(Util.capitalizeSentence(orderModelArrayList.get(position).first_name + "  " + orderModelArrayList.get(position).last_name));
        holder.time_interval.setText(TimeUtils.getTime(orderModelArrayList.get(position).window_start) + " - " + TimeUtils.getTime(orderModelArrayList.get(position).window_end));
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (orderModelArrayList.get(position).orderStatus.equalsIgnoreCase(Constants.ENROUTE) || orderModelArrayList.get(position).orderStatus.equalsIgnoreCase(Constants.DELIVERED) || orderModelArrayList.get(position).orderStatus.equalsIgnoreCase(Constants.INVALID)
                        || orderModelArrayList.get(position).orderStatus.equalsIgnoreCase(Constants.CUSTOMER_NOT_HOME) || orderModelArrayList.get(position).orderStatus.equalsIgnoreCase(Constants.PARTIAL_REJECTION) ||
                        orderModelArrayList.get(position).orderStatus.equalsIgnoreCase(Constants.ORDER_REJECTION)) {
                    Intent intent = new Intent(_context, OrderDetailActivity.class);
                    orderModel = orderModelArrayList.get(position);
                    intent.putExtra("orderModel", orderModel);
                    intent.putExtra("orderModelArrayList", orderModelArrayList);
                    ((Activity) _context).startActivityForResult(intent, 100);
                } else {
                    Toast.makeText(_context, "Please start the order", Toast.LENGTH_SHORT).show();

                }
            }
        });

        holder.parent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (orderModelArrayList.get(position).isOrdrderStarted) {
                    if (orderModelArrayList.get(position).orderStatus.equalsIgnoreCase(Constants.DELIVERED) || orderModelArrayList.get(position).orderStatus.equalsIgnoreCase(Constants.CUSTOMER_NOT_HOME) ||
                            orderModelArrayList.get(position).orderStatus.equalsIgnoreCase(Constants.PARTIAL_REJECTION) || orderModelArrayList.get(position).orderStatus.equalsIgnoreCase(Constants.ORDER_REJECTION)) {
                        Toast.makeText(_context, "Order already completed", Toast.LENGTH_SHORT).show();
                    } else {

                        AlertDialog.Builder dialog = new AlertDialog.Builder(_context);
                        dialog.setTitle("Invalid Order");
                        dialog.setMessage("Do you mark this order as invalid?");
                        dialog.setCancelable(true);
                        dialog.setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //GPSTracker tracker = new GPSTracker(_context);
                                        // tracker.stopUsingGPS();
                                        if (_context instanceof AllOrdersActivity) {
                                            ((AllOrdersActivity) _context).upDateInvalidOrder(Constants.INVALID, orderModelArrayList.get(position).order_id);
                                        }
                                    }
                                });
                        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        AlertDialog alert = dialog.create();
                        alert.setCanceledOnTouchOutside(true);
                        alert.show();
                    }

                } else {
                    Toast.makeText(_context, "Please start the order to mark invalid", Toast.LENGTH_SHORT).show();
                }
                return true;
            }

        });
        holder.call_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                call(position);
            }
        });
        holder.locate_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locate(position);
            }
        });
        holder.endorder_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(_context);
                dialog.setTitle("End Order");
                dialog.setMessage("Do you want to end this order?");
                dialog.setCancelable(true);
                dialog.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //GPSTracker tracker = new GPSTracker(_context);
                                // tracker.stopUsingGPS();
                                if (_context instanceof AllOrdersActivity) {
                                    ((AllOrdersActivity) _context).stopFusedLocation(orderModelArrayList.get(position).order_id);
                                }
                                holder.start_order.setVisibility(View.VISIBLE);
                                holder.action_layout.setVisibility(View.GONE);
                                holder.status_layout.setVisibility(View.GONE);
                                //holder.current_status.setText(Constants.PENDING);
                               // holder.current_status.setTextColor(_context.getResources().getColor(R.color.pending));
                                holder.orderstatus.setVisibility(View.INVISIBLE);
                                ContentValues contentValues = new ContentValues();
                                contentValues.put("orderStatus", Constants.PENDING);
                                DbAdapter.getDbAdapterInstance().update("ORDERS_TABLE", contentValues, "order_id='" + orderModelArrayList.get(position).order_id + "'");
                                isOrderEnded = true;
                                orderModelArrayList.get(position).orderStatus = Constants.PENDING;
                                orderModelArrayList.get(position).isOrdrderStarted = false;
                                orderModelArrayList.get(position).isOrderCancel = true;
                            }
                        });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = dialog.create();
                alert.setCanceledOnTouchOutside(true);
                alert.show();
            }
        });
        holder.start_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!orderModelArrayList.get(position).isOrdrderStarted && isOrderEnded) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(_context);
                    dialog.setTitle("Start Order");
                    dialog.setMessage("Do you want to start this order?");
                    dialog.setCancelable(true);
                    dialog.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    GPSTracker tracker = new GPSTracker(_context);
                                    if (tracker.canGetLocation()) {
                                        if (_context instanceof AllOrdersActivity) {
                                            ((AllOrdersActivity) _context).startLocationUpdates(orderModelArrayList.get(position).order_id, position);
                                        }
                                        holder.start_order.setVisibility(View.GONE);
                                        holder.action_layout.setVisibility(View.VISIBLE);
                                        orderModelArrayList.get(position).orderStatus = Constants.ENROUTE;
                                        //holder.current_status.setText(Constants.ENROUTE);
                                       // holder.current_status.setTextColor(_context.getResources().getColor(R.color.enroute));
                                        holder.orderstatus.setVisibility(View.VISIBLE);
                                        ContentValues contentValues = new ContentValues();
                                        contentValues.put("orderStatus", Constants.ENROUTE);
                                        DbAdapter.getDbAdapterInstance().update("ORDERS_TABLE", contentValues, "order_id='" + orderModelArrayList.get(position).order_id + "'");
                                        orderModelArrayList.get(position).isOrderCancel = false;
                                        isOrderEnded = false;
                                        isstarted = position;
                                        orderModelArrayList.get(position).isOrdrderStarted = true;
                                    } else {
                                        tracker.showSettingsAlert();
                                    }

                                }
                            });
                    dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alert = dialog.create();
                    alert.setCanceledOnTouchOutside(true);
                    alert.show();
                } else {
                    Toast.makeText(_context, "Please complete the current order", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


    private void locate(int position) {
        if (Util.isOnline(_context)) {

            int Pos = position;
            GPSTracker tracker = new GPSTracker(_context);
            if (tracker.canGetLocation()) {
                sourcelatitude = tracker.getLatitude();
                sourcelongitude = tracker.getLongitude();
                if (!(sourcelatitude == 0.0 || sourcelongitude == 0.0)) {
                    Intent intent = new Intent(_context, LocateActivity.class);
                    intent.putExtra("customer_name", orderModelArrayList.get(Pos).first_name + "  " + orderModelArrayList.get(Pos).last_name);
                    intent.putExtra("order_id", orderModelArrayList.get(Pos).order_id);
                    intent.putExtra("srclatitude", sourcelatitude);
                    intent.putExtra("srclongitude", sourcelongitude);
                    intent.putExtra("destlatitude", orderModelArrayList.get(Pos).lat);
                    intent.putExtra("destlongitude", orderModelArrayList.get(Pos).lng);
                    _context.startActivity(intent);
                } else {
                    Toast.makeText(_context, "GPS must be in High Accuracy mode,please change in location settings", Toast.LENGTH_SHORT).show();
                }

            } else {
                tracker.showSettingsAlert();
            }
        } else {
            Toast.makeText(_context, "You have to be online to view maps", Toast.LENGTH_SHORT).show();
        }
    }

    private void call(int position) {
        int Pos = position;
       /* Intent callIntent = new Intent(Intent.ACTION_CALL);
        if (!orderModelArrayList.get(Pos).mobile.equalsIgnoreCase("null")) {
            if (orderModelArrayList.get(Pos).mobile.length() == 10) {
                callIntent.setData(Uri.parse("tel:" + String.valueOf(orderModelArrayList.get(Pos).mobile)));
            } else {
                if (orderModelArrayList.get(Pos).mobile.length() == 12) {
                    callIntent.setData(Uri.parse("tel:" + String.valueOf(orderModelArrayList.get(Pos).mobile.substring(2, orderModelArrayList.get(Pos).mobile.length()))));
                } else {
                    Toast.makeText(_context, "Invalid mobile number", Toast.LENGTH_SHORT).show();
                }
            }
            if (ActivityCompat.checkSelfPermission(_context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((AllOrdersActivity) _context, new String[]{Manifest.permission.CALL_PHONE}, PERMISSIONS_REQUEST_PHONE_CALL);
            } else {
                _context.startActivity(callIntent);
            }
        } else {
            Toast.makeText(_context, "Mobile number not found", Toast.LENGTH_SHORT).show();
        }*/
        if (!orderModelArrayList.get(Pos).mobile.equalsIgnoreCase("null")||orderModelArrayList.get(Pos).mobile.length()==0) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + (orderModelArrayList.get(Pos).mobile)));

            if (ActivityCompat.checkSelfPermission(_context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((AllOrdersActivity) _context, new String[]{Manifest.permission.CALL_PHONE}, PERMISSIONS_REQUEST_PHONE_CALL);
            } else {
                _context.startActivity(intent);
            }


        }else{
            Toast.makeText(_context, "Mobile number not found", Toast.LENGTH_SHORT).show();
        }


    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        //declare views
        CardView parent;
        ImageView alcohol, meat;
        CircleImageView orderstatus;
        TextView order_id, customer_name, time_interval, call, locate, endorder, status, undo, startorder, current_status, customer_address;
        LinearLayout status_layout, action_layout, call_layout, start_order,locate_layout,endorder_layout;

        public ViewHolder(View itemView) {
            super(itemView);
            //initialize views
            parent = (CardView) itemView.findViewById(R.id.parent);
            alcohol = (ImageView) itemView.findViewById(R.id.alcohol);
            meat = (ImageView) itemView.findViewById(R.id.meat);
            order_id = (TextView) itemView.findViewById(R.id.order_id);
            customer_name = (TextView) itemView.findViewById(R.id.customer_name);
            time_interval = (TextView) itemView.findViewById(R.id.time_interval);
            call = (TextView) itemView.findViewById(R.id.call);

            locate = (TextView) itemView.findViewById(R.id.locate);
            endorder = (TextView) itemView.findViewById(R.id.endorder);
            startorder = (TextView) itemView.findViewById(R.id.startorder);
            status = (TextView) itemView.findViewById(R.id.status);
            current_status = (TextView) itemView.findViewById(R.id.current_status);
            status_layout = (LinearLayout) itemView.findViewById(R.id.status_layout);
            start_order = (LinearLayout) itemView.findViewById(R.id.start_order);

            action_layout = (LinearLayout) itemView.findViewById(R.id.action_layout);
            locate_layout=(LinearLayout)itemView.findViewById(R.id.locate_layout);
            endorder_layout=(LinearLayout)itemView.findViewById(R.id.endorder_layout);
            call_layout=(LinearLayout)itemView.findViewById(R.id.call_layout);

            customer_address = (TextView) itemView.findViewById(R.id.customer_address);
            orderstatus=(CircleImageView)itemView.findViewById(R.id.order_status);

        }
    }

    @Override
    public int getItemCount() {
        return this.orderModelArrayList.size();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                //Log.d("MainActivity", "Cancelled scan");
                Toast.makeText(_context, "Cancelled", Toast.LENGTH_LONG).show();
                ((Activity) _context).onBackPressed();
            } else {
                //Log.d("MainActivity", "Scanned");
                //Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                //startActivity(new Intent(ScanBagActivity.this,ScanBagActivity.class));
                _context.startActivity(new Intent(_context, OrderDetailActivity.class));
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            // super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void orderEnded() {
        isOrderEnded = true;
        notifyDataSetChanged();
    }

}
