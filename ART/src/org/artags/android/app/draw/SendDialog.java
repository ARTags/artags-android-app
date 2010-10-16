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
import org.artags.android.app.DrawActivity;
import org.artags.android.app.R;
import org.artags.android.app.preferences.PreferencesService;
import org.artags.android.app.util.location.LocationService;

/**
 *
 * @author Pierre Levy
 */
public class SendDialog extends Dialog implements OnClickListener, LocationListener
{

    private static final int TIMEOUT = 15000;
    private DrawActivity mActivity;
    private EditText mEditTitle;
    private CheckBox mCheckBoxLandscape;
    private Button mButtonSend;
    private Button mButtonCancel;
    private OnSendListener mListener;
    private Location mLocation;
    private LocationManager mLocationManager;
    private TextView mSeachTextView;
    private ProgressBar mProgress;
    public boolean mFound;

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
        // setTitle(R.string.dialog_send);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_send);


        mEditTitle = (EditText) findViewById(R.id.edit_title);
        mCheckBoxLandscape = (CheckBox) findViewById(R.id.checkbox_landscape);
        mButtonSend = (Button) findViewById(R.id.send_send_button);
        mButtonCancel = (Button) findViewById(R.id.send_cancel_button);
        mSeachTextView = (TextView) findViewById(R.id.send_search_location);
        mProgress = (ProgressBar) findViewById(R.id.send_search_location_progress);

        mCheckBoxLandscape.setOnClickListener(this);
        mButtonSend.setOnClickListener(this);
        mButtonCancel.setOnClickListener(this);

        mButtonSend.setEnabled(false);

        Log.i("ARTags:SendDialog", "Start searching GPS location");
        mLocationManager = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 0, this);

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
                mActivity.gotoMyLocation();
            }
        } else if (view == mButtonCancel)
        {
            dismiss();
        }
    }

    public void onMyLocationResult(boolean ok)
    {
        if (ok)
        {
            mSeachTextView.setText(mActivity.getString(R.string.send_approximate_location_found));
            mButtonSend.setText(mActivity.getString(R.string.send_button_send));
            mLocation = LocationService.getLocation(mActivity);
            mFound = true;
        } else
        {
            mSeachTextView.setText(mActivity.getString(R.string.send_search_gps_not_found));
            mButtonSend.setText(mActivity.getString(R.string.send_button_send));
            mButtonSend.setEnabled(false);
            mFound = false;
        }
        show();

    }

    private void send()
    {
        SendInfos si = new SendInfos();
        si.setTitle(mEditTitle.getText().toString());
        si.setLandscape(mCheckBoxLandscape.isChecked());
        if (mLocation != null)
        {
            si.setLatitude(mLocation.getLatitude());
            si.setLongitude(mLocation.getLongitude());
        } else
        {
            si.setLatitude(48.0);
            si.setLongitude(2.0);
        }
        mListener.setSendInfos(si);
        dismiss();

    }

    public void onLocationChanged(Location location)
    {
        Log.i("ARTags:SendDialog", "Location found (" + location.getLatitude() + "," + location.getLongitude() + ")");
        mLocation = location;
        mSeachTextView.setText(mActivity.getString(R.string.send_search_gps_found));
        mProgress.setVisibility(View.INVISIBLE);
        mLocationManager.removeUpdates(this);
        mFound = true;
        mButtonSend.setEnabled(true);
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
            if (PreferencesService.instance().getMyLocation(mActivity))
            {
                mSeachTextView.setText(mActivity.getString(R.string.send_search_gps_not_found_use_mylocation));
                mButtonSend.setText(mActivity.getString(R.string.send_button_mylocation));
                mButtonSend.setEnabled(true);
            } else
            {
                mSeachTextView.setText(mActivity.getString(R.string.send_search_gps_not_found));
            }
            mProgress.setVisibility(View.INVISIBLE);
        }
    }
}
