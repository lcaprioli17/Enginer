package com.example.enginer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioRecord;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.arthenica.ffmpegkit.FFmpegKit;
import com.google.common.io.LittleEndianDataInputStream;

import org.tensorflow.lite.support.audio.TensorAudio;
import org.tensorflow.lite.support.label.Category;
import org.tensorflow.lite.task.audio.classifier.AudioClassifier;
import org.tensorflow.lite.task.audio.classifier.Classifications;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LoadActivity extends AppCompatActivity {

    TextView result;
    AudioClassifier classifier;
    TensorAudio tensor;
    AudioRecord record;
    private static final int REQUEST_READ_PERMISSION = 1;
    private String [] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
    private boolean permissionToReadAccepted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        result = findViewById(R.id.resultView);
        ActivityCompat.requestPermissions(this, permissions, REQUEST_READ_PERMISSION);
    }

    @Override
    public void onActivityResult(int requestcode, int resultcode, Intent data){
        super.onActivityResult(requestcode, resultcode, data);
        if(requestcode == REQUEST_READ_PERMISSION){

            if(resultcode == Activity.RESULT_OK && data != null){
                String realPath = null;
                Uri uriFromPath = null;
                realPath = getPathForAudio(this, data.getData());
                System.out.println("******************************************" + realPath);
                File upload = new File(realPath);
                String s;
                if(upload.isFile())
                    s = "ok";
                else
                    s = "no";
                Toast.makeText(getApplicationContext(), s,Toast.LENGTH_SHORT).show();

                //uriFromPath = Uri.fromFile(new File(realPath));

            }
        }

/*        super.onActivityResult(requestcode,resultcode,data);
        Context context=getApplicationContext();

        if(requestcode== requestcode && resultcode == Activity.RESULT_OK){
            if(data==null)
                return;
            Uri uri= data.getData();
            // System.out.println("*****************\n" + uri);
            String src = uri.getPath();
            // Toast.makeText(context,src,Toast.LENGTH_SHORT).show();
            try {
                File upload = new File(src);
                classifier = AudioClassifier.createFromFile(this, MainActivity.path);
                tensor = classifier.createInputTensorAudio();
                *//* File tmpFile = File.createTempFile(Double.toString(System.currentTimeMillis()), ".wav");
                if (!tmpFile.exists()){
                    tmpFile.createNewFile();
                }
                FFmpegKit.execute("-i " + upload + " -ar 16000 -ac 1 -y " + tmpFile.getAbsolutePath());*//*
                List<Short> wavList = new ArrayList<>();
                String s;
                if(upload.isFile())
                    s = "ok";
                else
                    s = "no";
                Toast.makeText(context, s,Toast.LENGTH_SHORT).show();
                LittleEndianDataInputStream dis = new LittleEndianDataInputStream(new FileInputStream(src));
                while(true){
                    try{
                        Short d = dis.readShort();
                        wavList.add(d);
                    }catch(EOFException e){
                        break;
                    }
                }
                int size = wavList.size();
                float floatsForInference[];
                floatsForInference = new float[size];

                for(int i = 0; i < size-1; i++)
                    floatsForInference[i] = (wavList.get(i) / 32768F);

                tensor.load(floatsForInference);
                List<Classifications> output = classifier.classify(tensor);

                Category category = output.get(1).getCategories().get(0);

                String outputStr;

                *//*textView.setText(output.get(1).getCategories().toString());*//*

                outputStr = "Vehicle: " + category.getLabel() + ", Score: " + category.getScore() + "\n";
                result.setText(outputStr);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                result.setText("fag1");
            } catch (IOException e) {
                e.printStackTrace();
                result.setText("fag2");
            }
        }*/
    }

    public void openFile(View view){
        Intent intent = new Intent();
        intent.setType("audio/x-wav");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }

    public void backToMain(View view) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_READ_PERMISSION) {
            permissionToReadAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
        }
        if (!permissionToReadAccepted ) finish();
    }

    public static String getPathForAudio(Context context, Uri uri)
    {
        String result = null;
        Cursor cursor = null;

        try {
            String[] proj = { MediaStore.Audio.Media.DATA };
            cursor = context.getContentResolver().query(uri, proj, null, null, null);
            if (cursor == null) {
                result = uri.getPath();
            } else {
                cursor.moveToFirst();
                int column_index = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA);
                result = cursor.getString(column_index);
                cursor.close();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

}