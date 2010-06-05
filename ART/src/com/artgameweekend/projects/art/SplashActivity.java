/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.artgameweekend.projects.art;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

/**
 *
 * @author pierre
 */
public class SplashActivity extends Activity implements OnClickListener
{

    ImageView mImageView;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);

        setContentView(R.layout.splash);

        mImageView = (ImageView) findViewById(R.id.splash);
        mImageView.setImageResource(R.drawable.splash);
        mImageView.setOnClickListener(this);


    }

    public void onClick(View view)
    {
        if (view == mImageView)
        {
            Intent intent = new Intent();
            intent.setClassName("com.artgameweekend.projects.art", "com.artgameweekend.projects.art.MainActivity");
            startActivity(intent);
        }
    }
}
