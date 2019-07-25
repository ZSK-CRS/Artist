package com.mt.artist;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.just.agentweb.AgentWeb;
import com.mt.artist.view.ShareSelectPopu;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.ByteArrayOutputStream;


/**
 * Created by cenxiaozhong on 2017/5/14.
 * source code  https://github.com/Justson/AgentWeb
 */

public class AndroidInterface {

    private Handler deliver = new Handler(Looper.getMainLooper());
    private AgentWeb agent;
    private Context context;
    private View contain;


    private String mTitle = "";
    private String mDesc = "";
    private String mLink = "";
    private String mImageUrl = "";
    private IWXAPI wxapi;
    private ShareSelectPopu shareSelectPopu;

    public AndroidInterface(AgentWeb agent, Context context, View contain) {
        this.agent = agent;
        this.context = context;
        this.contain = contain;
    }


    @JavascriptInterface
    public void showShareTypeDialog(final String title, final String desc, final String link, final String imageUrl) {

        mTitle = title;
        mDesc = desc;
        mLink = link;
        mImageUrl = imageUrl;
        //弹出框
        shareSelectPopu = new ShareSelectPopu((Activity) context, itemsOnClick);
        shareSelectPopu.showAtLocation(contain, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
/*
        deliver.post(new Runnable() {
            @Override
            public void run() {
                mTitle = title;
                mDesc = desc;
                mLink = link;
                mImageUrl = imageUrl;
                //弹出框
                shareSelectPopu = new ShareSelectPopu((Activity) context, itemsOnClick);
                shareSelectPopu.showAtLocation(contain, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            }
        });

        Log.i("Info", "Thread:" + Thread.currentThread());*/

    }

    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {

        public void onClick(View v) {
            shareSelectPopu.dismiss();
            switch (v.getId()) {
                case R.id.weixinghaoyou:
                    shareMessage(0);
                    break;
                case R.id.pengyouquan:
                    shareMessage(1);
                    break;
            }
        }
    };

    public void shareMessage(int flag) {
        if (!isWeiXinAppInstall()) {
            return;
        }
        //初始化一个WXWebpageObject，填写url
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = mLink;

        //用 WXWebpageObject 对象初始化一个 WXMediaMessage 对象
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = mTitle;
        msg.description = mDesc;
        // Bitmap thumbBmp = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_logo);
        try {
            Bitmap thumbBmp = Glide.with(context).asBitmap()
                    .load(mImageUrl).submit(100, 100).get();

            msg.thumbData = bmpToByteArray(thumbBmp, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //构造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = flag == 0 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
        // req.userOpenId = getOpenId();

        //调用api接口，发送数据到微信
        wxapi.sendReq(req);

    }


    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }
        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 判断是否安装微信
     */
    public boolean isWeiXinAppInstall() {
        if (wxapi == null)
            wxapi = WXAPIFactory.createWXAPI(MyApplication.getContext(), Contant.APPID_WECHAT);
        if (wxapi.isWXAppInstalled()) {
            return true;
        } else {
            Toast.makeText(MyApplication.getContext(), "请安装微信之后进行分享", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private String buildTransaction(final String type) {

        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }
}
