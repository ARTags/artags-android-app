/* Copyright (c) 2010 ARTags Project owners (see http://artags.org)
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
package org.artags.android.app;

import org.artags.android.app.ar.wikitude.WikitudeDisplayService;
import org.artags.android.app.util.location.LocationService;
import org.artags.android.app.ar.POIService;
import org.artags.android.app.ar.GenericPOI;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import org.artags.android.app.preferences.PreferencesService;
import java.util.List;

/**
 *
 * @author pierre@artags.org
 */
public class MainActivity extends Activity implements OnClickListener
{


    private static final String INTENT_PACKAGE = "org.artags.android.app";
    private static final String INTENT_DRAW_CLASS = INTENT_PACKAGE + ".DrawActivity";
    private static final String INTENT_PREFERENCES_CLASS = INTENT_PACKAGE + ".PreferencesActivity";
    private static final String INTENT_CREDITS_CLASS = INTENT_PACKAGE + ".CreditsActivity";
    private static final String INTENT_MYLOCATION_CLASS = INTENT_PACKAGE + ".MyLocationActivity";
    private static final int DIALOG_PROGRESS = 0;
    private static final int PREFERENCES_MENU_ID = 0;
    private static final int CREDITS_MENU_ID = 1;
    private static final int MYLOCATION_MENU_ID = 2;
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
            intent.setClassName(INTENT_PACKAGE, INTENT_DRAW_CLASS);
            startActivity(intent);
        } else if (view == mButtonDisplay)
        {
            showDialog(DIALOG_PROGRESS);
        }
    }



    private boolean launchAugmentedReality()
    {
        String ARBrowser = PreferencesService.instance().getAugmentedRealityBrowser( this );

        if (PreferencesService.WIKITUDE.equalsIgnoreCase(ARBrowser))
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
        } else if (PreferencesService.LAYAR.equalsIgnoreCase(ARBrowser))
        {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse( "layar://artags" ));
            startActivity(intent);

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
            removeDialog(DIALOG_PROGRESS);
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
            Looper.prepare();
            boolean bLaunch = launchAugmentedReality();
            Message msg = mHandler.obtainMessage();
            Bundle b = new Bundle();
            b.putBoolean("completed", bLaunch);
            msg.setData(b);
            mHandler.sendMessage(msg);
            Looper.loop();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        menu.add(0, MYLOCATION_MENU_ID, 0, getString(R.string.menu_mylocation));
        menu.add(1, PREFERENCES_MENU_ID, 0, getString(R.string.menu_preferences));
        menu.add(2, CREDITS_MENU_ID, 0, getString(R.string.menu_credits));
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
                intent.setClassName(INTENT_PACKAGE, INTENT_PREFERENCES_CLASS);
                startActivity(intent);
                return true;

            case CREDITS_MENU_ID:
                Intent intentCredits = new Intent();
                intentCredits.setClassName(INTENT_PACKAGE, INTENT_CREDITS_CLASS);
                startActivity(intentCredits);
                return true;

            case MYLOCATION_MENU_ID:
                Intent intentMyLocation = new Intent();
                intentMyLocation.setClassName(INTENT_PACKAGE, INTENT_MYLOCATION_CLASS);
                startActivity(intentMyLocation );
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
