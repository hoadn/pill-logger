package uk.co.pilllogger.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

import uk.co.pilllogger.R;

/**
 * Created by alex on 30/10/2013.
 */
public class WebViewActivity extends PillLoggerActivityBase {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        Intent intent = getIntent();
        String url = intent.getStringExtra(getString(R.string.key_web_address));

        if(url != null){
            WebView webView = (WebView)findViewById(R.id.webView);
            webView.loadUrl(url);
        }
    }
}