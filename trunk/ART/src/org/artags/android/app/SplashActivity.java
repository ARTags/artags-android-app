/* Copyright (c) 2010 ARTags Project owners (see http://artags.org)
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
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.artags.android.app.preferences.PreferencesService;

/**
 *
 * @author pierre@artags.org
 */
public class SplashActivity extends Activity implements OnClickListener
{

    private static final String ASSET_EULA = "EULA";
    private static final int WHATS_NEW_DIALOG = 0;
    private static final int EULA_DIALOG = 1;
    private ImageView mImageView;
    private int mResTitle;
    private int mResMessage;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.splash);

        mImageView = (ImageView) findViewById(R.id.splash);
        mImageView.setOnClickListener(this);

        checkEulaAccepted();
        checkLastVersion();

    }

    public void onClick(View view)
    {
        if (view == mImageView)
        {
            Intent intent = new Intent();
            intent.setClassName(MainActivity.INTENT_PACKAGE, MainActivity.INTENT_MAIN_CLASS);
            startActivity(intent);
        }
    }

    @Override
    protected Dialog onCreateDialog(int id)
    {
        Dialog dialog;
        if (id == WHATS_NEW_DIALOG)
        {
            Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(mResTitle);
            builder.setPositiveButton(R.string.button_ok, null);
            builder.setMessage(mResMessage);
            dialog = builder.create();
        } else if ( id == EULA_DIALOG )
        {
             final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.eula_title);
            builder.setCancelable(true);
            builder.setPositiveButton(R.string.eula_button_accept, new DialogInterface.OnClickListener()
            {

                public void onClick(DialogInterface dialog, int which)
                {
                    PreferencesService.instance().setEulaAccepted(SplashActivity.this);
                }
            });
            builder.setNegativeButton(R.string.eula_button_refuse, new DialogInterface.OnClickListener()
            {

                public void onClick(DialogInterface dialog, int which)
                {
                    finish();
                }
            });
            builder.setOnCancelListener(new DialogInterface.OnCancelListener()
            {

                public void onCancel(DialogInterface dialog)
                {
                    finish();
                }
            });
            builder.setMessage(readEula());
            dialog = builder.create();

        }
        else
        {
            dialog = super.onCreateDialog(id);
        }
        return dialog;
    }

    private void checkLastVersion()
    {
        final int lastVersion = PreferencesService.instance().getVersion(this);
        if (lastVersion < Constants.VERSION)
        {
            if (lastVersion == 0)
            {
                // This is a new install
                mResTitle = R.string.first_run_dialog_title;
                mResMessage = R.string.first_run_dialog_message;
            } else
            {
                // This is an upgrade.
                mResTitle = R.string.whats_new_dialog_title;
                mResMessage = R.string.whats_new_dialog_message;
            }
            // show what's new message
            PreferencesService.instance().saveVersion(this, Constants.VERSION);
            showDialog(WHATS_NEW_DIALOG);
        }
    }

    private void checkEulaAccepted()
    {
        if (!PreferencesService.instance().isEulaAccepted(this))
        {
            showDialog(EULA_DIALOG);
        }
    }

    private CharSequence readEula()
    {
        BufferedReader in = null;
        try
        {
            in = new BufferedReader(new InputStreamReader(getAssets().open(ASSET_EULA)));
            String line;
            StringBuilder buffer = new StringBuilder();
            while ((line = in.readLine()) != null)
            {
                buffer.append(line).append('\n');
            }
            return buffer;
        } catch (IOException e)
        {
            return "";
        } finally
        {
            if (in != null)
            {
                try
                {
                    in.close();
                } catch (IOException e)
                {
                    // Ignore
                }
            }
        }
    }

 }
