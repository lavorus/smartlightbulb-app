package com.lavorus.skripsi;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class WifiScanActivity extends AppCompatActivity {

    WifiManager wifi;
    int size = 0;
    List<ScanResult> results;
    Handler mHandler;
    boolean skripsionly = false;

    private WifiAdapter adapter;
    private Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifiscan);

        Intent intent = getIntent();
        skripsionly = intent.getBooleanExtra("justSkripsi", false);

        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (wifi.isWifiEnabled() == false) {
            Toast.makeText(getApplicationContext(), "Waiting Wifi Ready", Toast.LENGTH_LONG).show();
            wifi.setWifiEnabled(true);
        }

        registerReceiver(new BroadcastReceiver()  {
            @Override
            public void onReceive(Context c, Intent intent) {
                results = wifi.getScanResults();
                size = results.size();
            }
        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }else{
            mHandler = new Handler();
            mHandler.postDelayed(m_Runnable,2000);
        }

        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                refreshWifi();
            }
        });

        final ListView listView = (ListView) findViewById(R.id.wifi_list_view);
        adapter = new WifiAdapter(this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Wifi selectedData = (Wifi) adapter.getItem(position);

                Intent data = new Intent();
                data.putExtra("name", selectedData.name);
                setResult(RESULT_OK, data);
                finish();
            }
        });

        refreshWifi();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mHandler = new Handler();
            mHandler.postDelayed(m_Runnable,15000);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_wifi, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            refreshWifi();
        }
        return super.onOptionsItemSelected(item);
    }

    private final Runnable m_Runnable = new Runnable() {
        public void run() {
            refreshWifi();
        }
    };


    private void refreshWifi() {
        button.setEnabled(false);
        adapter.clearData();
        wifi.startScan();

        if (size >= 0)
            Toast.makeText(this, "Scanning... " + size, Toast.LENGTH_SHORT).show();
        try {
            size = size - 1;
            while (size >= 0) {
                String wifiSSID = results.get(size).SSID;
                String wifiPass = results.get(size).capabilities;
                boolean wifiLock = false;
                if (wifiPass.equals("[ESS]") || wifiPass.equals("[WPS][ESS]")) {
                    wifiLock = false;
                } else {
                    wifiLock = true;
                }
                size--;

                if (skripsionly && !(wifiSSID.indexOf("Skripsi-") == 0))
                    continue;

                Wifi newUser = new Wifi(wifiSSID, wifiLock);
                adapter.addData(newUser);
                adapter.notifyDataSetChanged();
            }
        }
        catch (Exception e) {
        }
        button.setEnabled(true);
    }
}