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
package org.artags.android.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import org.artags.android.app.util.location.LocationService;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Pierre Levy
 */
public class MyLocationActivity extends Activity implements OnClickListener, LocationListener
{
    public static final int MYLOCATION_VALIDATE = 0;
    public static final int MYLOCATION_CANCEL = 1;
    private LocationManager mLocationManager;
    private Location mLocation;
    private String mProvider = "";
    private TextView mLatitude;
    private TextView mLongitude;
    private TextView mAddress;
    private Button mButtonValidate;
    private Button mButtonSelectSource;
    private Button mButtonGetPosition;
    private Button mButtonCancel;

//    private Button mSelectOnMapButton;
//    private Button mDefineAddressButton;
    private ProgressBar mProgressBar;

    @Override
    public void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.my_location);

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        mLatitude = (TextView) findViewById(R.id.mylocation_latitude);
        mLongitude = (TextView) findViewById(R.id.mylocation_longitude);
        mAddress = (TextView) findViewById(R.id.mylocation_address);
        mButtonValidate = (Button) findViewById(R.id.mylocation_validate_button);
        mButtonCancel = (Button) findViewById(R.id.mylocation_cancel_button);
        mButtonSelectSource = (Button) findViewById(R.id.mylocation_select_source_button);
        mButtonGetPosition = (Button) findViewById(R.id.mylocation_get_position_button);
//        mSelectOnMapButton = (Button) findViewById(R.id.mylocation_select_on_map_button);
//        mDefineAddressButton = (Button) findViewById(R.id.mylocation_enter_position_button);
        mProgressBar = (ProgressBar) findViewById( R.id.mylocation_progress );

        mButtonValidate.setOnClickListener(this);
        mButtonCancel.setOnClickListener(this);
        mButtonSelectSource.setOnClickListener(this);
        mButtonGetPosition.setOnClickListener(this);
//        mSelectOnMapButton.setOnClickListener(this);
//        mDefineAddressButton.setOnClickListener(this);

        mButtonValidate.setEnabled(false);

        resetForm();

    }

    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.mylocation_validate_button:
                validate();
                break;
            case R.id.mylocation_cancel_button:
                cancel();
                break;
            case R.id.mylocation_select_source_button:
                selectSource();
                break;
            case R.id.mylocation_get_position_button:
                getPosition();
                break;
            default:
                Toast.makeText(MyLocationActivity.this, "Not implemented yet!", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void validate()
    {
        LocationService.setLocation(mLocation);
        setResult( MYLOCATION_VALIDATE);
        finish();
    }
    
    private void cancel()
    {
        setResult( MYLOCATION_CANCEL);
        finish();
    }



    private void resetForm()
    {
        mLocation = LocationService.getLocation(this);
        if (mLocation == null)
        {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setAltitudeRequired(false);
            criteria.setBearingRequired(false);
            criteria.setCostAllowed(true);
            criteria.setPowerRequirement(Criteria.POWER_LOW);

            String bestProvider = mLocationManager.getBestProvider(criteria, true);
            mLocation = mLocationManager.getLastKnownLocation(bestProvider);
        }
        if (mLocation != null)
        {
            displayLocation();
            getAddress();
        }

        mButtonGetPosition.setEnabled(false);
        showProgress( false );
    }

    private void selectSource()
    {
        resetForm();

        List<String> providers = mLocationManager.getProviders(true);
        final String[] sources = new String[providers.size()];
        int i = 0;

        for (String provider : providers)
        {
            sources[i++] = provider;
        }

        new AlertDialog.Builder(MyLocationActivity.this).setItems(sources, new DialogInterface.OnClickListener()
        {

            public void onClick(DialogInterface dialog, int which)
            {
                mButtonGetPosition.setEnabled(true);
                mProvider = sources[which];
            }
        }).create().show();
    }

    private void getPosition()
    {
        showProgress(true);
        mButtonValidate.setEnabled(false);

        mLocationManager.requestLocationUpdates(mProvider, 60000, 0, this);
    }

    private void displayLocation()
    {
        mLatitude.setText(String.valueOf(mLocation.getLatitude()));
        mLongitude.setText(String.valueOf(mLocation.getLongitude()));
    }

    private void getAddress()
    {
        showProgress(true);

        Geocoder geo = new Geocoder(MyLocationActivity.this);
        try
        {
            List<Address> adresses = geo.getFromLocation(mLocation.getLatitude(), mLocation.getLongitude(), 1);

            if (adresses != null && adresses.size() == 1)
            {
                Address address = adresses.get(0);
                mAddress.setText(String.format("%s, %s %s",
                        address.getAddressLine(0),
                        address.getPostalCode(),
                        address.getLocality()));
                mButtonValidate.setEnabled(true);
            } else
            {
                mAddress.setText("No address found");
            }
        } catch (IOException e)
        {
            e.printStackTrace();
            mAddress.setText("Error retreiving the address");
        }
        showProgress(false);
    }

    public void onLocationChanged(Location location)
    {
        Log.i("ARTags - MyLocation", "Location changed");
        showProgress(false);

        this.mLocation = location;
        displayLocation();
        mLocationManager.removeUpdates(this);
        getAddress();
    }

    public void onProviderDisabled(String provider)
    {
        Log.i("ARTags - MyLocation", "Location Provider disabled");
        Toast.makeText(MyLocationActivity.this,
                String.format("Provider \"%s\" disabled", provider),
                Toast.LENGTH_SHORT).show();
        mLocationManager.removeUpdates(this);
        showProgress(false);
    }

    public void onProviderEnabled(String provider)
    {
        Log.i("ARTags - MyLocation", "Location Provider enabled.");
    }

    public void onStatusChanged(String provider, int status, Bundle extras)
    {
        Log.i("ARTags - MyLocation", "Location Provider status changed.");
    }

    private void showProgress( boolean show )
    {
        if( show )
        {
            mProgressBar.setVisibility( View.VISIBLE );
        }
        else
        {
            mProgressBar.setVisibility( View.INVISIBLE );
        }
    }
}
