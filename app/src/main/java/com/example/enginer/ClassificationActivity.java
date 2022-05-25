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

    private TextView textView;
    private AudioRecord record;
    private TimerTask task;
    private Timer t;
    private Classification classification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classification);

        textView = findViewById(R.id.output);
        TextView recorderSpecsTextView = findViewById(R.id.textViewAudioRecorderSpecs);
        try {
            classification = new Classification(this);
            TensorAudio.TensorAudioFormat format = classification.classifier.getRequiredTensorAudioFormat();
            String recorderSpecs = "Number Of Channels: " +  format.getChannels() + "\n" +
                    "Sample Rate: " + format.getSampleRate();
            recorderSpecsTextView.setText(recorderSpecs);
            record = classification.classifier.createAudioRecord();
            record.startRecording();
            task = new TimerTask() {
                public void run() {

                    textView.setText(classification.classify(record));

                    }
            };
            t = new Timer();
            t.schedule(task, 1, 500);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopRec(View view) {
        record.stop();
        t.cancel();
        t.purge();

        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}