package com.incture.cherrywork.freshdirect.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.incture.cherrywork.freshdirect.Models.OrderModel;
import com.incture.cherrywork.freshdirect.R;

import java.util.ArrayList;


/**
 * Created by Arun on 27-09-2016.
 */
public class PackagesAdapter extends ArrayAdapter{
    private Context _context=null;
    private ArrayList<OrderModel> packagesList;
    private TextView bagId,header;
    private ImageView isFrozen,isAlcohol;

    public PackagesAdapter(Context context, int packages_list_item, ArrayList<OrderModel> pacakges) {
        super(context,packages_list_item);
        this._context=context;
        this.packagesList=pacakges;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater vi = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = vi.inflate(R.layout.packages_list_item, null);
        bagId=(TextView)convertView.findViewById(R.id.bag_id);
        isFrozen=(ImageView) convertView.findViewById(R.id.isMeat);
        isAlcohol=(ImageView) convertView.findViewById(R.id.isAlcohol);
        //header=(TextView)convertView.findViewById(R.id.header);



        bagId.setText(packagesList.get(position).bag_id);

        if(packagesList.get(position).isAlcohol){
            isAlcohol.setImageResource(R.mipmap.alcoholblue);
        }else{
            isAlcohol.setImageResource(R.mipmap.alcoholgrey);
        }
        if(packagesList.get(position).isMeat){
            isFrozen.setImageResource(R.mipmap.frozenblue);
        }else{
            isFrozen.setImageResource(R.mipmap.frozengrey);
        }


        return convertView;
    }

    @Override
    public int getCount() {
        return packagesList.size();
    }
}
