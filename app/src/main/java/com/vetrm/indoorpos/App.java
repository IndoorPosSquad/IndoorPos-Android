package com.vetrm.indoorpos;

import android.app.Application;
import android.content.SharedPreferences;
import android.hardware.usb.UsbDevice;
import android.util.Log;

import java.util.Arrays;

/**
 * Created by simpleon on 30/3/15.
 */
public class App extends Application {
    private static App singleton;

    private static DeviceMan devman;

    static String pref_file_name = "pref";

    private static float displayFactor = 2f;

    private static XYZ displayOffset = new XYZ(0f, 0f, 0f);

    private static float[] distOffset = new float[]{115.23f, 125.12f, 90.32f};
    // true = down, false = up
    private static boolean downup = true;

    public static XYZ getDisplayOffset() {
        return displayOffset;
    }

    public static void setDisplayOffset(XYZ displayOffset) {
        App.displayOffset = displayOffset;
    }

    private static void log(Object obj) {
        Log.d("App", "" + obj);
    }

    private static XYZ[] ANCHOR_XYZ = new XYZ[]{
            new XYZ(0f, 4.8f, 8.000000f),
            new XYZ(0f, 0f, 8.000000f),
            new XYZ(4.8f, 0f, 8.000000f)
    };

    public static float getDisplayFactor() {
        return displayFactor;
    }

    public static void setDisplayFactor(float displayFactor) {
        App.displayFactor = displayFactor;
    }

    public static XYZ[] getANCHOR_XYZ() {
        return ANCHOR_XYZ;
    }

    public static XYZ getANCHOR_XYZ(int which) {
        log("getting");
        log(Arrays.toString(App.ANCHOR_XYZ));
        return ANCHOR_XYZ[which - 1];
    }

    public static void setANCHOR_XYZ(int which, XYZ ANCHOR_XYZ) {
        log("setting");
        App.ANCHOR_XYZ[which - 1] = ANCHOR_XYZ;
        log(Arrays.toString(App.ANCHOR_XYZ));
    }

    public static void setANCHOR_XYZ(XYZ[] ANCHOR_XYZ) {
        App.ANCHOR_XYZ = ANCHOR_XYZ;
    }

    public static float[] getDistOffset() {
        return distOffset;
    }

    public static float getDistOffset(int which) {
        return distOffset[which - 1];
    }

    public static void setDistOffset(float[] distOffset) {
        App.distOffset = distOffset;
    }

    public static void setDistOffset(int which, float distOffset) {
        App.distOffset[which - 1] = distOffset;
    }

    public static DeviceMan getDevman() {
        return devman;
    }

    public static void setDevman(DeviceMan devman) {
        App.devman = devman;
    }


    public static App getInstance() {
        return singleton;
    }

    public static boolean isDown() {
        return downup;
    }

    public static void setDownup(boolean downup) {
        App.downup = downup;
    }
    public void saveDistOffset() {
        SharedPreferences.Editor editor = getSharedPreferences(pref_file_name, 0).edit();

        editor.putFloat("range1_offset", getDistOffset(1));
        editor.putFloat("range2_offset", getDistOffset(2));
        editor.putFloat("range3_offset", getDistOffset(3));

        editor.commit();
    }

    public void onCreate() {
        super.onCreate();
        singleton = this;
    }
}
