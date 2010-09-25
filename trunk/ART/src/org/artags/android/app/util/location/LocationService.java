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
package org.artags.android.app.util.location;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

/**
 *
 * @author pierre@artags.org
 */
public class LocationService
{

    private static LocationManager locationManager;
    private static Location mLocation;

    public static Location getLocation(Context context)
    {
        if (mLocation == null)
        {
            mLocation = getDefaultLocation(context);
        }
        return mLocation;
    }

    public static void setLocation(Location location)
    {
        mLocation = location;
    }

    private static Location getDefaultLocation(Context context)
    {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);

        String bestProvider = manager.getBestProvider(criteria, true);
        Location location = manager.getLastKnownLocation(bestProvider);
        return location;

    }
}
