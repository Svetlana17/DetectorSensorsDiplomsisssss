package com.arkadygamza.shakedetector;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func2;

public class AccelerGyrosActivity extends AppCompatActivity implements  SensorEventListener, View.OnClickListener {

    private final List<SensorPlotterPrint> mPlotters = new ArrayList<>(3);
    private Observable<?> mShakeObservable;
    private Subscription mShakeSubscription;
    public String state = "DEFAULT";
    public Map<String, Double> increaseValue;
    EditText editValue;
    TextView linX;
    TextView linY;
    TextView linZ;
    TextView gerX;
    TextView gerY;
    TextView gerZ;
    private int VIEWPORT_SECONDS;
    TextView textView;

    SensorManager manager;
    Button buttonStart;
    Button buttonStop;
    EditText editAlpha;
    EditText editK;
    boolean isRunning;
    final String TAG = "SensorLog";
    FileWriter writer;
    Button shareButton;
    private SensorData data = new SensorData();
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  setContentView(R.layout.activity_acceler_gyros);
        setContentView(R.layout.layout_three);

        if (savedInstanceState == null) {
            VIEWPORT_SECONDS = 5;
        } else { VIEWPORT_SECONDS = (int) savedInstanceState.getSerializable("VIEWPORT_SECONDS"); }

        ///
        shareButton=(Button)findViewById(R.id.send);
        shareButton.setOnClickListener(new View.OnClickListener() {
                        @Override
            public void onClick(View v) { share(); }}
        );
        isRunning = false;
        manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        buttonStart = (Button)findViewById(R.id.button);
        buttonStop = (Button)findViewById(R.id.stop);
        editAlpha = (EditText)findViewById(R.id.editAlpha);
        editK = (EditText)findViewById(R.id.editK);

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
                    Toast.makeText(AccelerGyrosActivity.this, "Данные введены не верно", Toast.LENGTH_LONG).show();
                }

                File file = new File(getStorageDir(), "sensors.csv");
                if(file.exists())
                    file.delete();

                Log.d(TAG, "Writing to " + getStorageDir());
                try {
                    writer = new FileWriter(file);
                    writer.write("TIME;ACC X;ACC Y;ACC Z;ACC XF;ACC YF;ACC ZF;GYR X; GYR Y; GYR Z; GYR XF; GYR YF; GYR ZF;\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                manager.registerListener((SensorEventListener) AccelerGyrosActivity.this, manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), 0);
                manager.registerListener((SensorEventListener) AccelerGyrosActivity.this, manager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), 0);

                isRunning = true;
                return true;
            }
        });
        buttonStop.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                buttonStart.setEnabled(true);
                buttonStop.setEnabled(false);
                isRunning = false;
                manager.flush((SensorEventListener) AccelerGyrosActivity.this);
                manager.unregisterListener((SensorEventListener) AccelerGyrosActivity.this);
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }
        });
   // }
    increaseValue = new HashMap<>();
        increaseValue.put("X", 0.0);
        increaseValue.put("Y", 0.0);
        increaseValue.put("Z", 0.0);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<?> adapter =
                ArrayAdapter.createFromResource(this, R.array.list, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
       // textView= (TextView) findViewById(R.id.tv);
//        textView.setText(String.valueOf(VIEWPORT_SECONDS));
//        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar_both);
//        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//              //  mPlotters.get(0).changeViewPort(i);
//             //   mPlotters.get(1).changeViewPort(i);
//                if(i>0){
//                    VIEWPORT_SECONDS=i;
//                    textView.setText(String.valueOf(i));
//                }else {
//                    VIEWPORT_SECONDS=1;
//                    textView.setText("1");
//                }
//            }
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//            }
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//            restartActivity(AccelerGyrosActivity.this);
//            }
//        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        state = "DEFAULT";
                        changeState(state);
                        break;
                    case 1:
                        state = "X";
                        changeState(state);
                        break;
                    case 2:
                        state = "Y";
                        changeState(state);
                        break;
                    case 3:
                        state = "Z";
                        changeState(state);
                        break;
                    default:
                        state = "DEFAULT";
                        changeState(state);
                        break; }
                Toast.makeText(getApplicationContext(), i + " + " + l, Toast.LENGTH_SHORT).show(); }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }});
        editValue = (EditText) findViewById(R.id.value_edit);
        Button btnX = (Button) findViewById(R.id.btn_x);
        Button btnY = (Button) findViewById(R.id.btn_y);
        Button btnZ = (Button) findViewById(R.id.btn_z);
        Button btnAll = (Button) findViewById(R.id.btn_all);
        Button btnCancel = (Button) findViewById(R.id.btn_cancel);
//        linX = (TextView) findViewById(R.id.coordinats_acceler_x);
//        linY = (TextView) findViewById(R.id.coordinats_acceler_y);
//        linZ = (TextView) findViewById(R.id.coordinats_acceler_z);
//        gerX = (TextView) findViewById(R.id.coordinats_gyros_x);
//        gerY = (TextView) findViewById(R.id.coordinats_gyros_y);
//        gerZ = (TextView) findViewById(R.id.coordinats_gyros_z);


        btnX.setOnClickListener(this);
        btnY.setOnClickListener(this);
        btnZ.setOnClickListener(this);
        btnAll.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        setupPlotters();
        mShakeObservable = ShakeDetector.create(this);

        btnX.setEnabled(false);
        btnY.setEnabled(false);
        btnZ.setEnabled(false);
        btnCancel.setEnabled(false);
        btnAll.setEnabled(false);

        Observable<String> valueObservable = RxEditText.getTextWatcherObservable(editValue);
        Observable.combineLatest(valueObservable, valueObservable, new Func2<String, String, Boolean>() {
            @Override
            public Boolean call(String s, String s2) {
                if (s.isEmpty() || s2.isEmpty())
                    return false;
                else
                    return true;
            }
        }).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                btnX.setEnabled(aBoolean);
                btnY.setEnabled(aBoolean);
                btnZ.setEnabled(aBoolean);
                btnCancel.setEnabled(aBoolean);
                btnAll.setEnabled(aBoolean);
            }
        });
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
      if(VIEWPORT_SECONDS>0){
          outState.putSerializable("VIEWPORT_SECONDS", VIEWPORT_SECONDS);
      }}
    public void updateIncValue(String line, String value) {
        increaseValue.put(line, Double.valueOf(value));
        changeIncValue(increaseValue);
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
                Intent intent = new Intent(AccelerGyrosActivity.this, GyroscopeActivity.class);
                startActivity(intent);
                return true;
            case R.id.line_accelerometr:
                state = "accelerometr";
                Intent i = new Intent(AccelerGyrosActivity.this, MainActivity.class);
                startActivity(i);
                return true;
            case R.id.line_accelerometr_geroscope:
                return true;
            default:
                return true;
        }
    }
    public void changeState(String state) {
        mPlotters.get(0).setState(state);
    }
    public void changeIncValue(Map<String, Double> value) {
        mPlotters.get(0).setIncValue(value);
        mPlotters.get(1).setIncValue(value);
    }
    private void setupPlotters() {
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> linearAccSensors = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        List<Sensor> gyroscopeAccSensors = sensorManager.getSensorList(Sensor.TYPE_GYROSCOPE);
        GraphView graphViewAcc=(GraphView) findViewById(R.id.graph_accelerometr_both);
        graphViewAcc.setTitle("Акселерометр");
        GraphView graphViewGir=(GraphView) findViewById(R.id.graph_gyroscope_both);
        graphViewGir.setTitle("Гироскоп");
        mPlotters.add(new SensorPlotterPrint("LIN", graphViewAcc, SensorEventObservableFactory.createSensorEventObservable(linearAccSensors.get(0), sensorManager), state, increaseValue, this,VIEWPORT_SECONDS));
        mPlotters.add(new SensorPlotterPrint("GER", graphViewGir, SensorEventObservableFactory.createSensorEventObservable(gyroscopeAccSensors.get(0), sensorManager), state, increaseValue, this, VIEWPORT_SECONDS));
    }
    @Override
    protected void onResume() {
        super.onResume();
        Observable.from(mPlotters).subscribe(SensorPlotterPrint::onResume);
        mShakeSubscription = mShakeObservable.subscribe((object) -> Utils.beep());
    }
    @Override
    protected void onPause() {
        super.onPause();
        Observable.from(mPlotters).subscribe(SensorPlotterPrint::onPause);
        mShakeSubscription.unsubscribe();
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_x:
                updateIncValue("X", editValue.getText().toString());
                break;
            case R.id.btn_y:
                updateIncValue("Y", editValue.getText().toString());
                break;
            case R.id.btn_z:
                updateIncValue("Z", editValue.getText().toString());
                break;
            case R.id.btn_all:
                updateIncValue("X", editValue.getText().toString());
                updateIncValue("Y", editValue.getText().toString());
                updateIncValue("Z", editValue.getText().toString());
                break;
            case R.id.btn_cancel:
                updateIncValue("X", "0.0");
                updateIncValue("Y", "0.0");
                updateIncValue("Z", "0.0");
                break;
        }
    }
    public void printValueInText(SensorEvent event) {
        int type = event.sensor.getType();
        switch (type) {
            case Sensor.TYPE_ACCELEROMETER:
                linX.setText("X: " + String.format("%.2f", event.values[0]));
                linY.setText("Y: " + String.format("%.2f", event.values[1]));
                linZ.setText("Z: " + String.format("%.2f", event.values[2]));
                break;
            case Sensor.TYPE_GYROSCOPE:
                gerX.setText("X: " + String.format("%.2f", event.values[0]));
                gerY.setText("Y: " + String.format("%.2f", event.values[1]));
                gerZ.setText("Z: " + String.format("%.2f", event.values[2]));
                break;
        }
    }
    public void restartActivity(Activity activity) {
        if (Build.VERSION.SDK_INT >= 11) {
            activity.recreate();
        } else {
            activity.finish();
            activity.startActivity(activity.getIntent());
        }
    }
    private String getStorageDir() {
        return this.getExternalFilesDir(null).getAbsolutePath();
    }

    //@Override
    public void onFlushCompleted(Sensor sensor) {
    }

    //@Override
    public void onSensorChanged(SensorEvent evt) {
        if(isRunning) {
            try {
                switch(evt.sensor.getType()) {
                    case Sensor.TYPE_GYROSCOPE:
                        data.setGyr(evt);
                        if(data.isAccDataExists()){
                            writer.write(data.getStringData());
                            data.clear();
                        }

                        break;
                    case Sensor.TYPE_ACCELEROMETER:

                        data.setAcc(evt);
                        if(data.isGyrDataExists()){
                            writer.write(data.getStringData());
                            data.clear();
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
   // @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }
}
class SensorData {
    private SensorEvent gyrEvent;
    private SensorEvent accEvent;
    private float xaf, yaf, zaf;
    private float xgf, ygf, zgf;
    private float alpha = 0.05f;
    private float k = 0.5f;
    public void setParams(float alpha, float k){
        this.alpha = alpha;
        this.k = k;
    }
    public void setGyr(SensorEvent gyrEvent){
        this.gyrEvent = gyrEvent;
    }
    public void setAcc(SensorEvent accEvent){
        this.accEvent = accEvent;
    }
    public boolean isAccDataExists(){
        return accEvent != null;
    }
    public boolean isGyrDataExists(){
        return gyrEvent != null;
    }
    public void clear(){
        gyrEvent = null;
        accEvent = null;
    }
    public String getStringData(){
        xaf=xaf+alpha*(accEvent.values[0]-xaf);
        yaf=yaf+alpha*(accEvent.values[1]-yaf);
        zaf=zaf+alpha*(accEvent.values[2]-zaf);
       // xgf = 1-k*gyrEvent.values[0];
       // ygf = 1-k*gyrEvent.values[1];
       // zgf = (1-k)*gyrEvent.values[2];
        xgf = (1-k)*gyrEvent.values[0]+k*accEvent.values[0];
        ygf = (1-k)*gyrEvent.values[1]+k*accEvent.values[1];
        zgf = (1-k)*gyrEvent.values[2]+k*accEvent.values[2];
        return String.format("%d; %f; %f; %f; %f; %f; %f; %f; %f; %f; %f; %f; %f;\n", gyrEvent.timestamp,
                accEvent.values[0], accEvent.values[1], accEvent.values[2], xaf,yaf,zaf,
                gyrEvent.values[0], gyrEvent.values[1], gyrEvent.values[2], xgf, ygf, zgf);
    }
}

