/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.artgameweekend.projects.art;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import java.util.List;
import org.openintents.intents.WikitudeARIntent;
import org.openintents.intents.WikitudePOI;

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
        }
        else if (view == mButtonDisplay)
        {
            WikitudeARIntent intent = new WikitudeARIntent(this.getApplication(), null, "507419D8685F116E0AB61704F21734D0", "art");
            //intent.addTitleText("titleText");
            //intent.addTitleImageUri("http://www.clubtone.net/avatar/58/191139.png");

            intent.setPrintMarkerSubText(false);
            

            List<WikitudePOI> list = MyPOIs.getPOIs();
            intent.addPOIs(list);
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
