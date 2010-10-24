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

import android.os.Bundle;
import android.app.Dialog;
import android.content.Context;
import android.graphics.*;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import org.artags.android.app.R;

public class BrushDialog extends Dialog implements SeekBar.OnSeekBarChangeListener, OnClickListener
{

    private static final String DIALOG_BACKGROUND = "#707070";
    private OnBrushParametersChangedListener mListener;
    private int mInitialColor;
    private Context mContext;
    private TextView mProgressTextIntensity;
    private SeekBar mSeekBarIntensity;
    private TextView mProgressTextOpacity;
    private SeekBar mSeekBarOpacity;
    private TextView mProgressTextSize;
    private SeekBar mSeekBarSize;
    private Button mButtonOK;
    private ColorPickerView mColorPickerView;
    private BrushParameters mBP;
    private int mBrushSize;
    private int mIntensity;
    private int mOpacity;
    private int mColorBase;
    private boolean mEmboss;
    private boolean mBlur;
    private CheckBox mToggleEmboss;
    private CheckBox mToggleBlur;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        LinearLayout layout = new LinearLayout(mContext);
        layout.setBackgroundColor( Color.parseColor( DIALOG_BACKGROUND ) );

        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams dialogParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setPadding(15, 15, 15, 15);
        layout.setLayoutParams(dialogParams);

        setTitle(mContext.getString(R.string.dialog_brush));
        requestWindowFeature(Window.FEATURE_NO_TITLE);


        // Color picker
        TextView twColor = new TextView(mContext);
        twColor.setText(mContext.getString(R.string.label_color));

        layout.addView(twColor);

        mColorPickerView = new ColorPickerView(getContext(), mInitialColor);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        layout.addView(mColorPickerView, layoutParams);


        LayoutInflater factory = LayoutInflater.from(mContext);
        final View view = factory.inflate(R.layout.dialog_brush, null);

        layout.addView(view);
        // Color intensity seekbar
        mSeekBarIntensity = (SeekBar) view.findViewById(R.id.seek_intensity);
        mProgressTextIntensity = (TextView) view.findViewById(R.id.color_intensity);
        mSeekBarIntensity.setOnSeekBarChangeListener(this);
        mSeekBarIntensity.setProgress(mIntensity);

        // Color opacity seekbar
        mSeekBarOpacity = (SeekBar) view.findViewById(R.id.seek_opacity);
        mProgressTextOpacity = (TextView) view.findViewById(R.id.opacity);
        mSeekBarOpacity.setOnSeekBarChangeListener(this);
        mSeekBarOpacity.setProgress( (int) ( (float) (mOpacity + 1) / 2.55));

        // Brush size seekbar
        mSeekBarSize = (SeekBar) view.findViewById(R.id.seek_brush_size);
        mProgressTextSize = (TextView) view.findViewById(R.id.brush_size);
        mSeekBarSize.setOnSeekBarChangeListener(this);
        mSeekBarSize.setProgress(mBrushSize);

        // Filters
        mToggleEmboss = (CheckBox) view.findViewById(R.id.toggleEmboss);
        mToggleEmboss.setOnClickListener(this);
        mToggleEmboss.setChecked(mEmboss);
        mToggleBlur = (CheckBox) view.findViewById(R.id.toggleBlur);
        mToggleBlur.setOnClickListener(this);
        mToggleBlur.setChecked(mBlur);

        // OK button
        mButtonOK = (Button) view.findViewById(R.id.button_ok);
        mButtonOK.setOnClickListener(this);

        mSeekBarIntensity.setProgressDrawable( getContext().getResources().getDrawable(R.drawable.progress) );
        mSeekBarOpacity.setProgressDrawable( getContext().getResources().getDrawable(R.drawable.progress) );
        mSeekBarSize.setProgressDrawable( getContext().getResources().getDrawable(R.drawable.progress) );

        setContentView(layout);

    }

    public void onClick(View view)
    {
        if (view == mButtonOK)
        {
            Log.d("BrushDialog", "Click OK");
            mBP.setColor(mColorPickerView.getColor());
            mBP.setBrushSize(mBrushSize);
            mBP.setOpacity(mOpacity);
            mBP.setColorIntensity(mIntensity);
            mBP.setColorBase(mColorBase);
            mBP.setEmboss(mEmboss);
            mBP.setBlur(mBlur);
            mListener.setBrushParameter(mBP);
            dismiss();

        } else if (view == mToggleEmboss)
        {
            mEmboss = mToggleEmboss.isChecked();
        } else if (view == mToggleBlur)
        {
            mBlur = mToggleBlur.isChecked();
        }
        mColorPickerView.invalidate();
    }

    public void onProgressChanged(SeekBar seekbar, int progress, boolean arg2)
    {
        if (seekbar == mSeekBarSize)
        {
            Log.d("BrushSizeDialog", "Progress changed:" + progress);
            mProgressTextSize.setText("" + progress);
            mBrushSize = progress;


        } else if (seekbar == mSeekBarOpacity)
        {
            mProgressTextOpacity.setText("" + progress);
            mOpacity = (int) ( 2.55 * (float) progress);

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

    public interface OnBrushParametersChangedListener
    {

        void setBrushParameter(BrushParameters bp);
    }

    public class ColorPickerView extends View
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
            if (mEmboss)
            {
                mCenterPaint.setMaskFilter(mBP.getEmbossFilter());
            } else
            {
                if (mBlur)
                {
                    mCenterPaint.setMaskFilter(mBP.getBlurFilter());
                } else
                {
                    mCenterPaint.setMaskFilter(null);
                }
            }
            float r = CENTER_X - mPaint.getStrokeWidth() * 0.5f;

            canvas.translate(CENTER_X, CENTER_X);

            canvas.drawOval(new RectF(-r, -r, r, r), mPaint);
            mCenterPaint.setColor(getColor());
            mCenterPaint.setAlpha( mOpacity );
            canvas.drawCircle(0, 0, mBrushSize / 2, mCenterPaint);
/*
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
*/
            mSeekBarIntensity.setBackgroundColor( getColor());
            mSeekBarOpacity.setBackgroundColor( getColor());
            mSeekBarSize.setBackgroundColor( getColor());
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
                        mTrackingCenter = false;
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
        mOpacity = bp.getOpacity();
        mIntensity = bp.getColorIntensity();
        mColorBase = bp.getColorBase();
        mEmboss = bp.isEmboss();
        mBlur = bp.isBlur();
        mContext = context;
        mBP = bp;
    }

}
