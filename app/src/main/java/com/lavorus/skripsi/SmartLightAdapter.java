/*
 * Smart Light Bulb - Android App
 * GNU GPLv3 License
 *
 * created 01 September 2016
 * modified 27 January 2017
 * by Alvin Leonardo (alvin@lavorus.com)
 */

/*
 * Smart Light Bulb - Android App
 * GNU GPLv3 License
 *
 * created 01 September 2016
 * modified 27 January 2017
 * by Alvin Leonardo (alvin@lavorus.com)
 */

package com.lavorus.skripsi;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

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
        if (currentData.red == 0 && currentData.green == 0  && currentData.blue == 0) {
            tvCondition.setText("Off");
        } else if (currentData.red == 255 && currentData.green == 255  && currentData.blue == 255) {
            tvCondition.setText("On");
        } else {
            long total = currentData.red * 256 * 256 + currentData.green * 256 + currentData.blue * 1;
            String tmp = Long.toString(total, 16);
            for (int i = tmp.length(); i < 6; i++) {
                tmp = "0" + tmp;
            }
            tvCondition.setText("Color : #" + tmp);
        }
        return row;
    }
}