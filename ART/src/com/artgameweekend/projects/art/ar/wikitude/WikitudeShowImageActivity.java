/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.artgameweekend.projects.art.ar.wikitude;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import com.artgameweekend.projects.art.R;

/**
 *
 * @author Android
 */
public class WikitudeShowImageActivity  extends Activity{
/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN); ;

        setContentView(R.layout.displayart);
    }
}
