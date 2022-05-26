package com.example.enginer;

import android.os.AsyncTask;
import android.widget.TextView;

import com.google.common.io.LittleEndianDataInputStream;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class LoadAudioThread extends AsyncTask<String, String, String> {

    private String resp;
    private Classification classification;
    private InputStream inputStream;
    private TextView resultView;

    public LoadAudioThread(InputStream exInput, Classification exClass, TextView exResult){
        classification = exClass;
        inputStream = exInput;
        resultView = exResult;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            resp = classification.classify(convert(inputStream));
        } catch (Exception e) {
            e.printStackTrace();
            resp = e.getMessage();
        }
        return resp;
    }


    @Override
    protected void onPostExecute(String result) {

        resultView.setText(result);

    }


    @Override
    protected void onPreExecute() {

    }


    @Override
    protected void onProgressUpdate(String... text) {

    }

    public float [] convert(InputStream input) throws IOException {
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