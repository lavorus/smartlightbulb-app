package com.lavorus.skripsi;

import android.Manifest;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final int REQUEST_SCAN_WIFI = 1910;
    private WifiManager wifi;
    private SmartLightAdapter adapter;
    private int timeout = 10;
    private static final int TIME_INTERVAL = 2000;
    private long mBackPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ListView listView = (ListView) findViewById(R.id.bulb_listview);
        adapter = new SmartLightAdapter(this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SmartLight selectedData = (SmartLight) adapter.getItem(position);
//                Toast.makeText(MainActivity.this, selectedData.name, Toast.LENGTH_SHORT).show();
//                Log.d("TEST", selectedData.name);

                Intent i = new Intent(MainActivity.this, LightColorActivity.class);
                i.putExtra("name", selectedData.name);
                i.putExtra("host", selectedData.ip);
                i.putExtra("total", selectedData.red * 256 * 256 + selectedData.green * 256 + selectedData.blue * 1);
                startActivity(i);
            }
        });

        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (wifi.isWifiEnabled() == false) {
            Toast.makeText(getApplicationContext(), "Waiting Wifi Ready", Toast.LENGTH_LONG).show();
            wifi.setWifiEnabled(true);
        }

        final Button button = (Button) findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED){
                        requestPermissions(new String[]{Manifest.permission.INTERNET}, 2);
                    } else {
                        scanHost();
                    }
                } else {
                    scanHost();
                }
            }
        });

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.INTERNET}, 2);
            } else {
                scanHost();
            }
        } else {
            scanHost();
        }
    }

    private void scanHost() {
        WifiManager mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo  mWifiInfo = mWifiManager.getConnectionInfo();
        String subnet = getSubnetAddress(mWifiManager.getDhcpInfo().gateway);
        Log.d("TEST",subnet);
        checkHosts(subnet);
    }

    private String getSubnetAddress(int address) {
        String ipString = String.format(
                "%d.%d.%d",
                (address & 0xff),
                (address >> 8 & 0xff),
                (address >> 16 & 0xff));

        return ipString;
    }

    private void checkHosts(String subnet) {
        adapter.clearData();
        adapter.notifyDataSetChanged();

        for (int i = 1; i < 255; i++) {
            final String host = subnet + "." + i;
            try {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(host, 19105), timeout);
                socket.close();
                Log.d("TEST", "checkHosts() :: " + host + " is reachable");
                String url = "http://" + host + ":19105/status";
                RequestQueue queue = Volley.newRequestQueue(this);
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject mainObject = new JSONObject(response);
                                String tmpName = mainObject.getString("name");
                                int tmpRed = mainObject.getInt("red");
                                int tmpGreen = mainObject.getInt("green");
                                int tmpBlue = mainObject.getInt("blue");
                                Log.d("TESTI", tmpName);

                                SmartLight newBulb = new SmartLight(tmpName, host, tmpRed, tmpGreen, tmpBlue);
                                adapter.addData(newBulb);
                                adapter.notifyDataSetChanged();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("TEST", "Fail to Get Name ::" + host);
                    }
                });
                Volley.newRequestQueue(this).add(stringRequest);
            }
            catch(ConnectException ce){
                Log.i("TEST", "Fail ::" + host);
            }
            catch (Exception ex) {
                //ex.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_scanwifi) {
            Intent i = new Intent(MainActivity.this, WifiScanActivity.class);
            i.putExtra("justSkripsi", true);
            startActivityForResult(i, REQUEST_SCAN_WIFI);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode == RESULT_OK && requestCode == REQUEST_SCAN_WIFI) {
//            String name = data.getExtras().getString("name");
//            Toast.makeText(this, name, Toast.LENGTH_SHORT).show();
//        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        }
        if (requestCode == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            scanHost();
        }

    }
    @Override
    public void onBackPressed() {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        } else {
            Toast.makeText(MainActivity.this, "Tekan lagi untuk keluar", Toast.LENGTH_SHORT).show();
        }

        mBackPressed = System.currentTimeMillis();
    }
}