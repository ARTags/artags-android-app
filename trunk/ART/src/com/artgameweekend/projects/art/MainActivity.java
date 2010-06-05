/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.artgameweekend.projects.art;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import org.openintents.intents.WikitudeARIntent;

/**
 *
 * @author pierre
 */
public class MainActivity extends Activity implements OnClickListener
{
    private static final int BUTTON_SIZE = 20;

    ImageButton mButtonDraw;
    ImageButton mButtonDisplay;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);

        setContentView(R.layout.main);

        mButtonDraw = (ImageButton) findViewById(R.id.button_draw);
        Drawable image = getResources().getDrawable(R.drawable.bt_draw);
        mButtonDraw.setImageDrawable( image );
        mButtonDraw.setMaxWidth(BUTTON_SIZE);
        mButtonDraw.setMaxHeight(BUTTON_SIZE);

        mButtonDraw.setOnClickListener(this);

        mButtonDisplay = (ImageButton) findViewById(R.id.button_display);
        mButtonDisplay.setImageDrawable( getResources().getDrawable(R.drawable.bt_display));
        mButtonDisplay.setOnClickListener(this);
       mButtonDisplay.setMaxWidth(BUTTON_SIZE);
       mButtonDisplay.setMaxHeight(BUTTON_SIZE);
    }

    public void onClick(View view)
    {
        if (view == mButtonDraw)
        {
            Log.d("MainActivity", "onClick:DRAW");
            Intent intent = new Intent();
            intent.setClassName("com.artgameweekend.projects.art", "com.artgameweekend.projects.art.FingerPaint");
            startActivity(intent);
        }
        else if (view == mButtonDisplay)
        {
            WikitudeARIntent intent = new WikitudeARIntent(this.getApplication(), null, "507419D8685F116E0AB61704F21734D0", "art");
            //intent.addTitleText("titleText");
            //intent.addTitleImageUri("http://www.clubtone.net/avatar/58/191139.png");

            intent.setPrintMarkerSubText(false);

            //WikitudePOI poi;

            //WikitudePOI poi = new WikitudePOI();
            try {
                intent.startIntent(this);
            }
            catch (ActivityNotFoundException e)
            {
                WikitudeARIntent.handleWikitudeNotFound(this);
            }
        }
    }
}
