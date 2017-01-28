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