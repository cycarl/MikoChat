package com.xana.mikochat.app;

import android.content.Context;

import com.igexin.sdk.PushManager;
import com.xana.mikochat.common.app.Application;
import com.xana.mikochat.factory.Factory;

public class App extends Application {

    public static final int[] bgList = {
            R.drawable.bg_launch_alpha,
            R.drawable.bg_launch_beta,
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Factory.setup();
        PushManager.getInstance().initialize(this);
    }

    @Override
    protected void showLogin(Context context) {

    }
}
