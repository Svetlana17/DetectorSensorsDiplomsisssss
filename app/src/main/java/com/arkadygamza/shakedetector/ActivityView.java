package com.arkadygamza.shakedetector;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.arkadygamza.shakedetector.RecordValues.RecordValues;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ActivityView extends AppCompatActivity  implements  View.OnClickListener{
    SensorManager sensorManager;
    List<Sensor> sensorList=new ArrayList<>();
    SensorAdapter sensorAdapter;
    Sensor sensorAccelerometr;
    Sensor sensorGiroscope;
    final String tag = "IBMEyes";
    RecyclerView recyclerView;
    TextView tvText;
    public String state = "DEFAULT";
    StringBuilder sb = new StringBuilder();
    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        tvText = (TextView) findViewById(R.id.tvText);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorAccelerometr = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorGiroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorList.add(sensorAccelerometr);
        sensorList.add(sensorGiroscope);
        sensorAdapter = new SensorAdapter(this, sensorList);
        recyclerView.setAdapter(sensorAdapter);
        sensorAdapter.notifyDataSetChanged();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    public void onSensorChanged(int sensor, float[] value) {
        synchronized (this) {
            Log.d(tag, "onSensorChanged: " + sensor + ", x: " +
                    value[0] + ", y: " + value[1] + ", z: " + value[2]);
//            if (sensor == SensorManager.) {
//                xViewO.setText("Orientation X: " + values[0]);
//                yViewO.setText("Orientation Y: " + values[1]);
//                zViewO.setText("Orientation Z: " + values[2]);
//            }
            if (sensor == SensorManager.SENSOR_ACCELEROMETER) {
//                xViewA.setText("Accel X: " + value[0]);
//                yViewA.setText("Accel Y: " + value[1]);
//                zViewA.setText("Accel Z: " + value[2]);
            }
        } }
    @Override
    public void onClick(View v) {
        int selectedItemposition=recyclerView.getChildPosition(v);
        Sensor sensor=sensorList.get(selectedItemposition);
        Intent intent=new Intent(this,MainActivity.class);
        intent.putExtra( "sensortype",sensor.getType());
        this.startActivity(intent);
    }
    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(listener, sensorAccelerometr,
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(listener, sensorGiroscope,
                SensorManager.SENSOR_DELAY_NORMAL);

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showInfo();
                    }
                });
            }
        };
        timer.schedule(task, 0, 400);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(listener);
        // timer.cancel();
    }

    String format(float values[]) {
        return String.format("%1$.1f\t\t%2$.1f\t\t%3$.1f", values[0], values[1],
                values[2]);
    }
    void showInfo() {
        sb.setLength(0);
        sb.append("Акселерометр: " + format(valuesAccel) +  "  m/s^2")
                .append("\nГироскоп : " + format(valuesGiroscope) + "  rad/s" );
        tvText.setText(sb);
    }
    float[] valuesAccel = new float[3];
    float[] valuesGiroscope = new float[3];
    SensorEventListener listener = new SensorEventListener() {
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
        @Override
        public void onSensorChanged(SensorEvent event) {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    for (int i = 0; i < 3; i++) {
                        valuesAccel[i] = event.values[i];
                    }
                    break;
                case Sensor.TYPE_GYROSCOPE:
                    for (int i = 0; i < 3; i++) {
                        valuesGiroscope[i] = event.values[i];
                    }
                    break;
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.line_gyroscope:
                state = "gyroscope";
                Intent intent = new Intent(ActivityView.this,GyroscopeActivity.class);
                startActivity(intent);
                return true;
            case R.id.line_accelerometr:
                state = "accelerometr";
                Intent intents = new Intent(ActivityView.this,MainActivity.class);
                startActivity(intents);
                return true;
            case R.id.line_accelerometr_geroscope:
                state="gyroscope_acselerometr";
                Intent intent1=new Intent(ActivityView.this, AccelerGyrosActivity.class);
                startActivity(intent1);
                return  true;
            case R.id.main:
                state="main";
                Intent i = new Intent(ActivityView.this,StartActivity.class);
                startActivity(i);
                return true;
            case R.id.record:
                state="record";
                Intent ir=new Intent(ActivityView.this, RecordValues.class);
                startActivity(ir);
                return  true;
            case  R.id.movement:
                state="movement";
                Intent intetnm=new Intent(ActivityView.this,MovementActivity.class);
                startActivity(intetnm);
            default:
                return true;
        }
    }
}