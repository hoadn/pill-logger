/*
 *     Created by Daniel Nadeau
 *     daniel.nadeau01@gmail.com
 *     danielnadeau.blogspot.com
 * 
 *     Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
 */

package com.echo.holographlibrary;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;

import java.util.ArrayList;
import java.util.List;

public class StackBarGraph extends View {

	private final static int VALUE_FONT_SIZE = 15, AXIS_LABEL_FONT_SIZE = 15;

    private List<StackBar> _stackBars = new ArrayList<StackBar>();
    private Paint mPaint = new Paint();
    private Paint mTextPaint = new Paint();
    private RectF mRectangle = null;
    private boolean mShowBarText = true;
    private int mIndexSelected = -1;
    private OnBarClickedListener mListener;
    private Bitmap _fullImage;
    private boolean _shouldUpdate = false;
    private int rightPadding = 50;
    private boolean _noData;
    private int _lineColour;

    private Runnable animator = new Runnable() {
        @Override
        public void run() {
            boolean needNewFrame = false;
            long now = AnimationUtils.currentAnimationTimeMillis();
            for (StackBar dynamics : _stackBars) {
                for(StackBarSection section : dynamics.getSections()) {
                    section.update(now);
                    if (!section.isAtRest()) {
                        needNewFrame = true;
                    }
                }
            }
            if (needNewFrame) {
                postDelayed(this, 20);
            }
            _shouldUpdate = true;
            invalidate();
        }
    };

    public boolean getShouldDrawGrid() {
        return mShouldDrawGridHorizontal;
    }

    public void setShouldDrawGrid(boolean mShouldDrawGrid) {
        this.mShouldDrawGridHorizontal = mShouldDrawGrid;
    }

    private boolean mShouldDrawGridHorizontal = false;
    private boolean mShouldDrawGridVertical = false;

    private Context _context = null;

    public StackBarGraph(Context context) {
        super(context);
        _context = context;
    }

    public StackBarGraph(Context context, AttributeSet attrs) {
        super(context, attrs);
        _context = context;
    }
    
    public void setShowBarText(boolean show){
        mShowBarText = show;
    }

    public void setBars(List<StackBar> points){
        this._stackBars = points;
        _noData = false;
        _shouldUpdate = true;
        removeCallbacks(animator);
        post(animator);
    }
    
    public List<StackBar> getBars(){
        return this._stackBars;
    }

    public void setNoData(boolean noData) {
        _noData = noData;
    }

    public void setLineColour(int lineColour){
        _lineColour = lineColour;
    }

    public void onDraw(Canvas ca) {
        if (_fullImage == null || _shouldUpdate) {
            float density = _context.getResources().getDisplayMetrics().density;
            _fullImage = Bitmap.createBitmap(getWidth(), getHeight(), Config.ARGB_8888);
            Canvas canvas = new Canvas(_fullImage);

            if (_noData) {
                Paint textPaint = new Paint();
                textPaint.setTextSize(16 * density);
                textPaint.setColor(Color.WHITE);
                textPaint.setAntiAlias(true);
                String noConsumptions = "No Consumptions in the past 7 days";
                float textWidth = textPaint.measureText(noConsumptions);
                Rect bounds = new Rect();
                textPaint.getTextBounds("a", 0, 1, bounds);
                canvas.drawText(
                        noConsumptions,
                        ((getWidth() - textWidth) /2),
                        ((getHeight() - (textPaint.descent() + textPaint.ascent())) /2),
                        textPaint);
                ca.drawBitmap(_fullImage, 0, 0, null);
                return;
            }

            float maxValue = 0;
            float padding = 1 * _context.getResources().getDisplayMetrics().density;
            float bottomPadding = 20 * _context.getResources().getDisplayMetrics().density;
            float leftPadding = padding;

            float usableHeight;
            if (mShowBarText) {
                this.mPaint.setTextSize(VALUE_FONT_SIZE * _context.getResources().getDisplayMetrics().scaledDensity);
                Rect r3 = new Rect();
                this.mPaint.getTextBounds("$", 0, 1, r3);
                usableHeight = getHeight()-(bottomPadding*2)-Math.abs(r3.top-r3.bottom)-24 * density;
            } else {
                usableHeight = getHeight()-(bottomPadding*2);
            }

            this.mTextPaint.setTextSize(VALUE_FONT_SIZE * _context.getResources().getDisplayMetrics().scaledDensity);
            this.mTextPaint.setAntiAlias(true);
            // Maximum y value = sum of all values.
            for (final StackBar bar : _stackBars) {
                if (bar.getTotalValue() > maxValue) {
                    maxValue = bar.getTotalValue();
                }
            }

            if(maxValue >= 10){
                rightPadding = 75;
            }

            float barWidth = (getWidth() - (padding*2)* _stackBars.size() - (leftPadding + rightPadding))/ _stackBars.size();

            mRectangle = new RectF();

            mPaint.setColor(_lineColour);
            mPaint.setStrokeWidth(padding);

            mPaint.setAntiAlias(true);
            mTextPaint.setColor(Color.argb(150, 0, 0, 0));

            if(mShouldDrawGridHorizontal){
                float singleItemHeight = usableHeight / maxValue;
                for(int i = 0; i <= maxValue; i++){
                    if(maxValue > 5){
                        if(i % 5 > 0){
                            continue;
                        }
                    }

                    float y = getHeight() - (bottomPadding + ((singleItemHeight) * i) + (padding * i) - density) - (padding * 0.75f);
                    canvas.drawLine(0, y, getWidth() - rightPadding, y, mPaint);

                    Rect textBounds = new Rect();
                    String value = String.valueOf(i);
                    mTextPaint.getTextBounds(value, 0, value.length(), textBounds);

                    canvas.drawText(String.valueOf(i),
                            (getWidth() - rightPadding) + (rightPadding/2 - textBounds.width()/2f),
                            y + textBounds.height()/2f,
                            mTextPaint);
                }
            }

            int count = 0;
            for (final StackBar bar : _stackBars) {
                float currentTop = 0;
                for(final StackBarSection section : bar.getSections()){
                    // Set bar bounds
                    int left = (int)((padding*2)*count + padding + barWidth*count) + (int)leftPadding;
                    int top = (int)(getHeight() - bottomPadding - (usableHeight * (section.getValue() / maxValue)));
                    int right = (int)((padding*2)*count + padding + barWidth*(count+1)) + (int)leftPadding;
                    int bottom = (int)(getHeight()-bottomPadding);

                    if(section.getTargetValue() > 0){
                        bottom -= currentTop;
                        top -= currentTop;
                        if(section.getTargetValue() > 1)
                           top -= padding * (section.getValue() - 1);
                    }

                    int stroke = (int)(padding * 1.5f);

                    mRectangle.set(left + stroke / 2.0f, top + stroke / 2.0f, right - stroke / 2.0f, bottom - stroke / 2.0f);

                    if(section.getTargetValue() > 0){
                        if(!section.isTranslucent()) {
                            this.mPaint.setColor(Color.WHITE);
                            this.mPaint.setStrokeWidth(stroke);
                            this.mPaint.setStyle(Paint.Style.FILL);
                            canvas.drawRect(mRectangle, this.mPaint);
                        }

                        // Draw bar stroke
                        this.mPaint.setColor(section.getStrokeColor());
                        this.mPaint.setStrokeWidth(stroke);
                        this.mPaint.setStyle(Paint.Style.STROKE);
                        canvas.drawRect(mRectangle, this.mPaint);

                        this.mPaint.setColor(section.getColor());
                        this.mPaint.setAlpha(100);
                        this.mPaint.setStyle(Paint.Style.FILL);
                        mRectangle.set(left + stroke, top + stroke, right - stroke, bottom - stroke);
                        canvas.drawRect(mRectangle, this.mPaint);
                    }

                    if (mIndexSelected == count && mListener != null) {
                        this.mPaint.setColor(Color.parseColor("#33B5E5"));
                        this.mPaint.setAlpha(100);
                        //canvas.drawPath(section.getPath(), this.mPaint);
                        this.mPaint.setAlpha(255);
                    }
                    currentTop += (bottom-top);

                    if(section.getValue() > 0)
                        currentTop += padding;
                }

                // Draw x-axis label text
                mTextPaint.setColor(Color.argb(150, 0, 0, 0));
                this.mTextPaint.setTextSize(AXIS_LABEL_FONT_SIZE * _context.getResources().getDisplayMetrics().scaledDensity);
                int x = (int)(((mRectangle.left+mRectangle.right)/2)-(this.mTextPaint.measureText(bar.getName())/2));
                int y = (int) (getHeight()-6 * _context.getResources().getDisplayMetrics().scaledDensity);
                canvas.drawText(bar.getName(), x, y, this.mTextPaint);
                count++;
            }

            _shouldUpdate = false;
        }
        
        ca.drawBitmap(_fullImage, 0, 0, null);
        
    }
    
    @Override
    protected void onDetachedFromWindow()
    {
    	if(_fullImage != null)
    		_fullImage.recycle();
    	
    	super.onDetachedFromWindow();
    }
    
    public void setOnBarClickedListener(OnBarClickedListener listener) {
        this.mListener = listener;
    }
    
    public interface OnBarClickedListener {
        abstract void onClick(int index);
    }
}
