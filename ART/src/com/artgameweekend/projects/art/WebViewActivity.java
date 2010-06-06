/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.artgameweekend.projects.art;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebView;
import com.zmosoft.flickrfree.JavaMD5Sum;
import com.zmosoft.flickrfree.RestClient;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author Android
 */
public class WebViewActivity extends Activity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        /*requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        WebView webview = new WebView(this);
        setContentView(webview);

        // Generate the signature
        String signature = "";
        signature = RestClient.m_secret;

        signature += "api_key" + RestClient.m_apikey + "frob" + RestClient.m_frob + "permswrite";

        try {
            signature = JavaMD5Sum.computeSum(signature).toLowerCase();
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }



        String url = "http://www.flickr.com/services/auth/";

        url += "?api_key=" + RestClient.m_apikey;
        url += "&perms=write";
        url += "&frob=" + RestClient.m_frob;
        url += "&api_sig=" + signature;
        webview.loadUrl(url);

        //setContentView(R.layout.splash);

        /*mImageView = (ImageView) findViewById(R.id.splash);
        mImageView.setOnClickListener(this);*/


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Log.d(this.getClass().getName(), "back button pressed");
            FlickrUploader.uploadFile();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    /*@Override
    public void   onStop()
    {
    Log.i("OMGWTFBBQ","onStop");

    Log.i("OMGWTFBBQ","frob = "+RestClient.m_frob);
    if(!RestClient.m_frob.equals(""))
    {
    FlickrUploader.uploadFile();
    }
    super.onStop();
    }*/
}
