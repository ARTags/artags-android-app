/* Copyright (c) 2010-2011 ARTags Project owners (see http://www.artags.org)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.artags.android.app.ar.wikitude;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import java.util.List;
import org.artags.android.app.ARTagsApplication;
import org.openintents.intents.WikitudeARIntentHelper;
import org.openintents.intents.WikitudePOI;

/**
 *
 * @author Pierre Levy
 */
public class WikitudeShowImageActivity extends Activity
{

    /**
     * 
     * @param icicle
     */
    @Override
    public void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN); 
         
        WebView webview = new WebView(this);
        setContentView(webview);
        webview.getSettings().setJavaScriptEnabled(true);

        final Activity activity = this;
        webview.setWebChromeClient(new WebChromeClient()
        {

            @Override
            public void onProgressChanged(WebView view, int progress)
            {
                // Activities and WebViews measure progress with different scales.
                // The progress meter will automatically disappear when we reach 100%
                activity.setProgress(progress * 1000);
            }
        });
        webview.setWebViewClient(new WebViewClient()
        {

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
            {
                Toast.makeText(activity, "Error : " + description, Toast.LENGTH_SHORT).show();
            }
        });


        String url = "";
        int poiId = this.getIntent().getIntExtra(WikitudeARIntentHelper.EXTRA_INDEX_SELECTED_POI, -1);
        List<WikitudePOI> list = ((ARTagsApplication) this.getApplication()).getPOIs();
        if ((poiId >= 0) && (list != null))
        {
            WikitudePOI poi = list.get(poiId);
            url = poi.getLink();
            Log.d("ARTags", "Selected POI Id = " + poiId);
        } else
        {
            Log.d("ARTags", "POI Id = " + poiId + " not found");
        }
        Log.d("ARTags", "Loading url : " + url);

        webview.loadUrl( url );


    }
}
