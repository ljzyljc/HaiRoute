package com.hai.jackie.hairoute;

import android.app.Application;

import com.hai.jackie.router.EasyRouter;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        EasyRouter.getInstance().inject(this);
    }
}
