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
package org.artags.android.app.ar.wikitude;

import android.app.Activity;
import android.location.Location;
import java.util.List;
import org.artags.android.app.ar.BrowserHandler;
import org.artags.android.app.ar.GenericPOI;
import org.artags.android.app.ar.POIService;
import org.artags.android.app.util.location.LocationService;

/**
 *
 * @author Pierre Levy
 */
public class WikitudeBrowserHandler implements BrowserHandler
{

    private static final int MAX_POIS = 30;

    /**
     * {@inheritDoc }
     */

    public String getBrowserKey()
    {
        return "wikitude";
    }

    /**
     * {@inheritDoc }
     */

    public String getBrowserDescription()
    {
        return "Wikitude";
    }

    /**
     * {@inheritDoc }
     */

    public void startBrowser(Activity activity)
    {
        double latitude = 48.0; // default value
        double longitude = 2.0; // default value
        Location location = LocationService.getLocation(activity.getApplicationContext());
        if (location != null)
        {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
        List<GenericPOI> list = POIService.getPOIs(latitude, longitude, MAX_POIS);
        WikitudeDisplayService.display(list, activity);
    }

    /**
     * {@inheritDoc }
     */

    public String getPackageName()
    {
        return null;
    }


}
