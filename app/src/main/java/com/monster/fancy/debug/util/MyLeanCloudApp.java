package com.monster.fancy.debug.util;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.avos.avoscloud.AVOSCloud;
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
import com.monster.fancy.debug.mago.CalleeActivity;

import java.util.Arrays;

import static com.monster.fancy.debug.mago.MainActivity.mClient;


/**
 * Created by fancy on 2017/4/27.
 */

public class MyLeanCloudApp extends Application {


    final static int CALLEE = 0;
    final static int CALLER = 1;

    static Context ctx;

    public static boolean isBusy = false;

    public class CustomMessageHandler extends AVIMMessageHandler {
        //接收到消息后的处理逻辑
        @Override
        public void onMessage(AVIMMessage message, AVIMConversation conversation, AVIMClient client) {
            if (message instanceof AVIMLocationMessage) {
                String text = conversation.getName();
                if (text == null)
                    return;
                if (text.equals("hello")){
                    if (isBusy){
                        mClient.createConversation(Arrays.asList(message.getFrom()), "Iambusy", null,
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
                    else{
                        Intent intent = new Intent(getApplicationContext(), CalleeActivity.class);
                        intent.putExtra("locationMessage", message);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplicationContext().startActivity(intent);
                    }
                }
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ctx = this;
        // 初始化参数依次为 this, AppId, AppKey
        AVOSCloud.initialize(this, "xRBlai1ATNmdmRvpFtzOO4fj-gzGzoHsz", "D4hgUa86CD1X0WJ7bsbOkyc3");
        // 放在 SDK 初始化语句 AVOSCloud.initialize() 后面，只需要调用一次即可
        AVOSCloud.setDebugLogEnabled(true);
        //注册默认的消息处理逻辑
        AVIMMessageManager.registerMessageHandler(AVIMMessage.class, new CustomMessageHandler());
    }
}
