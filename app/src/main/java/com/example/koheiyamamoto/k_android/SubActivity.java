package com.example.koheiyamamoto.k_android;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class SubActivity extends Activity implements Runnable, SensorEventListener {
    SensorManager sm;
    TextView tv;
    Handler h;
    float gx, gy, gz;
    int state;
    String fileNameStand = "Stand.csv";
    String fileNameWalk = "Walk.csv";
    String fileNameRun = "Run.csv";

    @Override
    protected void onCreate(Bundle savedInstanceState) { // Initialize activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        //LinearLayout ll = new LinearLayout(this);
        //tv = new TextView(this);
        tv = (TextView) findViewById(R.id.textviewSub);
        //ll.addView(tv);

        h = new Handler();
        h.postDelayed(this, 500);
    }

   /* public void OnClickStand(View view) {
        Intent intent=new Intent();
        intent.setClassName("com.example.koheiyamamoto.k_android","com.example.koheiyamamoto.k_android.SubActivity");
        startActivity(intent);
    }*/

    public void OnClickStop(View view) {
        state = 0;
    }

    public void OnClickStand(View view) {
        state = 1;
    }

    public void OnClickWalk(View view) {
        state = 2;
    }

    public void OnClickRun(View view) {
        state = 3;
    }

    public void OnClickDone(View view) {
        finish();
    }

    @Override
    public void run() {
        tv.setText("X-axis : " + gx + "\n"
                + "Y-axis : " + gy + "\n"
                + "Z-axis : " + gz + "\n");
        h.postDelayed(this, 1);
    }

    @Override
    protected void onResume() { // Show activity
        super.onResume();
        sm = (SensorManager)getSystemService(SENSOR_SERVICE);
        List<Sensor> sensors =
                sm.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (0 < sensors.size()) {
            sm.registerListener(this, sensors.get(0),
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sm.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        h.removeCallbacks(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        gx = event.values[0];
        gy = event.values[1];
        gz = event.values[2];

        if(state == 1){
            saveFile(fileNameStand, String.valueOf(gx) + ",");
            saveFile(fileNameStand, String.valueOf(gy) + ",");
            saveFile(fileNameStand, String.valueOf(gz) + ",");
            saveFile(fileNameStand, "stand\n");
        }else if(state == 2){
            saveFile(fileNameWalk, String.valueOf(gx) + ",");
            saveFile(fileNameWalk, String.valueOf(gy) + ",");
            saveFile(fileNameWalk, String.valueOf(gz) + ",");
            saveFile(fileNameWalk, "walk\n");
        }else if(state == 3){
            saveFile(fileNameRun, String.valueOf(gx) + ",");
            saveFile(fileNameRun, String.valueOf(gy) + ",");
            saveFile(fileNameRun, String.valueOf(gz) + ",");
            saveFile(fileNameRun, "run\n");
        }else{

        }
    }

    public void saveFile(String file, String str) {
        FileOutputStream fileOutputstream = null;
        try {
            fileOutputstream = openFileOutput(file, Context.MODE_APPEND);
            fileOutputstream.write(str.getBytes());
            fileOutputstream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}