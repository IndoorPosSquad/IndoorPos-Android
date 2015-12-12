package com.vetrm.indoorpos;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.EdgeEffect;
import android.widget.EditText;
import android.widget.TextView;

import com.threed.jpct.SimpleVector;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;


public class Settings extends ActionBarActivity {
    private App app;
    private DeviceMan devman;

    private void log(Object obj) {
        Log.d("Settings", "" + obj);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        app = App.getInstance();
        devman = app.getDevman();
        doDisplay();

        //tv.setText("" + ranges[0]);
    }

    public SimpleVector transfer(float x, float y, float z) {
        final float factor = app.getDisplayFactor();
        final XYZ offset = app.getDisplayOffset();
        return new SimpleVector(factor * y + offset.y, factor * -z - offset.z, factor * -x - offset.x);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
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

    public void doDisplay() {
        // preference

        float[] ranges = devman.readDists();
        if (ranges == null) {}

        log(Arrays.toString(ranges));

        XYZ res =  Compute.Solve3d(app.getANCHOR_XYZ(), ranges);
        EditText a1x = (EditText) findViewById(R.id.a1x);
        EditText a1y = (EditText) findViewById(R.id.a1y);
        EditText a1z = (EditText) findViewById(R.id.a1z);
        log(app.getANCHOR_XYZ(1));
        a1x.setText((int)(app.getANCHOR_XYZ(1).x*100f)+"");
        a1y.setText((int)(app.getANCHOR_XYZ(1).y*100f)+"");
        a1z.setText((int)(app.getANCHOR_XYZ(1).z*100f)+"");

        EditText a2x = (EditText) findViewById(R.id.a2x);
        EditText a2y = (EditText) findViewById(R.id.a2y);
        EditText a2z = (EditText) findViewById(R.id.a2z);
        log(app.getANCHOR_XYZ(2));
        a2x.setText((int)(app.getANCHOR_XYZ(2).x*100f)+"");
        a2y.setText((int)(app.getANCHOR_XYZ(2).y*100f)+"");
        a2z.setText((int)(app.getANCHOR_XYZ(2).z*100f)+"");

        EditText a3x = (EditText) findViewById(R.id.a3x);
        EditText a3y = (EditText) findViewById(R.id.a3y);
        EditText a3z = (EditText) findViewById(R.id.a3z);
        log(app.getANCHOR_XYZ(3));
        a3x.setText((int)(app.getANCHOR_XYZ(3).x*100f)+"");
        a3y.setText((int)(app.getANCHOR_XYZ(3).y*100f)+"");
        a3z.setText((int)(app.getANCHOR_XYZ(3).z*100f)+"");

        // 整数部分
        EditText p1 = (EditText) findViewById(R.id.p1);
        EditText p2 = (EditText) findViewById(R.id.p2);
        EditText p3 = (EditText) findViewById(R.id.p3);
        p1.setText((int)Math.floor(app.getDistOffset(1))+"");
        p2.setText((int)Math.floor(app.getDistOffset(2))+"");
        p3.setText((int)Math.floor(app.getDistOffset(3))+"");

        // 小数部分
        EditText p1rest = (EditText) findViewById(R.id.p1rest);
        EditText p2rest = (EditText) findViewById(R.id.p2rest);
        EditText p3rest = (EditText) findViewById(R.id.p3rest);
        p1rest.setText((int)((app.getDistOffset(1) - (int)Math.floor(app.getDistOffset(1)))*100f)+"");
        p2rest.setText((int)((app.getDistOffset(2) - (int)Math.floor(app.getDistOffset(2)))*100f)+"");
        p3rest.setText((int)((app.getDistOffset(3) - (int)Math.floor(app.getDistOffset(3)))*100f)+"");

        TextView p1b = (TextView) findViewById(R.id.p1before);
        TextView p2b = (TextView) findViewById(R.id.p2before);
        TextView p3b = (TextView) findViewById(R.id.p3before);
        p1b.setText(ranges[0]+app.getDistOffset(1)+"");
        p2b.setText(ranges[1]+app.getDistOffset(2)+"");
        p3b.setText(ranges[2]+app.getDistOffset(3)+"");

        TextView p1a = (TextView) findViewById(R.id.p1after);
        TextView p2a = (TextView) findViewById(R.id.p2after);
        TextView p3a = (TextView) findViewById(R.id.p3after);
        p1a.setText(ranges[0]+"");
        p2a.setText(ranges[1]+"");
        p3a.setText(ranges[2]+"");

        TextView px = (TextView) findViewById(R.id.posx);
        TextView py = (TextView) findViewById(R.id.posy);
        TextView pz = (TextView) findViewById(R.id.posz);
        px.setText(res.x+"");
        py.setText(res.y+"");
        pz.setText(res.z+"");

    }

    public void doRefresh() {
        log("doing refresh...");

        try {
            EditText a1x = (EditText) findViewById(R.id.a1x);
            EditText a1y = (EditText) findViewById(R.id.a1y);
            EditText a1z = (EditText) findViewById(R.id.a1z);
            app.setANCHOR_XYZ(1, new XYZ(
                    Integer.parseInt(a1x.getText().toString()) / 100f,
                    Integer.parseInt(a1y.getText().toString()) / 100f,
                    Integer.parseInt(a1z.getText().toString()) / 100f
            ));

            EditText a2x = (EditText) findViewById(R.id.a2x);
            EditText a2y = (EditText) findViewById(R.id.a2y);
            EditText a2z = (EditText) findViewById(R.id.a2z);
            app.setANCHOR_XYZ(2, new XYZ(
                    Integer.parseInt(a2x.getText().toString()) / 100f,
                    Integer.parseInt(a2y.getText().toString()) / 100f,
                    Integer.parseInt(a2z.getText().toString()) / 100f
            ));

            EditText a3x = (EditText) findViewById(R.id.a3x);
            EditText a3y = (EditText) findViewById(R.id.a3y);
            EditText a3z = (EditText) findViewById(R.id.a3z);
            app.setANCHOR_XYZ(3, new XYZ(
                    Integer.parseInt(a3x.getText().toString()) / 100f,
                    Integer.parseInt(a3y.getText().toString()) / 100f,
                    Integer.parseInt(a3z.getText().toString()) / 100f
            ));

            EditText p1 = (EditText) findViewById(R.id.p1);
            EditText p2 = (EditText) findViewById(R.id.p2);
            EditText p3 = (EditText) findViewById(R.id.p3);
            EditText p1rest = (EditText) findViewById(R.id.p1rest);
            EditText p2rest = (EditText) findViewById(R.id.p2rest);
            EditText p3rest = (EditText) findViewById(R.id.p3rest);
            app.setDistOffset(1, (Integer.parseInt(p1.getText().toString()) + Integer.parseInt(p1rest.getText().toString()) / 100f));
            app.setDistOffset(2, (Integer.parseInt(p2.getText().toString()) + Integer.parseInt(p2rest.getText().toString()) / 100f));
            app.setDistOffset(3, (Integer.parseInt(p3.getText().toString()) + Integer.parseInt(p3rest.getText().toString()) / 100f));

            savePref();
        } catch (Exception e) {
            log(e);
        }
    }

    public void savePref() {
        // write to preference
        SharedPreferences.Editor editor = getSharedPreferences(app.pref_file_name, 0).edit();

        editor.putFloat("anchor1_x", app.getANCHOR_XYZ(1).x);
        editor.putFloat("anchor1_y", app.getANCHOR_XYZ(1).y);
        editor.putFloat("anchor1_z", app.getANCHOR_XYZ(1).z);

        editor.putFloat("anchor2_x", app.getANCHOR_XYZ(2).x);
        editor.putFloat("anchor2_y", app.getANCHOR_XYZ(2).y);
        editor.putFloat("anchor2_z", app.getANCHOR_XYZ(2).z);

        editor.putFloat("anchor3_x", app.getANCHOR_XYZ(3).x);
        editor.putFloat("anchor3_y", app.getANCHOR_XYZ(3).y);
        editor.putFloat("anchor3_z", app.getANCHOR_XYZ(3).z);

        editor.putFloat("range1_offset", app.getDistOffset(1));
        editor.putFloat("range2_offset", app.getDistOffset(2));
        editor.putFloat("range3_offset", app.getDistOffset(3));

        editor.commit();
    }

    public void refresh(View view) {
        doRefresh();
        doDisplay();
    }

    public void setOffset(View view) {
        float[] ranges = devman.readDists();
        app.setDistOffset(1, ranges[0] - 4.15f);
        app.setDistOffset(2, ranges[1] - 3.35f);
        app.setDistOffset(3, ranges[2] - 3.10f);

        SharedPreferences.Editor editor = getSharedPreferences(app.pref_file_name, 0).edit();
        editor.putFloat("range1_offset", app.getDistOffset(1));
        editor.putFloat("range2_offset", app.getDistOffset(2));
        editor.putFloat("range3_offset", app.getDistOffset(3));

        doRefresh();
        doDisplay();
    }

    public void pressOk(View view) {
        doRefresh();
        Intent intent = new Intent(this, Main.class);
        startActivity(intent);
    }

    public void revertUpDown(View view) {
        log("revertting up/down");
        log(app.isDown());
        app.setDownup(!app.isDown());
        log(app.isDown());
    }

    public void doAutoOffset() {
        Timer mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
            }
        }, 5 * 1000, 5 * 1000);
    }

/*    *//**
     * A placeholder fragment containing a simple view.
     *//*
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
            return rootView;
        }
    }*/
}
