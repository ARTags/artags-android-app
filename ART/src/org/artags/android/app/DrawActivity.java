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

import android.app.AlertDialog;
import org.artags.android.app.draw.BrushParameters;
import org.artags.android.app.draw.SendInfos;
import org.artags.android.app.tag.TagUploadService;
import org.artags.android.app.tag.Tag;
import org.artags.android.app.draw.GraphicsActivity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.*;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import java.util.Date;
import org.artags.android.app.draw.BrushDialog;
import org.artags.android.app.draw.DrawView;
import org.artags.android.app.draw.SendDialog;
import org.artags.android.app.preferences.PreferencesService;
import org.artags.android.app.util.bitmap.BitmapUtil;

public class DrawActivity extends GraphicsActivity
        implements BrushDialog.OnBrushParametersChangedListener, SendDialog.OnSendListener
{

    private static final String IMAGE_FILE = "last_sent_image.png";
    private static final String THUMBNAIL_FILE = "last_sent_thumbnail.png";
    private static final String IMAGE_FILE_BACKUP = "backup.png";
    private static final int COLOR_MENU_ID = Menu.FIRST;
    private static final int ERASE_MENU_ID = Menu.FIRST + 1;
    private static final int EYEDROPPER_MENU_ID = Menu.FIRST + 2;
    private static final int UNDO_MENU_ID = Menu.FIRST + 3;
    private static final int RESET_MENU_ID = Menu.FIRST + 4;
    private static final int SEND_MENU_ID = Menu.FIRST + 5;
    private static final int DIALOG_PROGRESS = 0;
    private DrawView mView;
    private ProgressThread progressThread;
    private ProgressDialog progressDialog;
    private MaskFilter mEmboss;
    private MaskFilter mBlur;
    private Paint mPaint;
    private BrushParameters mBP;
    private SendInfos mSendInfos;

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
        mBP.setEmbossFilter(mEmboss);
        mBP.setBlurFilter(mBlur);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
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
                progressDialog.setMessage(getString(R.string.dialog_progress));
                progressThread = new ProgressThread(handler);
                progressThread.start();
                return progressDialog;
        }
        return null;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        Resources res = getApplicationContext().getResources();

        menu.add(0, COLOR_MENU_ID, 0, getString(R.string.menu_color)).setIcon(res.getDrawable(R.drawable.menu_color));
        menu.add(0, ERASE_MENU_ID, 1, getString(R.string.menu_erase)).setIcon(res.getDrawable(R.drawable.menu_erase));
        menu.add(0, EYEDROPPER_MENU_ID, 2, getString(R.string.menu_eyedropper)).setIcon(res.getDrawable(R.drawable.menu_eyedropper));
        menu.add(0, UNDO_MENU_ID, 3, getString(R.string.menu_undo)).setIcon(res.getDrawable(R.drawable.menu_undo));
        menu.add(0, RESET_MENU_ID, 4, getString(R.string.menu_reset)).setIcon(res.getDrawable(R.drawable.menu_reset));
        menu.add(0, SEND_MENU_ID, 5, getString(R.string.menu_send)).setIcon(res.getDrawable(R.drawable.menu_send));

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
                Log.i("ARTags", "Menu Brush Parameters selected");
                showBrushDialog();
                return true;

            case EYEDROPPER_MENU_ID:
                Log.i("ARTags", "Menu Eyedropper selected");
                eyedropper();
                return true;

            case RESET_MENU_ID:
                Log.i("ARTags", "Menu Reset selected");
                reset();
                return true;

            case UNDO_MENU_ID:
                Log.i("ARTags", "Menu Undo selected");
                undo();
                return true;

            case ERASE_MENU_ID:
                Log.i("ARTags", "Menu Erase selected");
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
                Log.i("ARTags", "Menu Send selected");
                send();
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

    public void setSendInfos(SendInfos si)
    {
        mSendInfos = si;
        showDialog(DIALOG_PROGRESS);
    }

    private void undo()
    {

        mView.restore();
    }

    private void reset()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage( getString( R.string.confirm_reset))
                .setCancelable(false)
                .setPositiveButton( getString( R.string.yes ), new DialogInterface.OnClickListener()
        {

            public void onClick(DialogInterface dialog, int id)
            {
                mView.reset();
            }
        }).setNegativeButton( getString( R.string.no ), new DialogInterface.OnClickListener()
        {

            public void onClick(DialogInterface dialog, int id)
            {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void eyedropper()
    {
        mView.setEyeDropperMode();
    }

    private void send()
    {
        final SendDialog dialog = new SendDialog(this, this);
        dialog.show();
    }

    private void showBrushDialog()
    {
        final BrushDialog dialogBrushSize = new BrushDialog(this, this, mBP);
        dialogBrushSize.show();
    }

    public void showBrushDialog(int color)
    {
        mBP.setColor(color);
        mBP.setColorBase(color);
        mBP.setColorIntensity(50);
        showBrushDialog();
    }

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
            boolean bSend = send();
            Message msg = mHandler.obtainMessage();
            Bundle b = new Bundle();
            b.putBoolean("completed", bSend);
            msg.setData(b);
            mHandler.sendMessage(msg);
            Looper.loop();
        }

        private boolean send()
        {
            try
            {
                String filename = BitmapUtil.saveImage(IMAGE_FILE, mView.getBitmap());
                String thumbnail = BitmapUtil.saveImage(THUMBNAIL_FILE, mView.getThumbnail());

                Tag tag = new Tag();
                tag.setTitle(mSendInfos.getTitle());
                tag.setLatitude("" + mSendInfos.getLatitude());
                tag.setLongitude("" + mSendInfos.getLongitude());
                tag.setFilename(filename);
                tag.setThumbnail(thumbnail);
                tag.setOrientation(mSendInfos.isLandscape());
                Log.i("ARTags:DrawActivity:send", "Prepare tag post - Tag name : " + tag.getTitle());
                Log.i("ARTags:DrawActivity:send", "Prepare tag post - geoloc (" + tag.getLatitude() + "," + tag.getLongitude() + ")");

                Log.i("ARTags:DrawActivity:send", "Post tag");
                TagUploadService.upload(tag);
                Log.i("ARTags:DrawActivity:send", "Tag posted successfully");

                // Save a copy on the SD
                Date date = new Date();
                String savedfile = "tag-" + date.getTime() + ".png";
                BitmapUtil.saveImage(savedfile, mView.getBitmap());

                return true;


            } catch (Exception e)
            {
                Log.e("ARTags:DrawActivity:send", "Exception while writing or sending the tag", e);
                return false;
            }
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        PreferencesService.instance().saveBrushParameters(this, mBP);
        BitmapUtil.saveImage(IMAGE_FILE_BACKUP, mView.getBitmap());
    }

    @Override
    public void onResume()
    {
        super.onResume();
        PreferencesService.instance().restoreBrushParameters(this, mBP);
        setBrushParameter(mBP);
        Bitmap bm = BitmapUtil.loadImage(IMAGE_FILE_BACKUP);

        if (bm != null)
        {
            mView.setBitmap(bm);
        }

    }
}
