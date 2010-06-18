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

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 *
 * @author pierre
 */
public class BrushSizeDialog extends Dialog implements SeekBar.OnSeekBarChangeListener, OnClickListener
{

    public interface OnBrushSizeListener
    {

        void brushSizeChanged(int size);
    }
    Context mContext;
    SeekBar mSeekBar;
    Button mButton;
    OnBrushSizeListener mListener;
    int mBrushSize;
    TextView mProgressText;

    public BrushSizeDialog(Context context, OnBrushSizeListener listener, int initialSize)
    {
        super(context);

        mBrushSize = initialSize;
        mContext = context;
        mListener = listener;
    }

    public void onProgressChanged(SeekBar arg0, int progress, boolean arg2)
    {
        Log.d("BrushSizeDialog", "Progress changed:" + progress );
        mProgressText.setText( "" + progress );
        mBrushSize = progress;

    }

    public void onStartTrackingTouch(SeekBar arg0)
    {
    }

    public void onStopTrackingTouch(SeekBar arg0)
    {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mSeekBar = new SeekBar(mContext);
        mSeekBar.setOnSeekBarChangeListener(this);
        mButton = new Button(mContext);
        mButton.setText("OK");
        mButton.setOnClickListener(this);

        mProgressText = new TextView(mContext);
        mSeekBar.setProgress( mBrushSize );

        LinearLayout layout = new LinearLayout(mContext);
        layout.setMinimumWidth(300);
        layout.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);

        layout.setOrientation(LinearLayout.VERTICAL);

        layout.addView(mSeekBar,
                new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        LinearLayout.LayoutParams layoutText = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);


        layout.addView( mProgressText , layoutText );
        layout.addView(mButton);

        setContentView(layout);
        setTitle("Brush Size");
    }

    public void onClick(View view)
    {
        if (view == mButton)
        {
            Log.d("BrushSizeDialog", "Click OK" );
            mListener.brushSizeChanged(mBrushSize);
            dismiss();

        }
    }
}
