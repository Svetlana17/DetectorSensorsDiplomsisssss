package com.arkadygamza.shakedetector;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Date;

import rx.Observable;
import rx.android.MainThreadSubscription;

/**
 * Allows to treat sensor events as Observable
 */
public class SensorEventObservableFactory {
    private  android.hardware.Sensor sensor ;
    private SensorEventListener listener;
    private android.hardware.SensorManager SensorManager;
    public void updateperiod(int peroid){
        this.SensorManager.unregisterListener(this.listener);
        this.SensorManager.registerListener(this.listener, sensor, peroid);
    };
    public  Observable<SensorEvent> createSensorEventObservable(@NonNull Sensor sensor, @NonNull SensorManager sensorManager) {
       this.SensorManager=sensorManager;
       this.sensor=sensor;
       return Observable.create(subscriber -> {
            MainThreadSubscription.verifyMainThread();
             listener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    if (subscriber.isUnsubscribed()) {
                        return;
                    }
                    Log.i ("SensorEvent", " " + new Date().getTime());
                            subscriber.onNext(event);
                }
                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                    // NO-OP
                }
            };
            //sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_GAME);
            sensorManager.registerListener(listener, sensor, 1000);///
            ///4 параметр// максимальной задержкой передачи.

            // unregister listener in main thread when being unsubscribed
            subscriber.add(new MainThreadSubscription() {
                @Override
                protected void onUnsubscribe() {
                    sensorManager.unregisterListener(listener);
                }
            });
        });
    }
}
