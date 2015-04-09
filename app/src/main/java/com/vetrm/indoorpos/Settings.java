package com.vetrm.indoorpos;

import android.content.Intent;
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

import java.util.Arrays;


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
        float[] ranges = devman.readDists();
        log(Arrays.toString(ranges));
        XYZ res =  Compute.Solve3d(app.getANCHOR_XYZ(), ranges);

        EditText a1x = (EditText) findViewById(R.id.a1x);
        EditText a1y = (EditText) findViewById(R.id.a1y);
        EditText a1z = (EditText) findViewById(R.id.a1z);
        a1x.setText((int)(app.getANCHOR_XYZ(1).x*100f)*1+"");
        a1y.setText((int)(app.getANCHOR_XYZ(1).y*100f)+"");
        a1z.setText((int)(app.getANCHOR_XYZ(1).z*100f)+"");

        EditText a2x = (EditText) findViewById(R.id.a2x);
        EditText a2y = (EditText) findViewById(R.id.a2y);
        EditText a2z = (EditText) findViewById(R.id.a2z);
        a2x.setText((int)(app.getANCHOR_XYZ(2).x*100f)+"");
        a2y.setText((int)(app.getANCHOR_XYZ(2).y*100f)+"");
        a2z.setText((int)(app.getANCHOR_XYZ(2).z*100f)+"");

        EditText a3x = (EditText) findViewById(R.id.a3x);
        EditText a3y = (EditText) findViewById(R.id.a3y);
        EditText a3z = (EditText) findViewById(R.id.a3z);
        a3x.setText((int)(app.getANCHOR_XYZ(3).x*100f)+"");
        a3y.setText((int)(app.getANCHOR_XYZ(3).y*100f)+"");
        a3z.setText((int)(app.getANCHOR_XYZ(3).z*100f)+"");

        EditText p1 = (EditText) findViewById(R.id.p1);
        EditText p2 = (EditText) findViewById(R.id.p2);
        EditText p3 = (EditText) findViewById(R.id.p3);
        p1.setText((int)Math.floor(app.getDistOffset(1))+"");
        p2.setText((int)Math.floor(app.getDistOffset(2))+"");
        p3.setText((int)Math.floor(app.getDistOffset(3))+"");

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

    }

    public void refresh(View view) {
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
