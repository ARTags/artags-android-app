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
package org.artags.android.app.draw;

import android.app.Dialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.io.IOException;
import java.util.List;
import org.artags.android.app.DrawActivity;
import org.artags.android.app.R;

/**
 *
 * @author Pierre Levy
 */
public class SendDialog extends Dialog implements OnClickListener, LocationListener
{

    private static final int SEARCH_GPS = 0;
    private static final int SEARCH_NETWORK = 1;
    private static final int TIMEOUT = 12000;
    private DrawActivity mActivity;
    private EditText mEditTitle;
    private Button mButtonSend;
    private Button mButtonCancel;
    private OnSendListener mListener;
    private Location mLocation;
    private LocationManager mLocationManager;
    private TextView mSeachGpsTextView;
    private TextView mSeachNetworkTextView;
    private TextView mAddressTextView;
    private CheckBox mShareCheckBox;

    private ProgressBar mProgressGps;
    private ProgressBar mProgressNetwork;
    private boolean mFound;
    private int mLocationSearch;
    private boolean mShare;

    public interface OnSendListener
    {

        void setSendInfos(SendInfos si);
    }

    public SendDialog(DrawActivity context, OnSendListener listener)
    {
        super(context);
        mActivity = context;
        mListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_send);

        mEditTitle = (EditText) findViewById(R.id.edit_title);

        mButtonSend = (Button) findViewById(R.id.send_send_button);
        mButtonCancel = (Button) findViewById(R.id.send_cancel_button);
        mSeachGpsTextView = (TextView) findViewById(R.id.send_search_gps);
        mSeachNetworkTextView = (TextView) findViewById(R.id.send_search_network);
        mAddressTextView = (TextView) findViewById(R.id.send_search_address);
        mProgressGps = (ProgressBar) findViewById(R.id.send_search_location_progress_gps );
        mProgressNetwork = (ProgressBar) findViewById(R.id.send_search_location_progress_network );
        mProgressNetwork.setVisibility(View.INVISIBLE);
        mShareCheckBox = (CheckBox) findViewById(R.id.send_share_checkbox);

        mButtonSend.setOnClickListener(this);
        mButtonCancel.setOnClickListener(this);
        mShareCheckBox.setOnClickListener(this);

        mButtonSend.setEnabled(false);

        Log.i("ARTags:SendDialog", "Start searching GPS location");
        mLocationManager = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 0, this);

        mLocationSearch = SEARCH_GPS;
        startTimeout();


    }

    public void onClick(View view)
    {
        if (view == mButtonSend)
        {
            if (mFound)
            {
                send();
            } else
            {
                hide();
            }
        } else if (view == mShareCheckBox )
        {
            mShare = true;
        }
        else if (view == mButtonCancel)
        {
            dismiss();
        }
    }

    private void send()
    {
        SendInfos si = new SendInfos();
        si.setTitle(mEditTitle.getText().toString());
        if (mLocation != null)
        {
            si.setLatitude(mLocation.getLatitude());
            si.setLongitude(mLocation.getLongitude());
        } else
        {
            si.setLatitude(48.0);
            si.setLongitude(2.0);
        }
        si.setShare(mShare);
        mListener.setSendInfos(si);
        dismiss();

    }

    public void onLocationChanged(Location location)
    {
        Log.i("ARTags:SendDialog", "Location found (" + location.getLatitude() + "," + location.getLongitude() + ")");
        mLocation = location;
        if( mLocationSearch == SEARCH_GPS )
        {
            mSeachGpsTextView.setText(mActivity.getString(R.string.send_search_gps_found));
        } else if( mLocationSearch == SEARCH_NETWORK )
        {
            mSeachGpsTextView.setText( mActivity.getString(R.string.send_search_network_found));
        }
        mProgressGps.setVisibility(View.INVISIBLE);
        mLocationManager.removeUpdates(this);
        mFound = true;
        mButtonSend.setEnabled(true);
        getAddress();
    }

    public void onProviderDisabled(String provider)
    {
        Log.i("ARTags:SendDialog", "Location Provider disabled");
        mLocationManager.removeUpdates(this);
    }

    public void onProviderEnabled(String provider)
    {
        Log.i("ARTags:SendDialog", "Location Provider enabled.");
    }

    public void onStatusChanged(String provider, int status, Bundle extras)
    {
        Log.i("ARTags:SendDialog", "Location Provider status changed.");
    }
    //
    final Handler mHandler = new Handler();
    final Runnable mUpdateLocation = new Runnable()
    {

        public void run()
        {
            setLocation();
        }
    };

    protected void startTimeout()
    {

        Thread t = new TimeoutThread();
        t.start();
    }

    private class TimeoutThread extends Thread
    {

        @Override
        public void run()
        {
            SystemClock.sleep(TIMEOUT);
            mHandler.post(mUpdateLocation);
        }
    }

    private void setLocation()
    {
        if (!mFound)
        {
            if (mLocationSearch == SEARCH_GPS)
            {
                mSeachGpsTextView.setText(mActivity.getString(R.string.send_search_gps_not_found_use_mylocation));
                mSeachNetworkTextView.setText(mActivity.getString(R.string.send_search_network) );
                mLocationSearch = SEARCH_NETWORK;
                mLocationManager.removeUpdates(this);
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60000, 0, this);
                startTimeout();
                mProgressGps.setVisibility(View.INVISIBLE);
                mProgressNetwork.setVisibility(View.VISIBLE);
            }
            else if (mLocationSearch == SEARCH_NETWORK )
            {
                mSeachNetworkTextView.setText(mActivity.getString(R.string.send_search_network_not_found) );
                mLocationManager.removeUpdates(this);
                mProgressNetwork.setVisibility(View.INVISIBLE);
            }
        }
    }
    
    private void getAddress()
    {
        Geocoder geo = new Geocoder(mActivity);
        try
        {
            List<Address> adresses = geo.getFromLocation(mLocation.getLatitude(), mLocation.getLongitude(), 1);

            if (adresses != null && adresses.size() == 1)
            {
                Address address = adresses.get(0);
                mAddressTextView.setText(String.format("%s, %s %s",
                        address.getAddressLine(0),
                        address.getPostalCode(),
                        address.getLocality()));
            } else
            {
                mAddressTextView.setText("No address found");
            }
        } catch (IOException e)
        {
            Log.e("ARTags - MyLocation", "Error retreiving the address" + e.getMessage());
            mAddressTextView.setText("Error retreiving the address");
        }
    }

}
