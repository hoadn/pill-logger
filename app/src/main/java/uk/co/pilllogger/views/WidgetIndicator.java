package uk.co.pilllogger.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import uk.co.pilllogger.R;
import uk.co.pilllogger.helpers.ColourHelper;
import uk.co.pilllogger.helpers.LayoutHelper;
import uk.co.pilllogger.helpers.Logger;

/**
 * Created by alex on 10/11/2013.
 */
public class WidgetIndicator extends ImageView {

    private static final String TAG = "ColourIndicator";
    private int _colour = Color.BLACK;
    private Paint _beltPaint = new Paint();

    public WidgetIndicator(Context context) {
        super(context);
        preInit(context);
    }

    public WidgetIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        preInit(context);
        init(attrs);
    }

    public WidgetIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        preInit(context);
        init(attrs);
    }

    private void preInit(Context context) {
        Drawable background = context.getResources().getDrawable(R.drawable.widget_indicator);

        this.setImageDrawable(background);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//            this.setBackground(background);
//        } else {
//            this.setBackgroundDrawable(background);
//        }
    }

    private void init(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(
                attrs,
                R.styleable.ColourIndicator);

        //Use a
        if (a != null) {
            int colour = a.getColor(R.styleable.ColourIndicator_android_color, Color.BLACK);

            setColour(colour);

            //Don't forget this
            a.recycle();
        }
    }

    public int getColour(){
        return _colour;
    }

    public void setColour(int colour){
        setColour(colour, false);
    }

    public void setColour(int colour, boolean lightStroke){
        int stroke = lightStroke ? ColourHelper.getLighter(colour) : ColourHelper.getDarker(colour);

        if(this.getBackground() == null){
            preInit(getContext());
        }

        GradientDrawable background = (GradientDrawable) this.getDrawable();

        if (background != null) {
            //background.setColor(getContext().getResources().getColor(R.color.translucent_whiter));
            //background.setStroke(6, colour);
        }

        _colour = colour;

        _beltPaint = new Paint();
        _beltPaint.setColor(colour);
        //_beltPaint.setAlpha(255);
        _beltPaint.setAntiAlias(true);
    }

    @Override
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);

        float width = getWidth();
        float height = getHeight();

        Bitmap bitmap = Bitmap.createBitmap((int)width, (int)height, Bitmap.Config.ARGB_8888);
        Canvas maskCanvas  = new Canvas(bitmap);

        final Path path = new Path();
        path.addCircle(
                (width / 2)
                , (height / 2)
                , Math.min(width, (height / 2))
                , Path.Direction.CCW);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        maskCanvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        paint.setFilterBitmap(true);

        maskCanvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);

        final Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(getContext().getResources().getColor(R.color.translucent_whiter));

        _beltPaint.setStyle(Paint.Style.FILL);
        float top = LayoutHelper.dpToPx(getContext(), 30);
        float bottom = LayoutHelper.dpToPx(getContext(), 45);

        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                (bitmap.getWidth() / 2) -4, backgroundPaint);

        canvas.drawRect(0, top, getWidth(), bottom, _beltPaint);

        _beltPaint.setStyle(Paint.Style.STROKE);
        _beltPaint.setStrokeWidth(6);
        _beltPaint.setAntiAlias(true);

        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                (bitmap.getWidth() / 2) - 4, _beltPaint);

        paint.setAlpha(255);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

        canvas.drawBitmap(bitmap, rect, rect, paint);

    }
}
