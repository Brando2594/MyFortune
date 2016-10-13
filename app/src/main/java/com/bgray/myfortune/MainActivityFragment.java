package com.bgray.myfortune;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.TextView;

public class MainActivityFragment extends Fragment {
    private float acceleration;
    private float currentAcceleration;
    private float lastAcceleration;

    // value used to determine whether user shook the device to get answer
    private static final int ACCELERATION_THRESHOLD = 100000;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        //initialize acceleration values
        acceleration = 0.00f;
        currentAcceleration = SensorManager.GRAVITY_EARTH;
        lastAcceleration = SensorManager.GRAVITY_EARTH;

        return view;
    }

    //start listening for sensor events
    @Override
    public void onResume(){
        super.onResume();
        enableAccelerometerListening(); //listen for shake event
    }

    //enable listening for accelerometer events
    private void enableAccelerometerListening(){
        //get the SensorManager
        SensorManager sensorManager =
                (SensorManager) getActivity().getSystemService(
                        Context.SENSOR_SERVICE);

        //register to listen for accelerometer events
        sensorManager.registerListener(sensorEventListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    //stop listening for accelerometer events
    @Override
    public void onPause(){
        super.onPause();
        disableAccelerometerListening(); //stop listening for shake
    }

    //disable listening for accelerometer events
    private void disableAccelerometerListening(){
        //get the SensorManager
        SensorManager sensorManager =
                (SensorManager) getActivity().getSystemService(
                        Context.SENSOR_SERVICE);

        //stop listening for accelerometer events
        sensorManager.unregisterListener(sensorEventListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
    }

    //event handler for accelerometer events
    private final SensorEventListener sensorEventListener =
            new SensorEventListener() {
                //use accelerometer to determine whether user shook device
                @Override
                public void onSensorChanged(SensorEvent event) {
                    float x = event.values[0];
                    float y = event.values[1];
                    float z = event.values[2];

                    //save previous acceleration value
                    lastAcceleration = currentAcceleration;

                    //calculate the current acceleration
                    currentAcceleration = x * x + y * y + z * z;

                    //calculate the change in acceleration
                    acceleration = currentAcceleration *
                            (currentAcceleration - lastAcceleration);

                    //if the acceleration is above a certain threshold
                    if( acceleration > ACCELERATION_THRESHOLD)
                        getAnswer();
                }

                //required method of interface SensorEventListener
                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                }
            };

    public void getAnswer(){
        final TextView answer_text = (TextView) getView().findViewById(R.id.answer_text);
        final String[] answers = {"Yes", "Yup!", "Absolutely", "Of Course!", "Definitely", "I would say... Yes!",
                "No", "No Way!", "Definitely... Not", "Doubtful", "Nope", "Nuh-Uh", "Maybe", "Not Sure", "Undecided",
                "Ask again later..."};
        int random = (int) (Math.random() * answers.length);
        answer_text.setText(answers[random]);
    }
}
