package com.example.enginer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.AudioRecord;
import android.os.Bundle;
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
    int requestcode=1;
    AudioClassifier classifier;
    TensorAudio tensor;
    AudioRecord record;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        result = findViewById(R.id.resultView);
    }

    public void onActivityResult(int requestcode2, int resultcode, Intent data){
        super.onActivityResult(requestcode,resultcode,data);
        Context context=getApplicationContext();

        if(requestcode== requestcode2 && resultcode == Activity.RESULT_OK){
            if(data==null)
                return;
            Uri uri= data.getData();
            String src = uri.getPath();
            Toast.makeText(context,src,Toast.LENGTH_SHORT).show();
            try {
                File upload = new File(src);
                classifier = AudioClassifier.createFromFile(this, MainActivity.path);
                tensor = classifier.createInputTensorAudio();
                File tmpFile = File.createTempFile(Double.toString(System.currentTimeMillis()), ".wav");
                if (!tmpFile.exists()){
                    tmpFile.createNewFile();
                }
                FFmpegKit.execute("-i " + upload + " -ar 16000 -ac 1 -y " + tmpFile.getAbsolutePath());
                List<Short> wavList = new ArrayList<Short>();
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

                /*textView.setText(output.get(1).getCategories().toString());*/

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
    }

    public void openFile(View view){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setTypeAndNormalize("audio/*");
        startActivityForResult(intent,requestcode);
    }

    public void backToMain(View view) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}