package com.monster.fancy.debug.mago;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.avos.avoscloud.im.v2.messages.AVIMLocationMessage;

public class CalleeActivity extends AppCompatActivity {
    final static int CALLEE = 0;
    final static int CALLER = 1;

    private ImageView inviteImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_callee);
        inviteImg = (ImageView) findViewById(R.id.inviteAvatar_img);

        //set animation for avatar
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.1f, 1.0f);
        alphaAnimation.setDuration(1000);
        alphaAnimation.setRepeatCount(Animation.INFINITE);
        alphaAnimation.setRepeatMode(Animation.REVERSE);
        inviteImg.setAnimation(alphaAnimation);
        alphaAnimation.start();
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
