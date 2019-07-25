package com.mt.artist;

import android.app.Application;

/**
 * Author : ZSK
 * Date : 2019/7/22
 * Description :
 */
public class MyApplication extends Application {

    private static MyApplication mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        ToastUtil.init(this);
        mContext = this;
    }

    public static MyApplication getContext() {
        return mContext;
    }
}
