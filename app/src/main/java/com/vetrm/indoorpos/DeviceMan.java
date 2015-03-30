package com.vetrm.indoorpos;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by simpleon on 26/3/15.
 */
public class DeviceMan {
    // 0483:5741
    private UsbDevice device;
    private UsbManager manager;
    private UsbDeviceConnection conn;
    private UsbInterface intf;
    private UsbEndpoint ep;

    private void log(Object obj) {
        final String DBG_TAG = "DeviceMan";
        Log.d(DBG_TAG, "" + obj);
    }

    public DeviceMan(UsbDevice dev, UsbManager man) {
        device = dev;
        manager = man;

        conn = manager.openDevice(device);

        intf = device.getInterface(0);
        ep = intf.getEndpoint(2);

        log(conn.claimInterface(intf, true));
    }

    public float[] read() {
        final int len = 64;

        byte[] content = new byte[len];

        log("------------------------");
        for (int i = 0; i < 5; i++) {
            System.out.println(conn.bulkTransfer(ep, content, content.length, 20));

            for (int j = 0; j < len; j++) {
                System.out.print(content[j]);
                System.out.print(" ");
            }
            System.out.println();
        }
        log("------------------------");

        return new float[]{12.041595f, 2.23608f, 8.062258f};
    }
}
