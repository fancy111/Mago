package com.monster.fancy.debug.mago;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class SystemHelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_help);
    }

    public void back(View view) {
        finish();
    }

    public void onPageJump(View view) {
        int id = view.getId();
        Intent intent = null;
        switch (id) {
            case R.id.funcIntro_text:
                intent = new Intent(SystemHelpActivity.this, FunctionIntroActivity.class);
                startActivity(intent);
                break;
            case R.id.groupIntro_text:
                intent = new Intent(SystemHelpActivity.this, GroupIntroActivity.class);
                startActivity(intent);
                break;
        }
    }
}
