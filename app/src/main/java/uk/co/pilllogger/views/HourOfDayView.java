package uk.co.pilllogger.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import uk.co.pilllogger.R;

/**
 * Created by Alex on 17/03/14.
 */
public class HourOfDayView extends View
{
    Paint _fillPaint = new Paint();
    Paint _borderPaint = new Paint();
    Paint _textPaint = new Paint();
    Paint _indicatorPaint = new Paint();
    private Map<Integer, Integer> _data;
    private int _hour;
    private int _min = 0;
    private int _max = 0;
    private Context _context;
    private String _hourText = "";
    private int _desiredHeight = 0;
    private int _desiredWidth = 0;
    int _width;
    int _height;
    int _border;
    int _boxSize;

    public HourOfDayView(Context context) {
        super(context);
        preInit(context);
    }

    public HourOfDayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        preInit(context);
    }

    public HourOfDayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        preInit(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int border = 1;


        int boxSize = (width - (24*(border*2))) / 24;

        final int desiredHSpec = MeasureSpec.makeMeasureSpec(boxSize*5, MeasureSpec.EXACTLY);
        final int desiredWSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        setMeasuredDimension(desiredWSpec, desiredHSpec);

        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
//        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
//        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
//        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
//
//        _desiredHeight = _boxSize * 5;
//        _desiredWidth = _width;
//
//        int width;
//        int height;
//
//        //Measure Width
//        if (widthMode == MeasureSpec.EXACTLY) {
//            //Must be this size
//            width = widthSize;
//        } else if (widthMode == MeasureSpec.AT_MOST) {
//            //Can't be bigger than...
//            width = Math.min(_desiredWidth, widthSize);
//        } else {
//            //Be whatever you want
//            width = _desiredWidth;
//        }
//
//        //Measure Height
//        if (heightMode == MeasureSpec.EXACTLY) {
//            //Must be this size
//            height = heightSize;
//        } else if (heightMode == MeasureSpec.AT_MOST) {
//            //Can't be bigger than...
//            height = Math.min(_desiredHeight, heightSize);
//        } else {
//            //Be whatever you want
//            height = _desiredHeight;
//        }
//
//        //MUST CALL THIS
//        setMeasuredDimension(width, height);
    }

    private void preInit(Context context) {
        _context = context;
        _fillPaint.setColor(context.getResources().getColor(R.color.pill_colour1));
        _fillPaint.setStyle(Paint.Style.FILL);
        _borderPaint.setColor(Color.GRAY);
        _borderPaint.setStyle(Paint.Style.STROKE);
        _indicatorPaint.setColor(Color.WHITE);
        _indicatorPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        _indicatorPaint.setStrokeWidth(2);
        _indicatorPaint.setAntiAlias(true);
        _textPaint.setTextSize(36);
        _textPaint.setColor(Color.WHITE);
    }

    public void setData(Map<Integer, Integer> data, int hour, String hourText){
        _data = data;
        _hour = hour;
        _hourText = hourText;

        for(int key : _data.keySet()){
            int value = _data.get(key);

            if(value > _max)
                _max = value;

            if(value < _min)
                _min = value;
        }

        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas){

        _width = getWidth();
        _height = getHeight();
        _border = 1;

        _boxSize = (_width - (24*(_border*2))) / 24;

        int top = (int) ((_boxSize / 1.5f) + 2) + 30;
        int bottom = top + _boxSize;


        for(int i = 0; i < 24; i++){
            int left = i * _boxSize + (i * (_border * 2));
            int right = left + _boxSize;

            int value = 0;
            if(_data != null && _data.containsKey(i))
                value = _data.get(i);

            float percentage = (100.0f / (_max - _min)) * value;
            float opacity = (percentage / 100.0f) * 255;

            _fillPaint.setAlpha((int)opacity);

            canvas.drawRect(left, top, right, bottom, _fillPaint);
            canvas.drawRect(left, top, right, bottom, _borderPaint);

            if(i == _hour){
                //canvas.drawCircle(left + (boxSize / 2), top - (boxSize / 2), 8, _indicatorPaint);
                int triangleTop = (int) (top - (_boxSize / 2f));
                Point a = new Point(left + (_boxSize / 4), triangleTop);
                Point b = new Point(right - (_boxSize / 4), triangleTop);
                Point c = new Point(left + (_boxSize / 2), top - 5);

                Path path = new Path();
                path.setFillType(Path.FillType.EVEN_ODD);
                path.moveTo(a.x, a.y);
                path.lineTo(b.x, b.y);
                path.moveTo(b.x, b.y);
                path.lineTo(c.x, c.y);
                path.moveTo(c.x, c.y);
                path.lineTo(a.x, a.y);
                path.close();

                canvas.drawPath(path, _indicatorPaint);

                float timeWidth = _textPaint.measureText(_hourText);
                float timeLeft = (left + (_boxSize /2)) - timeWidth / 2f;

                if(timeLeft < 0)
                    timeLeft = 0;

                if(timeLeft + timeWidth > getWidth())
                    timeLeft = getWidth() - timeWidth - 5;

                canvas.drawText(_hourText, timeLeft, top - (_boxSize / 1.5f), _textPaint);
            }
        }

        String fewer = _context.getString(R.string.fewer);
        String more = _context.getString(R.string.more);
        float fewerWidth = _textPaint.measureText(fewer);
        float moreWidth = _textPaint.measureText(more);
        int fewerBoxes = (int) Math.ceil(fewerWidth / _boxSize);
        int moreBoxes = (int)Math.ceil(moreWidth / _boxSize);

        top += (_boxSize * 2);
        bottom = top + _boxSize;
        float percentage = 0;
        int endBox = 24 - (moreBoxes + 1);
        int startBox = endBox - 5;
        for(int i = startBox; i < endBox; i++){
            int left = i * _boxSize + (i * (_border * 2));
            int right = left + _boxSize;

            float opacity = (percentage / 100.0f) * 255;

            _fillPaint.setAlpha((int)opacity);
            canvas.drawRect(left, top, right, bottom, _fillPaint);
            canvas.drawRect(left, top, right, bottom, _borderPaint);

            percentage += 20;
        }

        canvas.drawText(fewer, (startBox * _boxSize) - fewerWidth, top + (_boxSize / 1.2f), _textPaint);
        canvas.drawText(more, ((endBox + 1)  * _boxSize) + 10, top + (_boxSize / 1.2f), _textPaint);
    }
}
