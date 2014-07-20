package uk.co.pilllogger.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import uk.co.pilllogger.R;
import uk.co.pilllogger.helpers.ColourHelper;

/**
 * Created by alex on 10/11/2013.
 */
public class ColourIndicator extends ImageView {

    private static final String TAG = "ColourIndicator";
    private int _colour = Color.BLACK;

    public ColourIndicator(Context context) {
        super(context);
        preInit(context);
    }

    public ColourIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        preInit(context);
        init(attrs);
    }

    public ColourIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        preInit(context);
        init(attrs);
    }

    private void preInit(Context context) {
        Drawable background = context.getResources().getDrawable(R.drawable.colour_indicator);
        this.setImageDrawable(background);
    }

    private void init(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(
                attrs,
                R.styleable.ColourIndicator);

        //Use a
        if (a != null) {
            int colour = a.getColor(R.styleable.ColourIndicator_android_color, Color.BLACK);
            boolean selected = a.getBoolean(R.styleable.ColourIndicator_selected, false);

            setColour(colour, false, selected);

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
        setColour(colour, lightStroke, false);
    }

    public void setColour(int colour, boolean lightStroke, boolean selected){
        int stroke = lightStroke ? ColourHelper.getLighter(colour) : ColourHelper.getDarker(colour);

        if(this.getBackground() == null){
            preInit(getContext());
        }

        GradientDrawable background = (GradientDrawable) this.getDrawable();

        if (background != null) {
            background.setColor(colour);
            int width = selected ? 6 : 1;
            background.setStroke(width, stroke);
        }

        _colour = colour;
    }
}
