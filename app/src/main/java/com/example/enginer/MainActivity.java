package com.example.enginer;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.media.AudioRecord;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import static android.Manifest.permission.CAPTURE_AUDIO_OUTPUT;
import static android.Manifest.permission.RECORD_AUDIO;

import org.checkerframework.checker.units.qual.A;
import org.tensorflow.lite.support.audio.TensorAudio;
import org.tensorflow.lite.task.audio.classifier.AudioClassifier;
import org.tensorflow.lite.task.audio.classifier.Classifications;
import org.tensorflow.lite.task.core.BaseOptions;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    // public static final Float probabilityThreshold = 0.3f;
    public static final String path= "secondary_car_model.tflite";
    private static final String recPermission = Manifest.permission.RECORD_AUDIO;
    private String [] sendRecPermission = {recPermission};
    private SensorManager sensorManager;
    private Switch simpleSwitch1;

    private SensorEventListener listener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
                if ((sensorEvent.values[0] > 1) || (sensorEvent.values[1] > 1) || (sensorEvent.values[2] > 1)) {
                    startRec();
                    sensorManager.unregisterListener(listener);
                }
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) { }
    };

    //Audio recording permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(!Permissions.grantPermissions(requestCode, permissions, grantResults, this)){
            simpleSwitch1.setChecked(false);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        simpleSwitch1 = (Switch) findViewById(R.id.switch1);
        simpleSwitch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    askRec();
                    sensorManager.registerListener(listener, sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_NORMAL);
                } else {
                    sensorManager.unregisterListener(listener);
                }
            }
        });

        sensorManager=(SensorManager) getSystemService(SENSOR_SERVICE);
    }

    // Request permission to record
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void askRec(View view) {
        if(Permissions.checkPermissions(this, this, recPermission, sendRecPermission))
            startRec();
    }

    public void askRec() {
        Permissions.checkPermissions(this, this, recPermission, sendRecPermission);
    }

    public void startRec(){
        Intent i = new Intent(this, ClassificationActivity.class);
        startActivity(i);
    }

    public void loadFile(View view) {
        Intent i = new Intent(this, LoadActivity.class);
        startActivity(i);
    }

}