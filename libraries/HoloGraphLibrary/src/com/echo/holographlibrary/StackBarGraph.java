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
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class StackBarGraph extends View {

	private final static int VALUE_FONT_SIZE = 30, AXIS_LABEL_FONT_SIZE = 15;

    private List<StackBar> mBars = new ArrayList<StackBar>();
    private Paint mPaint = new Paint();
    private Rect mRectangle = null;
    private boolean mShowBarText = true;
    private int mIndexSelected = -1;
    private OnBarClickedListener mListener;
    private Bitmap mFullImage;
    private boolean mShouldUpdate = false;

    public boolean getShouldDrawGrid() {
        return mShouldDrawGridHorizontal;
    }

    public void setShouldDrawGrid(boolean mShouldDrawGrid) {
        this.mShouldDrawGridHorizontal = mShouldDrawGrid;
    }

    private boolean mShouldDrawGridHorizontal = false;
    private boolean mShouldDrawGridVertical = false;

    private Context mContext = null;

    public StackBarGraph(Context context) {
        super(context);
        mContext = context;
    }

    public StackBarGraph(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }
    
    public void setShowBarText(boolean show){
        mShowBarText = show;
    }
    
    public void setBars(List<StackBar> points){
        this.mBars = points;
        mShouldUpdate = true;
        postInvalidate();
    }
    
    public List<StackBar> getBars(){
        return this.mBars;
    }

    public void onDraw(Canvas ca) {
    	
        if (mFullImage == null || mShouldUpdate) {
            float density = mContext.getResources().getDisplayMetrics().density;
            mFullImage = Bitmap.createBitmap(getWidth(), getHeight(), Config.ARGB_8888);
            Canvas canvas = new Canvas(mFullImage);
            canvas.drawColor(Color.TRANSPARENT);
            NinePatchDrawable popup = (NinePatchDrawable)this.getResources().getDrawable(R.drawable.popup_black);
            
            float maxValue = 0;
            float padding = 1 * mContext.getResources().getDisplayMetrics().density;
            int selectPadding = (int) (4 * mContext.getResources().getDisplayMetrics().density);
            float bottomPadding = 20 * mContext.getResources().getDisplayMetrics().density;
            float leftPadding = bottomPadding;
            
            float usableHeight;
            if (mShowBarText) {
                this.mPaint.setTextSize(VALUE_FONT_SIZE * mContext.getResources().getDisplayMetrics().scaledDensity);
                Rect r3 = new Rect();
                this.mPaint.getTextBounds("$", 0, 1, r3);
                usableHeight = getHeight()-(bottomPadding*2)-Math.abs(r3.top-r3.bottom)-24 * density;
            } else {
                usableHeight = getHeight()-(bottomPadding*2);
            }
            
            float barWidth = (getWidth() - (padding*2)*mBars.size() - leftPadding * 2)/mBars.size();

            // Maximum y value = sum of all values.
            for (final StackBar bar : mBars) {
                if (bar.getTotalValue() > maxValue) {
                    maxValue = bar.getTotalValue();
                }
            }
            
            mRectangle = new Rect();
            
            int count = 0;
            for (final StackBar bar : mBars) {
                float currentTop = 0;
                for(final StackBarSection section : bar.getSections()){
                    // Set bar bounds
                    int left = (int)((padding*2)*count + padding + barWidth*count) + (int)leftPadding;
                    int top = (int)(getHeight() - bottomPadding - (usableHeight * (section.getValue() / maxValue)));
                    int right = (int)((padding*2)*count + padding + barWidth*(count+1)) + (int)leftPadding;
                    int bottom = (int)(getHeight()-bottomPadding);

                    if(section.getValue() > 0){
                        bottom -= currentTop;
                        top -= currentTop;
                        if(section.getValue() > 1)
                           top -= padding * (section.getValue() - 1);
                    }

                    int stroke = (int)padding * 3;

                    mRectangle.set(left + stroke / 2, top + stroke / 2, right - stroke / 2, bottom - stroke / 2);

                    if(section.getValue() > 0){
                        // Draw bar stroke
                        this.mPaint.setColor(section.getColor());
                        this.mPaint.setStrokeWidth(stroke);
                        this.mPaint.setStyle(Paint.Style.STROKE);
                        //this.mPaint.setAlpha(255);
                        canvas.drawRect(mRectangle, this.mPaint);

                        this.mPaint.setAlpha(50);
                        this.mPaint.setStyle(Paint.Style.FILL);
                        mRectangle.set(left + stroke, top + stroke, right - stroke, bottom - stroke);
                        canvas.drawRect(mRectangle, this.mPaint);

                        // Create selection region
                        Path path = new Path();
                        path.addRect(new RectF(mRectangle.left-selectPadding, mRectangle.top-selectPadding, mRectangle.right+selectPadding, mRectangle.bottom+selectPadding), Path.Direction.CW);
                        section.setPath(path);
                        section.setRegion(new Region(mRectangle.left-selectPadding, mRectangle.top-selectPadding, mRectangle.right+selectPadding, mRectangle.bottom+selectPadding));
                    }

                    // Draw x-axis label text
                    mPaint.setColor(Color.argb(100, 255, 255, 255));
                    this.mPaint.setTextSize(AXIS_LABEL_FONT_SIZE * mContext.getResources().getDisplayMetrics().scaledDensity);
                    int x = (int)(((mRectangle.left+mRectangle.right)/2)-(this.mPaint.measureText(bar.getName())/2));
                    int y = (int) (getHeight()-6 * mContext.getResources().getDisplayMetrics().scaledDensity);
                    canvas.drawText(bar.getName(), x, y, this.mPaint);

                    // Draw value text
                    if (mShowBarText){
                        this.mPaint.setTextSize(VALUE_FONT_SIZE * mContext.getResources().getDisplayMetrics().scaledDensity);
                        this.mPaint.setColor(Color.WHITE);
                        Rect r2 = new Rect();
                        this.mPaint.getTextBounds(section.getValueString(), 0, 1, r2);

                        int boundLeft = (int) (((mRectangle.left+mRectangle.right)/2)-(this.mPaint.measureText(section.getValueString())/2)-10 * mContext.getResources().getDisplayMetrics().density);
                        int boundTop = (int) (mRectangle.top+(r2.top-r2.bottom)-18 * mContext.getResources().getDisplayMetrics().density);
                        int boundRight = (int)(((mRectangle.left+mRectangle.right)/2)+(this.mPaint.measureText(section.getValueString())/2)+10 * mContext.getResources().getDisplayMetrics().density);
                        popup.setBounds(boundLeft, boundTop, boundRight, mRectangle.top);
                        popup.draw(canvas);

                        canvas.drawText(section.getValueString(), (int)(((mRectangle.left+mRectangle.right)/2)-(this.mPaint.measureText(section.getValueString()))/2), mRectangle.top-(mRectangle.top - boundTop)/2f+(float)Math.abs(r2.top-r2.bottom)/2f*0.7f, this.mPaint);
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
                count++;
            }
            mPaint.setColor(Color.argb(50, 255, 255, 255));
            mPaint.setStrokeWidth(padding);
            
            mPaint.setAntiAlias(true);
            if(mShouldDrawGridHorizontal){
                float singleItemHeight = usableHeight / maxValue;
                for(int i = 0; i < maxValue; i++){
                    float y = getHeight() - (bottomPadding + ((singleItemHeight) * i) + (padding * i) - density);
                    canvas.drawLine(0, y, getWidth(), y, mPaint);
                }
            }
            mShouldUpdate = false;
        }
        
        ca.drawBitmap(mFullImage, 0, 0, null);
        
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        Point point = new Point();
        point.x = (int) event.getX();
        point.y = (int) event.getY();
        
        int count = 0;
        for (StackBar bar : mBars){
            for(StackBarSection section : bar.getSections()){
                if(section.getValue() == 0) continue;

                Region r = new Region();
                r.setPath(section.getPath(), section.getRegion());
                if (r.contains(point.x, point.y) && event.getAction() == MotionEvent.ACTION_DOWN){
                    mIndexSelected = count;
                } else if (event.getAction() == MotionEvent.ACTION_UP){
                    if (r.contains(point.x,point.y) && mListener != null){
                        if (mIndexSelected > -1) mListener.onClick(mIndexSelected);
                        mIndexSelected = -1;
                    }
                }
                else if(event.getAction() == MotionEvent.ACTION_CANCEL)
                    mIndexSelected = -1;

                count++;
            }
        }
        
        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL){
            mShouldUpdate = true;
            postInvalidate();
        }
        
        

        return true;
    }
    
    @Override
    protected void onDetachedFromWindow()
    {
    	if(mFullImage != null)
    		mFullImage.recycle();
    	
    	super.onDetachedFromWindow();
    }
    
    public void setOnBarClickedListener(OnBarClickedListener listener) {
        this.mListener = listener;
    }
    
    public interface OnBarClickedListener {
        abstract void onClick(int index);
    }
}
