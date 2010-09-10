/* Copyright (c) 2010 ARtags Project owners (see http://artags.org)
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
import android.text.style.LineHeightSpan.WithDensity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import org.artags.android.app.ARtagsApplication;
import org.artags.android.app.R;
import java.util.List;
import org.artags.android.app.Constants;
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

        String url =  Constants.SERVER + "/client/wikitude.jsp?id=111001";
        int poiId = this.getIntent().getIntExtra( WikitudeARIntentHelper.EXTRA_INDEX_SELECTED_POI, -1);
        List<WikitudePOI> list = ((ARtagsApplication) this.getApplication()).getPOIs();
        if( (poiId >= 0) && (list != null ))
        {
            WikitudePOI poi = list.get(poiId);
            url = poi.getLink();
        }
        else
        {
            Log.d("ARtags", "Selected POI Id = " + poiId );
        }
        mWebView.loadUrl( url );

    }
}
