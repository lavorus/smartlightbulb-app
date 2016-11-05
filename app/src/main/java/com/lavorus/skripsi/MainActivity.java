package com.lavorus.skripsi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView mListView;
    private UsersAdapter adapter;
    private WifiManager mainWifi;
    private WifiReceiver receiverWifi;
    private List wifiList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ListView listView = (ListView) findViewById(R.id.recipe_list_view);

        adapter = new UsersAdapter(this);
        listView.setAdapter(adapter);


        receiverWifi = new WifiReceiver();
        registerReceiver(receiverWifi, new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        scanWifiList();

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

    private void setAdapter() {
        for (int i = 0; i < wifiList.size(); i++) {
            Log.v("Test", wifiList.get(i).toString());
        }
    }

    private void scanWifiList() {
        mainWifi.startScan();
        wifiList = mainWifi.getScanResults();
        setAdapter();
    }
}

class WifiReceiver extends BroadcastReceiver {
    public void onReceive(Context c, Intent intent) {
    }
}