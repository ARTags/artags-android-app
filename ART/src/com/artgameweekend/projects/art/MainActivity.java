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
package com.artgameweekend.projects.art;

import com.artgameweekend.projects.art.ar.wikitude.WikitudeDisplayService;
import com.artgameweekend.projects.art.util.location.LocationService;
import com.artgameweekend.projects.art.ar.POIService;
import com.artgameweekend.projects.art.ar.GenericPOI;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
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
    private static final String INTENT_DRAW_PACKAGE = "com.artgameweekend.projects.art";
    private static final String INTENT_DRAW_CLASS = INTENT_DRAW_PACKAGE + ".DrawActivity";

    ImageButton mButtonDraw;
    ImageButton mButtonDisplay;

    @Override
    public void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);
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
            Intent intent = new Intent();
            intent.setClassName( INTENT_DRAW_PACKAGE , INTENT_DRAW_CLASS );
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
