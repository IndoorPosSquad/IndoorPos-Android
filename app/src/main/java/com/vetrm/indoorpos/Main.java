package com.vetrm.indoorpos;

import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.HashMap;

public class Main extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UsbDevice device = getIntent().getParcelableExtra(UsbManager.EXTRA_DEVICE);
        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        //HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
        //UsbDevice device = (UsbDevice) deviceList.values().toArray()[0];
        //System.out.println("Printing USBinfo:");
        System.out.println(device);
        //System.out.println(device.getVendorId()); // dec 1155 -> hex 0483
        //System.out.println(device.getProductId()); // dec 22337 -> hex 5741
        //System.out.println(deviceList.values().toArray()[0]);
        //UsbInterface intf = device.getInterface(0);
        //UsbEndpoint ep = intf.getEndpoint(0);

        byte[] content = new byte[64];

        //UsbDeviceConnection conn = manager.openDevice(device);
        //System.out.println(conn.claimInterface(intf, true));
        // System.out.println(conn.bulkTransfer(ep, content, content.length, 1));

        System.out.println(content.toString());
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

    public void listUSB() {

    }
}
