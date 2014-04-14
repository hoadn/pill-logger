package uk.co.pilllogger.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.LinearLayout;

import uk.co.pilllogger.R;
import uk.co.pilllogger.helpers.FeedbackHelper;
import uk.co.pilllogger.helpers.Logger;

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

        Display display = getWindowManager().getDefaultDisplay();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        Point size = new Point();
        display.getSize(size);
        int height = size.y;

        View container = findViewById(R.id.webviewcontainer);

        if(container != null)
            container.getLayoutParams().height = (int) (height * 0.75);

        boolean showFeedbackButton = intent.getBooleanExtra(getString(R.string.key_show_feedback_button), false);


        View button = findViewById(R.id.webview_button);
        if(!showFeedbackButton)
        {
            button.setVisibility(View.GONE);
        }
        else{
            final Activity activity = this;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FeedbackHelper.sendFeedbackIntent(activity);
                }
            });
        }
    }

    @Override
    public void onDestroy(){

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);

            int version = pInfo.versionCode;

            SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(this).edit();
            edit.putInt(getString(R.string.seenVersionKey), version);
            edit.commit();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        super.onDestroy();
    }
}