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
package org.artags.android.app;

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
 * @author Pierre Levy
 */
public class CreditsActivity  extends Activity implements OnClickListener
{

    ImageView mImageView;

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
            /*Intent intent = new Intent();
            intent.setClassName( MainActivity.INTENT_PACKAGE, MainActivity.INTENT_MAIN_CLASS );
            startActivity(intent);*/
            this.finish();
        }
    }

}
