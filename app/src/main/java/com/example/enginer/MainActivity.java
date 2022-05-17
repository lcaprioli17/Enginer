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

    public static final Float probabilityThreshold = 0.3f;
    public static final String path= "secondary_car_model.tflite";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static final String recPermission = Manifest.permission.RECORD_AUDIO;
    private String [] sendRecPermission = {recPermission};
    private SensorManager sensorManager;
    private SensorEventListener listener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                if ((sensorEvent.values[0] - 9.81 > 1) || (sensorEvent.values[1] - 9.81 > 1) || (sensorEvent.values[2] - 9.81 > 1)) {
                    startRec();
                    sensorManager.unregisterListener(listener);
                }
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // unused
        }
    };

    //Audio recording permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if( grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(MainActivity.this, "Permission to use the microphone granted, touch the car to start classification...", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(MainActivity.this, "Permission to use the microphone denied...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sensorManager=(SensorManager) getSystemService(SENSOR_SERVICE);
        askRec();
        if(ContextCompat.checkSelfPermission(this, recPermission) == PackageManager.PERMISSION_GRANTED)
            sensorManager.registerListener(listener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    // Request permission to record
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void askRec(View view) {
        if(ContextCompat.checkSelfPermission(this, recPermission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, sendRecPermission, REQUEST_RECORD_AUDIO_PERMISSION);
        }else{
            startRec();
        }
    }

    public void askRec() {
        if(ContextCompat.checkSelfPermission(this, recPermission) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, sendRecPermission, REQUEST_RECORD_AUDIO_PERMISSION);
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