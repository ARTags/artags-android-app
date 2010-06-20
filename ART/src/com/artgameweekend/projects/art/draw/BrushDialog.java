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
package com.artgameweekend.projects.art.draw;

import android.os.Bundle;
import android.app.Dialog;
import android.content.Context;
import android.graphics.*;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class BrushDialog extends Dialog implements SeekBar.OnSeekBarChangeListener, OnClickListener
{

    private static final int MAX_BRUSH_SIZE = 80;
    private OnBrushParametersChangedListener mListener;
    private int mInitialColor;
    private Context mContext;
    private TextView mProgressTextIntensity;
    private SeekBar mSeekBarIntensity;
    private TextView mProgressTextSize;
    private SeekBar mSeekBarSize;
    private Button mButtonOK;
    private ColorPickerView mColorPickerView;
    private BrushParameters mBP;
    private static int mBrushSize;
    private static int mIntensity;
    private static int mColorBase;

    public interface OnBrushParametersChangedListener
    {

        void setBrushParameter(BrushParameters bp);
    }

    private static class ColorPickerView extends View
    {

        private Paint mPaint;
        private Paint mCenterPaint;
        private final int[] mColors;
        private boolean mTrackingCenter;
        private boolean mHighlightCenter;

        ColorPickerView(Context c, int color)
        {
            super(c);
            mColors = new int[]
                    {
                        0xFFFF0000, 0xFFFF00FF, 0xFF0000FF, 0xFF00FFFF, 0xFF00FF00,
                        0xFFFFFF00, 0xFFFF0000
                    };
            Shader s = new SweepGradient(0, 0, mColors, null);

            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setShader(s);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(32);

            mCenterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mCenterPaint.setColor(color);
            mCenterPaint.setStrokeWidth(5);
        }

        @Override
        protected void onDraw(Canvas canvas)
        {
            float r = CENTER_X - mPaint.getStrokeWidth() * 0.5f;

            canvas.translate(CENTER_X, CENTER_X);

            canvas.drawOval(new RectF(-r, -r, r, r), mPaint);
            mCenterPaint.setColor(getColor());
            canvas.drawCircle(0, 0, mBrushSize / 2, mCenterPaint);

            if (mTrackingCenter)
            {
                int c = mCenterPaint.getColor();
                mCenterPaint.setStyle(Paint.Style.STROKE);

                if (mHighlightCenter)
                {
                    mCenterPaint.setAlpha(0xFF);
                } else
                {
                    mCenterPaint.setAlpha(0x80);
                }
                canvas.drawCircle(0, 0,
                        CENTER_RADIUS + mCenterPaint.getStrokeWidth(),
                        mCenterPaint);

                mCenterPaint.setStyle(Paint.Style.FILL);
                mCenterPaint.setColor(c);
            }
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
        {
            setMeasuredDimension(CENTER_X * 2, CENTER_Y * 2);
        }
        private static final int CENTER_X = 100;
        private static final int CENTER_Y = 100;
        private static final int CENTER_RADIUS = 32;

        private int floatToByte(float x)
        {
            int n = java.lang.Math.round(x);
            return n;
        }

        private int pinToByte(int n)
        {
            if (n < 0)
            {
                n = 0;
            } else if (n > 255)
            {
                n = 255;
            }
            return n;
        }

        private int ave(int s, int d, float p)
        {
            return s + java.lang.Math.round(p * (d - s));
        }

        private int interpColor(int colors[], float unit)
        {
            if (unit <= 0)
            {
                return colors[0];
            }
            if (unit >= 1)
            {
                return colors[colors.length - 1];
            }

            float p = unit * (colors.length - 1);
            int i = (int) p;
            p -= i;

            // now p is just the fractional part [0...1) and i is the index
            int c0 = colors[i];
            int c1 = colors[i + 1];
            int a = ave(Color.alpha(c0), Color.alpha(c1), p);
            int r = ave(Color.red(c0), Color.red(c1), p);
            int g = ave(Color.green(c0), Color.green(c1), p);
            int b = ave(Color.blue(c0), Color.blue(c1), p);

            return Color.argb(a, r, g, b);
        }

        private int rotateColor(int color, float rad)
        {
            float deg = rad * 180 / 3.1415927f;
            int r = Color.red(color);
            int g = Color.green(color);
            int b = Color.blue(color);

            ColorMatrix cm = new ColorMatrix();
            ColorMatrix tmp = new ColorMatrix();

            cm.setRGB2YUV();
            tmp.setRotate(0, deg);
            cm.postConcat(tmp);
            tmp.setYUV2RGB();
            cm.postConcat(tmp);

            final float[] a = cm.getArray();

            int ir = floatToByte(a[0] * r + a[1] * g + a[2] * b);
            int ig = floatToByte(a[5] * r + a[6] * g + a[7] * b);
            int ib = floatToByte(a[10] * r + a[11] * g + a[12] * b);

            return Color.argb(Color.alpha(color), pinToByte(ir),
                    pinToByte(ig), pinToByte(ib));
        }
        private static final float PI = 3.1415926f;

        @Override
        public boolean onTouchEvent(MotionEvent event)
        {
            float x = event.getX() - CENTER_X;
            float y = event.getY() - CENTER_Y;
            boolean inCenter = java.lang.Math.sqrt(x * x + y * y) <= CENTER_RADIUS;

            switch (event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    mTrackingCenter = inCenter;
                    if (inCenter)
                    {
                        mHighlightCenter = true;
                        invalidate();
                        break;
                    }
                case MotionEvent.ACTION_MOVE:
                    if (mTrackingCenter)
                    {
                        if (mHighlightCenter != inCenter)
                        {
                            mHighlightCenter = inCenter;
                            invalidate();
                        }
                    } else
                    {
                        float angle = (float) java.lang.Math.atan2(y, x);
                        // need to turn angle [-PI ... PI] into unit [0....1]
                        float unit = angle / (2 * PI);
                        if (unit < 0)
                        {
                            unit += 1;
                        }
                        mColorBase = interpColor(mColors, unit);
                        mCenterPaint.setColor(getColor());
                        invalidate();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (mTrackingCenter)
                    {
                        if (inCenter)
                        {
//                            mListener.colorChanged(mCenterPaint.getColor());
                        }
                        mTrackingCenter = false;    // so we draw w/o halo
                        invalidate();
                    }
                    break;
            }
            return true;
        }

        int getColor()
        {
            int r = pinToByte(Color.red(mColorBase) + (512 * mIntensity / 100) - 255);
            int g = pinToByte(Color.green(mColorBase) + (512 * mIntensity / 100) - 255);
            int b = pinToByte(Color.blue(mColorBase) + (512 * mIntensity / 100) - 255);
            return Color.rgb(r, g, b);

        }
    }

    public BrushDialog(Context context, OnBrushParametersChangedListener listener, BrushParameters bp)
    {
        super(context);

        mListener = listener;
        mInitialColor = bp.getColor();
        mBrushSize = bp.getBrushSize();
        mIntensity = bp.getColorIntensity();
        mColorBase = bp.getColorBase();
        mContext = context;
        mBP = bp;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        LinearLayout layout = new LinearLayout(mContext);

        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams dialogParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
//        dialogParams.setMargins( 20, 20, 20, 20);
        layout.setLayoutParams(dialogParams);
        setTitle("Brush parameters");

        // Color picker
        TextView twColor = new TextView(mContext);
        twColor.setText("Brush color ");
        layout.addView(twColor);

        mColorPickerView = new ColorPickerView(getContext(), mInitialColor);
        layout.addView(mColorPickerView);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
//        layoutParams.setMargins( 20, 20, 20, 20);


        // Color intensity seekbar
        TextView twIntensity = new TextView(mContext);
        twIntensity.setText("Color intensity ");
        layout.addView(twIntensity);

        mSeekBarIntensity = new SeekBar(mContext);
        mProgressTextIntensity = new TextView(mContext);
        mSeekBarIntensity.setOnSeekBarChangeListener(this);
        mSeekBarIntensity.setProgress(mIntensity);
        layout.addView(mSeekBarIntensity,
                new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        layout.addView(mProgressTextIntensity, layoutParams);

        // Brush size seekbar
        TextView twSize = new TextView(mContext);
        twSize.setText("Brush size ");
        layout.addView(twSize);

        mSeekBarSize = new SeekBar(mContext);
        mProgressTextSize = new TextView(mContext);
        mSeekBarSize.setOnSeekBarChangeListener(this);
        mSeekBarSize.setMax(MAX_BRUSH_SIZE);
        mSeekBarSize.setProgress(mBrushSize);
        layout.addView(mSeekBarSize,
                new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        layout.addView(mProgressTextSize, layoutParams);

        // OK button
        mButtonOK = new Button(mContext);
        mButtonOK.setText("OK");
        mButtonOK.setOnClickListener(this);
        layout.addView(mButtonOK);

        setContentView(layout);

    }

    public void onClick(View view)
    {
        if (view == mButtonOK)
        {
            Log.d("BrushDialog", "Click OK");
            mBP.setColor(mColorPickerView.getColor());
            mBP.setBrushSize(mBrushSize);
            mBP.setColorIntensity(mIntensity);
            mBP.setColorBase(mColorBase);
            mListener.setBrushParameter(mBP);
            dismiss();

        }
    }

    public void onProgressChanged(SeekBar seekbar, int progress, boolean arg2)
    {
        if (seekbar == mSeekBarSize)
        {
            Log.d("BrushSizeDialog", "Progress changed:" + progress);
            mProgressTextSize.setText("" + progress);
            mBrushSize = progress;
        } else if (seekbar == mSeekBarIntensity)
        {
            mProgressTextIntensity.setText("" + progress);
            mIntensity = progress;
        }
        mColorPickerView.invalidate();

    }

    public void onStartTrackingTouch(SeekBar arg0)
    {
    }

    public void onStopTrackingTouch(SeekBar arg0)
    {
    }
}
