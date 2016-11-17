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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    WifiManager wifi;
    int size = 0;
    List<ScanResult> results;
    Handler mHandler;

    private ListView mListView;
    private UsersAdapter adapter;
    private List wifiList;

    String ITEM_KEY = "key";
    String ITEM_CAP = "capabilities";
    String ITEM_LOCK = "lock";
    ArrayList<HashMap<String, String>> arraylist = new ArrayList<HashMap<String, String>>();

    @Override
    public void onRefresh() {
        /**
         * This method is called when swipe refresh is pulled down
         */
        refreshWifi();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(
            new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(true);
                }
            }
        );

        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (wifi.isWifiEnabled() == false)
        {
            Toast.makeText(getApplicationContext(), "wifi is disabled..making it enabled", Toast.LENGTH_LONG).show();
            wifi.setWifiEnabled(true);
        }

        registerReceiver(new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context c, Intent intent)
            {
                results = wifi.getScanResults();
                size = results.size();
                swipeRefreshLayout.setRefreshing(false);
            }
        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        }else{
            mHandler = new Handler();
            mHandler.postDelayed(m_Runnable,2000);
        }

        final ListView listView = (ListView) findViewById(R.id.recipe_list_view);

        adapter = new UsersAdapter(this);
        listView.setAdapter(adapter);

        User newUser = new User("Nathan", "San Diego");
        adapter.addData(newUser);
        adapter.notifyDataSetChanged();
        newUser = new User("NathanX", "San asd");
        adapter.addData(newUser);
        adapter.notifyDataSetChanged();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User selectedData = (User) adapter.getItem(position);

                Toast.makeText(getApplicationContext(), "Ini: " + selectedData.name, Toast.LENGTH_SHORT).show();
            }
        });
        Toast.makeText(getApplicationContext(), "HELLO", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Do something with granted permission
            mHandler = new Handler();
            mHandler.postDelayed(m_Runnable,15000);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            refreshWifi();
        }
        return super.onOptionsItemSelected(item);
    }

    private final Runnable m_Runnable = new Runnable()
    {
        public void run()

        {
            refreshWifi();
            //mHandler.postDelayed(m_Runnable, 10000);
        }
    };


    private void refreshWifi() {
        arraylist.clear();
        wifi.startScan();

        Toast.makeText(this, "Scanning.... " + size, Toast.LENGTH_SHORT).show();
        try
        {
            size = size - 1;
            while (size >= 0)
            {
                HashMap<String, String> items = new HashMap<String, String>();
                //items.put(ITEM_KEY, results.get(size).SSID + "  " + results.get(size).capabilities);
                items.put(ITEM_KEY, results.get(size).SSID);
                items.put(ITEM_CAP, results.get(size).capabilities);
                if (results.get(size).capabilities.equals("[ESS]") || results.get(size).capabilities.equals("[WPS][ESS]")) {
                    items.put(ITEM_LOCK, "");
                } else {
                    items.put(ITEM_LOCK, "Lock");
                }

                arraylist.add(items);
                size--;
                adapter.notifyDataSetChanged();
            }
        }
        catch (Exception e)
        { }
    }
}