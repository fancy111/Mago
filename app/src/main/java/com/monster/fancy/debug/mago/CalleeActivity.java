package com.monster.fancy.debug.mago;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.monster.fancy.debug.util.MyLeanCloudApp;
import com.squareup.picasso.Picasso;

import java.util.Arrays;

import static com.monster.fancy.debug.mago.MainActivity.mClient;

public class CalleeActivity extends AppCompatActivity {
    final static int CALLEE = 0;
    final static int CALLER = 1;

    private ImageView inviteImg;
    private TextView inviterText;

    private AVIMLocationMessage message;

    private String friendId;

    private class CustomMessageHandler extends AVIMMessageHandler {
        //接收到消息后的处理逻辑
        @Override
        public void onMessage(AVIMMessage message, AVIMConversation conversation, AVIMClient client) {
            if (message instanceof AVIMTextMessage) {
                String text = ((AVIMTextMessage) message).getText();
                if (text.equals("cancelFromCaller")){
                    finish();
                    MyLeanCloudApp.isBusy = false;
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AVIMMessageManager.registerMessageHandler(AVIMMessage.class, new CustomMessageHandler());

        MyLeanCloudApp.isBusy = true;

        message = getIntent().getParcelableExtra("locationMessage");
        friendId = message.getFrom();

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
        Intent intent = new Intent(getBaseContext(), LocaActivitySim.class);
        intent.putExtra("whoAmI", CALLEE);
        intent.putExtra("locationMessage", message);
        intent.putExtra("peerId", message.getFrom());
        startActivity(intent);
        finish();
    }

    public void cancelCall(View view) {
        finish();
        MyLeanCloudApp.isBusy = false;
        mClient.createConversation(Arrays.asList(friendId), "Iambusy", null,
                new AVIMConversationCreatedCallback() {
                    @Override
                    public void done(AVIMConversation conversation, AVIMException e) {
                        if (e == null) {
                            AVIMTextMessage msg = new AVIMTextMessage();
                            msg.setText("Iambusy");
                            // 发送消息
                            conversation.sendMessage(msg, new AVIMConversationCallback() {
                                @Override
                                public void done(AVIMException e) {
                                    if (e == null) {
                                        Log.d("Iambusy", "发送成功！");
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
