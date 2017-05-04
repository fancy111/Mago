package com.monster.fancy.debug.mago;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class CallerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caller);
    }

    public void cancelCall(View view) {
        finish();
    }
}
