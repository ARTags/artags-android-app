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
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import org.artags.android.app.DrawActivity;

/**
 *
 * @author Pierre Levy, Pierre Gros
 */
public class DrawView extends View
{
    private static final int THUMBNAIL_SIZE = 200;
    private static final int THUMBNAIL_MARGIN = 40;

    private final int BACKGROUND_COLOR = 0xFFAAAAAA;

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
    private boolean haveMoved = false;//to draw only 1 point

    //used in pinch
    private float oldDist;
    private PointF mid;
    private PointF newMid;

    private Matrix matrix = null;
    private Matrix savedMatrix = null;

    private int touchMode;
    private final int NONE = 0;
    private final int ZOOM = 1;
    private final int DRAW = 2;
            
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
        mid = new PointF();
        newMid = new PointF();
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
        canvas.drawColor(BACKGROUND_COLOR);
        //draw the bitmap with the zoom
        if(matrix == null || savedMatrix == null)
        {
            matrix = canvas.getMatrix();
            savedMatrix = canvas.getMatrix();
            matrix.reset();
        }
        canvas.setMatrix(matrix);
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
        this.haveMoved = false;
    }

    private void touch_move(float x, float y)
    {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (this.touchMode==DRAW && (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE))
        {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
            this.haveMoved = true;
        }
    }

    private void touch_up()
    {
        if (this.touchMode==DRAW)
        {
            mPath.lineTo(mX, mY);
            if(!haveMoved)
                mCanvas.drawPoint(mX, mY, mPaint);
        }
        // commit the path to our offscreen
        mCanvas.drawPath(mPath, mPaint);
        // kill this so we don't double draw
        mPath.reset();
        this.touchMode = NONE;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        float x = getNewX(event.getX());
        float y = getNewY(event.getY());

        if( ! mEyeDropper )
        {
            switch (event.getAction())
            {
                case MotionEvent.ACTION_POINTER_DOWN:
                case MotionEvent.ACTION_POINTER_2_DOWN://deprecated but fail on my N1 without it.
                    startZoom(event);
                    break;
                case MotionEvent.ACTION_DOWN:
                    this.touchMode = DRAW;
                    touch_start(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    if(this.touchMode == ZOOM)
                    {
                        zoom(event);

                    } else {
                        touch_move(x, y);
                        invalidate();
                    }
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

    private void startZoom(MotionEvent event)
    {
        oldDist = spacing(event);
        if (oldDist > 50) {

            savedMatrix.set(matrix);
            midPoint(mid, event);
            midPoint(newMid, event);
            this.touchMode = ZOOM;
        }
    }

    private void zoom(MotionEvent event)
    {
        float newDist = spacing(event);
        if (newDist > 50) {
            matrix.set(savedMatrix);
            float scale = newDist / oldDist;
            matrix.postScale(scale, scale, mid.x, mid.y);

            //translate the picture if both fingers moved
            midPoint(newMid, event);
            matrix.postTranslate(newMid.x-mid.x, newMid.y-mid.y);

            //be sure nothing outside the image will be visible
            float[] matrixValues = new float[9];
            matrix.getValues(matrixValues);

            if(matrixValues[Matrix.MTRANS_X]>0)
                matrixValues[Matrix.MTRANS_X] = 0;

            if(matrixValues[Matrix.MTRANS_Y]>0)
                matrixValues[Matrix.MTRANS_Y] = 0;

            if(matrixValues[Matrix.MSCALE_X]<1)
            {
                matrixValues[Matrix.MSCALE_X] = 1;
                matrixValues[Matrix.MSCALE_Y] = 1;
            }

            //let's do some math to know how many pixels are empty at the bottom and/or right of the screen
            float bottomPadding = (mHeight * (1-matrixValues[Matrix.MSCALE_Y])) - matrixValues[Matrix.MTRANS_Y];
            float rightPadding = (mWidth * (1-matrixValues[Matrix.MSCALE_X])) - matrixValues[Matrix.MTRANS_X];
            
            if(bottomPadding > 0)
            {
                matrixValues[Matrix.MTRANS_Y] += bottomPadding;
            }
            if(rightPadding > 0)
            {
                matrixValues[Matrix.MTRANS_X] += rightPadding;
            }

            matrix.setValues(matrixValues);

            
            invalidate();
        }
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    private float getNewX(float x) {
        float[] matrixValues = new float[9];
        matrix.getValues(matrixValues);
        float newX = (x - matrixValues[Matrix.MTRANS_X]) / matrixValues[Matrix.MSCALE_X];
        return newX;
    }
    private float getNewY(float y) {
        float[] matrixValues = new float[9];
        matrix.getValues(matrixValues);
        float newY = (y - matrixValues[Matrix.MTRANS_Y]) / matrixValues[Matrix.MSCALE_Y];
        //Log.d("ARTAGS", "MSCALE_Y=" + matrixValues[Matrix.MSCALE_Y]);
        return newY;
    }
}
