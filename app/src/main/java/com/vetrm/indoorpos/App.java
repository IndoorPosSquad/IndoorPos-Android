package com.vetrm.indoorpos;

import android.app.Application;
import android.hardware.usb.UsbDevice;

/**
 * Created by simpleon on 30/3/15.
 */
public class App extends Application {
    private static App singleton;

    public static DeviceMan getDevman() {
        return devman;
    }

    public static void setDevman(DeviceMan devman) {
        App.devman = devman;
    }

    private static DeviceMan devman;

    public static App getInstance() {
        return singleton;
    }

    public void onCreate() {
        super.onCreate();
        singleton = this;
    }
}
