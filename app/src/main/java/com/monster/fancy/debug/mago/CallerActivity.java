package com.monster.fancy.debug.mago;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avos.avoscloud.im.v2.messages.AVIMLocationMessage;
import com.monster.fancy.debug.dao.Friend;
import com.squareup.picasso.Picasso;

import java.util.Arrays;

import static com.monster.fancy.debug.mago.MainActivity.mClient;

public class CallerActivity extends AppCompatActivity {
    final static int CALLEE = 0;
    final static int CALLER = 1;

    private ImageView inviterImg;

    public static CallerActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;

        setContentView(R.layout.activity_caller);
        // here i change to receive the object friend, because i need to
        // show the friend's avatar
        Friend friend = (Friend) getIntent().getSerializableExtra("friend");
        String peerId = friend.getID();
        final double[] myGps = getIntent().getDoubleArrayExtra("myGps");

        inviterImg = (ImageView) findViewById(R.id.inviterAvatar_img);

        if (!TextUtils.isEmpty(friend.getPhotoUrl())) {
            Picasso.with(CallerActivity.this).load(friend.getPhotoUrl()).into(inviterImg);
        }

        mClient.createConversation(Arrays.asList(peerId), "hello", null,
                new AVIMConversationCreatedCallback() {
                    @Override
                    public void done(AVIMConversation conversation, AVIMException e) {
                        if (e == null) {
                            AVIMLocationMessage msg = new AVIMLocationMessage();
                            msg.setLocation(new AVGeoPoint(myGps[0], myGps[1]));
                            Log.d("hello", "" + myGps[0]);
                            Log.d("hello", "" + myGps[1]);
                            // 发送消息
                            conversation.sendMessage(msg, new AVIMConversationCallback() {
                                @Override
                                public void done(AVIMException e) {
                                    if (e == null) {
                                        Log.d("hello", "发送成功！");
                                    }
                                    else {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                        else {
                            e.printStackTrace();
                        }
                    }
                });

        //set animation for avatar
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.1f, 1.0f);
        alphaAnimation.setDuration(1000);
        alphaAnimation.setRepeatCount(Animation.INFINITE);
        alphaAnimation.setRepeatMode(Animation.REVERSE);
        inviterImg.setAnimation(alphaAnimation);
        alphaAnimation.start();
    }

    public void cancelCall(View view) {
        finish();
    }
}
