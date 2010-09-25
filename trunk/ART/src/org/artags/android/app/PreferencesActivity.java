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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import org.artags.android.app.preferences.PreferencesService;

/**
 *
 * @author pierre@artags.org
 */
public class PreferencesActivity extends Activity implements OnClickListener
{

    private TextView mBrowser;
    private Button mChangeBrowserButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.preferences);

        mBrowser = (TextView) findViewById(R.id.preferences_browser);
        mChangeBrowserButton = (Button) findViewById(R.id.preferences_change_browser_button);

        mChangeBrowserButton.setOnClickListener(this);

        mBrowser.setText(PreferencesService.instance().getAugmentedRealityBrowser(this));

    }

    public void onClick(View button)
    {
        if (button == mChangeBrowserButton)
        {
            selectBrowser();
        }
    }

    private void selectBrowser()
    {
        final String[] browsers =
        {
            PreferencesService.LAYAR , PreferencesService.WIKITUDE
        };

        new AlertDialog.Builder(PreferencesActivity.this).setItems(browsers, new DialogInterface.OnClickListener()
        {

            public void onClick(DialogInterface dialog, int which)
            {
                String browser = browsers[which];
                mBrowser.setText(browser);
                PreferencesService.instance().setAugmentedRealityBrowser(PreferencesActivity.this, browser);

            }
        }).create().show();
    }


    /*
    getPreferenceManager().setSharedPreferencesName( MainActivity.SHARED_PREFS_NAME);

    // Load the preferences from an XML resource
    addPreferencesFromResource(R.xml.preferences);
    getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
     */
}
/*
@Override
protected void onDestroy() {
getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
super.onDestroy();
}


public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,  String key)
{
}
 */


