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
package org.artags.android.app.draw;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import org.artags.android.app.DrawActivity;

/**
 *
 * @author Pierre Levy
 */
public class DrawView extends View
{
    private static final int THUMBNAIL_SIZE = 200;
    private static final int THUMBNAIL_MARGIN = 40;

    private Paint mPaint;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    private Paint mBitmapPaint;
    private int mHeight;
    private int mWidth;
    private Bitmap mBitmapUndo;
    private boolean mEyeDropper;
    private DrawActivity mActivity;
            
    public DrawView( DrawActivity activity, DisplayMetrics dm )
    {
        super( activity );

        mActivity = activity;

        mHeight = dm.heightPixels;
        mWidth = dm.widthPixels;


        mBitmap = Bitmap.createBitmap( mWidth, mHeight, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        mHeight = h;
        mWidth = w;
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
        save();
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

        if( ! mEyeDropper )
        {

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
        }
        else
        {
            if( event.getAction() == MotionEvent.ACTION_UP )
            {
                int color = mBitmap.getPixel( (int) x, (int) y );
                mActivity.showBrushDialog( color );
                mEyeDropper = false;
            }

        }
        return true;
    }

    public Bitmap getBitmap()
    {
        return mBitmap;
    }

    public void setPaint( Paint paint )
    {
        mPaint = paint;
    }

    public void setBitmap(Bitmap bm)
    {
        mBitmap = bm.copy(Bitmap.Config.ARGB_8888, true);
        mCanvas = new Canvas( mBitmap );

    }

    private void save()
    {
        mBitmapUndo = Bitmap.createBitmap(mBitmap);
    }

    public void restore()
    {
        if( mBitmapUndo != null )
        {
            mBitmap = Bitmap.createBitmap(mBitmapUndo);
            mCanvas = new Canvas( mBitmap );
            invalidate();
        }
    }

    public void reset()
    {
        save();
        mBitmap = Bitmap.createBitmap( mWidth, mHeight, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        invalidate();
    }

    public void setEyeDropperMode()
    {
         mEyeDropper = true;

    }


    public Bitmap getThumbnail()
    {
        Bitmap thumbnail = Bitmap.createBitmap( THUMBNAIL_SIZE , THUMBNAIL_SIZE , Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas( thumbnail );
        Rect rect = new Rect( THUMBNAIL_MARGIN , 0 , THUMBNAIL_SIZE - THUMBNAIL_MARGIN , THUMBNAIL_SIZE );
        canvas.drawBitmap( mBitmap, null, rect , null);
        return thumbnail;
    }
}
