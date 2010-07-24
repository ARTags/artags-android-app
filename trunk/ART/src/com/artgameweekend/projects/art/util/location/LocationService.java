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
package com.artgameweekend.projects.art.util.location;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 *
 * @author pierre
 */
public class LocationService
{
    public static Location getLocation( Context context )
    {
            LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String bestProvider = manager.getBestProvider(criteria, false);
            Location location = manager.getLastKnownLocation(bestProvider);
            return location;

    }

/*
    private static LocationManager locationManager;
    private static String locationProvider = LocationManager.GPS_PROVIDER;
    private static boolean init;
    private static Location mLocation;

    public static Location getLocation(Context context)
    {
        if( ! init )
        {
            init( context );
        }
        return mLocation;


    }

    private static void init(Context context)
    {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if( locationManager != null  && locationProvider != null )
        {
            setLocation();
            locationManager.requestLocationUpdates(locationProvider, 6000, 100, new MyLocationListener());
        }
    }
    
    private static void setLocation()
    {
        Location location = locationManager.getLastKnownLocation(locationProvider);
        if( location != null )
        {
            mLocation = location;
        }
    }

    static class MyLocationListener implements LocationListener
    {

        public void onLocationChanged(Location arg0)
        {
            setLocation();

        }

        public void onStatusChanged(String arg0, int arg1, Bundle arg2)
        {
        }

        public void onProviderEnabled(String arg0)
        {
        }

        public void onProviderDisabled(String arg0)
        {
        }

    }
 *
 */
}
