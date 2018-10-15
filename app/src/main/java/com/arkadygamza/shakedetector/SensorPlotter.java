package com.arkadygamza.shakedetector;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.hardware.SensorEvent;
import android.support.annotation.NonNull;
import android.widget.SeekBar;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Map;

import rx.Observable;
import rx.Subscription;

/**
 * Draws graph of sensor events
 */
public class SensorPlotter {
    public static final int MAX_DATA_POINTS = 50;
    private int VIEWPORT_SECONDS;
    public static final int FPS = 10;

    @NonNull
    private final String mName;

    private final long mStart = System.currentTimeMillis();

    protected final LineGraphSeries<DataPoint> mSeriesXs;
    protected final LineGraphSeries<DataPoint> mSeriesXf;
    protected final LineGraphSeries<DataPoint> mSeriesYs;
    protected final LineGraphSeries<DataPoint> mSeriesYf;
    protected final LineGraphSeries<DataPoint> mSeriesZs;
    protected final LineGraphSeries<DataPoint> mSeriesZf;
    private final Observable<SensorEvent> mSensorEventObservable;
    private long mLastUpdated = mStart;
    private Subscription mSubscription;
    private String state;
    private Map<String,Double> incValue;
    private float[] output;
    public SensorPlotter(@NonNull String name, @NonNull  GraphView graphView,
                         @NonNull Observable<SensorEvent> sensorEventObservable,String state,Map<String,Double> incValue) {
        this.incValue = incValue;
        this.state = state;
        mName = name;
        mSensorEventObservable = sensorEventObservable;

        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMinX(-20);
        graphView.getViewport().setMaxX(VIEWPORT_SECONDS * 1000); // number of ms in viewport

        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setMinY(-20);
        graphView.getViewport().setMaxY(20);

       // graphView.getGridLabelRenderer().setHorizontalLabelsVisible(true);
      //  graphView.getGridLabelRenderer().setVerticalLabelsVisible(false);
        graphView.getLegendRenderer().setVisible(true);
        graphView.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        mSeriesXs = new LineGraphSeries<>();
        mSeriesXf = new LineGraphSeries<>();
        mSeriesYs = new LineGraphSeries<>();
        mSeriesYf = new LineGraphSeries<>();
        mSeriesZs = new LineGraphSeries<>();
        mSeriesZf = new LineGraphSeries<>();

        mSeriesXs.setColor(Color.BLACK);
        mSeriesXs.setTitle("X");
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        paint.setPathEffect(new DashPathEffect(new float[]{8, 5}, 0));
        mSeriesXs.setCustomPaint(paint);

        mSeriesXf.setColor(Color.YELLOW);
        mSeriesXf.setTitle("XF");
        Paint paints = new Paint();
        paints.setStyle(Paint.Style.STROKE);
        paints.setStrokeWidth(10);
        paint.setPathEffect(new DashPathEffect(new float[]{8, 5}, 0));
        mSeriesXf.setCustomPaint(paints);

        mSeriesYs.setColor(Color.GREEN);
        mSeriesYs.setTitle("Y");
        Paint paintss = new Paint();
        paintss.setStyle(Paint.Style.STROKE);
        paintss.setStrokeWidth(10);
        paint.setPathEffect(new DashPathEffect(new float[]{8, 5}, 0));
        mSeriesXf.setCustomPaint(paintss);



        mSeriesYf.setColor(Color.GRAY);
        mSeriesYf.setTitle("YF");
   //     Paint paintss = new Paint();
//        paintsss.setStyle(Paint.Style.STROKE);
//        paintsss.setStrokeWidth(10);
//        paint.setPathEffect(new DashPathEffect(new float[]{8, 5}, 0));
//        mSeriesYf.setCustomPaint(paintsss);
//
        mSeriesZs.setColor(Color.BLUE);
        mSeriesZs.setTitle("Z");
        //  Paint paintsss = new Paint();
//        paintsss.setStyle(Paint.Style.STROKE);
//        paintsss.setStrokeWidth(10);
//        paint.setPathEffect(new DashPathEffect(new float[]{8, 5}, 0));
//        mSeriesZs.setCustomPaint(paintsss);
//

        mSeriesZf.setColor(Color.CYAN);
        mSeriesZf.setTitle("ZF");
        Paint paintd = new Paint();
//        paintd.setStyle(Paint.Style.STROKE);
//        paintd.setStrokeWidth(10);
//        paint.setPathEffect(new DashPathEffect(new float[]{8, 5}, 0));
//        mSeriesZf.setCustomPaint(paintd);

        graphView.addSeries(mSeriesXs);
        Paint paintds = new Paint();
//        paintds.setStyle(Paint.Style.STROKE);
//        paintds.setStrokeWidth(10);
//        paint.setPathEffect(new DashPathEffect(new float[]{8, 5}, 0));
//        mSeriesZf.setCustomPaint(paintd);
        graphView.addSeries(mSeriesXf);
        graphView.addSeries(mSeriesYs);
        graphView.addSeries(mSeriesYf);
        graphView.addSeries(mSeriesZs);
        graphView.addSeries(mSeriesZf);
    }
    public SensorPlotter(@NonNull String name, @NonNull  GraphView graphView,
                         @NonNull Observable<SensorEvent> sensorEventObservable,String state,Map<String,Double> incValue, int v) {
        this.incValue = incValue;
        this.state = state;
        this.VIEWPORT_SECONDS=v; System.out.println(VIEWPORT_SECONDS + "!!!!!!!!");
        mName = name;
        mSensorEventObservable = sensorEventObservable;

        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMinX(0);
        graphView.getViewport().setMaxX(VIEWPORT_SECONDS * 1000); // number of ms in viewport

        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setMinY(-20);
        graphView.getViewport().setMaxY(20);

        graphView.getGridLabelRenderer().setHorizontalLabelsVisible(true);
        graphView.getGridLabelRenderer().setVerticalLabelsVisible(true);

        mSeriesXs = new LineGraphSeries<>();
        mSeriesXf = new LineGraphSeries<>();
        mSeriesYs = new LineGraphSeries<>();
        mSeriesYf = new LineGraphSeries<>();
        mSeriesZs = new LineGraphSeries<>();
        mSeriesZf = new LineGraphSeries<>();

        mSeriesXs.setColor(Color.RED);
        mSeriesXs.setTitle("X");
        mSeriesXs.getDataPointsRadius();
        mSeriesXs.setDataPointsRadius(20);
        mSeriesXf.setColor(Color.YELLOW);
        mSeriesXf.setTitle("Ygggggggggggggggggggggggggggggggggggggggggggggggggggg");
        mSeriesYs.setColor(Color.GREEN);
        mSeriesYf.setColor(Color.GRAY);
        mSeriesZs.setColor(Color.BLUE);
        mSeriesZf.setColor(Color.CYAN);

        graphView.addSeries(mSeriesXs);
        graphView.addSeries(mSeriesXf);
        graphView.addSeries(mSeriesYs);
        graphView.addSeries(mSeriesYf);
        graphView.addSeries(mSeriesZs);
        graphView.addSeries(mSeriesZf);
    }


    public void onResume(){
        mSubscription = mSensorEventObservable.subscribe(this::onSensorChanged);
    }

    public void onPause(){
        mSubscription.unsubscribe();
    }

    private void onSensorChanged(SensorEvent event) {
        if (!canUpdateUi()) {
            return;
        }
        switch (state) {
            case "X":
                appendData(mSeriesXs, event.values[0]);


               appendData(mSeriesXf, incValue.get("X")+(1-incValue.get("X"))*event.values[0]);
               //appendData(mSeriesXf,   incValue.get("X") * output[0] + (1 - incValue.get("X")) * event.values[0]);




                break;
            case "XG":
                appendData(mSeriesXs, event.values[0]);

                appendData(mSeriesXf, 1-incValue.get("X")*event.values[0]);
                break;
            case "Y":
                appendData(mSeriesYs, event.values[1]);

                appendData(mSeriesYf, incValue.get("Y")+(1-incValue.get("Y"))*event.values[1]);
                break;
            case "YG":
                appendData(mSeriesYs, event.values[1]);

                appendData(mSeriesYf, 1-incValue.get("X")*event.values[1]);

                break;
            case "Z":
                appendData(mSeriesZs, event.values[2]);

                appendData(mSeriesZf, incValue.get("Z")+(1-incValue.get("Z"))*event.values[2]);

                break;
            case "ZG":
                appendData(mSeriesZs, event.values[0]);

                appendData(mSeriesZf, 1-incValue.get("Z")*event.values[2]);

                break;
            case "DEFAULT":
                appendData(mSeriesXs, event.values[0]);
                appendData(mSeriesXf, event.values[0] + incValue.get("X"));
                appendData(mSeriesYs, event.values[1]);
                appendData(mSeriesYf, event.values[1] + incValue.get("Y"));
                appendData(mSeriesZs, event.values[2]);
                appendData(mSeriesZf, event.values[2] + incValue.get("Z"));
                break;
            case "DEFAULTG":
                appendData(mSeriesXs, event.values[0]);
                appendData(mSeriesXf, event.values[0] + incValue.get("X"));
                appendData(mSeriesYs, event.values[1]);
                appendData(mSeriesYf, event.values[1] + incValue.get("Y"));
                appendData(mSeriesZs, event.values[2]);
                appendData(mSeriesZf, event.values[2] + incValue.get("Z"));
                break;

        }
    }
//    public  double averageAcelerometr(double valueX){
//        valueX=incValue.get("X");
//    }
    public double average(double valueX) {
        // valueX=alpha*valueX+(1-alpha)*incValue.get("X");

        return valueX;
    }
    /*
      public double average(double valueX) {
        if (oldValueX == null) {
            oldValueX = valueX;
            //  return value;
        }
        double newValueX=oldValueX+alpha*(valueX-oldValueX);
                //alpha*value+(1-alpha)*oldValue;
        oldValueX = newValueX;
        return newValueX;
    }


    public double averageY(double valueY) {
        if (oldValueY == null) {
            oldValueY= valueY;
            //  return value;
        }
        double newValueY=oldValueY+alpha*(valueY-oldValueY);
        //alpha*value+(1-alpha)*oldValue;
        oldValueY= newValueY;
        return newValueY;
    }

    public double averageZ(double valueZ) {
        if (oldValueZ == null) {
            oldValueZ= valueZ;
            //  return value;
        }
        double newValueZ=oldValueZ+alpha*(valueZ-oldValueZ);
        //alpha*value+(1-alpha)*oldValue;
        oldValueZ= newValueZ;
        return newValueZ;
    }

     */

    private boolean canUpdateUi() {
        long now = System.currentTimeMillis();
        if (now - mLastUpdated < 1000 / FPS) {
            return false;
        }
        mLastUpdated = now;
        return true;
    }

    private void appendData(LineGraphSeries<DataPoint> series, double value) {
        series.appendData(new DataPoint(getX(), value), true, MAX_DATA_POINTS);
    }

    public void setState(String s) {
        this.state = s;
    }

    public void  setIncValue(Map<String,Double> v) {
        this.incValue = v;
    }
    private long getX() {
        return System.currentTimeMillis() - mStart;
    }

    public void changeViewPort(int v) {
        this.VIEWPORT_SECONDS = v;
        System.out.println(VIEWPORT_SECONDS);
    }

    public void getViewPort(int v) {

    }
}
