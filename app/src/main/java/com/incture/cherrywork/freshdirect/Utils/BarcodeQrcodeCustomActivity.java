package com.incture.cherrywork.freshdirect.Utils;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;


import com.google.zxing.client.android.Intents;
import com.incture.cherrywork.freshdirect.Models.OrderModel;
import com.incture.cherrywork.freshdirect.R;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

/**
 *
 */
public class BarcodeQrcodeCustomActivity extends Activity {
    private CaptureManager capture;
    private TextView title_text,cancel,bags;
    private OrderModel orderModel;
    private String Bags="";
    private CompoundBarcodeView barcodeScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_barcode_capture);
        barcodeScannerView = (CompoundBarcodeView)findViewById(R.id.zxing_barcode_scanner);
        title_text=(TextView)findViewById(R.id.title);
        bags=(TextView)findViewById(R.id.bags);
        cancel=(TextView)findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               onBackPressed();
            }
        });
//        orderModel= (OrderModel) getIntent().getSerializableExtra("orderModel");
        String title=getIntent().getExtras().getString(Intents.Scan.PROMPT_MESSAGE,"SCAN MANIFEST");
        if(title.contains("Scan Bags")){
            Bags=title.substring(10,title.length());
            bags.setVisibility(View.VISIBLE);
            bags.setText(Bags);
            title_text.setText("Scan Bags");
        }else {
            title_text.setText(title);
        }


        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.decode();
    }

    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        capture.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }
}
