/* Copyright (c) 2010 ARt Project owners
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
package com.artgameweekend.projects.art;

import com.artgameweekend.projects.art.ar.wikitude.WikitudeDisplayService;
import com.artgameweekend.projects.art.util.location.LocationService;
import com.artgameweekend.projects.art.ar.POIService;
import com.artgameweekend.projects.art.ar.GenericPOI;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import java.util.List;

/**
 *
 * @author pierre
 */
public class MainActivity extends Activity implements OnClickListener
{

    public static final String SHARED_PREFS_NAME = "art.preferences";
    private static final String INTENT_DRAW_PACKAGE = "com.artgameweekend.projects.art";
    private static final String INTENT_DRAW_CLASS = INTENT_DRAW_PACKAGE + ".DrawActivity";
    private static final String INTENT_PREFERENCES_PACKAGE = "com.artgameweekend.projects.art";
    private static final String INTENT_PREFERENCES_CLASS = INTENT_PREFERENCES_PACKAGE + ".PreferencesActivity";
    private static final int DIALOG_PROGRESS = 0;
    private static final int PREFERENCES_MENU_ID = 0;
    private ImageButton mButtonDraw;
    private ImageButton mButtonDisplay;
    private ProgressThread progressThread;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.main);
        mButtonDraw = (ImageButton) findViewById(R.id.button_draw);
        mButtonDisplay = (ImageButton) findViewById(R.id.button_display);

        mButtonDraw.setOnClickListener(this);
        mButtonDisplay.setOnClickListener(this);

    }

    @Override
    protected Dialog onCreateDialog(int id)
    {
        switch (id)
        {
            case DIALOG_PROGRESS:
                progressDialog = new ProgressDialog(this);
                //               progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setMessage(getString(R.string.dialog_loading));
                progressThread = new ProgressThread(handler);
                progressThread.start();
                return progressDialog;
        }
        return null;
    }

    public void onClick(View view)
    {
        if (view == mButtonDraw)
        {
            Intent intent = new Intent();
            intent.setClassName(INTENT_DRAW_PACKAGE, INTENT_DRAW_CLASS);
            startActivity(intent);
        } else if (view == mButtonDisplay)
        {
            showDialog(DIALOG_PROGRESS);
        }
    }

    private String getAugmentedRealityBrowser()
    {
        SharedPreferences prefs = getSharedPreferences( SHARED_PREFS_NAME , MODE_PRIVATE );
        Log.d("ARt:MainActivity:Prefs", "ar_browser = " + prefs.getString("ar_browser", "not found"));
        return prefs.getString("ar_browser", "wikitude");

    }

    private boolean launchAugmentedReality()
    {
        String ARBrowser = getAugmentedRealityBrowser();

        if ("wikitude".equals(ARBrowser))
        {
            double latitude = 48.0; // default value
            double longitude = 2.0; // default value
            Location location = LocationService.getLocation(getApplicationContext());
            if (location != null)
            {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
            List<GenericPOI> list = POIService.getPOIs(latitude, longitude);
            WikitudeDisplayService.display(list, this);
            return true;
        } else if ("layar".equals(ARBrowser))
        {
            Intent intent = new Intent( Intent.ACTION_VIEW , Uri.parse("layar://artag2") );
            startActivity( intent );

        }
        return false;
    }
    final Handler handler = new Handler()
    {

        @Override
        public void handleMessage(Message msg)
        {
            boolean bOk = msg.getData().getBoolean("completed");
            dismissDialog(DIALOG_PROGRESS);
        }
    };

    private class ProgressThread extends Thread
    {

        Handler mHandler;

        ProgressThread(Handler h)
        {
            mHandler = h;
        }

        @Override
        public void run()
        {
//            Looper.prepare();
//            Looper.loop();
            boolean bLaunch = launchAugmentedReality();
            Message msg = mHandler.obtainMessage();
            Bundle b = new Bundle();
            b.putBoolean("completed", bLaunch);
            msg.setData(b);
            mHandler.sendMessage(msg);
//            Looper.myLooper().quit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        menu.add(0, PREFERENCES_MENU_ID, 0, getString(R.string.menu_preferences));
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {
            case PREFERENCES_MENU_ID:
                Intent intent = new Intent();
                intent.setClassName(INTENT_PREFERENCES_PACKAGE, INTENT_PREFERENCES_CLASS);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
