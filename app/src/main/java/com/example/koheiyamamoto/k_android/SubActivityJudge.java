package com.example.koheiyamamoto.k_android;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

public class SubActivityJudge extends Activity implements Runnable, SensorEventListener {
    SensorManager sm;
    TextView tv;
    TextView tv2;
    Handler h;
    float gx, gy, gz;
    char count = 0;
    //String fileNameRun = "Run.csv";
    //String fileNameStand = "Stand.csv";
    //String fileNameWalk = "Walk.csv";


    private J48 j48 = null;
    private Instances trainInstances = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) { // Initialize activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_judge);

        //LinearLayout ll = new LinearLayout(this);
        //tv1 = new TextView(textView);
        tv = (TextView) findViewById(R.id.textviewJudge);
        tv2 = (TextView) findViewById(R.id.textViewState);
        //ll.addView(tv);

        h = new Handler();
        h.postDelayed(this, 500);

        weka();

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
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
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
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        gx = event.values[0];
        gy = event.values[1];
        gz = event.values[2];

        if(this.j48 != null) {
            try {
                Attribute att1 = new Attribute("x", 0);
                Attribute att2 = new Attribute("y", 1);
                Attribute att3 = new Attribute("z", 2);

                Instance denseinstance = new DenseInstance(3);
                denseinstance.setValue(att1, gx);
                denseinstance.setValue(att2, gy);
                denseinstance.setValue(att3, gz);
                denseinstance.setDataset(trainInstances);

                double result = j48.classifyInstance(denseinstance);

                tv.setText(String.valueOf(result));
                h.postDelayed(this, 10);

                if(result <= 0.0){
                    count++;
                    tv2.setText("Run");
                    tv2.setBackgroundColor(Color.RED);

                    if(count>100) {
                        new AlertDialog.Builder(SubActivityJudge.this)
                                .setTitle("東京消防庁管内データ(H22~H26)")
                                .setMessage("歩きスマホ・自転車スマホによる事故で152人が救急搬送されました\n注意しましょう")
                                .setPositiveButton("気をつけます", null)
                                .show();
                        //wait(1000);
                        count = 0;
                    }

                }else if(result <= 1.0){
                    tv2.setText("Stand");
                    tv2.setBackgroundColor(Color.BLUE);
                }else {
                    count++;
                    tv2.setText("Walk");
                    tv2.setBackgroundColor(Color.GREEN);

                    if(count>100) {
                        new AlertDialog.Builder(SubActivityJudge.this)
                                .setTitle("東京消防庁管内データ(H22~H26)")
                                .setMessage("歩きスマホ・自転車スマホによる事故で152人が救急搬送されました\n注意しましょう")
                                .setPositiveButton("気をつけます", null)
                                .show();
                        //wait(1000);
                        count = 0;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void weka() {
        try {
            ArffLoader al = new ArffLoader();
            al.setFile(new File("data/data/com.example.koheiyamamoto.k_android/files/Data.arff"));
            trainInstances = new Instances(al.getDataSet());
            trainInstances.setClassIndex(trainInstances.numAttributes() - 1);
            j48 = new J48();
            j48.buildClassifier(trainInstances);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}