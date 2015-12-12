package com.vetrm.indoorpos;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;

import java.util.Arrays;

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

    private boolean DEBUG_GRAPHIC;
    private App app;

    private void log(Object obj) {
        final String DBG_TAG = "DeviceMan";
        Log.d(DBG_TAG, "" + obj);
    }

    public DeviceMan() {
        DEBUG_GRAPHIC = true;

        app = App.getInstance();
    }

    public DeviceMan(UsbDevice dev, UsbManager man) {
        DEBUG_GRAPHIC = false;

        app = App.getInstance();

        device = dev;
        manager = man;

        conn = manager.openDevice(device);

        intf = device.getInterface(0);
        ep = intf.getEndpoint(0);

        log(conn.claimInterface(intf, true));
    }

    public float[] read() {
        final int len = 64;

        byte[] content = new byte[len];

        log("------------------------");
        for (int i = 0; i < 5; i++) {
            System.out.println(conn.bulkTransfer(ep, content, content.length, 0));

            for (int j = 0; j < 12; j++) {
                System.out.print(content[j]);
                System.out.print(" ");
            }
            System.out.println();
        }
        log("------------------------");

        return new float[]{12.041595f, 2.23608f, 8.062258f};
    }

    public float[] readDists() {
        if (DEBUG_GRAPHIC) {
            return readPL();
        }
        final int len = 64;
        byte[] distInfo = new byte[len];

        // TODO
        int res = conn.bulkTransfer(ep, distInfo, distInfo.length, 0);
        log("BulkRead Status: " + res);
        if (res != 64) {
            return null;
        }
        log(Arrays.toString(distInfo));

        float[] result = new float[3];
        for (int i = 0; i < 3; i++) {
            byte[] aNum = new byte[4];
            for (int j = 0; j < 4; j++) {
                aNum[3 - j] = distInfo[j + i * 4];
            }
            result[i] = byteArrayToInt(aNum) / 100f;
        }

        result[0] -= app.getDistOffset(1);
        result[1] -= app.getDistOffset(2);
        result[2] -= app.getDistOffset(3);
        //[117.37, 124.98, 89.94]
        result[0] = justify(result[0]);
        result[1] = justify(result[1]);
        result[2] = justify(result[2]);

        log(Arrays.toString(result));
        return result;
    }

    public float[] readRawDists() {
        if (DEBUG_GRAPHIC) {
            return new float[] {104f, 103f, 88f};
        }
        final int len = 64;
        byte[] distInfo = new byte[len];

        // TODO
        int res = conn.bulkTransfer(ep, distInfo, distInfo.length, 0);
        log("BulkRead Status: " + res);
        if (res != 64) {
            return null;
        }
        log(Arrays.toString(distInfo));

        float[] result = new float[3];
        for (int i = 0; i < 3; i++) {
            byte[] aNum = new byte[4];
            for (int j = 0; j < 4; j++) {
                aNum[3 - j] = distInfo[j + i * 4];
            }
            result[i] = byteArrayToInt(aNum) / 100f;
        }

        log("Raw dists: " + Arrays.toString(result));
        return result;
    }

    private static float pos;
    private static float step = 0.3f;

    // test data generator for Compute.Solve3d
    public float[] readPL() {
        pos = setNewCount(pos);

        float x = (0.5f * pos * pos + 3);
        float y = pos / (pos + 7) + 3 ;
        float z = pos + 5;

        return  new float[]{x, y, z};
    }

    static float setNewCount(float x) {
        if (Math.abs(pos) >= 3) {
            step = - step;
        }
        return x + step;
    }

    public static int byteArrayToInt(byte[] b)
    {
        int value = 0;
        for (int i = 0; i < 4; i++) {
            int shift = (4 - 1 - i) * 8;
            value += (b[i] & 0x000000FF) << shift;
        }
        return value;
    }

    public static float justify(float x) {
        if (x < 0.1) {
            return 0.1f;
        }
        else {
            return x;
        }
    }
}
