package com.mt.artist;

import android.app.Application;
import android.widget.Toast;

import java.lang.ref.WeakReference;

/**
 * Author : ZSK
 * Date : 2019/5/23
 * Description :
 */
public class ToastUtil {

    private static WeakReference<Application> mAppLication;

    private static Toast mToast = null;

    public static void init(Application application) {
        mAppLication = new WeakReference<Application>(application);
    }

    public static void showToastShort(String message) {
        if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }
        mToast = Toast.makeText(mAppLication.get(),message,Toast.LENGTH_SHORT);
        mToast.show();
    }

}
