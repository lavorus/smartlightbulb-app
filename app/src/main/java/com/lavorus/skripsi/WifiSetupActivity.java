package com.lavorus.skripsi;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class WifiSetupActivity extends AppCompatActivity {
    private int lastWifiID;
    private String lastWifiName;
    private String passwordWifi = "password";
    private String ssidWifi;
    private WifiManager wifiManager;
    private int netId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        ssidWifi = intent.getStringExtra("ssid");

        wifiManager = (WifiManager)getSystemService(WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            lastWifiName = wifiManager.getConnectionInfo().getSSID().replace("\"", "");
            lastWifiID = wifiManager.getConnectionInfo().getNetworkId();
        }

        setTitle(ssidWifi);
        setContentView(R.layout.activity_wifisetup);

        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", ssidWifi);
        wifiConfig.preSharedKey = String.format("\"%s\"", passwordWifi);

        netId = wifiManager.addNetwork(wifiConfig);
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();

        final EditText inputSSID = (EditText) findViewById(R.id.editText);
        final EditText inputPass = (EditText) findViewById(R.id.editText2);
        inputSSID.setText(lastWifiName);

        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String url = "http://192.168.4.1:19105/setup?name=" + inputSSID.getText() + "&pass=" + inputPass.getText();

                RequestQueue queue = Volley.newRequestQueue(WifiSetupActivity.this);
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("TEST", response);

                                wifiManager.removeNetwork(netId);
                                wifiManager.disconnect();
                                wifiManager.enableNetwork(lastWifiID, true);
                                wifiManager.reconnect();

                                Intent n = new Intent();
                                n.putExtra("name", ssidWifi);
                                setResult(RESULT_OK, n);
                                finish();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
                Volley.newRequestQueue(WifiSetupActivity.this).add(stringRequest);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        wifiManager.removeNetwork(netId);
        wifiManager.disconnect();
        wifiManager.enableNetwork(lastWifiID, true);
        wifiManager.reconnect();

        super.onBackPressed();
    }
}