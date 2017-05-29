package com.monster.fancy.debug.mago;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.im.v2.messages.AVIMLocationMessage;
import com.squareup.picasso.Picasso;

public class CalleeActivity extends AppCompatActivity {
    final static int CALLEE = 0;
    final static int CALLER = 1;

    private ImageView inviteImg;
    private TextView inviterText;

    private AVIMLocationMessage message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        message = getIntent().getParcelableExtra("locationMessage");
        String friendId = message.getFrom();

        setContentView(R.layout.activity_callee);

        inviteImg = (ImageView) findViewById(R.id.inviteAvatar_img);
        inviterText = (TextView) findViewById(R.id.inviteUsername_text);

        //set the inviter's avatar and username
        AVQuery<AVUser> avQuery = new AVQuery<>("_User");
        avQuery.getInBackground(friendId, new GetCallback<AVUser>() {
                    @Override
                    public void done(AVUser avUser, AVException e) {
                        if(e == null){
                            inviterText.setText(avUser.getUsername());
                            if(avUser.getAVFile("avatar") !=null )
                            Picasso.with(CalleeActivity.this).load(avUser.getAVFile("avatar").getUrl()).into(inviteImg);

                        }
                        else {
                            Log.e("callee friend err:",e.getMessage());
                        }
                    }
                });

        //set animation for avatar
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.1f, 1.0f);
        alphaAnimation.setDuration(1000);
        alphaAnimation.setRepeatCount(Animation.INFINITE);
        alphaAnimation.setRepeatMode(Animation.REVERSE);
        inviteImg.setAnimation(alphaAnimation);
        alphaAnimation.start();
    }

    public void answerCall(View view){
        Intent intent = new Intent(getBaseContext(), LocaActivity.class);
        intent.putExtra("whoAmI", CALLEE);
        intent.putExtra("locationMessage", message);
        intent.putExtra("peerId", message.getFrom());
        startActivity(intent);
        finish();
    }

    public void cancelCall(View view) {
        finish();
    }
}
