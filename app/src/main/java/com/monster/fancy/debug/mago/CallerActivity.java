package com.monster.fancy.debug.mago;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avos.avoscloud.im.v2.messages.AVIMLocationMessage;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;

import java.util.Arrays;

import static com.monster.fancy.debug.mago.MainActivity.mClient;

public class CallerActivity extends AppCompatActivity {
    final static int CALLEE = 0;
    final static int CALLER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caller);
        String peerId = getIntent().getStringExtra("peerId");
        final double[] myGps = getIntent().getDoubleArrayExtra("myGps");

        mClient.createConversation(Arrays.asList(peerId), "hello", null,
                new AVIMConversationCreatedCallback() {
                    @Override
                    public void done(AVIMConversation conversation, AVIMException e) {
                        if (e == null) {
                            AVIMLocationMessage msg = new AVIMLocationMessage();
                            msg.setLocation(new AVGeoPoint(myGps[0], myGps[1]));
                            // 发送消息
                            conversation.sendMessage(msg, new AVIMConversationCallback() {
                                @Override
                                public void done(AVIMException e) {
                                    if (e == null) {
                                        Log.d("hello", "发送成功！");
                                    }
                                }
                            });
                        }
                    }
                });
    }

    public void cancelCall(View view) {
        finish();
    }
}
