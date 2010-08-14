/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.artgameweekend.projects.art.ar.wikitude;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import com.artgameweekend.projects.art.R;
import org.openintents.intents.WikitudeARIntentHelper;

/**
 *
 * @author Android
 */
public class WikitudeShowImageActivity  extends Activity
{
    private WebView mWebView;
    @Override
    public void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN); 

        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
//        int poiId = this.getIntent().getIntExtra( WikitudeARIntentHelper.EXTRA_INDEX_SELECTED_POI, -1);

        mWebView.loadUrl("http://art-server.appspot.com/display?id=22005");

        setContentView( mWebView );
    }
}
