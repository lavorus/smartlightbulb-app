package com.lavorus.skripsi;

/**
 * Created by Alvin on 11/5/16.
 */

public class Wifi {
    public String name;
    public boolean ispassword;

    public Wifi(String name, boolean islock) {
        this.name = name;
        this.ispassword = islock;
    }
}