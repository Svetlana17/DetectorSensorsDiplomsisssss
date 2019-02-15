package com.arkadygamza.shakedetector;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorEventListener2;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class RecordActivity extends AppCompatActivity implements SensorEventListener2 {

    SensorManager manager;
    Button buttonStart;
    Button buttonStop;
    EditText editAlpha;
    EditText editK;
    boolean isRunning;
    final String TAG = "SensorLog";
    FileWriter writer;
    Button shareButton;
    Timer timer;
    private SensorData data = new SensorData();
    Sensor sensorAccel;
    Sensor sensorGiros;
    StringBuilder sb = new StringBuilder();
    TextView tvText;
    public String state = "DEFAULTG";
    EditText editTextShag;

    long t;
    float vx,vy,vz;
    float pxaf, pyaf, pzaf;
    float Sx, Sy, Sz;
    float Sxr, Syr, Szr;
    int descritazation;
    TextView textX, textY, textZ;
    TextView tv_accX, tv_accY, tv_accZ;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_record);
       // textX = (TextView) findViewById(R.id.textX);
//        textY = (TextView) findViewById(R.id.textY);
//        textZ = (TextView) findViewById(R.id.textZ);
//        tv_accX = (TextView) findViewById(R.id.accX);
//        tv_accY = (TextView)findViewById(R.id.accY);
//        tv_accZ = (TextView) findViewById(R.id.accZ);
        editTextShag=(EditText)findViewById(R.id.editShag);
        editTextShag.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {

                if(editable.length()>0) {
                    int gers = Integer.parseInt(editable.toString());
                    descritazation = (int) (1.0 / gers * 1000);
                    System.out.println(descritazation);
                }
            }
        });
        shareButton = (Button) findViewById(R.id.buttonShare);
        shareButton.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {
                                               share();
                                           }
                                       }
        );
        isRunning = false;
        manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        buttonStart = (Button) findViewById(R.id.buttonStart);
        buttonStop = (Button) findViewById(R.id.buttonStop);
        editAlpha = (EditText) findViewById(R.id.editAlpha);
        editK = (EditText) findViewById(R.id.editK);
        buttonStart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                buttonStart.setEnabled(false);
                buttonStop.setEnabled(true);
                try {
                    float alpha = Float.parseFloat(editAlpha.getText().toString());
                    float k = Float.parseFloat(editK.getText().toString());
                    data = new SensorData();
                    data.setParams(alpha, k);
                } catch (NumberFormatException e) {
                    Toast.makeText(RecordActivity.this, "Данные введены не верно", Toast.LENGTH_LONG).show();
                }

                File file = new File(getStorageDir(), "sensors.csv");
                if (file.exists())
                    file.delete();

                Log.d(TAG, "Writing to " + getStorageDir());
                try {
                    writer = new FileWriter(file);

                    //writer.write("TIME;ACC X;ACC Y;ACC Z;ACC XF;ACC YF;ACC ZF;GYR X; GYR Y; GYR Z; GYR XF; GYR YF; GYR ZF;\n");
                    //    writer.write("TIME;ACC X;ACC Y;ACC Z;ACC XF;ACC YF;ACC ZF;GYR X; GYR Y; GYR Z; GYR XF; GYR YF; GYR ZF;  VX);
                    writer.write("TIME;ACC X;ACC Y;ACC Z;ACC XF;ACC YF;ACC ZF;GYR X; GYR Y; GYR Z; GYR XF; GYR YF; GYR ZF;  VX; VY; VZ; VxFiltr;  VyFiltr; VzFiltr; Sx; Sy; Sz; SxR; SyR; SzR\n");

                } catch (IOException e) {
                    e.printStackTrace();
                }

                manager.registerListener(RecordActivity.this, manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),  descritazation);//было
                manager.registerListener(RecordActivity.this, manager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), descritazation);///было


                isRunning = true;
                return true;
            }
        });

        buttonStop.setOnTouchListener(new View.OnTouchListener() {
          //  @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                buttonStart.setEnabled(true);
                buttonStop.setEnabled(false);
                isRunning = false;
                manager.flush(RecordActivity.this);
                manager.unregisterListener(RecordActivity.this);
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }
        });
    }

    private String getStorageDir() {
        return this.getExternalFilesDir(null).getAbsolutePath();
    }

    @Override
    public void onFlushCompleted(Sensor sensor) {
    }

    @Override
    protected void onPause() {
        super.onPause();
        manager.unregisterListener(listener);
        timer.cancel();
    }

    String format(float values[]) {
        return String.format("%1$.1f\t\t%2$.1f\t\t%3$.1f", values[0], values[1],
                values[2]);
    }

    void showInfo() {
        sb.setLength(0);
        sb.append("Accelerometer: " + format(valuesAccel))
                .append("\n\nAccel motion: " + format(valuesAccel))
                .append("\nAccel gravity : " + format(valuesGiroscope));

//        tvText.setText(sb);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // manager.registerListener(listener, sensorAccel, (descritazation));
        // manager.registerListener(listener, sensorGiros, (descritazation));

        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //  showInfo();
                    }
                });
            }
        };
        timer.schedule(task, 0, 400);
    }
    float[] valuesAccel = new float[3];
    float[] valuesGiroscope = new float[3];
    SensorEventListener listener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent hardEvent) {
            MySensorEvent event = new MySensorEvent(hardEvent);
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    for (int i = 0; i < 3; i++) {
                        valuesAccel[i] = event.values[i];
                        float x = event.values[0];
                        float y = event.values[1];
                        float z = event.values[2];
                        textX.setText("X : " + x + " rad/s");
                        textY.setText("Y : " + y + " rad/s");
                        textZ.setText("Z : " + z + " rad/s");
                    }
                    break;
                case Sensor.TYPE_GYROSCOPE:
                    for (int i = 0; i < 3; i++) {
                        valuesGiroscope[i] = event.values[i];
                        float x = event.values[0];
                        float y = event.values[1];
                        float z = event.values[2];
                        tv_accX.setText("X Acc : " + x + " m/s2");
                        tv_accY.setText("Y Acc: " + y + " m/s2");
                        tv_accZ.setText("Z Acc: " + z + " m/s2");
                    }
                    break;
            }

        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }

    };
    long lastgiroscopeEventDate=0;
    long lastacselerometrEventDate=0;
    @Override
    public void onSensorChanged(SensorEvent event) {
        MySensorEvent evt=new MySensorEvent(event);
        if (isRunning) {
            if(t==0){
                t=System.currentTimeMillis();
            }
            long s=System.currentTimeMillis()-t;
            long date=System.currentTimeMillis();
            try {
                switch (evt.sensor.getType()) {
                    case Sensor.TYPE_GYROSCOPE:
                        if(date-lastgiroscopeEventDate<=descritazation)
                            return;
                        lastgiroscopeEventDate=date;
                        data.setGyr(evt);
                        if (data.isAccDataExists()) {
                            //  writer.write(data.getStringData(s, (int) descritazation));
                        }

                        break;
                    case Sensor.TYPE_ACCELEROMETER:
                        if(date-lastacselerometrEventDate<=descritazation)
                            return;
                        lastacselerometrEventDate=date;
                        data.setAcc(evt);
                        if (data.isGyrDataExists()) {
                            writer.write(data.getStringData(s, descritazation));
                            System.out.println("acselerometr =" );
                        }
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void share() {
        File dir = getExternalFilesDir(null);
        File zipFile = new File(dir, "accel.zip");
        if (zipFile.exists()) {
            zipFile.delete();
        }
        File[] fileList = dir.listFiles();
        try {
            zipFile.createNewFile();
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));
            for (File file : fileList) {
                zipFile(out, file);
            }
            out.close();
            sendBundleInfo(zipFile);
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Can't send file!", Toast.LENGTH_LONG).show();
        }
    }
    private static void zipFile(ZipOutputStream zos, File file) throws IOException {
        zos.putNextEntry(new ZipEntry(file.getName()));
        FileInputStream fis = new FileInputStream(file);
        byte[] buffer = new byte[10000];
        int byteCount = 0;
        try {
            while ((byteCount = fis.read(buffer)) != -1) {
                zos.write(buffer, 0, byteCount);
            }
        } finally {
            safeClose(fis);
        }
        zos.closeEntry();
    }
    private static void safeClose(FileInputStream fis) {
        try {
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void sendBundleInfo(File file) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file));
        startActivity(Intent.createChooser(emailIntent, "Send data"));
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    class SensorData {
        private MySensorEvent gyrEvent;
        private MySensorEvent accEvent;
        private float xaf, yaf, zaf;
        private float xgf, ygf, zgf;
        private float alpha = 0.05f;
        private float k = 0.5f;

        float vx,vy,vz;
        float pxaf, pyaf, pzaf;
        private MySensorEvent prefaccEvent;
        private float vxfit, vyfit, vzfit;
        public void setParams(float alpha, float k) {
            this.alpha = alpha;
            this.k = k; }
        public void setGyr(MySensorEvent gyrEvent) {
            this.gyrEvent = gyrEvent;
        }
        public void setAcc(MySensorEvent accEvent) {
           // this.prefaccEvent=this.accEvent;
           /// this.accEvent = accEvent;
            this.prefaccEvent=this.accEvent;
            this.accEvent = accEvent;
        }
        public boolean isAccDataExists() {
            return accEvent != null;
        }
        public boolean isGyrDataExists() {
            return gyrEvent != null;
        }
        public void clear() {
            gyrEvent = null;
            accEvent = null;
        }
        public String getStringData(long date, int descritazation) {
            xaf = xaf + alpha * (accEvent.values[0] - xaf);
            yaf = yaf + alpha * (accEvent.values[1] - yaf);
            zaf = zaf + alpha * (accEvent.values[2] - zaf);
            xgf = ((1-k)*gyrEvent.values[0])+(k*accEvent.values[0]);
            ygf = ((1-k)*gyrEvent.values[1])+(k*accEvent.values[1]);
            zgf = ((1-k)*gyrEvent.values[2])+(k*accEvent.values[2]);
            float sec= (float) (descritazation*1.0/1000.0);
            if(prefaccEvent!=null){
                vx= (float) ((accEvent.values[0]+prefaccEvent.values[0])/2.0*sec);
                vy= (float) ((accEvent.values[1]+prefaccEvent.values[1])/2.0*sec);
                vz= (float) ((accEvent.values[2]+prefaccEvent.values[2])/2.0*sec);
                vxfit= (float) ((xaf+pxaf)/2.0* sec);
                vyfit= (float) ((yaf+pyaf)/2.0* sec);
                vzfit= (float) ((zaf+pzaf)/2.0* sec);
                Sx=(float)(sec*vxfit);
                Sy=(float)(sec*vyfit);
                Sz=(float)(sec*vzfit);
                Sxr=(sec*vx);
                Syr=(sec*vy);
                Szr=(sec*vz);
            }
            pxaf=xaf;
            pyaf=yaf;
            pzaf=zaf;
            return String.format("%d; %f; %f; %f; %f; %f; %f; %f; %f; %f; %f; %f; %f; %f; %f; %f; %f; %f; %f;%f; %f; %f; %f; %f; %f;\n",
                    //gyrEvent.timestamp,
                    date,
                    accEvent.values[0], accEvent.values[1], accEvent.values[2], xaf,yaf,zaf,
                    gyrEvent.values[0], gyrEvent.values[1], gyrEvent.values[2], xgf, ygf, zgf,
                    vx,vy,vz, vxfit, vyfit, vzfit, Sxr, Syr, Szr, Sx, Sy, Sz);
        }
    }



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
                Intent i=new Intent(RecordActivity.this,GyroscopeActivity.class);
                startActivity(i);
                return true;
            case R.id.line_accelerometr:
                state = "accelerometr";
                Intent intent = new Intent(RecordActivity.this,MainActivity.class);
                startActivity(intent);
                return true;
            case R.id.movement:
                state="movement";
                Intent is=new Intent(RecordActivity.this,MovementActivity.class);
                startActivity(is);
            case R.id.start:
                state="start";
                Intent start=new Intent(RecordActivity.this, ActivityView.class);
                startActivity(start);
            default:
                return true;
        }
    }


}