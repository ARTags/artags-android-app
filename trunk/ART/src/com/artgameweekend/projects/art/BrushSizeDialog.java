/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.artgameweekend.projects.art;

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
