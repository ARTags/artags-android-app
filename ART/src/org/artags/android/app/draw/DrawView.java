/* Copyright (c) 2010-2011 ARTags Project owners (see http://www.artags.org)
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
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import org.artags.android.app.DrawActivity;
import org.artags.android.app.apilevels.ApiLevel5;
import org.artags.android.app.apilevels.ApiLevels;

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
    private Point mGlobalOffset = new Point(0, 0);
    //used in pinch
    private float oldDist;
    //private float oldAngle;
    private PointF mid;
    private PointF newMid;
    private Matrix matrix = null;
    private Matrix savedMatrix = null;
    private int touchMode;
    private final int NONE = 0;
    private final int ZOOM = 1;
    private final int DRAW = 2;

    /**
     * Constructor
     * @param activity The activity
     * @param dm Display metrics
     */
    public DrawView(DrawActivity activity, DisplayMetrics dm)
    {
        super(activity);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        {
            mGlobalOffset.y = 60; // ActionBar Height
        }

        mActivity = activity;

        mHeight = dm.heightPixels;
        mWidth = dm.widthPixels;
        this.mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        mid = new PointF();
        newMid = new PointF();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        mHeight = h;
        mWidth = w;


    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void onDraw(Canvas canvas)
    {
        canvas.drawColor(BACKGROUND_COLOR);
        //draw the bitmap with the zoom
        if (matrix == null || savedMatrix == null)
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
        if (this.touchMode == DRAW && (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE))
        {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
            this.haveMoved = true;
        }
    }

    private void touch_up()
    {
        if (this.touchMode == DRAW)
        {
            mPath.lineTo(mX, mY);
            if (!haveMoved)
            {
                mCanvas.drawPoint(mX, mY, mPaint);
            }
        }
        // commit the path to our offscreen
        mCanvas.drawPath(mPath, mPaint);
        // kill this so we don't double draw
        mPath.reset();
        this.touchMode = NONE;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        float x = getNewX(event.getX());
        float y = getNewY(event.getY() + (float) mGlobalOffset.y);

        if (!mEyeDropper)
        {
            switch (event.getAction())
            {
                /*case MotionEvent.ACTION_POINTER_DOWN:
                case MotionEvent.ACTION_POINTER_2_DOWN://deprecated but fail on my N1 without it.
                startZoom(event);
                break;*/
                case MotionEvent.ACTION_DOWN:
                    this.touchMode = DRAW;
                    touch_start(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (this.touchMode == ZOOM)
                    {
                        zoom(event);

                    } else
                    {
                        touch_move(x, y);
                        invalidate();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up();
                    invalidate();
                    break;
                default:
                    if (ApiLevels.getApiLevel() >= 5
                            && (event.getAction() == ApiLevel5.ACTION_POINTER_DOWN || event.getAction() == ApiLevel5.ACTION_POINTER_2_DOWN))
                    {
                        startZoom(event);
                    }
            }
        } else
        {
            if (event.getAction() == MotionEvent.ACTION_UP)
            {
                int color = mBitmap.getPixel((int) x, (int) y);
                mActivity.showBrushDialog(color);
                mEyeDropper = false;
            }

        }
        return true;
    }

    /**
     * Gets the bitmap
     * @return The bitmap
     */
    public Bitmap getBitmap()
    {
        return mBitmap;
    }

    /**
     * Sets the painter
     * @param paint The painter
     */
    public void setPaint(Paint paint)
    {
        mPaint = paint;
    }

    /**
     * Sets the bitmap
     * @param bm The bitmap
     */
    public void setBitmap(Bitmap bm)
    {
        mBitmap = bm.copy(Bitmap.Config.ARGB_8888, true);
        Log.d("ARTags", " w " + mWidth + " h " + mHeight + " bmw " + bm.getWidth() + " bmh " + bm.getHeight());
        if ((mHeight > mWidth) && (bm.getHeight() < bm.getWidth()))
        {
            Matrix m = new Matrix();
            m.postRotate(90);
            mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), m, true);
            Log.d("ARTAgs:Draw", "rotate +90");
        }
        if ((mHeight < mWidth) && (bm.getHeight() > bm.getWidth()))
        {
            Matrix m = new Matrix();
            m.postRotate(-90);
            mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), m, true);
            Log.d("ARTAgs:Draw", "rotate +90");
        }
        mCanvas = new Canvas(mBitmap);

    }

    private void save()
    {
        mBitmapUndo = Bitmap.createBitmap(mBitmap);
    }

    /**
     * Restore the drawing
     */
    public void restore()
    {
        if (mBitmapUndo != null)
        {
            mBitmap = Bitmap.createBitmap(mBitmapUndo);
            mCanvas = new Canvas(mBitmap);
            invalidate();
        }
    }

    /**
     * Create a new drawing
     */
    public void reset()
    {
        save();
        mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        invalidate();
    }

    /**
     * Sets the Eyedropper mode
     */
    public void setEyeDropperMode()
    {
        mEyeDropper = true;

    }

    /**
     * Create a thumbnail 
     * @return The thumbnail
     */
    public Bitmap getThumbnail()
    {
        Bitmap thumbnail = Bitmap.createBitmap(THUMBNAIL_SIZE, THUMBNAIL_SIZE, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(thumbnail);
        Rect rect = (mBitmap.getWidth() < mBitmap.getHeight())
                ? new Rect(THUMBNAIL_MARGIN, 0, THUMBNAIL_SIZE - THUMBNAIL_MARGIN, THUMBNAIL_SIZE)
                : new Rect(0, THUMBNAIL_MARGIN, THUMBNAIL_SIZE, THUMBNAIL_SIZE - THUMBNAIL_MARGIN);

        canvas.drawBitmap(mBitmap, null, rect, null);
        return thumbnail;
    }

    private void startZoom(MotionEvent event)
    {
        oldDist = spacing(event);
        //oldAngle = angle(event);
        if (oldDist > 50)
        {

            savedMatrix.set(matrix);
            midPoint(mid, event);
            midPoint(newMid, event);
            this.touchMode = ZOOM;
        }
    }

    private void zoom(MotionEvent event)
    {
        float newDist = spacing(event);
        //float newAngle = angle(event);
        if (newDist > 50)
        {
            matrix.set(savedMatrix);
            float scale = newDist / oldDist;
            //float angle = newAngle - oldAngle;
            matrix.postScale(scale, scale, mid.x, mid.y);

            //translate the picture if both fingers moved
            midPoint(newMid, event);
            matrix.postTranslate(newMid.x - mid.x, newMid.y - mid.y);
            //matrix.postRotate(angle);

            //be sure nothing outside the image will be visible
            float[] matrixValues = new float[9];
            matrix.getValues(matrixValues);

            if (matrixValues[Matrix.MTRANS_X] > 0)
            {
                matrixValues[Matrix.MTRANS_X] = 0;
            }

            if (matrixValues[Matrix.MTRANS_Y] > 0)
            {
                matrixValues[Matrix.MTRANS_Y] = 0;
            }

            if (matrixValues[Matrix.MSCALE_X] < 1)
            {
                matrixValues[Matrix.MSCALE_X] = 1;
                matrixValues[Matrix.MSCALE_Y] = 1;
            }

            //let's do some math to know how many pixels are empty at the bottom and/or right of the screen
            float bottomPadding = (mHeight * (1 - matrixValues[Matrix.MSCALE_Y])) - matrixValues[Matrix.MTRANS_Y];
            float rightPadding = (mWidth * (1 - matrixValues[Matrix.MSCALE_X])) - matrixValues[Matrix.MTRANS_X];

            if (bottomPadding > 0)
            {
                matrixValues[Matrix.MTRANS_Y] += bottomPadding;
            }
            if (rightPadding > 0)
            {
                matrixValues[Matrix.MTRANS_X] += rightPadding;
            }

            matrix.setValues(matrixValues);


            invalidate();
        }
    }

    private float spacing(MotionEvent event)
    {
        float x = 0;
        float y = 0;
        if (ApiLevels.getApiLevel() >= 5 && (event.getPointerCount() > 1))
        {
            x = ApiLevel5.getX(event, 0) - ApiLevel5.getX(event, 1);
            y = ApiLevel5.getY(event, 0) - ApiLevel5.getY(event, 1);
        }
        return FloatMath.sqrt(x * x + y * y);
    }

    private void midPoint(PointF point, MotionEvent event)
    {
        float x = 0;
        float y = 0;
        if (ApiLevels.getApiLevel() >= 5 && (event.getPointerCount() > 1))
        {
            x = ApiLevel5.getX(event, 0) + ApiLevel5.getX(event, 1);
            y = ApiLevel5.getY(event, 0) + ApiLevel5.getY(event, 1);
        }
        point.set(x / 2, y / 2);
    }

    private float getNewX(float x)
    {
        float[] matrixValues = new float[9];
        matrix.getValues(matrixValues);
        float newX = (x - matrixValues[Matrix.MTRANS_X]) / matrixValues[Matrix.MSCALE_X];
        return newX;
    }

    private float getNewY(float y)
    {
        float[] matrixValues = new float[9];
        matrix.getValues(matrixValues);
        float newY = (y - matrixValues[Matrix.MTRANS_Y]) / matrixValues[Matrix.MSCALE_Y];
        //Log.d("ARTAGS", "MSCALE_Y=" + matrixValues[Matrix.MSCALE_Y]);
        return newY;
    }
}
