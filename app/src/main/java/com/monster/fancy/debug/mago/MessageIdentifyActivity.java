package com.monster.fancy.debug.mago;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MessageIdentifyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_identify);
    }

    public void back(View view) {
        finish();
    }

    public void nextStep(View view) {
        Intent intent = new Intent(MessageIdentifyActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
