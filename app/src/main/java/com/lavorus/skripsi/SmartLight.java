package com.lavorus.skripsi;

/**
 * Created by Alvin on 11/5/16.
 */

public class SmartLight {
    public String name;
    public String ip;
    public int red;
    public int green;
    public int blue;

    public SmartLight(String name, String ip, int red, int green, int blue) {
        this.name = name;
        this.ip = ip;
        this.red = red;
        this.green = green;
        this.blue = blue;
    }
}