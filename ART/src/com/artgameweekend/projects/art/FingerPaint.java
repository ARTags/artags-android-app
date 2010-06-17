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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.*;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;

public class FingerPaint extends GraphicsActivity
        implements ColorPickerDialog.OnColorChangedListener, BrushSizeDialog.OnBrushSizeListener
{

    private static final int DIALOG_SEND = 1;
    MyView mView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        mView = new MyView(this);
        setContentView(mView);
        mBrushSize = 12;

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(0xFFA5C739);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(mBrushSize);

        mEmboss = new EmbossMaskFilter(new float[]
                {
                    1, 1, 1
                },
                0.4f, 6, 3.5f);

        mBlur = new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL);
    }
    private Paint mPaint;
    private MaskFilter mEmboss;
    private MaskFilter mBlur;
    private int mBrushSize;
    private EditText mEditTitle;

    public void colorChanged(int color)
    {
        mPaint.setColor(color);
    }

    public void brushSizeChanged(int size)
    {
        mBrushSize = size;
        mPaint.setStrokeWidth(mBrushSize);
        Log.d("FingerPaint", "SetBrushSize=" + size);
    }

    @Override
    protected Dialog onCreateDialog(int id)
    {
        LayoutInflater factory = LayoutInflater.from(this);
        final View textEntryView = factory.inflate(R.layout.dialog_send, null);
        mEditTitle = (EditText) textEntryView.findViewById(R.id.edit_title);
        AlertDialog.Builder builder = new AlertDialog.Builder(FingerPaint.this);
        builder.setIcon(R.drawable.icon);
        builder.setTitle(R.string.dialog_send);
        builder.setView(textEntryView);
        builder.setPositiveButton(R.string.send, new DialogInterface.OnClickListener()
        {

            public void onClick(DialogInterface dialog, int whichButton)
            {
                String title = mEditTitle.getText().toString();
                mView.send(title);

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

    public class MyView extends View
    {

        private Bitmap mBitmap;
        private Canvas mCanvas;
        private Path mPath;
        private Paint mBitmapPaint;
        private Context mContext;

        public MyView(Context c)
        {
            super(c);

            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            int height = dm.heightPixels;
            int width = dm.widthPixels;


            mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
            mPath = new Path();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
            mContext = c;
        }

        private void send(String title)
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
                    mBitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
                    fos.close();

                    double latitude = 48.0; // default value
                    double longitude = 2.0; // default value
                    Location location = LocationService.getLocation(mContext);
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

                    Toast.makeText(mContext, "Uploading tag. Please wait ...", Toast.LENGTH_LONG).show();

                    UploadService.upload(tag);

                    Toast.makeText(mContext, "Tag uploaded successfuly", Toast.LENGTH_LONG).show();

                } catch (Exception e)
                {
                    Log.e("Finge", "exception while writing image", e);
                }
            }
        }

        void showDialog()
        {
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh)
        {
            super.onSizeChanged(w, h, oldw, oldh);
        }

        @Override
        protected void onDraw(Canvas canvas)
        {
            canvas.drawColor(0xFFAAAAAA);

            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);

            canvas.drawPath(mPath, mPaint);
        }
        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 4;

        private void touch_start(float x, float y)
        {
            mPath.reset();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;
        }

        private void touch_move(float x, float y)
        {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE)
            {
                mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
                mX = x;
                mY = y;
            }
        }

        private void touch_up()
        {
            mPath.lineTo(mX, mY);
            // commit the path to our offscreen
            mCanvas.drawPath(mPath, mPaint);
            // kill this so we don't double draw
            mPath.reset();
        }

        @Override
        public boolean onTouchEvent(MotionEvent event)
        {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up();
                    invalidate();
                    break;
            }
            return true;
        }
    }
    private static final int COLOR_MENU_ID = Menu.FIRST;
    private static final int EMBOSS_MENU_ID = Menu.FIRST + 1;
    private static final int BLUR_MENU_ID = Menu.FIRST + 2;
    private static final int ERASE_MENU_ID = Menu.FIRST + 3;
//    private static final int SRCATOP_MENU_ID = Menu.FIRST + 6;
    private static final int SEND_MENU_ID = Menu.FIRST + 5;
    private static final int BRUSH_SIZE_MENU_ID = Menu.FIRST + 4;

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
                new ColorPickerDialog(this, this, mPaint.getColor()).show();
                return true;
            case BRUSH_SIZE_MENU_ID:
                final BrushSizeDialog dialogBrushSize = new BrushSizeDialog(this, this, mBrushSize);
                dialogBrushSize.show();
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
}
