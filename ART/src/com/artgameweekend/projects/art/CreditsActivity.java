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
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

/**
 *
 * @author pierre
 */
public class CreditsActivity  extends Activity implements OnClickListener
{

    ImageView mImageView;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.credits);

        mImageView = (ImageView) findViewById(R.id.credits);
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
