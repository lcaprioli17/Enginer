package com.example.enginer;

import android.content.Context;
import android.media.AudioRecord;

import androidx.core.content.ContextCompat;

import org.tensorflow.lite.support.audio.TensorAudio;
import org.tensorflow.lite.support.label.Category;
import org.tensorflow.lite.task.audio.classifier.AudioClassifier;
import org.tensorflow.lite.task.audio.classifier.Classifications;

import java.io.IOException;
import java.util.List;

public class Classification {

    public AudioClassifier classifier;
    public TensorAudio tensor;

    public Classification(Context context) throws IOException {
        classifier = AudioClassifier.createFromFile(context, MainActivity.path );
        tensor = classifier.createInputTensorAudio();
    }

    public String classify(AudioRecord record){
        tensor.load(record);
        List<Classifications> output = classifier.classify(tensor);

        Category category = output.get(1).getCategories().get(0);

        String outputStr;

        // textView.setText(output.get(1).getCategories().toString());

        outputStr = "Vehicle: " + category.getLabel() + ", Score: " + category.getScore() + "\n";

        return outputStr;
    }

    public String classify(float [] floatsForInference){
        tensor.load(floatsForInference);
        List<Classifications> output = classifier.classify(tensor);

        Category category = output.get(1).getCategories().get(0);

        String outputStr;

        // textView.setText(output.get(1).getCategories().toString());

        outputStr = "Vehicle: " + category.getLabel() + ", Score: " + category.getScore() + "\n";

        return outputStr;
    }

}
