/* Copyright (c) 2010 ARTags Project owners (see http://www.artags.org)
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
package org.artags.android.app.ar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import java.text.MessageFormat;
import java.util.HashMap;
import org.artags.android.app.R;
import org.artags.android.app.ar.junaio.JunaioBrowserHandler;
import org.artags.android.app.ar.layar.LayarBrowserHandler;
import org.artags.android.app.ar.wikitude.WikitudeBrowserHandler;
import org.artags.android.app.preferences.PreferencesService;

/**
 *
 * @author Pierre Levy
 */
public class BrowserService
{

    private static BrowserService singleton = new BrowserService();
    private HashMap<String, BrowserHandler> registry = new HashMap<String, BrowserHandler>();

    private BrowserService()
    {
        BrowserHandler layar = new LayarBrowserHandler();
        BrowserHandler wikitude = new WikitudeBrowserHandler();
        BrowserHandler junaio = new JunaioBrowserHandler();

        registry.put(layar.getBrowserKey(), layar);
        registry.put(wikitude.getBrowserKey(), wikitude);
        registry.put(junaio.getBrowserKey(), junaio);
    }

    public static BrowserService instance()
    {
        return singleton;
    }

    public void startBrowser(final Activity activity)
    {
        String ARBrowser = PreferencesService.instance().getAugmentedRealityBrowser(activity);
        final BrowserHandler browser = registry.get(ARBrowser.toLowerCase());
        if (browser != null)
        {
            try
            {
                browser.startBrowser(activity);
            } catch (ActivityNotFoundException e)
            {
                Log.i("ARTags", "Browser " + browser.getBrowserDescription() + " not found !" );
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                String title = activity.getString( R.string.title_browser_not_found );
                String message = activity.getString( R.string.message_browser_not_found );
                Object[] args = { browser.getBrowserDescription() };
                builder.setTitle( MessageFormat.format(title, args));
                builder.setMessage( MessageFormat.format(message, args));
                builder.setCancelable(false);
                builder.setPositiveButton(activity.getString(R.string.button_download), new DialogInterface.OnClickListener()
                {

                    public void onClick(DialogInterface dialog, int id)
                    {
                        Uri uri = Uri.parse("market://search?q=pname:" + browser.getPackageName());
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        activity.startActivity(intent);
                        dialog.cancel();
                    }
                });
                builder.setNegativeButton(activity.getString(R.string.cancel), new DialogInterface.OnClickListener()
                {

                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.cancel();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }

        }

    }

}
