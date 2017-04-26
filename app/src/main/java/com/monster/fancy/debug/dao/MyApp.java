package com.monster.fancy.debug.dao;

import android.app.Application;

/**
 * Created by fancy on 2017/4/24.
 */
public class MyApp extends Application {
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
