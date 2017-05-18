package com.monster.fancy.debug.mago;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.avos.avoscloud.im.v2.messages.AVIMLocationMessage;

public class CalleeActivity extends AppCompatActivity {
    final static int CALLEE = 0;
    final static int CALLER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_callee);
    }

    public void answerCall(View view){
        AVIMLocationMessage message = getIntent().getParcelableExtra("locationMessage");
        Intent intent = new Intent(getBaseContext(), LocaActivity.class);
        intent.putExtra("whoAmI", CALLEE);
        intent.putExtra("locationMessage", message);
        startActivity(intent);
    }

    public void cancelCall(View view) {
        finish();
    }
}
