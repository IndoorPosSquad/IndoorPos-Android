package com.vetrm.indoorpos;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


public class Main extends ActionBarActivity {
    private App app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        app = App.getInstance();
        getAndApplyPreference();

        // debug graphics
        if (true) {
            UsbDevice device = getIntent().getParcelableExtra(UsbManager.EXTRA_DEVICE);
            UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
            app.setDevman(new DeviceMan(device, manager));
        } else {
            app.setDevman(new DeviceMan());
        }
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
            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showVisualization(View view) {
        Intent intent = new Intent(this, Visualization.class);
        //intent.putExtra("dev", devman);
        startActivity(intent);
    }

    public void read(View view) {
        app.getDevman().readDists();
    }

    public void showSettings(View view) {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }

    public void getAndApplyPreference() {
        SharedPreferences pref = getSharedPreferences(app.pref_file_name, 0);

        app.setANCHOR_XYZ(new XYZ[]{
                new XYZ(pref.getFloat("anchor1_x", 4.8f), pref.getFloat("anchor1_y", 0f),   pref.getFloat("anchor1_z", 4.0f)),
                new XYZ(pref.getFloat("anchor2_x", 0f),   pref.getFloat("anchor2_y", 0f),   pref.getFloat("anchor2_z", 4.0f)),
                new XYZ(pref.getFloat("anchor3_x", 0f),   pref.getFloat("anchor3_y", 4.8f), pref.getFloat("anchor3_z", 4.0f))
        });

        app.setDistOffset(new float[]{pref.getFloat("range1_offset", 0f), pref.getFloat("range2_offset", 0f), pref.getFloat("range3_offset", 0f)});
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }


}
