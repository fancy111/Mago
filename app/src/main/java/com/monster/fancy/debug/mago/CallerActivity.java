package com.monster.fancy.debug.mago;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.Toast;

import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMMessageHandler;
import com.avos.avoscloud.im.v2.AVIMMessageManager;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avos.avoscloud.im.v2.messages.AVIMLocationMessage;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.monster.fancy.debug.dao.Friend;
import com.monster.fancy.debug.util.MyLeanCloudApp;
import com.squareup.picasso.Picasso;

import java.util.Arrays;

import static com.monster.fancy.debug.mago.MainActivity.mClient;

public class CallerActivity extends AppCompatActivity {
    final static int CALLEE = 0;
    final static int CALLER = 1;

    private ImageView inviterImg;

    private String peerId;
    private CountDownTimer mCounter;

    private int isJumped;

    private class CustomMessageHandler extends AVIMMessageHandler {
        //接收到消息后的处理逻辑
        @Override
        public void onMessage(AVIMMessage message, AVIMConversation conversation, AVIMClient client) {
            if (message instanceof AVIMLocationMessage) {
                String text = conversation.getName();
                if (text == null || isJumped != 0)
                    return;
                if (text.equals("whatsup")){
                    mCounter.cancel();
                    finish();
                    isJumped ++;
                    Log.d("isJumped", ""+isJumped);
                    Intent intent = new Intent(getBaseContext(), LocaActivitySim.class);
                    intent.putExtra("locationMessage", message);
                    intent.putExtra("whoAmI", CALLER);
                    intent.putExtra("peerId", message.getFrom());
                    startActivity(intent);
                }
            }
            else if (message instanceof AVIMTextMessage) {
                String text = ((AVIMTextMessage) message).getText();
                if (text.equals("Iambusy")){
                    Toast.makeText(getApplication(), "对方正忙", Toast.LENGTH_LONG).show();
                    finish();
                    MyLeanCloudApp.isBusy = false;
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_caller);

        AVIMMessageManager.registerMessageHandler(AVIMMessage.class, new CustomMessageHandler());

        MyLeanCloudApp.isBusy = true;

        isJumped = 0;

        Friend friend = (Friend) getIntent().getSerializableExtra("friend");
        peerId = friend.getID();
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

        mCounter = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                finish();
                MyLeanCloudApp.isBusy = false;
                Toast.makeText(getApplication(), "无人接听", Toast.LENGTH_LONG).show();
                mClient.createConversation(Arrays.asList(peerId), "cancelFromCaller", null,
                        new AVIMConversationCreatedCallback() {
                            @Override
                            public void done(AVIMConversation conversation, AVIMException e) {
                                if (e == null) {
                                    AVIMTextMessage msg = new AVIMTextMessage();
                                    msg.setText("cancelFromCaller");
                                    // 发送消息
                                    conversation.sendMessage(msg, new AVIMConversationCallback() {
                                        @Override
                                        public void done(AVIMException e) {
                                            if (e == null) {
                                                Log.d("cancelFromCaller", "发送成功！");
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
            }
        }.start();

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

        MyLeanCloudApp.isBusy = false;

        mClient.createConversation(Arrays.asList(peerId), "cancelFromCaller", null,
                new AVIMConversationCreatedCallback() {
                    @Override
                    public void done(AVIMConversation conversation, AVIMException e) {
                        if (e == null) {
                            AVIMTextMessage msg = new AVIMTextMessage();
                            msg.setText("cancelFromCaller");
                            // 发送消息
                            conversation.sendMessage(msg, new AVIMConversationCallback() {
                                @Override
                                public void done(AVIMException e) {
                                    if (e == null) {
                                        Log.d("cancelFromCaller", "发送成功！");
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AVIMMessageManager.unregisterMessageHandler(AVIMMessage.class, new CustomMessageHandler());
    }
}
