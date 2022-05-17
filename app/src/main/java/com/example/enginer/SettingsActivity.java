package com.example.enginer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    Switch simpleSwitch1;
    Button submit;
    String statusSwitch1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        simpleSwitch1 = (Switch) findViewById(R.id.switch1);
        submit= (Button) findViewById(R.id.button);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(simpleSwitch1.isChecked()){
                    statusSwitch1= "Checked";
                }
                else
                    statusSwitch1="Unchecked";
                Toast.makeText(getApplicationContext(), "Switch1 :" + statusSwitch1 + "\n", Toast.LENGTH_LONG).show(); // display the current state for switch's


            }
        });
    }
}