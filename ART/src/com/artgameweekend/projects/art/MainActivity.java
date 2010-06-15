/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.artgameweekend.projects.art;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import java.util.List;

/**
 *
 * @author pierre
 */
public class MainActivity extends Activity implements OnClickListener
{

    ImageButton mButtonDraw;
    ImageButton mButtonDisplay;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);
        Log.i("OMGWTFBBQ", "onCreate");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.main);
        mButtonDraw = (ImageButton) findViewById(R.id.button_draw);
        mButtonDisplay = (ImageButton) findViewById(R.id.button_display);

        mButtonDraw.setOnClickListener(this);
        mButtonDisplay.setOnClickListener(this);

    }

    public void onClick(View view)
    {
        if (view == mButtonDraw)
        {
            Log.d("MainActivity", "onClick:DRAW");
            Intent intent = new Intent();
            intent.setClassName("com.artgameweekend.projects.art", "com.artgameweekend.projects.art.FingerPaint");
            startActivity(intent);
        } else if (view == mButtonDisplay)
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
        }
    }
}
