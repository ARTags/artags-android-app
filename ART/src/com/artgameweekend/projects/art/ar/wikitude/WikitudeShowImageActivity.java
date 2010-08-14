/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.artgameweekend.projects.art.ar.wikitude;

import android.app.Activity;
import android.os.Bundle;
import android.text.style.LineHeightSpan.WithDensity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import com.artgameweekend.projects.art.ARtApplication;
import com.artgameweekend.projects.art.R;
import java.util.List;
import org.openintents.intents.WikitudeARIntentHelper;
import org.openintents.intents.WikitudePOI;

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
/*        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN); 
*/
        setContentView( R.layout.webview );
        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.getSettings().setJavaScriptEnabled(true);

        String url = "http://art-server.appspot.com/client/wikitude.jsp?id=111001";
        int poiId = this.getIntent().getIntExtra( WikitudeARIntentHelper.EXTRA_INDEX_SELECTED_POI, -1);
        List<WikitudePOI> list = ((ARtApplication) this.getApplication()).getPois();
        if( (poiId >= 0) && (list != null ))
        {
            WikitudePOI poi = list.get(poiId);
            url = poi.getLink();
        }
        else
        {
            Log.d("ARt", "Selected POI Id = " + poiId );
        }
        mWebView.loadUrl( url );

    }
}
