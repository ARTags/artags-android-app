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

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import org.artags.android.app.draw.BrushParameters;
import org.artags.android.app.draw.SendInfos;
import org.artags.android.app.tag.TagUploadService;
import org.artags.android.app.tag.Tag;
import org.artags.android.app.draw.GraphicsActivity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.*;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import java.text.MessageFormat;
import java.util.Date;
import org.artags.android.app.draw.BrushDialog;
import org.artags.android.app.draw.DrawView;
import org.artags.android.app.draw.SendDialog;
import org.artags.android.app.menu.ActionItem;
import org.artags.android.app.menu.QuickAction;
import org.artags.android.app.preferences.PreferencesService;
import org.artags.android.app.util.bitmap.BitmapUtil;

/**
 *
 * @author Pierre Levy, Pierre Gros
 */

public class DrawActivity extends GraphicsActivity
        implements BrushDialog.OnBrushParametersChangedListener, SendDialog.OnSendListener
{

    private static final int INTENT_RESULT_MY_LOCATION = 0;
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
    private static final int DIALOG_README = 1;
    private DrawView mView;
    private ProgressThread progressThread;
    private ProgressDialog progressDialog;
    private MaskFilter mEmboss;
    private MaskFilter mBlur;
    private Paint mPaint;
    private BrushParameters mBP;
    private SendInfos mSendInfos;
    private SendDialog mDialogSend;
    

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
        {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        
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
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
        {
            createMenu();
        }
        displayReadme();
    }

    public void setBrushParameter(BrushParameters bp)
    {
        mBP = bp;
        mPaint.setColor(mBP.getColor());
        mPaint.setAlpha(mBP.getOpacity());
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
            case DIALOG_README:
                Dialog dialog;
                Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.draw_readme_title);
                builder.setPositiveButton(R.string.button_ok, null);
                builder.setMessage(R.string.draw_readme_text);
                dialog = builder.create();
                return dialog;
        }
        return null;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_draw, menu);
        }

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
        {
            showHideMenu();
        }
        return true;
    }
 
     /**
     * {@inheritDoc }
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        mPaint.setXfermode(null);

        switch (item.getItemId())
        {
            case R.id.menu_color:
                Log.i("ARTags", "Menu Brush Parameters selected");
                showBrushDialog();
                return true;
            case R.id.menu_eyedropper:
                Log.i("ARTags", "Menu Eyedropper selected");
                eyedropper();
                return true;
            case R.id.menu_reset:
                Log.i("ARTags", "Menu Reset selected");
                reset();
                return true;
            case R.id.menu_undo:
                Log.i("ARTags", "Menu Undo selected");
                undo();
                return true;
            case R.id.menu_erase:
                Log.i("ARTags", "Menu Erase selected");
                mPaint.setXfermode(new PorterDuffXfermode(
                        PorterDuff.Mode.CLEAR));
                return true;
            case R.id.menu_send:
                Log.i("ARTags", "Menu Send selected");
                send();
                return true;
 
        }
        return false;
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
                if( mSendInfos.isShare() )
                {
                    share();
                }
            } else
            {
                Toast.makeText(getApplicationContext(), getString(R.string.upload_failed), Toast.LENGTH_LONG).show();
            }

        }
    };


    private void share()
    {
        Object[] args =
        {
            mSendInfos.getTitle(),
            "" + mSendInfos.getTagId()
        };
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String sharePattern = getString(R.string.share_pattern);
        String shareString = MessageFormat.format(sharePattern, args);
        intent.putExtra(Intent.EXTRA_TEXT, shareString);
        startActivity(Intent.createChooser(intent, getString(R.string.share_chooser_title)));
    }

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
        builder.setMessage(getString(R.string.confirm_reset)).setCancelable(false).setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener()
        {

            public void onClick(DialogInterface dialog, int id)
            {
                mView.reset();
            }
        }).setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener()
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
        mDialogSend = new SendDialog(this, this);
        mDialogSend.show();
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
                String tagId = TagUploadService.upload(tag);
                mSendInfos.setTagId(Long.parseLong(tagId));
                Log.i("ARTags:DrawActivity:send", "Tag posted successfully");

                // Save a copy on the SD
                Date date = new Date();
                String savedfile = "tag-" + date.getTime() + ".png";
                BitmapUtil.saveImage(savedfile, mView.getBitmap(), mSendInfos.isLandscape());

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

    public void gotoMyLocation()
    {
        Intent intentMyLocation = new Intent();
        intentMyLocation.setClassName(MainActivity.INTENT_PACKAGE, MainActivity.INTENT_MYLOCATION_CLASS);
        startActivityForResult(intentMyLocation, INTENT_RESULT_MY_LOCATION);
    }
    
    private void displayReadme()
    {
        if(PreferencesService.instance().isDrawReadme(this))
        {
            showDialog(DIALOG_README);
            PreferencesService.instance().setDrawReadme(this, false);
        }
    }

    
    ////////////////////////////////////////////////////////////////////////////
    // Specific menu implementation < Honeycomb
    
        //create and populate the menu
    private void createMenu()
    {
        LayoutInflater inflater = (LayoutInflater)getWindow().getLayoutInflater();
        View menuView = inflater.inflate(R.layout.menu_draw, null);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT );
        addContentView(menuView, layoutParams);

        Button toolsButton = (Button) this.findViewById(R.id.button_tools);

        toolsButton.setOnClickListener(new View.OnClickListener() {
                //@Override
                public void onClick(View v) {
                        final QuickAction qa = new QuickAction(v);
                        //tools
                        qa.addActionItem(new ActionItem(getString(R.string.menu_color),
                                getResources().getDrawable(R.drawable.menu_color_small),
                                new OnClickListener() {
                                    //@Override
                                    public void onClick(View v) { qa.dismiss(); menuSelected(COLOR_MENU_ID);}
                                }
                        ));

                        //eraser
                        qa.addActionItem(new ActionItem(getString(R.string.menu_erase),
                                getResources().getDrawable(R.drawable.menu_erase_small),
                                new OnClickListener() {
                                    //@Override
                                    public void onClick(View v) { qa.dismiss(); menuSelected(ERASE_MENU_ID);}
                                }
                        ));

                        //eyedropper
                        qa.addActionItem(new ActionItem(getString(R.string.menu_eyedropper),
                                getResources().getDrawable(R.drawable.menu_eyedropper_small),
                                new OnClickListener() {
                                    //@Override
                                    public void onClick(View v) { qa.dismiss(); menuSelected(EYEDROPPER_MENU_ID);}
                                }
                        ));

                        //reset
                        qa.addActionItem(new ActionItem(getString(R.string.menu_reset),
                                getResources().getDrawable(R.drawable.menu_reset_small),
                                new OnClickListener() {
                                    //@Override
                                    public void onClick(View v) { qa.dismiss(); menuSelected(RESET_MENU_ID);}
                                }
                        ));
                        qa.show();
                }
        });

        Button undoButton = (Button) this.findViewById(R.id.button_undo);
        undoButton.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {
                menuSelected(UNDO_MENU_ID);
            }
        });

        Button sendButton = (Button) this.findViewById(R.id.button_send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {
                menuSelected(SEND_MENU_ID);
            }
        });
    }

    //Call this to show the menu or hide it if already displayed
    private void showHideMenu() {
    	LinearLayout footer = (LinearLayout) this.findViewById(R.id.footer_organize);
        if(footer == null) {
            return;
        }
    	if(isMenuVisible()) {
            footer.setVisibility(View.GONE);
        } else {
            footer.setVisibility(View.VISIBLE);
        }
    }
    private void showMenu() {
        LinearLayout footer = (LinearLayout) this.findViewById(R.id.footer_organize);
        if(footer == null) {
            return;
        }
    	footer.setVisibility(View.VISIBLE);

    }
    private void hideMenu() {
        LinearLayout footer = (LinearLayout) this.findViewById(R.id.footer_organize);
        if(footer == null) {
            return;
        }
    	footer.setVisibility(View.GONE);

    }
    private boolean isMenuVisible() {
    	LinearLayout footer = (LinearLayout) this.findViewById(R.id.footer_organize);
        if(footer == null) {
            return false;
        }
    	int visible = footer.getVisibility();
    	switch (visible) {
		case View.GONE:
		case View.INVISIBLE:
			return false;
		case View.VISIBLE:
		default:
			return true;
		}
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && isMenuVisible()) {
        	hideMenu();
        	return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    
    public void menuSelected(int item)
    {
        mPaint.setXfermode(null);

        switch (item)
        {
            case COLOR_MENU_ID:
                Log.i("ARTags", "Menu Brush Parameters selected");
                showBrushDialog();
                break;
            case EYEDROPPER_MENU_ID:
                Log.i("ARTags", "Menu Eyedropper selected");
                eyedropper();
                break;
            case RESET_MENU_ID:
                Log.i("ARTags", "Menu Reset selected");
                reset();
                break;
            case UNDO_MENU_ID:
                Log.i("ARTags", "Menu Undo selected");
                undo();
                break;
            case ERASE_MENU_ID:
                Log.i("ARTags", "Menu Erase selected");
                mPaint.setXfermode(new PorterDuffXfermode(
                        PorterDuff.Mode.CLEAR));
                break;
            case SEND_MENU_ID:
                Log.i("ARTags", "Menu Send selected");
                send();
                break;
        }
        //hideMenu();
    }
   
}
