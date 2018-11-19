package com.example.jason.watchapp;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.client.Firebase;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class MainActivity extends WearableActivity implements SensorEventListener{

    private TextView mHeartText;
    private TextView mOrientationText;
    private ImageButton btnStart;
    private ImageButton btnPause;

    private SensorManager mSensorManager;
    private Sensor mHeartRateSensor;
    private Sensor mOrientationSensor;
    private SensorEventListener listener;

    private Firebase orientation;
    private  Firebase heartRate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Firebase.setAndroidContext(this);

        mHeartText = findViewById(R.id.heart);
        mOrientationText = findViewById(R.id.orientation);
        btnStart = (ImageButton) findViewById(R.id.btnStart);
        btnPause = (ImageButton) findViewById(R.id.btnPause);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        listener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                Sensor sensor = event.sensor;
                if (sensor.getType() == Sensor.TYPE_HEART_RATE) {
                    float mHeartRateFloat = event.values[0];

                    int mHeartRate = Math.round(mHeartRateFloat);

                    mHeartText.setText("Heaart Rate: " + Integer.toString(mHeartRate));
                    heartRate.setValue(Integer.toString(mHeartRate));
                } else if (sensor.getType() == Sensor.TYPE_ORIENTATION) {
                    float mOrientationFloat = event.values[0];

                    int mOrientation = Math.round(mOrientationFloat);

                    mOrientationText.setText("Orientation: " + Integer.toString(mOrientation));
                    orientation.setValue(Integer.toString(mOrientation));
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        mOrientationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnStart.setVisibility(ImageButton.GONE);
                btnPause.setVisibility(ImageButton.VISIBLE);
                startMeasure();
            }
        });

        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnPause.setVisibility(ImageButton.GONE);
                btnStart.setVisibility(ImageButton.VISIBLE);
                stopMeasure();
            }
        });

        // Enables Always-on
        setAmbientEnabled();
    }

    private void startMeasure() {
        mSensorManager.registerListener(listener, mHeartRateSensor, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(listener, mOrientationSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    private void stopMeasure() {
        mSensorManager.unregisterListener(listener);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {}

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    protected void onStart() {
        super.onStart();
        orientation = new Firebase("https://cmpe295b-a3734.firebaseio.com/users/jason/orientation");
        heartRate = new Firebase("https://cmpe295b-a3734.firebaseio.com/users/jason/heart rate");
    }

    @Override
    public void onResume() {
        super.onResume();
        startMeasure();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopMeasure();
    }
}
