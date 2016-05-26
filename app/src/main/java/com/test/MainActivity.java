package com.test;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {


    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView t = (TextView) findViewById(R.id.hello);
        t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clientInfo();
            }
        });

        final RainbowBar loading =  (RainbowBar)findViewById(R.id.loading);
        final SwipeRefreshLayout refreshLayout =  (SwipeRefreshLayout)findViewById(R.id.refresh);
        refreshLayout.setColorSchemeResources(R.color.color_00d446, R.color.color_0dbdbb, R.color.color_0aa4e7);
        refreshLayout.setProgressViewOffset(true, 10, 120);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loading.setVisibility(View.VISIBLE);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                        loading.setVisibility(View.GONE);
                    }
                },3000);
            }
        });
    }

    public String clientInfo() {
        JSONObject json = new JSONObject();
        TelephonyManager phoneMgr=(TelephonyManager)getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        try {
            json.put("imei",phoneMgr.getDeviceId());//imei
            json.put("clientDevice", Build.MANUFACTURER+" "+Build.MODEL);//手机型号
            json.put("clientSysVer","Android "+Build.VERSION.RELEASE);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Toast.makeText(this,json.toString(),Toast.LENGTH_LONG).show();
        return json.toString();
    }
}
