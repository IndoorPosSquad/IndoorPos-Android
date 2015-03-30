package com.vetrm.indoorpos;

import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.nio.ByteBuffer;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

public class Main extends ActionBarActivity {
    private UsbDevice device;
    private UsbManager manager;
    /*
    private UsbDeviceConnection conn;
    private UsbInterface intf;
    private UsbEndpoint ep;
    */

    private DeviceMan devman;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        device = getIntent().getParcelableExtra(UsbManager.EXTRA_DEVICE);
        manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        devman = new DeviceMan(device, manager);
        /*
        conn = manager.openDevice(device);

        intf = device.getInterface(0);
        ep = intf.getEndpoint(2);

        System.out.println(conn.claimInterface(intf, true));
        */

        //HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
        //UsbDevice device = (UsbDevice) deviceList.values().toArray()[0];
        //System.out.println("Printing USBinfo:");
        //System.out.println(device);
        //System.out.println(device.getVendorId()); // dec 1155 -> hex 0483
        //System.out.println(device.getProductId()); // dec 22337 -> hex 5741
        //System.out.println(deviceList.values().toArray()[0]);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showGraphics(View view) {
        Intent intent = new Intent(this, Visualization.class);
        startActivity(intent);
    }

    public void read(View view) {
        devman.read();
        //System.out.println(ep.getDirection());
        //System.out.println(UsbConstants.USB_DIR_IN); // => 128
        //Thread read = new Thread(this);
        //read.start();

        /*
        final int len = 64;

        byte[] content = new byte[len];

        System.out.println("------------------------");
        for (int i = 0; i < 5; i++) {
            System.out.println(conn.bulkTransfer(ep, content, content.length, 20));

            for (int j = 0; j < len; j++) {
                System.out.print(content[j]);
                System.out.print(" ");
            }
            System.out.println();
        }
        System.out.println("------------------------");
        */
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    /*
    @Override
    public void run() {
        ByteBuffer bf = ByteBuffer.allocate(64);
        UsbRequest request = new UsbRequest();
        request.initialize(conn, device.getInterface(0).getEndpoint(0));


        System.out.println("1");
        while (true) {
            request.queue(bf, 1);
            System.out.println("2");
            if (conn.requestWait() == request) {
                byte rec = bf.get(0);
                System.out.println(rec);

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                break;
            }

        }

    }
    */
}
