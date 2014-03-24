package uk.co.pilllogger.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.Map;

import uk.co.pilllogger.R;
import uk.co.pilllogger.helpers.DateHelper;
import uk.co.pilllogger.state.State;

/**
 * Created by Alex on 17/03/14.
 */
public class DayOfWeekView extends View
{
    Paint _fillPaint = new Paint();
    Paint _borderPaint = new Paint();
    Paint _textPaint = new Paint();
    Paint _indicatorPaint = new Paint();
    private Map<Integer, Map<Integer, Integer>> _data;
    private int _day;
    private int _min = 0;
    private int _max = 0;
    private Context _context;
    int _width;
    int _height;
    int _border;
    int _textColour;

    public DayOfWeekView(Context context) {
        super(context);
        preInit(context);
    }

    public DayOfWeekView(Context context, AttributeSet attrs) {
        super(context, attrs);
        preInit(context);
    }

    public DayOfWeekView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        preInit(context);
    }

    private void preInit(Context context) {
        _textColour = getResources().getColor(R.color.text_grey_medium);
        _context = context;
        _fillPaint.setColor(context.getResources().getColor(R.color.pill_colour6));
        _fillPaint.setStyle(Paint.Style.FILL);
        _borderPaint.setColor(Color.DKGRAY);
        _borderPaint.setStyle(Paint.Style.STROKE);
        _indicatorPaint.setColor(_textColour);
        _indicatorPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        _indicatorPaint.setStrokeWidth(2);
        _indicatorPaint.setAntiAlias(true);
        _textPaint.setTextSize(13 * getResources().getDisplayMetrics().density);
        _textPaint.setColor(_textColour);
        _textPaint.setTypeface(State.getSingleton().getTypeface());
        _textPaint.setAntiAlias(true);
    }

    public void setData(Map<Integer, Map<Integer, Integer>> data, int day){
        _data = data;
        _day = day;

        for(int key : _data.keySet()){
            Map<Integer, Integer> values = _data.get(key);

            for(int valueKey : values.keySet()) {
                int value = values.get(valueKey);

                if (value > _max)
                    _max = value;

                if (value < _min)
                    _min = value;
            }
        }

        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas){

        _width = getWidth();
        _height = getHeight();
        _border = 0;

        float boxes = 24 * 7;
        float boxSize = _width / boxes;

        int top = 0;
        float bottom = top + (boxSize * 6);

        if(_data == null)
            return;

        for(int i = 1; i <= 7; i++) {
            Map<Integer, Integer> dayValues = _data.get(i);
            float dayLeft = ((i-1)*24* boxSize);
            for (int j = 0; j < 24; j++) {
                float left = dayLeft + (j * boxSize + (j * (_border * 2)));
                float right = left + boxSize;

                int value = 0;
                if (dayValues != null && dayValues.containsKey(j))
                    value = dayValues.get(j);

                float percentage = (100.0f / (_max - _min)) * value;
                float opacity = (percentage / 100.0f) * 255;

                _fillPaint.setAlpha((int) opacity);

                canvas.drawRect(left, top, right, bottom, _fillPaint);
                //canvas.drawRect(left, top, right, bottom, _borderPaint);
            }

            float dayRight = dayLeft + _width / 7;
            float dayLineTop = top + (boxSize * 7);
            canvas.drawLine(dayLeft, dayLineTop, dayRight, dayLineTop, _indicatorPaint);
            canvas.drawLine(dayLeft, dayLineTop, dayLeft, dayLineTop - boxSize, _indicatorPaint);

            String dayOfWeek = DateHelper.getShortDayOfWeek(i, _context);

            float measureText = _textPaint.measureText(dayOfWeek);

            _textPaint.setFakeBoldText(i == _day);
            _textPaint.setColor(i == _day ? Color.WHITE : _textColour);

            canvas.drawText(dayOfWeek, dayLeft + ((dayRight - dayLeft) - measureText) / 2f, dayLineTop + (boxSize * 8), _textPaint);

            if(i == 7)
                canvas.drawLine(dayRight, dayLineTop, dayRight, dayLineTop - boxSize, _indicatorPaint);
        }
    }
}
