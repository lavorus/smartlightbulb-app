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

public class UsersAdapter extends BaseAdapter {
    private ArrayList<User> data = new ArrayList<User>();
    private Activity activity;

    public UsersAdapter(Activity activity){
        this.activity = activity;
    }

    public void addData(User data){
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
        View row = inflater.inflate(R.layout.item_user, parent, false);

        User currentData = data.get(position);
        TextView tvName = (TextView) row.findViewById(R.id.tvName);
        TextView tvHome = (TextView) row.findViewById(R.id.tvHome);
        tvName.setText(currentData.name);
        tvHome.setText(currentData.hometown);

        return row;
    }
}