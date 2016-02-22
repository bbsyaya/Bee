package com.lingshi321.bee;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.lingshi321.bee.common.HttpClientSingleton;
import com.lingshi321.bee.util.DataInterfaceConstants;

import org.apache.http.cookie.Cookie;

import java.util.List;

/**
 * Created by Administrator on 2015/12/11.
 */
public class WebViewActivity extends Activity {

    private WebView webView;
    private ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_webview);

        webView = (WebView) findViewById(R.id.webview);
        pb = (ProgressBar) findViewById(R.id.more_progress);

        List<Cookie> cookies = HttpClientSingleton.getHttpClient().getCookieStore().getCookies();
//        Log.i("******************", cookies.toString() + "####" + cookies.get(0).getValue());


        synCookies(WebViewActivity.this, "www.lingshi321.com/",cookies.toString());

        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("http://www.lingshi321.com:8888/admin");
        webView.setWebViewClient(new MyWebViewClient());
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        //开启 Application Caches 功能
        webView.getSettings().setAppCacheEnabled(true);
//        webView.getSettings().setBuiltInZoomControls(true);



    }


    public static void synCookies(Context context,String url,String cookies) {
        CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeSessionCookie();//移除
        cookieManager.setCookie(url, cookies);//cookies是在HttpClient中获得的cookie
        CookieSyncManager.getInstance().sync();
    }


    class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            // 页面上有数字会导致连接电话
            if (url.indexOf("tel:") < 0) {

                view.loadUrl(url);

            }

            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
//            CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(WebViewActivity.this);
//            cookieSyncManager.sync();
//            CookieManager cookieManager = CookieManager.getInstance();
//            Log.i("$$$$$$$",""+cookieManager.getCookie("http://www.lingshi321.com/"));
            pb.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            pb.setVisibility(View.VISIBLE);
        }

    }

//    @Override
//    public boolean onKeyDown(int keyCoder, KeyEvent event) {
//        if (webView.canGoBack() && keyCoder == KeyEvent.KEYCODE_BACK) {
//            webView.goBack(); // goBack()表示返回webView的上一页面
//            return true;
//        }
//        return false;
//    }
}
