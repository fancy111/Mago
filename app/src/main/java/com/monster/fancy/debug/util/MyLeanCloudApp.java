package com.monster.fancy.debug.util;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMMessageHandler;
import com.avos.avoscloud.im.v2.AVIMMessageManager;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.monster.fancy.debug.mago.CalleeActivity;
import com.monster.fancy.debug.mago.CallerActivity;
import com.monster.fancy.debug.mago.LocaActivity;

/**
 * Created by fancy on 2017/4/27.
 */

public class MyLeanCloudApp extends Application {

    private Context ctx;

    private class CustomMessageHandler extends AVIMMessageHandler {
        //接收到消息后的处理逻辑
        @Override
        public void onMessage(AVIMMessage message, AVIMConversation conversation, AVIMClient client){
            if(message instanceof AVIMTextMessage){
                String content = ((AVIMTextMessage) message).getText();
                if(content.equals("hello")){
                    Intent intent = new Intent(ctx, CalleeActivity.class);
                    intent.putExtra("from" ,message.getFrom());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ctx.startActivity(intent);
                }
                else if(content.equals("whatsup")){
                    Intent intent = new Intent(ctx, LocaActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ctx.startActivity(intent);
                }
                else
                    Log.d("content", content);
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
        AVIMMessageManager.registerDefaultMessageHandler(new CustomMessageHandler());
    }
}
