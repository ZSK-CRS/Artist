package com.mt.artist;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


import static android.view.KeyEvent.KEYCODE_BACK;

public class MainActivity extends AppCompatActivity {

    private LinearLayout container;
    private IWXAPI wxapi;
    private AgentWeb mAgentWeb;


    // 定义一个变量，来标识是否退出
    private static boolean isExit = false;


    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        regToWeiXin();
        initView();    //初始化界面

        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent((LinearLayout) container, new LinearLayout.LayoutParams(-1, -1))
                .useDefaultIndicator()
                .createAgentWeb()
                .ready()
                .go("http://www.jiagtong.com/");

        mAgentWeb.getJsInterfaceHolder().addJavaObject("AndroidShare",new AndroidInterface(mAgentWeb,this,container));
    }


    private void initView() {
        container = findViewById(R.id.ll_contain);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KEYCODE_BACK)) {
            if(mAgentWeb.getWebCreator().getWebView().canGoBack()) {
                mAgentWeb.back();
                return true;
            } else {
                exit();
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    private void exit() {
        if (!isExit) {
            isExit = true;
            ToastUtil.showToastShort("再按一次退出程序");
            // 利用handler延迟发送更改状态信息
            mHandler.sendEmptyMessageDelayed(0, 500);
        } else {
            finish();
        }
    }

    public void regToWeiXin() {
        wxapi = WXAPIFactory.createWXAPI(MyApplication.getContext(), Contant.APPID_WECHAT, true);
        wxapi.registerApp(Contant.APPID_WECHAT);
    }

}
