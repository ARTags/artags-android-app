/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.artgameweekend.projects.art;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 *
 * @author pierre
 */
public class MainActivity extends Activity implements OnClickListener
{

    Button mButtonDraw;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);

        setContentView(R.layout.main);

        mButtonDraw = (Button) findViewById(R.id.button_draw);
        mButtonDraw.setOnClickListener(this);

        // ToDo add your GUI initialization code here        
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
