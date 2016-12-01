package com.lavorus.skripsi;

import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SVBar;
import com.larswerkman.holocolorpicker.ValueBar;

import org.json.JSONObject;

public class LightColorActivity extends AppCompatActivity {
    private String name;
    private String ip;
    private int total = 0;
    private ColorPicker picker;
    private RequestQueue queue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color);

        Intent intent = getIntent();
        ip = intent.getStringExtra("host");
        name = intent.getStringExtra("name");
        total = intent.getIntExtra("total", 0);

        String url = "http://" + ip + ":19105/status";
        queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject mainObject = new JSONObject(response);
                            name = mainObject.getString("name");
                            int tmpRed = mainObject.getInt("red");
                            int tmpGreen = mainObject.getInt("green");
                            int tmpBlue = mainObject.getInt("blue");
                            total = tmpRed * 256 * 256 + tmpGreen * 256 + tmpBlue * 1;
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("TEST", "Fail to Get Status");
            }
        });
        queue.add(stringRequest);
        setTitle(name);

        picker = (ColorPicker) findViewById(R.id.picker);
        SVBar svBar = (SVBar) findViewById(R.id.svbar);
        final TextView hexCode = (TextView) findViewById(R.id.textView3);
        picker.addSVBar(svBar);
        picker.setShowOldCenterColor(false);

        picker.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() {
            @Override
            public void onColorChanged(int intColor) {
                if (intColor < 0)
                    intColor += 16777216;
                String hexColor = Integer.toHexString(intColor).toUpperCase();
                for (int i = hexColor.length(); i < 6; i++) {
                    hexColor = "0" + hexColor;
                }
                hexCode.setText("#" + hexColor);
                int tmpRed = intColor / (256 * 256);
                int tmpGreen = intColor / 256 % 256;
                int tmpBlue = intColor % 256;
                String url = "http://" + ip + ":19105/setcolor?red=" + tmpRed + "&green=" + tmpGreen + "&blue=" + tmpBlue;

                Log.d("testi", url);
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // Do Nothing
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("testi", "Failed" + ip);
                    }
                });
                stringRequest.setTag("CHANGE");
                if (queue != null) {
                    queue.cancelAll("CHANGE");
                }
                queue.add(stringRequest);
            }
        });
        picker.setColor(total);

        final Button white = (Button) findViewById(R.id.button3);
        white.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                picker.setColor(16777215);
            }
        });

        final Button black = (Button) findViewById(R.id.button4);
        black.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                picker.setColor(0);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}