package com.monster.fancy.debug.mago;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;

import java.util.Arrays;

public class CalleeActivity extends AppCompatActivity {

    private AVIMClient mClient;
    private Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_callee);

        mClient = MainActivity.mClient;
        mIntent = getIntent();
    }

    public void answerCall(View view){
        String from = mIntent.getStringExtra("from");
        mClient.createConversation(Arrays.asList(from), "what's up", null,
                new AVIMConversationCreatedCallback() {
                    @Override
                    public void done(AVIMConversation conversation, AVIMException e) {
                        if (e == null) {
                            AVIMTextMessage msg = new AVIMTextMessage();
                            msg.setText("whatsup");
                            // 发送消息
                            conversation.sendMessage(msg, new AVIMConversationCallback() {
                                @Override
                                public void done(AVIMException e) {
                                    if (e == null) {
                                        Log.d("what's up", "发送成功！");
                                        Intent intent = new Intent(getBaseContext(), LocaActivity.class);
                                        startActivity(intent);
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
