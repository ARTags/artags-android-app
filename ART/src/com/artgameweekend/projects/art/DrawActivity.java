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

import com.artgameweekend.projects.art.draw.BrushParameters;
import com.artgameweekend.projects.art.tag.TagUploadService;
import com.artgameweekend.projects.art.util.location.LocationService;
import com.artgameweekend.projects.art.tag.Tag;
import com.artgameweekend.projects.art.draw.GraphicsActivity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.*;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import com.artgameweekend.projects.art.draw.BrushDialog;
import com.artgameweekend.projects.art.draw.DrawView;
import java.io.File;
import java.io.FileOutputStream;

public class DrawActivity extends GraphicsActivity
        //        implements ColorPickerDialog.OnColorChangedListener, BrushSizeDialog.OnBrushSizeListener
        implements BrushDialog.OnBrushParametersChangedListener
{

    private static final int COLOR_MENU_ID = Menu.FIRST;
    private static final int EMBOSS_MENU_ID = Menu.FIRST + 1;
    private static final int BLUR_MENU_ID = Menu.FIRST + 2;
    private static final int ERASE_MENU_ID = Menu.FIRST + 3;
//    private static final int SRCATOP_MENU_ID = Menu.FIRST + 6;
    private static final int SEND_MENU_ID = Menu.FIRST + 5;
    private static final int BRUSH_SIZE_MENU_ID = Menu.FIRST + 4;
    private static final int DIALOG_PROGRESS = 0;
    private static final int DIALOG_SEND = 1;
    private static final int DEFAULT_BRUSH_SIZE = 12;
    private static final int DEFAULT_COLOR = 0xFFA5C739;
    private static final int DEFAULT_INTENSITY = 50;
    private DrawView mView;
    private ProgressThread progressThread;
    private ProgressDialog progressDialog;
    private MaskFilter mEmboss;
    private MaskFilter mBlur;
    private EditText mEditTitle;
    private boolean mLandscape;
    private Paint mPaint;
    private BrushParameters mBP;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mView = new DrawView(this, dm);
        setContentView(mView);

        mEmboss = new EmbossMaskFilter(new float[]
                {
                    1, 1, 1
                },
                0.4f, 6, 3.5f);

        mBlur = new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL);

        mBP = new BrushParameters();
        mBP.setBrushSize(DEFAULT_BRUSH_SIZE);
        mBP.setColor(DEFAULT_COLOR);
        mBP.setColorBase(DEFAULT_COLOR);
        mBP.setColorIntensity(DEFAULT_INTENSITY);
        mBP.setEmbossFilter(mEmboss);
        mBP.setBlurFilter(mBlur);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(mBP.getColor());
        mPaint.setStrokeWidth(mBP.getBrushSize());
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        mView.setPaint(mPaint);
    }

    public void setBrushParameter(BrushParameters bp)
    {
        mBP = bp;
        mPaint.setColor(mBP.getColor());
        mPaint.setStrokeWidth(mBP.getBrushSize());
        if (bp.isEmboss())
        {
            mPaint.setMaskFilter(bp.getEmbossFilter());
        } else
        {
            if (bp.isBlur())
            {
                mPaint.setMaskFilter(bp.getBlurFilter());
            } else
            {
                mPaint.setMaskFilter(null);
            }
        }
    }

    @Override
    protected Dialog onCreateDialog(int id)
    {
        switch (id)
        {
            case DIALOG_PROGRESS:
                progressDialog = new ProgressDialog(this);
                //               progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setMessage(getString(R.string.dialog_progress));
                progressThread = new ProgressThread(handler);
                progressThread.start();
                return progressDialog;
            case DIALOG_SEND:
                LayoutInflater factory = LayoutInflater.from(this);
                final View viewSend = factory.inflate(R.layout.dialog_send, null);
                mEditTitle = (EditText) viewSend.findViewById(R.id.edit_title);
                final CheckBox checkbox = (CheckBox) viewSend.findViewById(R.id.checkbox_landscape);
                checkbox.setOnClickListener(new OnClickListener()
                {

                    public void onClick(View v)
                    {
                        mLandscape = ((CheckBox) v).isChecked() ? true : false;
                    }
                });
                AlertDialog.Builder builder = new AlertDialog.Builder(DrawActivity.this);
                builder.setIcon(R.drawable.icon);
                builder.setTitle(R.string.dialog_send);
                builder.setView(viewSend);
                builder.setPositiveButton(R.string.send, new DialogInterface.OnClickListener()
                {

                    public void onClick(DialogInterface dialog, int whichButton)
                    {
                        showDialog(DIALOG_PROGRESS);

                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
                {

                    public void onClick(DialogInterface dialog, int whichButton)
                    {

                        /* User clicked cancel so do some stuff */
                    }
                });
                return builder.create();
        }
        return null;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        Resources res = getApplicationContext().getResources();

        menu.add(0, COLOR_MENU_ID, 0, getString(R.string.menu_color)).setShortcut('3', 'c').setIcon(res.getDrawable(R.drawable.menu_color));
        menu.add(0, EMBOSS_MENU_ID, 0, getString(R.string.menu_emboss)).setShortcut('4', 's').setIcon(res.getDrawable(R.drawable.menu_emboss));
        menu.add(0, BLUR_MENU_ID, 0, getString(R.string.menu_blur)).setShortcut('5', 'z').setIcon(res.getDrawable(R.drawable.menu_blur));
        menu.add(0, ERASE_MENU_ID, 0, getString(R.string.menu_erase)).setShortcut('5', 'z').setIcon(res.getDrawable(R.drawable.menu_erase));
        //       menu.add(0, SRCATOP_MENU_ID, 0, getString(R.string.menu_srcatop)).setShortcut('5', 'z');
        menu.add(0, BRUSH_SIZE_MENU_ID, 0, getString(R.string.menu_brush_size)).setShortcut('5', 'z').setIcon(res.getDrawable(R.drawable.menu_brush));
        menu.add(0, SEND_MENU_ID, 0, getString(R.string.menu_send)).setShortcut('5', 'z').setIcon(res.getDrawable(R.drawable.menu_save));


        /****   Is this the mechanism to extend with filter effects?
        Intent intent = new Intent(null, getIntent().getData());
        intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
        menu.addIntentOptions(
        Menu.ALTERNATIVE, 0,
        new ComponentName(this, NotesList.class),
        null, intent, 0, null);
         *****/
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        mPaint.setXfermode(null);
        mPaint.setAlpha(0xFF);

        switch (item.getItemId())
        {
            case COLOR_MENU_ID:
//                new ColorPickerDialog(this, this, mPaint.getColor()).show();
                final BrushDialog dialogBrushSize = new BrushDialog(this, this, mBP);
                dialogBrushSize.show();
                return true;

            case BRUSH_SIZE_MENU_ID:
                /*               final BrushSizeDialog dialogBrushSize = new BrushSizeDialog(this, this, mBrushSize);
                dialogBrushSize.show();
                return true;
                 */
                final BrushDialog dialogBrushSize2 = new BrushDialog(this, this, mBP);
                dialogBrushSize2.show();
                return true;

            case EMBOSS_MENU_ID:
                if (mPaint.getMaskFilter() != mEmboss)
                {
                    mPaint.setMaskFilter(mEmboss);
                } else
                {
                    mPaint.setMaskFilter(null);
                }
                return true;
            case BLUR_MENU_ID:
                if (mPaint.getMaskFilter() != mBlur)
                {
                    mPaint.setMaskFilter(mBlur);
                } else
                {
                    mPaint.setMaskFilter(null);
                }
                return true;
            case ERASE_MENU_ID:
                mPaint.setXfermode(new PorterDuffXfermode(
                        PorterDuff.Mode.CLEAR));
                return true;
            /*            case SRCATOP_MENU_ID:
            mPaint.setXfermode(new PorterDuffXfermode(
            PorterDuff.Mode.SRC_ATOP));
            mPaint.setAlpha(0x80);
            return true;
             */
            case SEND_MENU_ID:
                showDialog(DIALOG_SEND);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    final Handler handler = new Handler()
    {

        @Override
        public void handleMessage(Message msg)
        {
            boolean bOk = msg.getData().getBoolean("completed");
            dismissDialog(DIALOG_PROGRESS);
            removeDialog(DIALOG_PROGRESS);
            if (bOk)
            {
                Toast.makeText(getApplicationContext(), getString(R.string.upload_successful), Toast.LENGTH_LONG).show();
            } else
            {
                Toast.makeText(getApplicationContext(), getString(R.string.upload_failed), Toast.LENGTH_LONG).show();
            }


        }
    };

    private class ProgressThread extends Thread
    {

        Handler mHandler;

        ProgressThread(Handler h)
        {
            mHandler = h;
        }

        @Override
        public void run()
        {
            Looper.prepare();
            String title = mEditTitle.getText().toString();
            boolean bSend = send(title, mLandscape);
            Message msg = mHandler.obtainMessage();
            Bundle b = new Bundle();
            b.putBoolean("completed", bSend);
            msg.setData(b);
            mHandler.sendMessage(msg);
            Looper.loop();
        }

        private boolean send(String title, boolean bLandscape)
        {
            File root = Environment.getExternalStorageDirectory();
            if (root.canWrite())
            {
                try
                {
                    File directory = new File(root.getPath() + "/ARt");
                    if (!directory.exists())
                    {
                        directory.mkdir();
                    }
                    String filename = directory.getPath() + "/ARt.jpeg";
                    File file = new File(filename);
                    FileOutputStream fos = new FileOutputStream(file);
                    Bitmap bmTag = mView.getBitmap();
                    bmTag.compress(Bitmap.CompressFormat.JPEG, 80, fos);
                    fos.close();

                    double latitude = 48.0; // default value
                    double longitude = 2.0; // default value
                    Location location = LocationService.getLocation(getApplicationContext());
                    if (location != null)
                    {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }

                    Tag tag = new Tag();
                    tag.setTitle(title);
                    tag.setLatitude("" + latitude);
                    tag.setLongitude("" + longitude);
                    tag.setFilename(filename);
                    tag.setOrientation(bLandscape);

                    TagUploadService.upload(tag);
                    return true;


                } catch (Exception e)
                {
                    Log.e("ARt:DrawActivity:send", "Exception while writing or sending the tag", e);
                }
            }
            return false;
        }
    }
}
