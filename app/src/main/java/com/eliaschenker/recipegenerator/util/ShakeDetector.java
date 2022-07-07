package com.eliaschenker.recipegenerator.util;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * @author Elia Schenker
 * 06.07.2022
 * ShakeDetector provides an easy way to detect if the device is being shaken by the user
 */
public class ShakeDetector implements SensorEventListener {

    //Sensor objects
    private SensorManager sensorManager;
    private Sensor sensor;

    //Variables used to detect the shake
    private long lastUpdate;
    private double last_x;
    private double last_y;
    private double last_z;
    private static final int SHAKE_THRESHOLD = 800;
    private boolean shakeActive;

    //ShakeEvent provided to the class
    private final ShakeEventListener shakeEventListener;

    /**
     * Constructor of the ShakeDetector class
     * @param activity The activity serving as the context of registering the sensors
     * @param shakeEventListener A ShakeEventListener which is called when the device is shook
     */
    public ShakeDetector(Activity activity, ShakeEventListener shakeEventListener) {
        //Prepare the sensor
        sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        //Set the event listener
        this.shakeEventListener = shakeEventListener;
    }

    /**
     * Method called when the sensor values changed
     * @param event Event given by the SensorEventListener
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        long curTime = System.currentTimeMillis();
        /* Detect the shake
           Source: https://stackoverflow.com/a/5271532/10744012
         */
        if ((curTime - lastUpdate) > 100) {
            long diffTime = (curTime - lastUpdate);
            lastUpdate = curTime;

            double x = event.values[0];
            double y = event.values[0];
            double z = event.values[0];

            double speed = Math.abs(x+y+z - last_x - last_y - last_z) / diffTime * 10000;

            if (speed > SHAKE_THRESHOLD && !shakeActive) {
                shakeActive = true;
                shakeEventListener.onShakeStart();
                Log.d("sensor", "shake detected w/ speed: " + speed);
            }else if(speed < SHAKE_THRESHOLD && shakeActive) {
                shakeActive = false;
                shakeEventListener.onShakeStop();
            }
            last_x = x;
            last_y = y;
            last_z = z;
        }
    }

    /**
     * Called when the accuracy of a sensor changes.
     * Currently not implemented
     * @param sensor The sensor of which the accuracy changed
     * @param i The new accuracy
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //Not implemented
    }
}