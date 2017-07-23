package com.example.koheiyamamoto.k_android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class MainActivity extends Activity implements Runnable, SensorEventListener {
    SensorManager sm;
    TextView tv;
    Handler h;
    float gx, gy, gz;
    String fileName = "Data.arff";

    @Override
    protected void onCreate(Bundle savedInstanceState) { // Initialize activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView) findViewById(R.id.textview);
        //ll.addView(tv);

        h = new Handler();
        h.postDelayed(this, 500);
    }

    public void OnClickReset(View v){
        File file1 = new File("data/data/com.example.koheiyamamoto.k_android/files/Run.csv");
        if (file1.exists()){
            if (file1.delete()){
                System.out.println("ファイルを削除しました");
            }else{
                System.out.println("ファイルの削除に失敗しました");
            }
        }else{
            System.out.println("ファイルが見つかりません");
        }

        File file2 = new File("data/data/com.example.koheiyamamoto.k_android/files/Stand.csv");
        if (file2.exists()){
            if (file2.delete()){
                System.out.println("ファイルを削除しました");
            }else{
                System.out.println("ファイルの削除に失敗しました");
            }
        }else{
            System.out.println("ファイルが見つかりません");
        }

        File file3 = new File("data/data/com.example.koheiyamamoto.k_android/files/Walk.csv");
        if (file3.exists()){
            if (file3.delete()){
                System.out.println("ファイルを削除しました");
            }else{
                System.out.println("ファイルの削除に失敗しました");
            }
        }else{
            System.out.println("ファイルが見つかりません");
        }

        File file4 = new File("data/data/com.example.koheiyamamoto.k_android/files/Data.arff");
        if (file4.exists()){
            if (file4.delete()){
                System.out.println("ファイルを削除しました");
            }else{
                System.out.println("ファイルの削除に失敗しました");
            }
        }else{
            System.out.println("ファイルが見つかりません");
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void OnClickSub(View v) {
        Intent intent = new Intent();
        intent.setClassName("com.example.koheiyamamoto.k_android", "com.example.koheiyamamoto.k_android.SubActivity");
        startActivity(intent);
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

    public void OnClickJudge(View v) {
        String meta = "@relation Acc\n\n@attribute x REAL\n@attribute y REAL\n@attribute z REAL\n" +
                "@attribute class {run,stand,walk}\n\n@data\n";
        saveFile(fileName, meta);

        /*csvファイル読み込み&書き込み*/
        try {
            File f3 = new File("data/data/com.example.koheiyamamoto.k_android/files/Run.csv");
            byte[] b3 = new byte[(int) f3.length()];
            FileInputStream fi3 = new FileInputStream(f3);

            String str3;
            fi3.read(b3);
            str3 = new String(b3);

            fi3.close();
            saveFile(fileName, str3);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            File f1 = new File("data/data/com.example.koheiyamamoto.k_android/files/Stand.csv");
            byte[] b1 = new byte[(int) f1.length()];
            FileInputStream fi1 = new FileInputStream(f1);

            String str1;
            fi1.read(b1);
            str1 = new String(b1);

            fi1.close();
            saveFile(fileName, str1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            File f2 = new File("data/data/com.example.koheiyamamoto.k_android/files/Walk.csv");
            byte[] b2 = new byte[(int) f2.length()];
            FileInputStream fi2 = new FileInputStream(f2);

            String str2;
            fi2.read(b2);
            str2 = new String(b2);

            fi2.close();
            saveFile(fileName, str2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent intent = new Intent();
        intent.setClassName("com.example.koheiyamamoto.k_android", "com.example.koheiyamamoto.k_android.SubActivityJudge");
        startActivity(intent);
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
        /*saveFile(fileName, String.valueOf(gx) + ",");
        saveFile(fileName, String.valueOf(gy) + ",");
        saveFile(fileName, String.valueOf(gz) + "\n");*/
    }
    /*
    public void saveFile(String file, String str) {
        FileOutputStream fileOutputstream = null;
        try {
            fileOutputstream = openFileOutput(file, Context.MODE_APPEND);
            fileOutputstream.write(str.getBytes());
            fileOutputstream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}