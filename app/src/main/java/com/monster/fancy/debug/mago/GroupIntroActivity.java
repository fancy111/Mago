package com.monster.fancy.debug.mago;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class GroupIntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_intro);
    }

    public void back(View view) {
        finish();
    }
}
