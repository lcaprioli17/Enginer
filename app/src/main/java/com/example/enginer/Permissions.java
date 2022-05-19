package com.example.enginer;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class Permissions {

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static final int REQUEST_READ_PERMISSION = 1;

    public static boolean grantPermissions(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, Activity activity){

        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if( grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(activity.getApplicationContext(), "Permission to use the microphone granted, touch the car to start classification...", Toast.LENGTH_SHORT).show();
                return true;
            }else{
                Toast.makeText(activity.getApplicationContext(), "Permission to use the microphone denied...", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        if (requestCode == REQUEST_READ_PERMISSION) {
            if( grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ){
                Toast.makeText(activity.getApplicationContext(), "Permission to read the storage granted...", Toast.LENGTH_SHORT).show();
                return true;
            }else{
                Toast.makeText(activity.getApplicationContext(), "Permission to read the storage denied...", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return false;
    }

    public static boolean checkPermissions(Activity activity, Context context, String permission, String [] sendPermission){
        if(permission == Manifest.permission.RECORD_AUDIO) {
            if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(activity, sendPermission, REQUEST_RECORD_AUDIO_PERMISSION);
                return false;
            }
        }
        if(permission == Manifest.permission.READ_EXTERNAL_STORAGE){
            if(ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                return true;
            }else{
                ActivityCompat.requestPermissions(activity, sendPermission, REQUEST_READ_PERMISSION);
                return false;
            }
        }
        return false;
    }

}
