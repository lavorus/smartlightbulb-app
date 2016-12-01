package com.lavorus.skripsi;

/**
 * Created by Alvin on 11/5/16.
 */

public class SmartLight {
    public String name;
    public String ip;
    public String lastcondition;

    public SmartLight(String name, String ip, String lastcondition) {
        this.name = name;
        this.ip = ip;
        this.lastcondition = lastcondition;
    }
}