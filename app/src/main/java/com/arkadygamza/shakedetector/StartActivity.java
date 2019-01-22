package com.arkadygamza.shakedetector;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity implements  View.OnClickListener {
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener((View.OnClickListener) this);
    }
    public void onClick (View v){
        Intent intent = new Intent(StartActivity.this, ActivityView.class);
        startActivity(intent);
    }

}
