package com.echo.holographlibrary;

import android.graphics.Path;
import android.graphics.Region;

/**
 * Created by alex on 30/11/2013.
 */
public class StackBarSection {
    private int mColor;
    private int mStrokeColor;
    private float mValue;
    private String mValueString = null;

    private Path mPath = null;
    private Region mRegion = null;
    private boolean _translucent;

    public int getColor() {
        return mColor;
    }
    public void setColor(int color) {
        this.mColor = color;
    }
    public int getStrokeColor(){return mStrokeColor;}
    public void setStrokeColor(int strokeColor){this.mStrokeColor = strokeColor;}
    public float getValue() {
        return mValue;
    }
    public void setValue(float value) {
        this.mValue = value;
    }

    public String getValueString()
    {
        if (mValueString != null) {
            return mValueString;
        } else {
            return String.valueOf((int)mValue);
        }
    }

    public void setValueString(final String valueString)
    {
        mValueString = valueString;
    }

    public Path getPath() {
        return mPath;
    }
    public void setPath(Path path) {
        this.mPath = path;
    }
    public Region getRegion() {
        return mRegion;
    }
    public void setRegion(Region region) {
        this.mRegion = region;
    }

    public boolean isTranslucent() {
        return _translucent;
    }

    public void setTranslucent(boolean translucent) {
        _translucent = translucent;
    }
}
