package uk.co.pilllogger.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

/**
 * Created by Alex on 17/03/14.
 */
public class HourOfDayView extends View
{
    Paint _fillPaint = new Paint();
    Paint _borderPaint = new Paint();
    private List<Integer> _data;
    private int _min;
    private int _max;

    public HourOfDayView(Context context) {
        super(context);
        preInit();
    }

    public HourOfDayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        preInit();
    }

    public HourOfDayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        preInit();
    }

    private void preInit() {
        _fillPaint.setColor(Color.BLUE);
        _fillPaint.setStyle(Paint.Style.FILL);
        _borderPaint.setColor(Color.WHITE);
        _borderPaint.setStyle(Paint.Style.STROKE);
    }

    public void setData(List<Integer> data){
        _data = data;

        for(int i : _data){
            if(i > _max)
                _max = i;

            if(i < _min)
                _min = i;
        }
    }

    @Override
    public void onDraw(Canvas canvas){
        int width = getWidth();
        int height = getHeight();

        int boxSize = Math.min(width / 24, height);

        int top = 0;
        int bottom = top + boxSize;
        for(int i = 0; i < 24; i++){
            int left = i * boxSize;
            int right = left + boxSize;
            canvas.drawRect(left, 0, right, bottom, _fillPaint);
            canvas.drawRect(left, 0, right, bottom, _borderPaint);
        }
    }
}
