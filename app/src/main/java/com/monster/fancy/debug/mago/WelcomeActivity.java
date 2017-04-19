package com.monster.fancy.debug.mago;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Timer;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Timer timer = new Timer();
    }
}
