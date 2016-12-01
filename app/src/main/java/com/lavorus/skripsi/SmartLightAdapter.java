package com.lavorus.skripsi;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Alvin on 11/5/16.
 */

public class SmartLightAdapter extends BaseAdapter {
    private ArrayList<SmartLight> data = new ArrayList<SmartLight>();
    private Activity activity;

    public SmartLightAdapter(Activity activity){
        this.activity = activity;
    }

    public void addData(SmartLight newdata){
        if (this.data.contains(newdata)) {
            // true
        } else {
            this.data.add(newdata);
        }
    }

    public void clearData(){
        this.data.clear();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View row = inflater.inflate(R.layout.item_smartbulb, parent, false);

        SmartLight currentData = data.get(position);
        TextView tvName = (TextView) row.findViewById(R.id.tvName);
        TextView tvCondition = (TextView) row.findViewById(R.id.tvCondition);
        tvName.setText(currentData.name);
        if (currentData.lastcondition == "") {
            tvCondition.setText("Off");
        } else {
            tvCondition.setText("Colour : #" + currentData.lastcondition);
        }
        return row;
    }
}