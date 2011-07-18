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
package org.artags.android.app.ar.mixare;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import org.artags.android.app.ar.BrowserHandler;
import org.artags.android.app.util.location.LocationService;

/**
 *
 * @author Pierre Levy
 */
public class MixareBrowserHandler implements BrowserHandler
{

    private static final String URL_MIXARE_JSON_ENDPOINT = "http://art-server.appspot.com/mixare";
    private static final String MIME_TYPE = "application/mixare-json";

    /**
     * {@inheritDoc }
     */
    public String getBrowserKey()
    {
        return "mixare";
    }

    /**
     * {@inheritDoc }
     */
    public String getBrowserDescription()
    {
        return "Mixare";
    }

    /**
     * {@inheritDoc }
     */
    public void startBrowser(Activity activity)
    {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(getUrl(activity)), MIME_TYPE);
        activity.startActivity(intent);
    }

    /**
     * {@inheritDoc }
     */
    public String getPackageName()
    {
        return "org.mixare";
    }

    private String getUrl(Context context)
    {
        Location location = LocationService.getLocation(context);
        StringBuilder url = new StringBuilder(URL_MIXARE_JSON_ENDPOINT);
        url.append("?latitude=").append(location.getLatitude());
        url.append("&longitude=").append(location.getLongitude());
        return url.toString();
    }
}
