package com.example.enginer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.AudioRecord;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.tensorflow.lite.support.audio.TensorAudio;
import org.tensorflow.lite.support.label.Category;
import org.tensorflow.lite.task.audio.classifier.AudioClassifier;
import org.tensorflow.lite.task.audio.classifier.Classifications;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ClassificationActivity extends AppCompatActivity {

    TextView textView;
    AudioClassifier classifier;
    TensorAudio tensor;
    AudioRecord record;
    TimerTask task;
    Timer t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classification);

        textView = findViewById(R.id.output);
        TextView recorderSpecsTextView = findViewById(R.id.textViewAudioRecorderSpecs);
        try {
            classifier = AudioClassifier.createFromFile(this, MainActivity.path );
            tensor = classifier.createInputTensorAudio();
            TensorAudio.TensorAudioFormat format = classifier.getRequiredTensorAudioFormat();
            String recorderSpecs = "Number Of Channels: " +  format.getChannels() + "\n" +
                    "Sample Rate: " + format.getSampleRate();
            recorderSpecsTextView.setText(recorderSpecs);
            record = classifier.createAudioRecord();
            record.startRecording();
            task = new TimerTask() {
                public void run() {
                    tensor.load(record);
                    List<Classifications> output = classifier.classify(tensor);

                    Category category = output.get(1).getCategories().get(0);

                    String outputStr;

                    // textView.setText(output.get(1).getCategories().toString());

                    outputStr = "Vehicle: " + category.getLabel() + ", Score: " + category.getScore() + "\n";
                    textView.setText(outputStr);

                    /*List<Category> finalOutput1 = categories;
                    runOnUiThread(() -> {
                        if (finalOutput1.isEmpty())
                            textView.setText("Could not identify the car");
                        else
                            textView.setText(outputStr.toString());*/

                    }
            };
            t = new Timer();
            t.schedule(task, 1, 500);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopRec(View view) {
        record.startRecording();
        t.cancel();
        t.purge();

        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}