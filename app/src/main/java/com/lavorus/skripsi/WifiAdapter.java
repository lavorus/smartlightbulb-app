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

public class WifiAdapter extends BaseAdapter {
    private ArrayList<Wifi> data = new ArrayList<Wifi>();
    private Activity activity;

    public WifiAdapter(Activity activity){
        this.activity = activity;
    }

    public void addData(Wifi data){ 
        this.data.add(data);
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
        View row = inflater.inflate(R.layout.item_wifi, parent, false);

        Wifi currentData = data.get(position);
        TextView tvName = (TextView) row.findViewById(R.id.tvName);
        TextView tvPassword = (TextView) row.findViewById(R.id.tvPassword);
        tvName.setText(currentData.name);
        if (currentData.ispassword) {
            tvPassword.setVisibility(View.VISIBLE);
        } else {
            tvPassword.setVisibility(View.GONE);
        }
        return row;
    }
}