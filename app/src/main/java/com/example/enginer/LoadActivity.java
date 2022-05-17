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
    private static final int REQUEST_READ_PERMISSION = 1;
    private static final String readPermission = Manifest.permission.READ_EXTERNAL_STORAGE;
    private String [] sendReadPermission = {readPermission};

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_READ_PERMISSION) {
            if( grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ){
                openFile();
            }else{
                Toast.makeText(LoadActivity.this, "Permission to read the storage denied...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        result = findViewById(R.id.resultView);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(REQUEST_READ_PERMISSION, PackageManager.PERMISSION_GRANTED, data);

        Context context=getApplicationContext();
        if(data==null)
            return;
        Uri uri= data.getData();
        String src = uri.getPath();
        try {
            File upload = new File(src);
            classifier = AudioClassifier.createFromFile(this, MainActivity.path);
            tensor = classifier.createInputTensorAudio();
            File tmpFile = File.createTempFile(Double.toString(System.currentTimeMillis()), ".wav");
            if (!tmpFile.exists()){
                tmpFile.createNewFile();
            }
            FFmpegKit.execute("-i " + upload + " -ar 16000 -ac 1 -y " + tmpFile.getAbsolutePath());
            List<Short> wavList = new ArrayList<>();
            String s;
            if(upload.isFile())
                s = "ok";
            else
                s = "no";
            Toast.makeText(context, s,Toast.LENGTH_SHORT).show();
            LittleEndianDataInputStream dis = new LittleEndianDataInputStream(new FileInputStream(tmpFile));
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

            outputStr = "Vehicle: " + category.getLabel() + ", Score: " + category.getScore() + "\n";
            result.setText(outputStr);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            result.setText("fag1");
        } catch (IOException e) {
            e.printStackTrace();
            result.setText("fag2");
        }
    }


    public void askFile(View view){
        if(ContextCompat.checkSelfPermission(this, readPermission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, sendReadPermission, REQUEST_READ_PERMISSION);
        }else{
            openFile();
        }
    }

    public void openFile(){
        Intent intent = new Intent();
        intent.setType("audio/x-wav");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,REQUEST_READ_PERMISSION);
    }

    public void backToMain(View view) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

}