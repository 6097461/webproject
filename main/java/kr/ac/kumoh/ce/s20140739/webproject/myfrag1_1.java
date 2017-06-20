package kr.ac.kumoh.ce.s20140739.webproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by 60974 on 2017-05-30.
 */

public class myfrag1_1 extends Activity {

    String videoID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myfrag1_1);
        Intent intent =getIntent();
        videoID = intent.getStringExtra("videoId");
        WebView web = (WebView)findViewById(R.id.webview);

        WebSettings set = web.getSettings();

        set.setJavaScriptEnabled(true);
        web.setWebViewClient(new WebViewClient());

        web.loadUrl("https://youtu.be/"+videoID);


    }




}
