package com.example.enginer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import android.net.Uri;
import android.widget.TextView;

import com.google.common.io.LittleEndianDataInputStream;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class LoadActivity extends AppCompatActivity {

    private TextView result;
    private static final int REQUEST_READ_PERMISSION = 1;
    private static final String readPermission = Manifest.permission.READ_EXTERNAL_STORAGE;
    private String [] sendReadPermission = {readPermission};
    private Classification classification;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Permissions.grantPermissions(requestCode, permissions, grantResults, this);
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

        if(data==null)
            return;
        Uri uri= data.getData();

        // result.setText((uri.getPath()));
        // StringBuilder stringBuilder = new StringBuilder();
        try {
            classification = new Classification(this);
            result.setText("Classifying...");
            InputStream inputStream = getContentResolver().openInputStream(uri);
            LoadAudioThread loadAudioThread = new LoadAudioThread(inputStream, classification, result);
            loadAudioThread.execute();
            // result.setText(classification.classify(convert(inputStream)));
            /*
            BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream)));
            String line;
            while ((line = reader.readLine()) != null)
                stringBuilder.append(line);
            */
        } catch (Exception e) {
            e.printStackTrace();
        }
/*

            classification = new Classification(this);
            result.setText(classification.classify(convert(src)));

 */
    }


    public void askFile(View view){
        if(Permissions.checkPermissions(this, this, readPermission, sendReadPermission)){
            openFile();
        }
    }

    public void openFile(){
        Intent intent = new Intent();
        intent.setType("audio/x-wav");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, REQUEST_READ_PERMISSION);
    }

    public void backToMain(View view) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    public float [] convert(InputStream input) throws IOException {

        /********** Application cannot find file in the Uri passed as a Result **********/
        /*
        File upload = new File(src);
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
        Toast.makeText(this, s,Toast.LENGTH_SHORT).show();
         */
        /********** END **********/

        LittleEndianDataInputStream dis = new LittleEndianDataInputStream(input);
        List<Short> wavList = new ArrayList<>();
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

        return floatsForInference;
    }

}