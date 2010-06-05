/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.artgameweekend.projects.art;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

/**
 *
 * @author pierre
 */
public class MainActivity extends Activity implements OnClickListener
{
    private static final int BUTTON_SIZE = 390;

    ImageButton mButtonDraw;
    ImageButton mButtonDisplay;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);

        setContentView(R.layout.main);

        mButtonDraw = (ImageButton) findViewById(R.id.button_draw);
        Drawable image = getResources().getDrawable(R.drawable.icon);
        mButtonDraw.setImageDrawable( image );
        mButtonDraw.setMinimumWidth(BUTTON_SIZE);
        mButtonDraw.setMinimumHeight(BUTTON_SIZE);

        mButtonDraw.setOnClickListener(this);

        mButtonDisplay = (ImageButton) findViewById(R.id.button_display);
        mButtonDisplay.setImageDrawable( getResources().getDrawable(R.drawable.icon));
        mButtonDisplay.setOnClickListener(this);
        mButtonDisplay.setMinimumWidth(BUTTON_SIZE);
        mButtonDisplay.setMinimumHeight(BUTTON_SIZE);
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
    }
}
