package com.vetrm.indoorpos;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.lang.reflect.Array;
import java.util.Arrays;


public class Main extends ActionBarActivity {
    private App app;
    private ImageView anchor1;
    private ImageView anchor2;
    private ImageView anchor3;


    private float a1 = 0;
    private int count1 = 0;
    private float a2 = 0;
    private int count2 = 0;
    private float a3 = 0;
    private int count3 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        anchor1 = (ImageView) findViewById(R.id.imageView1);
        anchor2 = (ImageView) findViewById(R.id.imageView2);
        anchor3 = (ImageView) findViewById(R.id.imageView3);

        app = App.getInstance();
        getAndApplyPreference();

        // debug graphics
        if (false) {
            UsbDevice device = getIntent().getParcelableExtra(UsbManager.EXTRA_DEVICE);
            UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
            app.setDevman(new DeviceMan(device, manager));
        } else {
            app.setDevman(new DeviceMan());
        }

        final Handler handler = new Handler();
        Runnable runnable = new Runnable(){
            @Override
            public void run() {
                // TODO Auto-generated method stub
                // 在此处添加执行的代码
                testAnchors();
                Log.d("Handler:", "running testAnchors()");

                handler.postDelayed(this, 230);// 50ms后执行this，即runable
            }
        };
        handler.postDelayed(runnable, 2000);// 打开定时器，50ms后执行runnable操作
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

    public void testAnchors() {
        float res[] = app.getDevman().readDists();
        Log.d("Main: testAnchors;", Arrays.toString(res));

        if (Math.abs(a1 - res[0]) <= 0.01f) { count1 += 1; } else { count1 = 0; }
        if (Math.abs(a2 - res[1]) <= 0.01f) { count2 += 1; } else { count2 = 0; }
        if (Math.abs(a3 - res[2]) <= 0.01f) { count3 += 1; } else { count3 = 0; }
        a1 = res[0]; a2 = res[1]; a3 = res[2];

        if (Math.abs(res[0]) > 200) {
            anchor1.setImageResource(R.mipmap.red);
        } else if (count1 >= 2) {
            anchor1.setImageResource(R.mipmap.red);
        } else {
            anchor1.setImageResource(R.mipmap.green);
        }

        if (Math.abs(res[0]) > 200) {
            anchor2.setImageResource(R.mipmap.red);
        } else if (count2 >= 2) {
            anchor2.setImageResource(R.mipmap.red);
        } else {
            anchor2.setImageResource(R.mipmap.green);
        }

        if (Math.abs(res[0]) > 200) {
            anchor3.setImageResource(R.mipmap.red);
        } else if (count3 >= 2) {
            anchor3.setImageResource(R.mipmap.red);
        } else {
            anchor3.setImageResource(R.mipmap.green);
        }
    }

    public void setOffset(View view) {
        anchor1.setImageResource(R.mipmap.red);
        anchor2.setImageResource(R.mipmap.green);
        anchor3.setImageResource(R.mipmap.green);
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
