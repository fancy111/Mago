package com.monster.fancy.debug.util;

import android.app.Application;

import com.avos.avoscloud.AVOSCloud;

/**
 * Created by fancy on 2017/4/27.
 */

public class MyLeanCloudApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化参数依次为 this, AppId, AppKey
        AVOSCloud.initialize(this, "xRBlai1ATNmdmRvpFtzOO4fj-gzGzoHsz", "D4hgUa86CD1X0WJ7bsbOkyc3");
        // 放在 SDK 初始化语句 AVOSCloud.initialize() 后面，只需要调用一次即可
        AVOSCloud.setDebugLogEnabled(true);
    }
}
