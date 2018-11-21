package com.example.jason.watchapp;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class MainActivity extends WearableActivity implements SensorEventListener{

    public static final int REQUEST_CODE = 100;
    private TextView mHeartText;
    private TextView mOrientationText;
    private TextView mAccelerometerText;
    private TextView mStepText;

    private ImageButton btnStart;
    private ImageButton btnPause;

    private SensorManager mSensorManager;
    private Sensor mHeartRateSensor;
    private Sensor mOrientationSensor;
    private Sensor mAccelerometerSensor;
    private Sensor mStepSensor;

    private SensorEventListener listener;

    private Firebase myRootRef;
    private Firebase heartRate;
    private Firebase orientation;
    private Firebase accelerometer;
    private Firebase step;


    private String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Firebase.setAndroidContext(this);
        Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isFirstRun", true);

        if (isFirstRun) {
            Intent intent = new Intent(MainActivity.this, RegisterDeviceActivity.class);
            startActivityForResult(intent, REQUEST_CODE);
        }


        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putBoolean("isFirstRun", false).apply();

        mHeartText = findViewById(R.id.heart);
        mOrientationText = findViewById(R.id.orientation);
        mAccelerometerText = findViewById(R.id.accelerometer);
        mStepText = findViewById(R.id.step);

        btnStart = (ImageButton) findViewById(R.id.btnStart);
        btnPause = (ImageButton) findViewById(R.id.btnPause);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        mOrientationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mStepSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                userId = data.getStringExtra("userId");
                setupFirebase();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setupFirebase() {

        myRootRef = new Firebase("https://cmpe295b-a3734.firebaseio.com/users").child(userId);
        heartRate = myRootRef.child("heart rate");
        orientation = myRootRef.child("orientation");
        accelerometer = myRootRef.child("accelerometer");
        step = myRootRef.child("step");


        listener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                int sensorType = event.sensor.getType();
                switch (sensorType) {
                    case Sensor.TYPE_HEART_RATE:
                        float mHeartRateFloat = event.values[0];

                        int mHeartRate = Math.round(mHeartRateFloat);

                        mHeartText.setText("Heart Rate   " + Integer.toString(mHeartRate));
                        heartRate.setValue(Integer.toString(mHeartRate));
                        break;
                    case Sensor.TYPE_ORIENTATION:
                        float mOrientationFloat = event.values[0];

                        int mOrientation = Math.round(mOrientationFloat);

                        mOrientationText.setText("Orientation   " + Integer.toString(mOrientation));
                        orientation.setValue(Integer.toString(mOrientation));
                        break;
                    case Sensor.TYPE_ACCELEROMETER:
                        float mAcceleromrterFloat = event.values[0];

                        int mAcceleromrter = Math.round(mAcceleromrterFloat);

                        mAccelerometerText.setText("Accelerometer   " + Integer.toString(mAcceleromrter));
                        accelerometer.setValue(Integer.toString(mAcceleromrter));
                        break;
                    case Sensor.TYPE_STEP_COUNTER:
                        float mStepFloat = event.values[0];

                        int mStep = Math.round(mStepFloat);

                        mStepText.setText("Step   " + Integer.toString(mStep));
                        step.setValue(Integer.toString(mStep));

                    default:
                        Log.d("onSensorChange", "we dont have sensor data");
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {}
        };
    }
    private void startMeasure() {
        mSensorManager.registerListener(listener, mHeartRateSensor, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(listener, mOrientationSensor, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(listener, mAccelerometerSensor, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(listener, mStepSensor, SensorManager.SENSOR_DELAY_FASTEST);
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
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopMeasure();
    }
}
